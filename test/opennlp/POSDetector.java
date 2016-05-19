/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opennlp;

import java.io.File;
import java.io.IOException;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import wikipedia.DataFetcher;

/**
 *
 * @author Arthur
 */
public class POSDetector
{

    public static void main(String[] args) throws Exception
    {
        // Load the model that we want to use
//        InputStream modelIn = new FileInputStream("lib/apache-opennlp-1.6.0/models/en-spos-maxent.bin");
        File modelFile = new File("lib/apache-opennlp-1.6.0/models/en-pos-maxent.bin");
        POSModel model = new POSModelLoader().load(modelFile);

        POSTaggerME tagger = new POSTaggerME(model);

        // Fetch the data from Wikipedia
        DataFetcher wiki = new DataFetcher();
        String data = "What HAVE you been up to lately?";//wiki.fetchData("sw", "Karatasi");

        // Run the model against the data
        String tokens[] = WhitespaceTokenizer.INSTANCE.tokenize(data);
        String[] tags = tagger.tag(tokens);

        for (int i = 0; i < tokens.length; i++)
        {
            System.out.println(String.format("%s [%s]",
                    new Object[]
                    {
                        tokens[i], tags[i]
                    }));

        }

    }
}
