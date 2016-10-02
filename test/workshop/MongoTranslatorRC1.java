/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workshop;

import MongoDB.MongoDB;
import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.ibm.watson.developer_cloud.alchemy.v1.model.Keyword;
import nlp.*;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import opennlp.tools.tokenize.SimpleTokenizer;

public class MongoTranslatorRC1 extends TranslatorLogger implements EnglishStopWords, SwahiliStopWords
{

    private final MongoClient mongoClient;
    private final MongoDatabase db;
    private final MongoDB connection;
    private final HashMap<String, String> relationship;
    int retries = 1;
    private ArrayList<String> translation = new ArrayList<>();

    public MongoTranslatorRC1()
    {
        connection = new MongoDB();
        relationship = new HashMap<>();
        mongoClient = new MongoClient();
        db = mongoClient.getDatabase("corpus");
    }

    public static void main(String[] args)
    {
//        String english = "The red elephant had a headache at 12 o'clock";
//        String english = "The city of Nairobi";
//        String english = "They went to the zoo";
//        String english = "The red elephant had a headache at noon";
//        String english = "Cancer of the blood";
//        String english = "Visit the doctor";
//        String english = "The exams will be done next week";
//        String english = "The horn of Africa";
//        String english = "Kenya is a country";
//        String english = "Machakos is a town in Kenya";
//        String english = "That is a pretty girl";
//        String english = "Tomorrow is a Wednesday";
//        String english = "A gospel is an account describing the life, death, and resurrection of Jesus of Nazareth";
//        String english = "The Bible is a collection of sacred texts in Judaism and Christianity";

//        String english = "Paper is a thin material";
//        String english = "United Nations Security Council is an organ of The United Nations";
//        String english = "A benediction is a short prayer";
//        String english = "An avalanche is a rapid flow of snow down the slope of a hill";
        String english = "Ocean is a large body of water";
//        String english = "Jomo kenyatta was the founder of the Republic of Kenya and its first president";
//        String english = "An arthropod is an invertebrate animal having an exoskeleton";
//        String english = "Antoine Hey (born 19 September 1970) is a German football coach and former professional player who played in the Bundesliga";
//        String english = "The Universal Declaration of Human Rights";
//        String english = "Archaeology is the study of human activity through the recovery and analysis of material culture";
//        String english = "Part of speech";
//        String english = "Virtue is moral excellence";
//        String english = "\"Don't Forget About Us\" is a song by American singer and songwriter Mariah Carey";
//        String english = "The United States dollar";
//        String english = "Sin is disobeying God's will by not following commandments";
//        String english = "The Islamic State of Iraq and the Levant";
//        String english = "Culture are habits acquired by man as a member of society";
//        String english = "A toilet collects human waste";
//        String english = "Czechoslovakia was a sovereign state in Central Europe ";
//        String english = "Barack Obama went to Kenyatta National Hospital";

        MongoTranslatorRC1 t = new MongoTranslatorRC1();

        System.out.println(t.translate(english));

    }

    public String translate(String original)
    {
        log(Level.INFO, String.format("Translating \"%s\"", new Object[]
        {
            original
        }));

        // Sometimes the original word is just a title in Wikipedia
        TitleMatcher titleMatcher = new TitleMatcher();

        if ((titleMatcher.translate(original.trim()).replaceAll("\\[", "").replaceAll("\\]", "").length() > 0))
        {
            return (titleMatcher.translate(original.trim()));
        }
        else if (original.startsWith("an "))
        {
            return translate(original.replaceFirst("an", "").trim());
        }
        else if (original.startsWith("a "))
        {
            return translate(original.replaceFirst("a", "").trim());
        }
        else if (original.startsWith("of"))
        {
            translation.add("ya ");
            return translate(original.replaceFirst("of", "").trim());
        }
        else if (original.startsWith("is"))
        {
            translation.add("ni ");
            return translate(original.replaceFirst("is", "").trim());
        }
        else if (original.startsWith("the"))
        {
            return translate(original.replaceFirst("the", "").trim());
        }

        log(Level.INFO, "Searching MongoDB: " + original);

        HashMap<String, String> mongoData = connection.fetchFromMongoDB(String.format("\"%s\"", new Object[]
        {
            original
        }));

        if (mongoData.isEmpty()) // Whole phrase not in Wikipedia. We break it down
        {

            for (String segment : Chunker.chunk(original))
            {
                String subTranslation = translate(segment.trim());

                log(Level.INFO, "Empty segment. Translating -> " + (segment));

                if (subTranslation.isEmpty())
                {
                    System.out.println(removeStopWords(segment));
                    
                    String segmentTranslation = translate(removeStopWords(segment));

                    if (!segmentTranslation.contains("|"))
                    {
                        translation.add(segmentTranslation);
                    }
                    
                }
                else
                {
                    System.out.println(segment);

                    String segmentTranslation = translate(segment);

                    if (!segmentTranslation.contains("|"))
                    {
                        translation.add(segmentTranslation);
                    }
                }

                System.gc();
            }
        }
        else // Whole phrase exists in Wikipedia
        {
            for (Map.Entry<String, String> entry : mongoData.entrySet())
            {
                String[] englishSentences = SentenceDetector.detectSentences(entry.getKey());
                String[] swahiliSentences = SentenceDetector.detectSentences(entry.getValue());

                if (englishSentences[0].toLowerCase().trim().contains(original.toLowerCase().trim()))
                {
                    return swahiliSentences[0];
                }
                else
                {
                    return Arrays.toString(swahiliSentences);
                }
            }
        }

        HashMap<String, Integer> occurranceCount = new HashMap<>();

        for (String s : translation)
        {
            if (occurranceCount.keySet().contains(s))
            {
                int initialValue = occurranceCount.get(s);
                occurranceCount.put(s, initialValue + 1);
            }
            else
            {
                occurranceCount.put(s, 1);
            }
        }

//        System.out.println(ChunkFrequency.sortByValue(occurranceCount));
//        return translation.toString();
        return ChunkFrequency.sortByValue(occurranceCount).toString();
    }

    private String removeStopWords(String sentence)
    {
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] wordTokens = tokenizer.tokenize(sentence);

        StringBuilder clean = new StringBuilder();

        for (String token : wordTokens)
        {
            if (Arrays.asList(englishStopWords).contains(token.toLowerCase()))
            {
//                    System.out.println("Keyword found: "+ token);
            }
            else
            {
                clean.append(token).append(" ");
            }
        }

//            System.out.printf("%s cleaned to %s\n", sentence, clean.toString().trim());
        return clean.toString().trim();
    }
}
