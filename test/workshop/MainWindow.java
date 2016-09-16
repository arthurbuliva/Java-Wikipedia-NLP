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
public class MainWindow extends JFrame
{
    public MainWindow()
    {
        super("Retro Old School Window");
    }
    
    public static void main(String[] args) throws Exception
    {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        
        JFrame.setDefaultLookAndFeelDecorated(true);
        
        MainWindow window = new MainWindow();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(500, 300);
        window.setVisible(true);
    }
}
