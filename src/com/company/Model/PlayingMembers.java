package com.company.Model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Setter
@Getter
public class PlayingMembers {
    private String team;
    private List<Player> players;
}
