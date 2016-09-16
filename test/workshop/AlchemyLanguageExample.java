/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workshop;

/**
 *
 * @author arthur
 */
import java.util.HashMap;
import java.util.Map;

import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.ibm.watson.developer_cloud.alchemy.v1.model.DocumentSentiment;
import com.ibm.watson.developer_cloud.alchemy.v1.model.Entities;
import com.ibm.watson.developer_cloud.alchemy.v1.model.TypedRelations;

public class AlchemyLanguageExample
{

    public static void main(String[] args)
    {
        AlchemyLanguage service = new AlchemyLanguage();
        service.setApiKey("e24397903a386ad615e7922ed5907557e76bb336");

        Map<String, Object> params = new HashMap<>();
        params.put(AlchemyLanguage.TEXT,
                "IBM Watson won the Jeopardy television show hosted by Alex Trebek");

        // get sentiment
        DocumentSentiment sentiment = service.getSentiment(params).execute();
        System.out.println("Sentiment: " + sentiment);

        // get entities
        Entities entities = service.getEntities(params).execute();
        System.out.println("Entities: " + entities);
        
        System.out.println(service.getKeywords(params).execute());

        // get typed relations
        TypedRelations relations = service.getTypedRelations(params).execute();
        System.out.println("Relations: " + relations);

    }

}
