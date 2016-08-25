/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package corpus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import nlp.EntityFinder;

/**
 *
 * @author arthur
 */
public class TestEntityFinder
{
    public static void main(String[] args) throws Exception
    {
        EntityFinder name = new EntityFinder();
        HashMap entities = name.getEntities("William is a thug. He was the last person to see Fred. "
                + "He saw him in Boston at McKenzie's pub at 3:00 where he paid $2.45 for an ale, "
                + "after which they proceeded to the UN building.");
        
                
                
        System.out.println(entities);
    }
}
