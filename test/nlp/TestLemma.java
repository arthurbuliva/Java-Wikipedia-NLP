/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import java.util.Map;

/**
 *
 * @author arthur
 */
public class TestLemma
{

    public static void main(String[] args)
    {
        String word = "Thanks for calling";

        Map<String, String> tokens = Chunker.getSpanTypes(word);
        
          Lemmatizer lemmatizer = new Lemmatizer();
          
          System.out.println(tokens);

        for (Map.Entry<String, String> entry : tokens.entrySet())
        {
            String key = entry.getKey().trim();
            String value = entry.getValue();

            System.out.println(lemmatizer.lemmatize(key, value));
        }
    }
}
