package com.simulation.socialforce;

import com.badlogic.gdx.math.MathUtils;

import java.util.*;
import java.util.Map.Entry;


public class Test {

    public static void main(String[] args) {
        List<Integer> list = new LinkedList<>(Arrays.asList(0, 3, 1, 4, 1, 5, 9, 2, 6));
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator);
            int num = iterator.next();
            System.out.println(num);
        }
    }
}