/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workshop;

import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 *
 * @author Arthur Buliva
 */
public class RetroWindow extends JFrame
{
    public RetroWindow()
    {
        super("Retro Old School Window");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
    }
    
    public static void main(String[] args) throws Exception
    {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        
        JFrame.setDefaultLookAndFeelDecorated(true);
        
        RetroWindow window = new RetroWindow();
        window.setVisible(true);
    }
}
