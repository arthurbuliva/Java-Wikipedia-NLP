/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kasahorow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 *
 * @author arthur
 */
public class KasahorowTranslator
{

    public static void main(String[] args) throws Exception
    {
        File file = new File("lib/kasahorow/english_swahili_woaka.tsv.txt");

        ArrayList<String> sentences = new ArrayList<>();

        ArrayList<String> translationSentences = new ArrayList<>();

        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line;

        while ((line = bufferedReader.readLine()) != null)
        {
            if (line.contains(" dog "))
            {
                sentences.add(line.split("\t\t\t")[1]);
            }
        }

        fileReader.close();

        sentences.stream().forEach((sentence) ->
        {
            //            System.out.println(sentence);

            translationSentences.add(sentence.split("\t")[0].trim());
        });

//        System.out.println(translationSentences);
        Map<String, Integer> freqs = new HashMap<>();
        List<String> haystack = translationSentences;

        // Load the model that we want to use
        InputStream modelIn = new FileInputStream("lib/apache-opennlp-1.6.0/models/en-token.bin");
        TokenizerModel model = new TokenizerModel(modelIn);
        Tokenizer tokenizer = new TokenizerME(model);

        for (String sentence : haystack)
        {
            String[] tokens = tokenizer.tokenize(sentence);

            for (String token : tokens)
            {
                if (freqs.containsKey(token))
                {
                    freqs.put(token, freqs.get(token) + 1);
                }
                else
                {
                    freqs.put(token, 1);
                }
            }
        }
// freqs now has all your word frequencies

        System.out.println(freqs);

    }
}
