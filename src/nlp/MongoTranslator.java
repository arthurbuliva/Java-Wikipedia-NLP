/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

/**
 * MongoDB Quick Guide
 *
 * @author arthur
 *
 * This is how we insert a record into the db:
 *
 * db.getCollection("test").insertOne( new Document() .append("en",
 * "\"Nakumatt\" is an abbreviation for Nakuru Mattress.[1]") .append("sw",
 * "Nakumatt ni mnyororo wa maduka nchini Kenya.\n" + "Ina maduka 18 kote nchini
 * Kenya [1] na inaajiri watu 3,200.\n" + "Ni mipango ya kupanua maduka yake
 * mpaka nchini Uganda, Rwanda na nchi nyingine za Afrika Mashariki.\n" +
 * "Nakumatt ni kampuni ya Kenya inayomilikiwa na familia na Atul Shah Hotnet
 * Ltd.[2] [3]")); This is how we created an index for the text:
 *
 * db.wikipedia.createIndex({ "en" : "text", "sw" : "text" }, {
 * default_language: "english" });
 *
 * This is how we dump data from MongoDB:
 *
 * mongodump --collection wikipedia --db corpus --out __db/dump
 *
 * This is how we restore from the dump:
 *
 * mongorestore --db corpus --noIndexRestore --drop __db/dump/corpus/
 *
 * Search without index ++++++++++++++++++++ DBQuery.shellBatchSize = 300
 * db.wikipedia.find({"sw": /Adelaide wa Italia/}).pretty();
 * db.wikipedia.find().sort({_id:-1}).pretty().limit(1);
 *
 * Search with index ++++++++++++++++++++ db.wikipedia.find({$text: {$search:
 * "Msimu wa mvua"}}).pretty(); db.wikipedia.find({$text: {$search: "\"Jamhuri
 * ya Kenya\""}}).pretty();
 */
public class MongoTranslator extends  TranslatorLogger
{

    private final MongoClient mongoClient;
    private final MongoDatabase db;
    private final HashMap<String, String> relationship;
    private boolean exactMatch;
    int counter = 1;
    
    public MongoTranslator()
    {
        relationship = new HashMap<>();
        exactMatch = true;
        mongoClient = new MongoClient();
        db = mongoClient.getDatabase("corpus");

// Stop Words
//
//Stop words are the irrelevant words that should be filtered out from a text. For example: a, an, the, is, at, which, etc.
//Stemming
//
//Stemming is the process of reducing the words to their stem. For example: words like standing, stands, stood, etc. have a common base stand.
//Scoring
//
//A relative ranking to measure which of the search results is most relevant.  
    }

    public static final String[] PREPOSITIONS =
    {
        "above", "across", "after", "at",
        "around", "before", "behind", "below", "beside", "between", "by", "down",
        "during", "for", "from", "in", "inside", "onto", "of", "off", "on",
        "out", "through", "to", "under", "up", "with", "is", "as", "which",
        "it", "that", "he", "she", "was"
    };

    public static final String[] VIHUSISHI =
    {
        "juu", "across", "baada", "katika",
        "around", "kabla", "nyuma", "chini", "kando", "katikati", "by", "chini",
        "mnamo", "kwa ajili", "kutoka", "ndani", "inside", "onto", "ya", "off",
        "nje", "through", "hadi", "under", "na", "kama", "wa", "ni"
    };

    Map<String, String> test = new HashMap<String, String>()
    {
        {
            for (String PREPOSITION : PREPOSITIONS)
            {
                //put("kabla ya", "before");
            }

            put("baada ya", "after");
            put("nje ya", "outside of");
            put("ndani ya", "inside");
            put("juu ya", "on top of");
            put("chini ya", "under");
            put("baina ya", "between");
            put("kati ya", "between");
            put("mbele ya", "in front of");
            put("nyuma ya", "behind");
            put("karibu na", "near");
            put("mbali na", "far from");
            put("kando ya", "beside");
            put("mpaka", "until");
            put("kisha", "then");
            put("tangu", "from");
            put("katika", "in");
            put("miongoni mwa", "among");
            put("toka", "from");
            put("katikati ya", "in between");
            put("usoni pa", "in the face of");
            put("ukingoni mwa", "at the edge / bank of");
            put("mvunguni mwa", "under");
            put("pembeni mwa", "in the corner of");
            put("ubavuni pa", "at the side of");
            put("machoni pa", "in front of/ near");
            put("kisogoni pa", "behind, at the back");
        }
    };

