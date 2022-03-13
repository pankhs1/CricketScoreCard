package com.company.Model;

import com.company.Enums.BallType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class BowlerOver {
    private Player bowlerName;
    private Map<Integer, Over> overMap;
    private Map<BallType, Integer> extrasBowled;
    private int wicketsTaken;

    public BowlerOver(Player bowlerName) {
        this.bowlerName = bowlerName;
        overMap = new HashMap<>();
        extrasBowled = new HashMap<>();
    }
}
