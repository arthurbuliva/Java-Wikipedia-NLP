/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author arthur
 */
public class TitleMatcher
{

    private final String DATABASE = "lib/wikipedia-parallel-titles-master/Titles.txt";
    private String translation = "";
    ArrayList<String> translations = new ArrayList<>();

    Map<String, String> corpus = new HashMap<>();

    public String translate(String word)
    {
        try
        {
            File file = new File(DATABASE);

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;

            while ((line = bufferedReader.readLine()) != null)
            {
                if (line.trim().toLowerCase().contains(word.trim()))
                {
                    translations.add(line);
                }
            }

            translation = translations.toString();

            for (int i = 0; i < translations.size(); i++)
            {
//                System.out.println(translations.get(i));

                String rawString = translations.get(i);
                String[] components = rawString.split("\\|\\|\\|");

                corpus.put(components[0].trim(), components[1].trim());
            }

            for (Map.Entry<String, String> entry : corpus.entrySet())
            {
                String key = entry.getKey();
                String value = entry.getValue();

                if(key.equalsIgnoreCase(word))
                {
                    translation = value;
                }
                else if(value.equalsIgnoreCase(word))
                {
                    translation = key;
                }
            }
            
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            translation = ex.getMessage();
        }

        return translation;

    }

    public static void main(String[] args)
    {
        String word = "paper";
        
        TitleMatcher translator = new TitleMatcher();
        System.out.println(translator.translate(word));
    }
}
