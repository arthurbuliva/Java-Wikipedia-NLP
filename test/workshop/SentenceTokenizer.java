/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workshop;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 *
 * @author Arthur
 */
public class SentenceTokenizer
{

    public static void main(String[] args) throws Exception
    {
        InputStream modelIn = new FileInputStream("lib/apache-opennlp-1.6.0/models/en-token.bin");

        TokenizerModel model = new TokenizerModel(modelIn);
        Tokenizer tokenizer = new TokenizerME(model);

        String tokens[] = tokenizer.tokenize("An input sample sentence from Mr. Otieno, preferably with commas, isn't it?");

        modelIn.close();

        System.out.println(Arrays.toString(tokens));

    }
}
