/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workshop;

import opennlp.POSDetector;

/**
 *
 * @author arthur
 */
public class TestPOSDetector
{
    public static void main(String[] args)
    {
        System.out.println(POSDetector.detectPOS("My name is Rogers and I am an old man"));
    }
}
