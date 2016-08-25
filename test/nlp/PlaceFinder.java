/*
 * Extracts names of places, people, organization, money etc from strings
 */
package nlp;

import java.io.File;
import java.util.Arrays;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

/**
 *
 * @author arthur
 */
public class PlaceFinder
{
    public String namefind(String sentence) throws Exception
    {
        Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
        
        TokenNameFinderModel model = new TokenNameFinderModel(
                new File("lib/apache-opennlp-1.6.0/models/en-ner-person.bin")
        );

        NameFinderME finder = new NameFinderME(model);

        String[] tokens = tokenizer.tokenize(sentence);
        Span[] nameSpans = finder.find(tokens);

        return (Arrays.toString(
                Span.spansToStrings(nameSpans, tokens)));

    }
}
