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
        String sentence = "State Leaders";
        
        MongoTranslator mongo = new MongoTranslator();
//        System.out.println(mongo.translate("Candle"));
        System.out.println(mongo.translate(sentence));
//        System.out.println(mongo.translate("cattle"));
    }

}
