package dev.tymen.MrCorgi.services.VLR;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class VLRService {
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
    public List<MessageEmbed> getUpcomingMatches() throws IOException {
        String url = "https://www.vlr.gg/event/matches/1657/valorant-champions-2023/?series_id=3264";
        List<MessageEmbed> embeds = new ArrayList<>();

        Document document = Jsoup.connect(url).get();
        Elements days = document.select(".wf-label.mod-large");

        for (Element day : days) {
            String date = day.text().split(" ")[2] + " " + day.text().split(" ")[1] + ", " + day.text().split(" ")[3];
            Elements matches = day.nextElementSibling().select(".wf-module-item.match-item");

            for (Element match : matches) {
                String matchLink = "https://www.vlr.gg" + match.attr("href");
                String matchTime = match.select(".match-item-time").text();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d, MMMM, yyyy h:mm a", Locale.ENGLISH);
                LocalDateTime matchDateTime = LocalDateTime.parse(date + " " + matchTime, formatter);

                String team1Name = match.select(".match-item-vs-team .match-item-vs-team-name .text-of").get(0).ownText().trim();
                String team2Name = match.select(".match-item-vs-team .match-item-vs-team-name .text-of").get(1).ownText().trim();

                String team1Score = match.select(".match-item-vs-team").get(0).select(".match-item-vs-team-score").text();
                String team2Score = match.select(".match-item-vs-team").get(1).select(".match-item-vs-team-score").text();

                String matchStatus = match.select(".ml-status").text();
                String matchETA = match.select(".ml-eta").text();
                String matchEvent = match.select(".match-item-event-series.text-of").text();

                long epochSeconds = matchDateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
                String discordTimestamp = "<t:" + epochSeconds + ":R>";

                LocalDateTime now = LocalDateTime.now();
                Duration durationBetweenNowAndMatch = Duration.between(now, matchDateTime);

                if ((durationBetweenNowAndMatch.getSeconds() > 0 && durationBetweenNowAndMatch.toHours() <= 24)
                        || matchStatus.equalsIgnoreCase("LIVE")) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle(matchEvent, matchLink);
                    embed.addField("Teams", team1Name + " (" + team1Score + ") vs " + team2Name + " (" + team2Score + ")", false);
                    embed.addField("Time", discordTimestamp, true);
                    embed.addField("Status", matchStatus, true);
                    if (!matchETA.isEmpty()) {
                        embed.addField("ETA", matchETA, true);
                    }
                    embed.setColor(Color.BLUE);

                    embeds.add(embed.build());
                }
            }
        }
        return embeds;
    }
}
