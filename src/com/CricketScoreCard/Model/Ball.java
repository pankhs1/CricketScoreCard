package com.CricketScoreCard.Model;


import com.CricketScoreCard.Enums.BallType;
import com.CricketScoreCard.Enums.RunType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Ball {
    private int overNumber;
    private BallType ballType;
    private Player bowledBy;
    private Player facedBy;
    private RunType runType;
    private Wicket wicket;
    public Ball(int overNumber, Player bowledBy, Player facedBy) {
        this.overNumber = overNumber;
        this.bowledBy = bowledBy;
        this.facedBy = facedBy;
    }

}
