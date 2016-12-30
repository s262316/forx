package com.github.s262316.forx.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class TestFontGraphics {

    public static void main(String[] args) {
        new TestFontGraphics();
    }

    public TestFontGraphics() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                }

                JFrame frame = new JFrame("Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);                
            }
        });
    }

    public class TestPane extends JPanel {

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(200, 200);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            ((Graphics2D)g).scale(96.0/72, 96.0/72);
            
            int x = 10;
            int y = 10;
            y += drawFont("Arial", x, y, g);
            y += drawFont("Times New Roman", x, y, g);
        }

        private int drawFont(String fontName, int x, int y, Graphics g) {

            Font font = new Font(fontName, Font.PLAIN, 12);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();

            g.setColor(Color.RED);
//            g.drawLine(x, y, x + fm.stringWidth(fontName), y);
            g.setColor(Color.GREEN);
  //          g.drawLine(x, y + fm.getAscent(), x + fm.stringWidth(fontName), y + fm.getAscent());
            g.setColor(Color.BLUE);
    //        g.drawLine(x, y + (fm.getDescent() + fm.getAscent()), x + fm.stringWidth(fontName), y + (fm.getDescent() + fm.getAscent()));
            g.setColor(Color.BLACK);
            g.drawString(fontName, x, y + fm.getAscent());

            return fm.getHeight();

        }

    }

}
