/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workshop;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nlp.SentenceDetector;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 *
 * @author arthur
 */
public class WordFrequency
{

    // Determines the frequency of words in a sentence
    public static void main(String[] args) throws Exception
    {
        String paragraph = "In the case of malaria, an infection of the erythrocytes "
                + "(red blood cells), the genetic change is an alteration of the "
                + "hemoglobin molecule or cellular proteins or enzymes of erythrocytes "
                + "that inhibits invasion by or replication of Plasmodia, the "
                + "microorganisms that cause the disease or replication. Red blood cells are.";

        Map<String, Integer> freqs = new HashMap<>();
        String[] haystack = SentenceDetector.detectSentences(paragraph);

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
        
        System.out.println(freqs);

    }
}
