package dev.tymen.MrCorgi.slashcommands;

import dev.tymen.MrCorgi.models.entities.User;
import dev.tymen.MrCorgi.services.UserService;
import dev.tymen.MrCorgi.services.VLR.VLRService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.io.IOException;
import java.util.Set;

@Component
public class SlashCommandsHandler extends ListenerAdapter {
    private final JDA jda;
    private final VLRService vlrService;
    private final UserService userService;
    private Set<String> votedUsers = new HashSet<>();

    @Autowired
    public SlashCommandsHandler(@Lazy JDA jda, UserService userService, VLRService vlrService) {
        this.jda = jda;
        this.userService = userService;
        this.vlrService = vlrService;
    }

    private void initilizeSlashCommands() {
        jda.updateCommands().addCommands(
                Commands.slash("ping", "Calculate ping of the bot"),
                Commands.slash("val", "valorant champions"),
                Commands.slash("upcoming", "upcoming matches"),
                Commands.slash("vote", "Vote for the next matches")
//                Commands.slash("ban", "Ban a user from the server")
//                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS)) // only usable with ban permissions
//                        .setGuildOnly(true) // Ban command only works inside a guild
//                        .addOption(OptionType.USER, "user", "The user to ban", true) // required option of type user (target to ban)
        ).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
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
                event.replyEmbeds(sendEmbedResults(vlrService.VLRGetEvents()).build()).queue();
                break;
            case "vote":
                sendVotingMessage(event);
                break;
            case "upcoming":
                event.reply("Loading Matches").queue();
                TextChannel channel = event.getChannel().asTextChannel();
                channel.sendMessage("Upcoming matches in the next 24 hours").queue();
                vlrService.sendEmbedUpcomingMatches(channel);
                User user = new User(event.getUser().getId(), event.getUser().getName());
                userService.create(user);
                break;
        }
    }

    public void sendVotingMessage(SlashCommandInteractionEvent event) {
        // Create buttons for the teams
        Button button1 = Button.primary("VOTE_PAPER_REX", "Paper Rex");
        Button button2 = Button.primary("VOTE_LOUD", "Loud");

        // Create an embed message to describe the match
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Who do you think will win?");
        embedBuilder.setDescription("Vote for the team you believe is going to win!");
        embedBuilder.addField("Voted for Paper Rex:", "Nobody yet", true);
        embedBuilder.addField("Voted for Loud:", "Nobody yet", true);

        MessageEmbed embed = embedBuilder.build();

        // Send the message with the buttons
        event.replyEmbeds(embed).setActionRow(button1, button2).queue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        net.dv8tion.jda.api.entities.User user = event.getUser();

        if (votedUsers.contains(user.getId())) {
            // Notify user they've already voted
            event.reply("You've already voted!").setEphemeral(true).queue();
            return;
        }

        Message message = event.getMessage();
        EmbedBuilder embedBuilder = new EmbedBuilder(message.getEmbeds().get(0));
        String fieldName;
        String replyText;

        if (event.getComponentId().equals("VOTE_PAPER_REX")) {
            fieldName = "Voted for Paper Rex:";
            replyText = "You voted for Paper Rex!";
        } else if (event.getComponentId().equals("VOTE_LOUD")) {
            fieldName = "Voted for Loud:";
            replyText = "You voted for Loud!";
        } else {
            return;
        }

        // Update the embed field with the user's name
        for (int i = 0; i < embedBuilder.getFields().size(); i++) {
            MessageEmbed.Field field = embedBuilder.getFields().get(i);
            if (field.getName().equals(fieldName)) {
                embedBuilder.getFields().set(i, new MessageEmbed.Field(fieldName, user.getAsMention(), true));
                break;
            }
        }

        // Update the original message with the new embed
        message.editMessageEmbeds(embedBuilder.build()).queue();

        // Add the user to the set of users who've voted
        votedUsers.add(user.getId());

        // Reply to the user to confirm their vote
        event.reply(replyText).queue();
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