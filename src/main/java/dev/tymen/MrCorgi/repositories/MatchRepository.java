package dev.tymen.MrCorgi.repositories;

import dev.tymen.MrCorgi.models.entities.Match;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchRepository extends CrudRepository<Match, Integer> {
    @Query("SELECT m FROM Match m WHERE m.matchDateTime BETWEEN :now AND :oneDayLater")
    List<Match> findUpcomingAndOngoingMatches(LocalDateTime now, LocalDateTime oneDayLater);
}
