/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workshop;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

/**
 *
 * @author arthur
 */
public class MainTest
{
    public static void main(String[] args)
    {
        List<String> a = new ArrayList<>();
        List<String> b = new ArrayList<>();
        
        
        a.add("hello");
        a.add("World");
        b.add("Hello");
        b.add("You");
        b.add("are");
        b.add("all");
        b.add("sick!");
        
        if (CollectionUtils.containsAny(a, b))
                    {
                        System.out.println(a);
                        System.out.println(b);
                    }
    }
}
