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
        CharSequence s = "Hello World";
        CharSequence t = "Hillo World";
        
        System.out.println(StringUtils.getLevenshteinDistance(s, t));
    }
}
