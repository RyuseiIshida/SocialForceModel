package com.simulation.socialforce;

import com.badlogic.gdx.math.MathUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        final int a = 1;
        int b = 2;
        int c = 3;
        ArrayList<Integer> i = new ArrayList<>(Arrays.asList(a,b,c));
        for (Integer integer : i) {
            integer++;
            System.out.println(integer);
        }
    }
}