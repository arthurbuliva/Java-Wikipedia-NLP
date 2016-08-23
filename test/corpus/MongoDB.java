/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package corpus;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import java.util.HashMap;
import java.util.Map;
import nlp.ChunkFrequency;

import org.bson.Document;

/**
 *
 * @author arthur
 */
public class MongoDB
{

    public static final String[] PREPOSITIONS =
    {
        "above", "across", "after", "at",
        "around", "before", "behind", "below", "beside", "between", "by", "down",
        "during", "for", "from", "in", "inside", "onto", "of", "off", "on",
        "out", "through", "to", "under", "up", "with", "is", "as", "which",
        "it", "that"
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

    public static void main(String[] args) throws Exception
    {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase db = mongoClient.getDatabase("corpus");
        HashMap<String, String> relationship = new HashMap<>();

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
        // This is how we restore from the dump:
        //
        // mongorestore --db corpus --noIndexRestore --drop __db/dump/corpus/
        //
        // db.wikipedia.find({$text: {$search: "Msimu wa mvua"}}).pretty();
        //
        // db.wikipedia.find({$text: {$search: "\"Jamhuri ya Kenya\""}}).pretty();
        //
        FindIterable<Document> iterable = db.getCollection("wikipedia").find(
                new Document("$text", new Document("$search", "\"Kamusi Project\""))
        );

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

        mongoClient.close();

        try
        {
            Map enFreq = ChunkFrequency.getFrequencies(englishWords.toString());
            Map swFreq = ChunkFrequency.getFrequencies(swahiliWords.toString());

            for (String preposition : PREPOSITIONS)
            {
                enFreq.remove(preposition);
            }

            for (String kihusishi : VIHUSISHI)
            {
                swFreq.remove(kihusishi);
            }

            System.out.println(enFreq);
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println(swFreq);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
