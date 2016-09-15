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
//        String sentence = "Sewing machine is light weight";
//        String sentence = "I am pretty sure this is a Glenfiddich.";
//        String sentence = "Kwa hakika haswa hii si nishati endelezi";
//        String sentence = "Chicago Heights";
        String sentence = "A piece of paper is light in weight";
//        String sentence = "Tsavo East National Park";
//        String sentence = "He works for the US government";

        MongoTranslator mongo = new MongoTranslator();
//        System.out.println(mongo.translate("Candle"));
        System.out.println(mongo.translate(sentence));
//        System.out.println(mongo.translate("cattle"));
    }

}
