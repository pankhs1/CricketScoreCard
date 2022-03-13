package com.CricketScoreCard.Model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Team {
    private final String name;
    private List<Player> players;
    public Team(String name) {
        this.name = name;
    }
}
