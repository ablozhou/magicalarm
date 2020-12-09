package com.abloz;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class AnotherWindow extends JFrame
{
    public AnotherWindow()
    {
        super("Another GUI");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(200,200);
        add(new JLabel("Empty JFrame"));
//        pack();
        setVisible(true);
    }
}

