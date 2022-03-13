package com.company.Model;

import com.company.Enums.WicketType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlayerScore {
    private Player name;
    private List<Integer> runsScored;
    private WicketType wicketType;
    private Player bowler;
    private boolean isOut;
    private int totalBoundaries;
    private int totalSixes;
    private int  bowlsPlayed;
    private int runs;
    private double strikeRate;
    public PlayerScore(Player name) {
        this.name = name;
        runsScored = new ArrayList<>();
    }
}
