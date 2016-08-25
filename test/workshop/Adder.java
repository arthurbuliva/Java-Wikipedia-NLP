/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workshop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author arthur
 */
public class Adder
{

    public static void main(String[] args)
    {
        Map<String, ArrayList> hashMap = new HashMap<>();
       
        hashMap.put("name", new ArrayList<>());
        
        System.out.println(hashMap);
    }
}
