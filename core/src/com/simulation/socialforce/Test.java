package com.simulation.socialforce;

import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Test {

    public static void main(String[] args) {
        Map<String, Integer> mMap = new HashMap<String, Integer>();

        // 1. Mapにデータを格納
        mMap.put( "apple", 1);
        mMap.put( "orange", 2);
        mMap.put( "pineapple", 4);
        mMap.put( "strawberry", 5);
        mMap.put( "melon", 3);

        // 2.Map.Entryのリストを作成する
        List<Entry<String, Integer>> list_entries = new ArrayList<Entry<String, Integer>>(mMap.entrySet());

        // 3.比較関数Comparatorを使用してMap.Entryの値を比較する(昇順)
        Collections.sort(list_entries, new Comparator<Entry<String, Integer>>() {
            public int compare(Entry<String, Integer> obj1, Entry<String, Integer> obj2) {
                // 4. 昇順
                return obj1.getValue().compareTo(obj2.getValue());
            }
        });

        System.out.println("昇順でのソート");
        // 5. ループで要素順に値を取得する
        for(Entry<String, Integer> entry : list_entries) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        // 6. 比較関数Comparatorを使用してMap.Entryの値を比較する（降順）
        Collections.sort(list_entries, new Comparator<Entry<String, Integer>>() {
            //compareを使用して値を比較する
            public int compare(Entry<String, Integer> obj1, Entry<String, Integer> obj2)
            {
                //降順
                return obj2.getValue().compareTo(obj1.getValue());
            }
        });

        System.out.println("降順でのソート");
        // 7. ループで要素順に値を取得する
        for(Entry<String, Integer> entry : list_entries) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

}