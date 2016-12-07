/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workshop;

import nlp.TranslatorRC2;

/**
 *
 * @author arthur
 */
public class KamusiTranslator
{
    public static void main(String[] args)
    {
        TranslatorRC2 translator = new TranslatorRC2();
        String translation = translator.kamusiTranslate("elephant");
        
        System.out.println(translation);
    }
}
