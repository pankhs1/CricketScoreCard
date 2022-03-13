package com.company.Model;

public class TeamsBetween {
    Team team1;
    Team team2;
    PlayingMembers t1PlayingMembers;
    PlayingMembers t2PlayingMembers;

    public TeamsBetween(Team team1, Team team2,PlayingMembers t1PlayingMembers, PlayingMembers t2PlayingMembers) {
        this.team1 = team1;
        this.team2 = team2;
        this.t1PlayingMembers = t1PlayingMembers;
        this.t2PlayingMembers = t2PlayingMembers;
    }
}
