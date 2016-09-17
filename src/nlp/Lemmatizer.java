/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import nlp.TranslatorLogger;
import opennlp.tools.lemmatizer.SimpleLemmatizer;

/**
 *
 * @author arthur
 */
public class Lemmatizer extends TranslatorLogger
{

    private static SimpleLemmatizer lemmatizer;

    public String lemmatize(String word, String postag)
    {
        String lemma = "";
        try
        {
            if (lemmatizer == null)
            {
                InputStream posModelStream = new FileInputStream("lib/apache-opennlp-1.6.0/models/en-lemmatizer.dict");
                lemmatizer = new SimpleLemmatizer(posModelStream);
                posModelStream.close();
            }
            lemma = lemmatizer.lemmatize(word, postag);
        }
        catch (Exception ex)
        {
            log(Level.SEVERE, ex.getMessage());
        }
        return lemma;
    }

}
