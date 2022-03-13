package com.company.Model;

import com.company.Enums.InningStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Innings {
    private int inningsNumber;
    private Team battingTeam;
    private Map<Integer, Over> overs;
    private Player strikePlayer;
    private Player nonStrikePlayer;
    private int playerNumber;
    private List<Player> players;
    private InningStatus inning_status;
    public Innings(int inningsNumber,Team battingTeam) {
        this.inningsNumber = inningsNumber;
        overs = new HashMap<>();
        this.battingTeam = battingTeam;
    }
}
