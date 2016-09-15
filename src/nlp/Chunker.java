/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.Span;

/**
 *
 * @author Arthur
 */
public class Chunker extends TranslatorLogger
{

    public static Map<String, String> getSpanTypes(String sentence)
    {
        Map<String, String> spanTypes = new HashMap<>();

        try
        {
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

            Span[] spans = chunkerME.chunkAsSpans(sentenceTokens, tags);

            for (Span span : spans)
            {
                String subChunk = "";

                for (int j = span.getStart(); j < span.getEnd(); j++)
                {
                    subChunk += (sentenceTokens[j] + " ");
                }

                spanTypes.put(subChunk, span.getType());
            }
        }
        catch (Exception ex)
        {
            log(Level.SEVERE, ex.toString());
        }

        return spanTypes;

    }

    /**
     * Break a given sentence into chunks
     *
     * @param sentence
     * @return
     */
    public static ArrayList<String> chunk(String sentence)
    {
        ArrayList<String> chunks = new ArrayList<>();

        try
        {
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

//        String result[] = chunkerME.chunk(sentenceTokens, tags);
//        for (int i = 0; i < result.length; i++)
//        {
//            System.out.println("[" + sentenceTokens[i] + "] " + result[i]);
//        }
            Span[] spans = chunkerME.chunkAsSpans(sentenceTokens, tags);

            for (Span span : spans)
            {
                String subChunk = "";

//            System.out.print("Type: " + span.getType() + " - "
//                    + " Begin: " + span.getStart()
//                    + " End:" + span.getEnd()
//                    + " Length: " + span.length() + "  [");
                for (int j = span.getStart(); j < span.getEnd(); j++)
                {
//                System.out.print((sentenceTokens[j] + " "));

                    subChunk += (sentenceTokens[j] + " ");
                }

                chunks.add(subChunk);

//            System.out.println("]");
            }
        }
        catch (FileNotFoundException ex)
        {
            log(Level.SEVERE, ex.getMessage());
        }
        finally
        {
            return chunks;
        }

    }
}
