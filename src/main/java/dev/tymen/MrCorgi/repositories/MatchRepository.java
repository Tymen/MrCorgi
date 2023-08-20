package dev.tymen.MrCorgi.repositories;

import dev.tymen.MrCorgi.models.entities.Match;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchRepository extends CrudRepository<Match, Integer> {
    @Query("SELECT m FROM Match m WHERE m.matchDateTime BETWEEN :now AND :twentyFourHoursLater OR m.matchStatus = 'LIVE'")
    List<Match> findUpcomingAndOngoingMatches(LocalDateTime now, LocalDateTime twentyFourHoursLater);

}
