/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 *
 * @author Arthur Buliva
 */
public class MainWindow extends JFrame
{

    private JLabel englishLabel;
    private JTextField englishField;
    private JButton translateButton;
    private JTextArea swahiliOutput;
    private final TranslatorRC2 translator;

    public MainWindow()
    {
        super("Wikipedia Translator");

        translator = new TranslatorRC2();

        initComponents();

    }

    private void translate()
    {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        String translation = translator.translate(englishField.getText().trim());
        
        swahiliOutput.setText(translation);

        System.out.println(englishField.getText().trim() + " => " + translation);
        
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public static void main(String[] args) throws Exception
    {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

        JFrame.setDefaultLookAndFeelDecorated(true);

        MainWindow window = new MainWindow();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(500, 300);
        window.setVisible(true);
        window.setLocationRelativeTo(null);
    }

    private void initComponents()
    {
        englishLabel = new JLabel("English Text:");
        englishField = new JTextField();
        translateButton = new JButton("Translate");
        swahiliOutput = new JTextArea();

        swahiliOutput.setLineWrap(true);
        swahiliOutput.setWrapStyleWord(true);
        swahiliOutput.setEditable(false);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        inputPanel.add(englishLabel, BorderLayout.WEST);
        inputPanel.add(englishField, BorderLayout.CENTER);
        inputPanel.add(translateButton, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);

        JScrollPane scroller = new JScrollPane(swahiliOutput);
        add(scroller, BorderLayout.CENTER);

        translateButton.addActionListener((ActionEvent e)
                -> 
                {
                    translate();
        });

        englishField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                if (e.getKeyCode() == 10) // Enter button
                {
                    translate();
                }
                else
                {
                    swahiliOutput.setText(null);
                }
            }
        });
    }
}
