package dev.tymen.MrCorgi.slashcommands;

import dev.tymen.MrCorgi.models.entities.User;
import dev.tymen.MrCorgi.repositories.UserRepository;
import dev.tymen.MrCorgi.services.UserService;
import dev.tymen.MrCorgi.services.VLR.VLRService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;
import java.io.IOException;

@Component
public class SlashCommandsHandler extends ListenerAdapter {
    private final JDA jda;
    private final UserService userService;

    @Autowired
    public SlashCommandsHandler(@Lazy JDA jda, UserService userService) {
        this.jda = jda;
        this.userService = userService;
    }
    private void initilizeSlashCommands() {
        jda.updateCommands().addCommands(
                Commands.slash("ping", "Calculate ping of the bot"),
                Commands.slash("val", "valorant champions"),
                Commands.slash("upcoming", "upcoming matches")
//                Commands.slash("ban", "Ban a user from the server")
//                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS)) // only usable with ban permissions
//                        .setGuildOnly(true) // Ban command only works inside a guild
//                        .addOption(OptionType.USER, "user", "The user to ban", true) // required option of type user (target to ban)
        ).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
        VLRService vlr = new VLRService();
        // make sure we handle the right command
        switch (event.getName()) {
            case "ping":
                long time = System.currentTimeMillis();
                event.reply("Pong!").setEphemeral(true) // reply or acknowledge
                        .flatMap(v ->
                                event.getHook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time) // then edit original
                        ).queue(); // Queue both reply and edit
                break;
            case "val":
                event.replyEmbeds(sendEmbedResults(vlr.VLRGetEvents()).build()).queue();
                break;
            case "upcoming":
                List<MessageEmbed> embedList = null;
                try {
                    embedList = vlr.getUpcomingMatches();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                TextChannel channel = jda.getTextChannelById(event.getChannel().getId());
                for (MessageEmbed embed : embedList) {
                    assert channel != null;
                    channel.sendMessageEmbeds(embed).queue();
                }
                event.reply("Upcoming matches in the next 24 hours").queue();
                User user = new User(event.getUser().getId(), event.getUser().getName());
                userService.create(user);
//                event.replyEmbeds(sendEmbedResults(vlr.VLRGetEvents()).build()).queue();
                break;
        }
    }
    private EmbedBuilder sendEmbedResults(String results) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Tournament Bracket Results");
        embedBuilder.setColor(Color.BLUE);

        // Splitting results into separate lines to check length and divide embed fields
        String[] lines = results.split("\n");
        StringBuilder fieldContent = new StringBuilder();
        String fieldName = lines[0];  // Assuming the first line is a bracket label

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];

            if (line.trim().isEmpty()) {  // An empty line indicates a new bracket section
                embedBuilder.addField(fieldName, fieldContent.toString(), false);
                if (i + 1 < lines.length) {
                    fieldName = lines[i + 1];  // Set next bracket label
                }
                fieldContent = new StringBuilder();
                i++;  // Skip the bracket label line
            } else {
                fieldContent.append(line).append("\n");
            }
        }
        if (fieldContent.length() > 0) {
            embedBuilder.addField(fieldName, fieldContent.toString(), false);
        }
        return embedBuilder;
    }
}