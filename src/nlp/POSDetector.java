/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import java.io.File;
import java.util.ArrayList;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;

/**
 *
 * @author Arthur
 */
public class POSDetector
{
    public static ArrayList<String> detectPOS(String sentence)
    {
        ArrayList<String> pos = new ArrayList<>();

        // Load the model that we want to use
        File modelFile = new File("lib/apache-opennlp-1.6.0/models/en-pos-maxent.bin");
        POSModel model = new POSModelLoader().load(modelFile);

        POSTaggerME tagger = new POSTaggerME(model);

        // Run the model against the data
        String tokens[] = WhitespaceTokenizer.INSTANCE.tokenize(sentence);
        String[] tags = tagger.tag(tokens);

        for (int i = 0; i < tokens.length; i++)
        {
            System.out.println(String.format("%s [%s]",
                    new Object[]
                    {
                        tokens[i], tags[i]
                    }));

            pos.add(tags[i]);

        }

        return pos;

    }
}
