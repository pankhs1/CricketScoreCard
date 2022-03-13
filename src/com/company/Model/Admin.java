package com.company.Model;

import com.company.Exceptions.InvalidMatch;
import com.company.Repository.*;

public class Admin extends Person {
    public Admin(String name) {
        super(name);
    }



    public void addMatch(Match match)  {

        MatchRepository.matchMap.putIfAbsent(match.getMatchId(), match);
    }

    public void addTeam(Team team)  {
        TeamRepository.teamMap.putIfAbsent(team.getName(), team);
    }

    public void addPlayer(Player player, String team, String tour)  {

        PlayerRepository.playerMap.putIfAbsent(player.getName(), player);

    }
}
