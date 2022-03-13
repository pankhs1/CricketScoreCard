package com.CricketScoreCard.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Person {
    private String name;
    private PersonalInfo personalInfo;

    public Person(String name) {
        this.name = name;
    }
}
