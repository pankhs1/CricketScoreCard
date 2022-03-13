package com.company.Model;

import com.company.Enums.PlayerType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player extends Person {
    public Player(String name) {
        super(name);
    }

    private PlayerType playerType;
}
