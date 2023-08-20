package dev.tymen.MrCorgi.services.VLR;

import dev.tymen.MrCorgi.models.entities.Match;
import dev.tymen.MrCorgi.models.entities.Vote;
import dev.tymen.MrCorgi.services.MatchService;
import dev.tymen.MrCorgi.services.VLR.Matches.MatchScraper;
import dev.tymen.MrCorgi.services.VoteService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VLRService {
    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchScraper matchScraper;

    @Autowired
    private VoteService voteService;

    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);

    @Scheduled(fixedRate = 90000)
    public void scrapeUpcomingMatches() throws IOException {
        matchScraper.getUpcomingMatches();
        logger.info("[VLRService] Obtained new match data at {}", LocalDateTime.now());
    }

    public String VLRGetEvents() {
        return matchScraper.VLRGetEvents();
    }

    public void sendEmbedUpcomingMatches(TextChannel channel) {
        List<Match> getUpcomingMatches = matchService.findUpcomingAndOngoingMatches();
        for (Match match : getUpcomingMatches) {
            String matchId = String.valueOf(match.getId());
            Button buttonTeam1 = Button.primary(matchId + "," + match.getTeam1Name(), match.getTeam1Name());
            Button buttonTeam2 = Button.primary(matchId + "," + match.getTeam2Name(), match.getTeam2Name());

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(match.getMatchEvent(), match.getMatchLink());

            // Match Information - Teams
            embed.addField(":fire: " + match.getTeam1Name(), match.getTeam1Score(), true);
            embed.addField("versus", "", true);
            embed.addField(":fire: " + match.getTeam2Name(), match.getTeam2Score(), true);

            // Time and Status
            embed.addField("**Time:**", match.getDiscordTimestamp(), true);
            embed.addField("**Status:**", match.getMatchStatus(), true);
            if (!match.getMatchETA().isEmpty()) {
                embed.addField("**ETA:**", match.getMatchETA(), true);
            }

            // Blank Space for separation
            embed.addField("\u200B", "\u200B", false);

            // Call to Action for Voting
            embed.addField(":ballot_box: **Cast Your Vote!**",
                    "Who do you think will win? Choose a team below!",
                    false
            );

            //Uncomment these once you have the voting retrieval working
            embed.addField("Voters for " + match.getTeam1Name(), getVotersForTeam(match.getId(), match.getTeam1Name()), true);
            embed.addField("Voters for " + match.getTeam2Name(), getVotersForTeam(match.getId(), match.getTeam2Name()), true);

            embed.setColor(Color.decode("#f18535"));
            channel.sendMessageEmbeds(embed.build()).setActionRow(buttonTeam1, buttonTeam2).queue();
        }
    }

    private String getVotersForTeam(Integer matchId, String teamName) {
        List<Vote> voters = voteService.getVotersByMatchAndTeam(matchId, teamName);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < voters.size(); i++) {
            Vote vote = voters.get(i);
            String getMention = "<@" + vote.getUser().getId() + ">";

            // Add comma if it's not the first value
            if (i != 0) {
                result.append(", ");
            }
            result.append(getMention);
        }
        return result.toString();
    }
}
