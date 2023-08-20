package dev.tymen.MrCorgi.services.VLR.Matches;

import dev.tymen.MrCorgi.models.entities.Match;
import dev.tymen.MrCorgi.services.MatchService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MatchScraper {
    @Autowired
    private MatchService matchService;

    public void getUpcomingMatches() throws IOException {
        String url = "https://www.vlr.gg/event/matches/1657/valorant-champions-2023/?series_id=3264";


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

                String matchEvent = match.select(".match-item-event-series.text-of").text();

                int matchID = Integer.parseInt(extractMatchID(match.attr("href")));

                Match currentMatch = new Match(matchID, team1Name, team2Name, team1Score, team2Score, matchDateTime, matchStatus, matchLink, matchEvent);
                matchService.save(currentMatch);
            }
        }
    }

    private String extractMatchID(String href) {
        Pattern pattern = Pattern.compile("/(\\d+)/");  // Regular expression to match /digits/
        Matcher matcher = pattern.matcher(href);
        if (matcher.find()) {
            return matcher.group(1);  // Return the matched digits
        }
        return null;  // or throw an exception if no match found
    }
}
