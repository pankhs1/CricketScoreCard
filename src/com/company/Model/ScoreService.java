package com.company.Model;

import com.company.Exceptions.InvalidMatch;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScoreService  {


    public static void setScore(Ball ball, String match, int innings) throws InvalidMatch {
        ScoreCard scoreCard = ScoreCard.INSTANCE(match, innings);
        scoreCard.setScoreCardForBall(ball);
    }
}
