/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Arthur
 */
public class NLP
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException
    {
        Document doc = Jsoup.connect("http://en.wikipedia.org/wiki/Central_Bank_of_Kenya").timeout(5000).get();

        Elements paragraphs = doc.select(".mw-content-ltr p, .mw-content-ltr li");

        Element firstParagraph = paragraphs.first();

        System.out.println(firstParagraph.text()); //Print out just the first paragraph

        /*
        Element lastParagraph = paragraphs.last();
        Element p = firstParagraph;

        int i = 1;
        while (p != lastParagraph)
        {
            p = paragraphs.get(i);
            System.out.println(p.text());
            i++;
        }
         */
    }

}
