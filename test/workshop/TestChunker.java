/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workshop;

import java.io.IOException;
import java.util.ArrayList;
import opennlp.Chunker;

/**
 *
 * @author arthur
 */
public class TestChunker
{
    public static void main(String[] args) throws IOException
    {
        String sentence = "I am a professor who works at the United States International University - Africa.";
        sentence += " My name is Rogers and I am an old man";
        
        ArrayList<String> chunks = Chunker.chunk(sentence);
        ArrayList<String> spanTypes = Chunker.getSpanTypesFromChunks(sentence);
        
//        System.out.println(Chunker.chunk(sentence));
//        System.out.println(Chunker.getSpanTypesFromChunks(sentence));
        
        int length = chunks.size();
        
        for (int i = 0; i < length; i++)
        {
            System.out.print(chunks.get(i));
            System.out.print("  =>  ");
            System.out.println(spanTypes.get(i));
            
        }
    }
}
