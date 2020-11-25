package org.example.client;

import java.awt.*;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

import java.util.ArrayList;

/**
 * For testing purposes
 *
 */


public class BasicDisplayer {

    private ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
    private JFrame frame;

    public BasicDisplayer() {
        this.frame = new JFrame();
        this.frame.setLayout(new FlowLayout());
        this.frame.setSize(800, 600);
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.frame.setVisible(true);
    }

    public void addColor(int b, int g, int r, int ind){
        Color c = new Color(r,g,b);
        JLabel label = new JLabel();

        // JLabel label = new JLabel(String.valueOf(ind + 1));
//        JLabel label = new JLabel(String.valueOf(r) + ", "
//                +  String.valueOf(g) + ", "
//                +  String.valueOf(b));

        label.setForeground(Color.white);
        label.setOpaque(true);
        label.setBackground(c);
        label.setPreferredSize(new Dimension(20,20));
        this.frame.add(label);
        this.frame.setVisible(true);
    }

    public void addImage(String filePath) {
        BufferedImage image = readImage(filePath);
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(image));
        this.images.add(image);
        this.frame.add(label);
        this.frame.setVisible(true);
    }

    public void addImage(BufferedImage image) {
        JLabel label = new JLabel();
        Image scaled_image = image.getScaledInstance(this.frame.getWidth(), this.frame.getHeight(), Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(scaled_image));
        // label.setIcon(new ImageIcon(image));
        this.frame.add(label);
        this.frame.setVisible(true);
    }

    public void setImage(String filePath, int labelId) {
        // ...
    }

    public void setImage(BufferedImage image) {
        // ...
    }

    public static BufferedImage readImage(String filePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(filePath));
        } catch (IOException e) {
            System.out.println(e);
        }
        return image;
    }
}
