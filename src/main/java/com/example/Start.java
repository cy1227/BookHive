package com.example;
import javax.swing.JFrame;



public class Start {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUI gui = new GUI();
                gui.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                gui.getFrame().setSize(520, 400);
                gui.getFrame().setVisible(true);
            }
        });
    }
}

