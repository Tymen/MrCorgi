package dev.tymen.MrCorgi.services;

import dev.tymen.MrCorgi.models.entities.Match;
import dev.tymen.MrCorgi.repositories.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MatchService {
    @Autowired
    private MatchRepository matchRepository;

    public Match save(Match match) {
        return matchRepository.save(match);
    }

    public Optional<Match> findById(int id) {
        return matchRepository.findById(id);
    }

    public List<Match> findUpcomingAndOngoingMatches() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayLater = now.plusDays(1);
        return matchRepository.findUpcomingAndOngoingMatches(now, oneDayLater);
    }
}
