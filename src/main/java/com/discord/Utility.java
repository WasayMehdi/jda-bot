package com.discord;

public class Utility {

    public static int random(int min, int max) {

        max = max  + 1;

        return min + (int)Math.floor((max - min) * Math.random());

    }

}
