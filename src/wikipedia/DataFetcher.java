/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Fetches data from a Wikipedia page
 *
 * @author arthu
 */
public class DataFetcher
{

    public final String fetchData(String locale, String title)
    {
        try
        {
            Object[] parameters =
            {
                locale, title.replaceAll(" ", "_")
            };

            String link = String.format("http://%s.wikipedia.org/wiki/%s", parameters);

            Document doc = Jsoup.connect(link).timeout(5000).get();

            Elements paragraphs = doc.select(".mw-content-ltr p, .mw-content-ltr li");

            Element firstParagraph = paragraphs.first();

            return firstParagraph.text(); //Print out just the first paragraph
        }
        catch (Exception ex)
        {
            return ex.toString();
        }

    }
}
