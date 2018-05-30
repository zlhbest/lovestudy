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
public class ArrayDataDemo {

    private static final Map<String, Map<String, List<String>>> DATAs = new LinkedHashMap<>();

    private static void init() {
        if (!DATAs.isEmpty()) {
            return;
        }

        for (int i = 1; i <30; i++) {
            Map<String, List<String>> city = new HashMap<>();
            for (int j = 1; j <8 ; j++) {
                List<String> data = new ArrayList<>();
                for (int k = 1; k <5; k++) {
                    data.add(""+ k);
                }
                city.put("" + j, data);
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
