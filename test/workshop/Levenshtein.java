/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workshop;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Arthur Buliva
 */
public class Levenshtein
{
    public static void main(String[] args)
    {
          String original = "Nini wewe wamekuwa hadi siku za hivi karibuni?";

        String target = "Umekuwa ukishugulika na nini hivi karibuni?";
        
        System.out.println(StringUtils.getLevenshteinDistance(original, target));
    }
}
