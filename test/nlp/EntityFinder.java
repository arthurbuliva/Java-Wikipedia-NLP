/*
 * Extracts names of people from strings
 */
package nlp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

/**
 *
 * @author arthur
 */
public class EntityFinder
{

    public HashMap<String, ArrayList> getEntities(String sentence) throws Exception
    {
        try
        {
            InputStream tokenStream = new FileInputStream(
                    new File("lib/apache-opennlp-1.6.0/models/en-token.bin"));
            TokenizerModel tokenModel = new TokenizerModel(tokenStream);
            Tokenizer tokenizer = new TokenizerME(tokenModel);

            String modelNames[] =
            {
                "lib/apache-opennlp-1.6.0/models/en-ner-date.bin",
                "lib/apache-opennlp-1.6.0/models/en-ner-location.bin",
                "lib/apache-opennlp-1.6.0/models/en-ner-money.bin",
                "lib/apache-opennlp-1.6.0/models/en-ner-organization.bin",
                "lib/apache-opennlp-1.6.0/models/en-ner-percentage.bin",
                "lib/apache-opennlp-1.6.0/models/en-ner-person.bin",
                "lib/apache-opennlp-1.6.0/models/en-ner-time.bin",
            };

            HashMap<String, ArrayList> entityMap = new HashMap<>();

            for (String name : modelNames)
            {
                TokenNameFinderModel entityModel = new TokenNameFinderModel(
                        new FileInputStream(name));

                NameFinderME nameFinder = new NameFinderME(entityModel);

                ArrayList elements = new ArrayList();

                String tokens[] = tokenizer.tokenize(sentence);

//                System.out.println(Arrays.toString(tokens));
                Span nameSpans[] = nameFinder.find(tokens);

//                When the en-ner-money.bin model is used, the index in the
//                tokens array in the earlier code sequence has to be increased by
//                one. Otherwise, all that is returned is the dollar sign.
                for (Span span : nameSpans)
                {
                    if (entityMap.containsKey(span.getType()))
                    {
                        elements = entityMap.get(span.getType());

                        elements.add(((name.contains("money.bin"))
                                ? (tokens[span.getStart()] + tokens[span.getStart() + 1])
                                : tokens[span.getStart()]));
                        
                        entityMap.put(span.getType(), elements);
                    }
                    else
                    {
                        elements.add(((name.contains("money.bin"))
                                ? (tokens[span.getStart()] + tokens[span.getStart() + 1])
                                : tokens[span.getStart()]));
                        entityMap.put(span.getType(), elements);
                    }
                }
            }

            return entityMap;
        }
        catch (Exception ex)
        {
// Handle exceptions
            ex.printStackTrace();
            return null;
        }
    }
}
