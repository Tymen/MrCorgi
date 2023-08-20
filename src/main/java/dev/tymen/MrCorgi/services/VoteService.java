package dev.tymen.MrCorgi.services;

import dev.tymen.MrCorgi.models.entities.Match;
import dev.tymen.MrCorgi.models.entities.User;
import dev.tymen.MrCorgi.models.entities.Vote;
import dev.tymen.MrCorgi.repositories.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoteService {
    private MatchService matchService;
    private UserService userService;
    private VoteRepository voteRepository;

    @Autowired
    public VoteService(VoteRepository voteRepository, MatchService matchService, UserService userService) {
        this.voteRepository = voteRepository;
        this.matchService = matchService;
        this.userService = userService;
    }

    public Vote castVote(int matchId, String userId, String selectedTeam) {
        Optional<User> getUser = userService.findById(userId);
        Optional<Match> getMatch = matchService.findById(matchId);
        Vote vote = null;

        if (getUser.isPresent() && getMatch.isPresent()) {
            vote = voteRepository.save(new Vote(getUser.get(), getMatch.get(), selectedTeam));
        }

        return vote;
    }

    public List<Vote> getVotersByMatchAndTeam(int matchId, String teamName) {
        return voteRepository.findVotesByMatchIdAndTeamName(matchId, teamName);
    }

    public Boolean didUserVoteForMatch(String userId, int matchId) {
        return voteRepository.hasUserVotedForMatch(userId, matchId);
    }
}
