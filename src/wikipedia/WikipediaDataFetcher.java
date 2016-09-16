/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.logging.Level;
import nlp.TranslatorLogger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Fetches data from a Wikipedia page
 *
 * @author arthu
 */
public class WikipediaDataFetcher extends TranslatorLogger
{

    public final String fetchData(String locale, String title)
    {
        String data = "";

        try
        {
            Object[] parameters =
            {
                locale, URLEncoder.encode(title, "UTF-8")
            };

            String link = String.format("https://%s.wikipedia.org/w/api.php?format=json"
                    + "&action=query&prop=extracts&exlimit=max&explaintext&exintro"
                    + "&titles=%s", parameters);

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

                    String articleTitle = (String) (article.get("title"));
                    String articleBody = (String) (article.get("extract"));
                    data = articleBody;
                }
            }

        }
        catch (URISyntaxException | IOException | JSONException ex)
        {
            data = "";
            log(Level.SEVERE, ex.getMessage());
        }

        return data;

    }
}
