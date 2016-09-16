/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package corpus;

import nlp.Chunker;
import nlp.POSDetector;

/**
 *
 * @author Arthur
 */
public class TestSentenceTokenizer
{

    public static void main(String[] args) throws Exception
    {
       String sentence = ("An input sample sentence from Mr. Otieno, preferably with commas, isn't it?");

        System.out.println(Chunker.chunk(sentence));
//        System.out.println(Chunker.getSpanTypesFromChunks(sentence));
        System.out.println(POSDetector.detectPOS(sentence));
    }
}
