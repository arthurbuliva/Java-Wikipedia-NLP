/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workshop;

import nlp.*;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import opennlp.tools.tokenize.SimpleTokenizer;

import org.bson.Document;

public class MongoTranslatorRC1 extends TranslatorLogger implements EnglishStopWords, SwahiliStopWords
{

    private final MongoClient mongoClient;
    private final MongoDatabase db;
    private final HashMap<String, String> relationship;
    int retries = 1;
    private ArrayList<String> translation = new ArrayList<>();

    public MongoTranslatorRC1()
    {
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
//        String english = "The United Nations Security Council (UNSC) is one of the six principal organs of the United Nations";
//        String english = "A benediction is a short invocation for divine help";
//        String english = "An avalanche is a rapid flow of snow down a sloping surface";
//        String english = "A sea is a large body of salt water";
//        String english = "Augustus was the founder of the Roman Empire and its first Emperor";
//        String english = "An arthropod is an invertebrate animal having an exoskeleton";
//        String english = "Antoine Hey (born 19 September 1970) is a German football coach and former professional player who played in the Bundesliga";
//        String english = "The Universal Declaration of Human Rights";
//        String english = "Archaeology is the study of human activity through the recovery and analysis of material culture";
//        String english = "Part of speech";
//        String english = "Virtue is moral excellence";
//        String english = "\"Don't Forget About Us\" is a song by American singer and songwriter Mariah Carey";
//        String english = "The United States dollar";
//        String english = "Sin is the act of violating God's will by transgressing his commandments";
//        String english = "The Islamic State of Iraq and the Levant";
//        String english = "Culture are habits acquired by man as a member of society";
//        String english = "A pit latrine or pit toilet is a type of toilet that collects human feces in a hole in the ground.";
        String english = "Czechoslovakia was a sovereign state in Central Europe ";
//        String english = "The teacher will not come today";

        MongoTranslatorRC1 t = new MongoTranslatorRC1();

        System.out.println(t.translate(english.toLowerCase()));

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

        String parsedKeyWords = original;

        System.out.println("Searching MongoDB: " + parsedKeyWords);

        HashMap<String, String> mongoData = fetchFromMongoDB(String.format("\"%s\"", new Object[]
        {
            parsedKeyWords
        }));

        if (mongoData.isEmpty())
        {
            ArrayList<String> whole = new BetaStringBreaker().breakString(original);

            System.out.println("Segmented: " + whole);

            for (String segment : whole)
            {

//                System.out.println(segment);
                System.out.println("Empty segment. Translating -> " + (segment));
                System.gc();
                translation.add(translate(segment));
            }
        }
        else
        {
//            SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
//                        String[] wordTokens = tokenizer.tokenize(removeStopWords(original));
//
//            for (String entry : wordTokens)
//            {
//                System.out.println(entry);
//            }

            for (Map.Entry<String, String> entry : mongoData.entrySet())
            {
//                System.out.println(entry.getKey() + " => " + entry.getValue());

                String[] englishSentences = SentenceDetector.detectSentences(entry.getKey());
                String[] swahiliSentences = SentenceDetector.detectSentences(entry.getValue());

                System.out.println(englishSentences.length);
                System.out.println(swahiliSentences.length);

                if (englishSentences[0].toLowerCase().trim().contains(original.toLowerCase().trim()))
                {
                    return swahiliSentences[0];
                }

                for (String sentence : englishSentences)
                {
                    System.out.println(sentence);
                }
                for (String sentensi : swahiliSentences)
                {
                    System.out.println(sentensi);
                }

//                System.out.println(Arrays.toString(SentenceDetector.detectSentences(entry.getKey())));
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

    private HashMap<String, String> fetchFromMongoDB(String original)
    {
        Document projection = new Document("score", new Document("$meta", "textScore"));

        HashMap<String, String> data = new HashMap<>();

        try (
                MongoCursor<Document> cursor = db.getCollection("wikipedia")
                .find(
                        new Document("$text",
                                new Document("$search", String.format("%s", original))
                                .append("$language", "en")
                                .append("$caseSensitive", false)
                        )
                )
                .projection(projection)
                .sort(projection)
                .limit(0)
                .iterator())
        {
            while (cursor.hasNext())
            {
                Document document = cursor.next();

//                System.out.println(Chunker.getSpanTypes(document.getString("en")));
                String english = document.getString("en");
                String swahili = document.getString("sw");
                String title = document.getString("title");
                String kichwa = document.getString("kichwa");

                data.put(title, kichwa);
                data.put(english, swahili);
            }
        }

        return data;
    }
}
