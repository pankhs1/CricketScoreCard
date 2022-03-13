package com.CricketScoreCard.Model;

import com.CricketScoreCard.Enums.MatchResult;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Match {
    private String matchId;
    private String venue;
    private Toss toss;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String winner;
    private String lost;
    private MatchResult matchResult;
    private Map<Integer, Innings> innings;
    private int numberOfOvers;
    private final TeamsBetween teamsBetween;

    public Match(TeamsBetween teamsBetween,int numberOfOvers) {
        innings = new HashMap<>();
        this.numberOfOvers = numberOfOvers;
        this.teamsBetween = teamsBetween;
    }
}
