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
import java.util.*;

public class HashMapDemo
{

    public static void main(String args[])
    {

        // Create a hash map
        HashMap hm = new HashMap();
        // Put elements to the map
        hm.put("Zara", 3434.34);
        hm.put("Mahnaz", 123.22);
        hm.put("Ayan", 1378.00);
        hm.put("Daisy", 99.22);
        hm.put("Qadir", -19.08);

        // Get a set of the entries
        Set set = hm.entrySet();
        // Get an iterator
        Iterator i = set.iterator();
        // Display elements
        while (i.hasNext())
        {
            Map.Entry me = (Map.Entry) i.next();
            System.out.print(me.getKey() + ": ");
            System.out.println(me.getValue());
        }
        System.out.println();
        // Deposit 1000 into Zara's account
        double balance = ((Double) hm.get("Zara"));
        System.out.println(hm.get("Benda"));
        hm.put("Zara", balance + 1000);
        System.out.println("Zara's new balance: "
                + hm.get("Zara"));
    }
}
