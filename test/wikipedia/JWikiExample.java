/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia;

import java.net.URI;
import java.util.Iterator;
import org.json.JSONArray;

import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author arthur
 */
//Edit a Wikipedia page by replacing its text with text of your choosing.
public class JWikiExample
{

    public static void main(String[] args) throws Throwable
    {
        String link = "https://sw.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exlimit=max&explaintext&exintro&titles=1403";
        URI uri = new URI(link);
        JSONTokener tokener = new JSONTokener(uri.toURL().openStream());

        JSONObject root = new JSONObject(tokener);
        JSONObject query = (JSONObject) (root.get("query"));
        JSONObject pages = (JSONObject) (query.get("pages"));

        Iterator<?> keys = pages.keys();

        while (keys.hasNext())
        {
            String key = (String) keys.next();
            
            if (pages.get(key) instanceof JSONObject)
            {
                JSONObject article = (JSONObject) (pages.get(key));
                
                System.out.println(article.get("title"));
                System.out.println(article.get("extract"));
            }
        }

    }
}
