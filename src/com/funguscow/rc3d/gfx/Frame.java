package com.funguscow.rc3d.gfx;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {

    public static final int WIDTH = 1280, HEIGHT = 720;

    public RaycastCanvas canvas;

    public Frame(){
        super("Raycasting");
        JPanel panel = (JPanel)getContentPane();
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        panel.setLayout(null);
        canvas = new RaycastCanvas(WIDTH, HEIGHT, WIDTH);
        panel.add(canvas);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        while(true){
            canvas.redraw();
        }
    }

    public static void main(String[] args){
        new Frame();
    }

}
