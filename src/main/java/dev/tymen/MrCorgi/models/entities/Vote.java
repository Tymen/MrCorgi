package dev.tymen.MrCorgi.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "votes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer voteId; // A unique identifier for each vote

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // The user who cast the vote

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match; // The match the vote was cast for

    @Column(nullable = false)
    private String selectedTeam; // The team the user believes will win. You can use team name or some unique identifier.

    // You can add a timestamp for when the vote was cast, if needed

    public Vote(User user, Match match, String selectedTeam) {
        this.user = user;
        this.match = match;
        this.selectedTeam = selectedTeam;
    }
}