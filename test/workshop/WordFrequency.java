/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workshop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import nlp.Chunker;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

/**
 *
 * @author arthur
 */
public class WordFrequency
{

    // Determines the frequency of words in a sentence
    public static void main(String[] args) throws Exception
    {
        String paragraph = "The Republic of Malawi, is a landlocked country in "
                + "southeast Africa that was formerly known as Nyasaland. "
                + "It is bordered by Zambia to the northwest, Tanzania to the "
                + "northeast, and Mozambique on the east, south and west. "
                + "The country is also nicknamed \"The Warm Heart of Africa\".";

        ArrayList<String> chunks = Chunker.chunk(paragraph);

        Map<String, Integer> freqs = new CaseInsensitiveMap<>();

        for (String sentence : chunks)
        {

            if (freqs.containsKey(sentence.trim()))
            {
                freqs.put(sentence.trim(), freqs.get(sentence.trim()) + 1);
            }
            else
            {
                freqs.put(sentence.trim(), 1);
            }

        }

//        for (Map.Entry<String, Integer> entry : freqs.entrySet())
//        {
//            String key = entry.getKey();
//            Object value = entry.getValue();
//
//            System.out.println(String.format("%s -> %d", new Object[]
//            {
//                key, value
//            }));
//        }

        Map<String, Integer> sortedMap = sortByValue(freqs);

        for (Map.Entry<String, Integer> entry : sortedMap.entrySet())
        {
            String key = entry.getKey();
            Object value = entry.getValue();

            System.out.println(String.format("%s -> %d", new Object[]
            {
                key, value
            }));
        }
    }

    private static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap)
    {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, Integer>> list
                = new LinkedList<>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>()
        {
            @Override
            public int compare(Map.Entry<String, Integer> o1,
                    Map.Entry<String, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

}
