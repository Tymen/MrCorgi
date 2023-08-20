package dev.tymen.MrCorgi.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "valorant_matches")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Match {
    @Id
    @Column(nullable = false, unique = true, updatable = false)
    private int id; // A unique identifier for the match

    @Column(nullable = false)
    private String team1Name;
    @Column(nullable = false)
    private String team2Name;
    @Column(nullable = false)
    private String team1Score;
    @Column(nullable = false)
    private String team2Score;
    @Column(nullable = false)
    private LocalDateTime matchDateTime;
    @Column(nullable = false)
    private String matchStatus;
    @Column(nullable = false)
    private String matchLink;
    @Column(nullable = false)
    private String matchEvent;

    public Match(int id, String team1Name, String team2Name, String team1Score, String team2Score, LocalDateTime matchDateTime, String matchStatus, String matchLink, String matchEvent) {
        this.id = id;
        this.team1Name = team1Name;
        this.team2Name = team2Name;
        this.team1Score = team1Score;
        this.team2Score = team2Score;
        this.matchDateTime = matchDateTime;
        this.matchStatus = matchStatus;
        this.matchLink = matchLink;
        this.matchEvent = matchEvent;
    }

    @Transient
    private String matchETA;

    @Transient
    private String discordTimestamp; // This will not be stored in the database

    @Transient
    private Duration durationBetweenNowAndMatch; // This will also not be stored in the database

    @PostLoad
    private void onLoad() {
        long epochSeconds = matchDateTime.atZone(ZoneId.of("Europe/Amsterdam")).toInstant().getEpochSecond();
        this.discordTimestamp = "<t:" + epochSeconds + ":t>";
        this.matchETA = "<t:" + epochSeconds + ":R>";

        LocalDateTime now = LocalDateTime.now();
        this.durationBetweenNowAndMatch = Duration.between(now, matchDateTime);
    }
}