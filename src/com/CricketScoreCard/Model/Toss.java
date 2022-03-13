package com.CricketScoreCard.Model;

import com.CricketScoreCard.Enums.TossAction;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Toss {
    private String tossedBy;
    private String askedBy;
    private String wonByTeam;
    private TossAction tossAction;
}
