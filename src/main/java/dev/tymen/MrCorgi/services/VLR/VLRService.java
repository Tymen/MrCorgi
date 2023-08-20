package dev.tymen.MrCorgi.services.VLR;

import dev.tymen.MrCorgi.models.entities.Match;
import dev.tymen.MrCorgi.services.MatchService;
import dev.tymen.MrCorgi.services.VLR.Matches.MatchScraper;
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
public class VLRService implements ApplicationRunner {
    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchScraper matchScraper;

    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        scrapeUpcomingMatches();
    }

    public String VLRGetEvents() {
        try {
            String url = "https://www.vlr.gg/event/1657/valorant-champions-2023";
            StringBuilder results = new StringBuilder();

            try {
                Document document = Jsoup.connect(url).get();
                Element bracketContainer = document.select(".bracket-container.mod-upper").first();

                for (Element bracketCol : bracketContainer.select(".bracket-col")) {
                    String bracketLabel = bracketCol.select(".bracket-col-label").text();

                    // Add the bracket label with some decoration for better separation
                    results.append("▶ **").append(bracketLabel).append("** ◀\n\n");

                    for (Element bracketRow : bracketCol.select(".bracket-row")) {
                        Element bracketItem = bracketRow.select(".bracket-item").first();

                        String team1 = bracketItem.select(".bracket-item-team.mod-first .bracket-item-team-name span").text();
                        String team1Score = bracketItem.select(".bracket-item-team.mod-first .bracket-item-team-score").text();

                        String team2 = bracketItem.select(".bracket-item-team:not(.mod-first) .bracket-item-team-name span").text();
                        String team2Score = bracketItem.select(".bracket-item-team:not(.mod-first) .bracket-item-team-score").text();

                        int score1 = 0;
                        int score2 = 0;

                        try {
                            score1 = Integer.parseInt(team1Score);
                        } catch (NumberFormatException e) {
                            // handle or log the error if needed
                        }

                        try {
                            score2 = Integer.parseInt(team2Score);
                        } catch (NumberFormatException e) {
                            // handle or log the error if needed
                        }


                        // Add indicator to highlight the winning team
                        if (score1 > score2) {
                            team1 = "⭐ " + team1;
                        } else if (score2 > score1) {
                            team2 = "⭐ " + team2;
                        } // if scores are equal, it's a tie, so no team is highlighted

                        results.append(team1).append(" (").append(team1Score).append(") vs ")
                                .append(team2).append(" (").append(team2Score).append(")").append("\n");
                    }
                    results.append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed to fetch match results.";
            }
            System.out.println(results);
            return results.toString();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    static class MatchupResult {
        String title;
        String team1Name;
        String team1Score;
        String team2Name;
        String team2Score;

        public MatchupResult(String title, String team1Name, String team1Score, String team2Name, String team2Score) {
            this.title = title;
            this.team1Name = team1Name;
            this.team1Score = team1Score;
            this.team2Name = team2Name;
            this.team2Score = team2Score;
        }

        @Override
        public String toString() {
            return title + ": " + team1Name + " (" + team1Score + ") vs " + team2Name + " (" + team2Score + ")";
        }
    }

    @Scheduled(fixedRate = 90000)
    public void scrapeUpcomingMatches() throws IOException {
        matchScraper.getUpcomingMatches();
        logger.info("[VLRService] Obtained new match data at {}", LocalDateTime.now());
    }

    public void sendEmbedUpcomingMatches(TextChannel channel) {
        List<Match> getUpcomingMatches = matchService.findUpcomingAndOngoingMatches();
        for (Match match : getUpcomingMatches) {
            Button buttonTeam1 = Button.primary("VOTE_" + match.getTeam1Name().toUpperCase(), match.getTeam1Name());
            Button buttonTeam2 = Button.primary("VOTE_" + match.getTeam2Name().toUpperCase(), match.getTeam2Name());

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

            // Uncomment these once you have the voting retrieval working
            // embed.addField("Voters for " + match.getTeam1Name(), getVotersForTeam(match.getId(), match.getTeam1Name()), true);
            // embed.addField("Voters for " + match.getTeam2Name(), getVotersForTeam(match.getId(), match.getTeam2Name()), true);

            embed.setColor(Color.decode("#f18535"));
            channel.sendMessageEmbeds(embed.build()).setActionRow(buttonTeam1, buttonTeam2).queue();
        }
    }
}
