/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package corpus;

import java.io.IOException;
import java.util.Map;
import nlp.Chunker;

/**
 *
 * @author arthur
 */
public class TestChunker
{
    public static void main(String[] args) throws IOException
    {
        String sentence = "My name is Rogers and I am an old man. My light is shining. My load is light.";
        
        Map<String, String> spanTypes = Chunker.getSpanTypes(sentence);
        
//        System.out.println(Chunker.chunk(sentence));
//        System.out.println(Chunker.getSpanTypesFromChunks(sentence));
        
        int length = spanTypes.size();
        
        for (Map.Entry<String, String> entry : spanTypes.entrySet())
        {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            System.out.print(key);
            System.out.print("  =>  ");
            System.out.println(value);
            
        }
    }
}