    public String translate(String original)
    {
        // If it is a preposition, return its language equivalent
        if (Arrays.asList(PREPOSITIONS).contains(original)
                || Arrays.asList(PREPOSITIONS).contains(original))
        {
            return original;
        }

        // If this item is a proper noun (eg name of a place)
        // as obtained from EntityFinder, return it as is
        EntityFinder entityFinder = new EntityFinder();
        HashMap entities = entityFinder.getEntities(original);

        Iterator it = entities.entrySet().iterator();

        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry) it.next();
            ArrayList value = (ArrayList) pair.getValue();

            // TODO: This code is not correct!
            if (value.contains(original))
            {
                return original;
            }

            it.remove(); // avoids a ConcurrentModificationException
        }

        String translation = "";

        // Sometimes the original word is just a title in Wikipedia
        TitleMatcher titleMatcher = new TitleMatcher();

        if ((titleMatcher.translate(original.trim()).replaceAll("\\[", "").replaceAll("\\]", "").length() > 0))
        {
            return (titleMatcher.translate(original.trim()));
        }

        

        // db.wikipedia.find({$text: {$search: "Paper is a thin material"}}, {score: {$meta: "textScore"}}).sort({score:{$meta:"textScore"}}).pretty().limit(1);
        // TODO: Full-Text Search in MongoDB
        // http://code.tutsplus.com/tutorials/full-text-search-in-mongodb--cms-24835
        // Find the highest scoring match
        Document projection = new Document("score", new Document("$meta", "textScore"));

        FindIterable<Document> iterable = db.getCollection("wikipedia")
                .find(
                        new Document("$text", new Document("$search", exactMatch ? String.format("\"%s\"", original) : original)
                        )
                )
                .projection(projection)
                .sort(projection)
                .limit(100);

        StringBuilder englishWords = new StringBuilder();
        StringBuilder swahiliWords = new StringBuilder();

        iterable.forEach(new Block<Document>()
        {
            @Override
            public void apply(final Document document)
            {

                String english = document.getString("en");
                String swahili = document.getString("sw");

                englishWords.append(english);
                swahiliWords.append(swahili);

                relationship.put(swahili, english);
            }
        });

        ArrayList<String> sentences = new ArrayList<>(
                Arrays.asList(
                        SentenceDetector.detectSentences(englishWords.toString()
                        )
                )
        );
        ArrayList<String> sentensi = new ArrayList<>(
                Arrays.asList(
                        SentenceDetector.detectSentences(swahiliWords.toString()
                        )
                )
        );
        for (String sentence : sentences)
        {

            if (sentence.contains(original)
                    || sentensi.contains(original))
            {

                if (sentence.contains(original))
                {
                    // Get the index of this string in the array
                    int indexInArray = sentences.indexOf(sentence);

//                        System.out.println(Chunker.chunk(sentensi.get(indexInArray)));
                    return (sentensi.get(indexInArray));
                }
                else
                {
                    // Get the index of this string in the array

                    // Get the index of this string in the array
                    int indexInArray = sentensi.indexOf(sentence);

//                        System.out.println(Chunker.chunk(sentences.get(indexInArray)));
                    return (sentences.get(indexInArray));
                }

            }
        }

        Map enFreq = ChunkFrequency.getFrequencies(englishWords.toString());
        Map swFreq = ChunkFrequency.getFrequencies(swahiliWords.toString());

        if (!enFreq.isEmpty() || !swFreq.isEmpty())
        {
            for (String preposition : PREPOSITIONS)
            {
                enFreq.remove(preposition);
            }

            for (String kihusishi : VIHUSISHI)
            {
                swFreq.remove(kihusishi);
            }

            translation += enFreq + "\n\n";
            translation += "+++++++++++++++++++++++++++++++++++++++++++++++++++\n";
            translation += swFreq + "\n";

//                System.out.println(enFreq + "\n"
//                        + "+++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
//                System.out.println(swFreq);
        }
        else
        {
            System.out.println("Translating  \"" + original + "\" in chunks: ");

//            Map
//            
//            for (Map.Entry<String, String> entry : spanTypes.entrySet())
//        {
//            String key = entry.getKey();
//            Object value = entry.getValue();
//            
//            System.out.print(key);
//            System.out.print("  =>  ");
//            System.out.println(value);
//            
//        }
//
//            for (int i = 0; i < chunks.size(); i++)
//            {
//                System.out.println(chunks.get(i));
//
//                System.out.println(translateInContext(chunks.get(i),
//                        spanTypes.get(i)));
//
//            }
        }

        return translation;
    }

}
