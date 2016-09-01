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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bson.Document;

/**
 *
 * @author arthur
 */
public class MongoTranslator
{

    private final MongoClient mongoClient;
    private final MongoDatabase db;
    private final HashMap<String, String> relationship;
    private boolean exactMatch;
    int counter = 1;

    public MongoTranslator()
    {
        mongoClient = new MongoClient();
        db = mongoClient.getDatabase("corpus");
        relationship = new HashMap<>();
        exactMatch = true;
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
            for (int i = 0; i < PREPOSITIONS.length; i++)
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

        if (Arrays.asList(PREPOSITIONS).contains(original)
                || Arrays.asList(PREPOSITIONS).contains(original))
        {
            return original;
        }

        // If this item is a proper noun, obtained from EntityFinder, return it as is
        EntityFinder entityFinder = new EntityFinder();
        HashMap entities = entityFinder.getEntities(original);

        Iterator it = entities.entrySet().iterator();
        
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry) it.next();
            
//            System.out.println(pair.getKey() + " = " + pair.getValue());
            
            ArrayList value = (ArrayList) pair.getValue();
            
            System.out.println(value);
            
            if(value.contains(original))
            {
                
                return original;
            }
            
            it.remove(); // avoids a ConcurrentModificationException
        }

        String translation = "";

        TitleMatcher translator = new TitleMatcher();

        if ((translator.translate(original.trim()).replaceAll("\\[", "").replaceAll("\\]", "").length() > 0))
        {
            //TODO: There is a bug here
//            return (translator.translate(original.trim()));
            System.out.println(translator.translate(original.trim()));
        }

        // This is how we insert a record into the db:
        //db.getCollection("test").insertOne(
        //        new Document()
        //        .append("en", "\"Nakumatt\" is an abbreviation for Nakuru Mattress.[1]")
        //        .append("sw", "Nakumatt ni mnyororo wa maduka nchini Kenya.\n"
        //                + "Ina maduka 18 kote nchini Kenya [1] na inaajiri watu 3,200.\n"
        //                + "Ni mipango ya kupanua maduka yake mpaka nchini Uganda, Rwanda na nchi nyingine za Afrika Mashariki.\n"
        //                + "Nakumatt ni kampuni ya Kenya inayomilikiwa na familia na Atul Shah Hotnet Ltd.[2] [3]"));
        // This is how we created an index for the text:
        //
        // db.wikipedia.createIndex({ "en" : "text", "sw" : "text" })
        //
        // This is how we dump data from MongoDB:
        //
        // mongodump --collection wikipedia --db corpus --out __db/dump
        //
        // This is how we restore from the dump:
        //
        // mongorestore --db corpus --noIndexRestore --drop __db/dump/corpus/
        //
        // Search without index
        // ++++++++++++++++++++
        // DBQuery.shellBatchSize = 300
        // db.wikipedia.find({"sw":  /Adelaide wa Italia/}).pretty();
        //
        // Search with index
        // ++++++++++++++++++++
        // db.wikipedia.find({$text: {$search: "Msimu wa mvua"}}).pretty();
        // db.wikipedia.find({$text: {$search: "\"Jamhuri ya Kenya\""}}).pretty();
        
        FindIterable<Document> iterable = db.getCollection("wikipedia")
                .find(
                        new Document("$text", new Document("$search", exactMatch ? String.format("\"%s\"", original) : original)
                        )
                ).limit(10);

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

        try
        {
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

                ArrayList chunks = Chunker.chunk(original);
                
                

                for (Object chunk : chunks)
                {
                    System.out.println(chunk);
                    exactMatch = false;
                    System.out.println(translate(((String) chunk).trim()));
                    exactMatch = true;
                    System.out.println("_______________________________________________");
                }

            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            // TODO: Close the connections
//            mongoClient.close();
        }

        return translation;
    }

}
