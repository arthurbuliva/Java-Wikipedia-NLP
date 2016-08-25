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
        MongoTranslator mongo = new MongoTranslator();
//        System.out.println(mongo.translate("Candle"));
        System.out.println(mongo.translate("William is a thug"));
//        System.out.println(mongo.translate("cattle"));
    }

}
