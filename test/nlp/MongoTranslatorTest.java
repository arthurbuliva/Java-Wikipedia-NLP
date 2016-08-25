/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

/**
 *
 * @author arthur
 */
public class MongoTranslatorTest
{

    public static void main(String[] args) throws Exception
    {
        String sentence = "My name is Arthur Buliva. William is a thug. He was the last person to see Fred. "
                + "He saw him in Boston at McKenzie's pub at 3:00 where he paid $2.45 for an ale, "
                + "after which they proceeded to the UN building.";
        
        MongoTranslator mongo = new MongoTranslator();
//        System.out.println(mongo.translate("Candle"));
        System.out.println(mongo.translate(sentence));
//        System.out.println(mongo.translate("cattle"));
    }

}
