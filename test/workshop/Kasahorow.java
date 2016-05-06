/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workshop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author arthur
 */
public class Kasahorow
{

    public static void main(String[] args) throws Exception
    {
        File file = new File("lib/kasahorow/english_swahili_woaka.tsv.txt");
        
        ArrayList<String> sentences = new ArrayList<>();

        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line;
        
        while ((line = bufferedReader.readLine()) != null)
        {
            if(line.contains(" dog "))
            {
                sentences.add(line.split("\t\t\t")[1]);
            }
        }

        fileReader.close();
        
        
        for(String sentence : sentences)
        {
            System.out.println(sentence);
        }
    }
}
