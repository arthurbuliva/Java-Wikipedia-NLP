/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
public class WikipediaParseTitles
{

    private String wikiTextEnglish;
    private String wikiTextSwahili;
    private String[] wikiSentences;
    private DataFetcher wikiFetcher;
    private SentenceDetector sentenceDetector;
    
    public void fetchWikis()  throws Exception
    {
        wikiFetcher = new DataFetcher();
        sentenceDetector = new SentenceDetector();
        
        File file = new File("lib/wikipedia-parallel-titles-master/Titles.txt");

        Map<String, String> titlesMap = new HashMap<>();

        FileReader fileReader = new FileReader(file);

        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line;

        while ((line = bufferedReader.readLine()) != null)
        {
            String[] titlesArray = line.split("\\|\\|\\|");

            titlesMap.put(titlesArray[1].trim(), titlesArray[0].trim());
        }

        fileReader.close();

        titlesMap.forEach((englishTitle, swahiliTitle) -> 
                {
                    System.out.println(englishTitle + " -> " + swahiliTitle);
                    
                    wikiTextEnglish = wikiFetcher.fetchData("en", englishTitle);
                    wikiTextSwahili = wikiFetcher.fetchData("sw", swahiliTitle);
                    
                    for(String sentence : sentenceDetector.detectSentences(wikiTextEnglish))
                    {
                        System.out.println(sentence);
                    }
                    for(String sentence : sentenceDetector.detectSentences(wikiTextSwahili))
                    {
                        System.out.println(sentence);
                    }
                    
                    System.out.println("========================================");
                    
        });
    }
    
    public static void main(String[] args) throws Exception
    {
        WikipediaParseTitles wpt = new WikipediaParseTitles();
        wpt.fetchWikis();

    }
}
