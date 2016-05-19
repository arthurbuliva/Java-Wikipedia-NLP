/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opennlp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.ml.model.Sequence;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.Span;
import wikipedia.DataFetcher;

/**
 *
 * @author Arthur
 */
public class Chunker
{

    public static void main(String[] args) throws Exception
    {

        String sentence = "I am a professor who works at the United States International University - Africa.";

        // Load the model that we want to use
        InputStream posModelStream = new FileInputStream("lib/apache-opennlp-1.6.0/models/en-pos-maxent.bin");
        InputStream chunkerStream = new FileInputStream("lib/apache-opennlp-1.6.0/models/en-chunker.bin");

        // Run the model against the data
        POSModel model = new POSModel(posModelStream);
        POSTaggerME tagger = new POSTaggerME(model);

        String sentenceTokens[] = WhitespaceTokenizer.INSTANCE.tokenize(sentence);
        String[] tags = tagger.tag(sentenceTokens);

        ChunkerModel chunkerModel = new ChunkerModel(chunkerStream);
        ChunkerME chunkerME = new ChunkerME(chunkerModel);

        String result[] = chunkerME.chunk(sentenceTokens, tags);

        for (int i = 0; i < result.length; i++)
        {
            System.out.println("[" + sentenceTokens[i] + "] " + result[i]);
        }

        Span[] spans = chunkerME.chunkAsSpans(sentenceTokens, tags);
        
        for (Span span : spans)
        {
            System.out.print("Type: " + span.getType() + " - "
                    + " Begin: " + span.getStart()
                    + " End:" + span.getEnd()
                    + " Length: " + span.length() + "  [");
            for (int j = span.getStart(); j < span.getEnd(); j++)
            {
                System.out.print(sentenceTokens[j] + " ");
            }
            System.out.println("]");
        }

    }
}
