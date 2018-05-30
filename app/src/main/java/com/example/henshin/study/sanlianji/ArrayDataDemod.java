package com.example.henshin.study.sanlianji;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @version 0.1 king 2015-11
 */
public class ArrayDataDemod {

    private static final Map<String, Map<String, List<String>>> DATAs = new LinkedHashMap<>();

    private static void init() {
        if (!DATAs.isEmpty()) {
            return;
        }

        for (int i = 17; i <27; i++) {
            Map<String, List<String>> city = new HashMap<>();
            for (int j = 1; j <13; j++) {
                List<String> data = new ArrayList<>();
                for (int k = 1; k < 30; k++) {
                   if(Integer.toString(k).length()==1) {
                       data.add("0"+k);
                   }else {
                       data.add(Integer.toString(k));
                   }

                }
                if(Integer.toString(j).length()==1) {
                    city.put("0"+j,data);
                }else {
                    city.put(""+j,data);
                }
            }
            DATAs.put(""+i, city);
        }
    }

    private static Random random = new Random();


    public static Map<String, Map<String, List<String>>> getAll() {
        init();
        return new HashMap<>(DATAs);
    }

}
