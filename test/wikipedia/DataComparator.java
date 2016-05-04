/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author arthu
 */
public class DataComparator
{
    
    /**
     * Test of fetchData method, of class DataFetcher.
     */
    @Test
    public void testFetchData()
    {
        DataFetcher wiki = new DataFetcher();
        
        String expectedResult = "The Central Bank of Kenya (Swahili: Benki Kuu ya Kenya) "
                + "is Kenya's central bank. The bank is located in Nairobi. "
                + "The bank's name is abbreviated to \"CBK\". Central Bank of Kenya was "
                + "founded in 1966 after the dissolution of East African Currency Board (EACB).";

        String data = wiki.fetchData("en", "Central Bank of Kenya");
        
        assertEquals(expectedResult, data);

    }
    
    
    /**
     * Test fetching of random data
     */
    @Test
    public void testRandomData()
    {
       DataFetcher wiki = new DataFetcher();
       
       String expectedResult = "Domestic cat";
       
       String data = wiki.fetchData("en", "Cat");
       
       assertEquals(expectedResult, data);
    }
    
}
