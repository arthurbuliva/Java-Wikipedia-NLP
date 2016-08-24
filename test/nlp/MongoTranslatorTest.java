/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
        System.out.println(mongo.translate("Huu ni mwaka wa mwisho"));
//        System.out.println(mongo.translate("cattle"));
    }

}
