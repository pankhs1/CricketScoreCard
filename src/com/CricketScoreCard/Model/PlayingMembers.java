package com.CricketScoreCard.Model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PlayingMembers {
    private String team;
    private List<Player> players;
}
