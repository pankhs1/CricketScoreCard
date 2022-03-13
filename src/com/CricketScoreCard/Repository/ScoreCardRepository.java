package com.CricketScoreCard.Repository;

import com.CricketScoreCard.Model.ScoreCard;

import java.util.HashMap;
import java.util.Map;

public class ScoreCardRepository {
    public static Map<String, Map<Integer, ScoreCard>> scoreCardMap = new HashMap<>();
}
