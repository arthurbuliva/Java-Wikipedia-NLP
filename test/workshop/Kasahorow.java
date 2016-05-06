/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workshop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 *
 * @author arthur
 */
public class Kasahorow
{

    public static void main(String[] args) throws Exception
    {
        File file = new File("lib/kasahorow/english_swahili_woaka.tsv.txt");

        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuffer stringBuffer = new StringBuffer();

        String line;
        while ((line = bufferedReader.readLine()) != null)
        {
            if(line.contains(" cat "))
            {
                System.out.println(line);
            }
        }

        fileReader.close();
    }
}
