/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import java.util.ArrayList;
import java.util.Arrays;
import opennlp.tools.tokenize.SimpleTokenizer;

/**
 *
 * @author arthur
 */
public class StringBreaker
{

    public ArrayList<String> breakString(String text)
    {
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] wordTokens = tokenizer.tokenize(text);

        ArrayList<String> temp = new ArrayList<>();
        temp.addAll(Arrays.asList(wordTokens));

        ArrayList finalArray = new ArrayList();
        finalArray.add(text);

        for (int i = 0; i < wordTokens.length; i++)
        {
            String wordToken = wordTokens[i];

            temp.remove(0);

            StringBuilder dataBuilder = new StringBuilder();

            for (String data : temp)
            {
                dataBuilder.append(data)
                        .append(" ");
            }

            finalArray.add(dataBuilder.toString().trim());
        }
        finalArray.removeAll(Arrays.asList(null,""));
        return (finalArray);

    }
}
