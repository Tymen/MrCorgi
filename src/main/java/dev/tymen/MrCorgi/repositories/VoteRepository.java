package dev.tymen.MrCorgi.repositories;

import dev.tymen.MrCorgi.models.entities.Vote;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends CrudRepository<Vote, Integer> {
    @Query("SELECT v FROM Vote v WHERE v.match.id = :matchId AND v.selectedTeam = :teamName")
    List<Vote> findVotesByMatchIdAndTeamName(@Param("matchId") Integer matchId, @Param("teamName") String teamName);

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN TRUE ELSE FALSE END FROM Vote v WHERE v.user.id = :userId AND v.match.id = :matchId")
    Boolean hasUserVotedForMatch(@Param("userId") String userId, @Param("matchId") Integer matchId);
}
