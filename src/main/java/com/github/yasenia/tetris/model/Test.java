package com.github.yasenia.tetris.model;

import com.github.yasenia.tetris.model.impl.TetrisModelImpl;
import com.github.yasenia.tetris.ui.TetrisPanel;

import javax.swing.*;

/**
 * @author Yasenia
 * @since 2015/1/17.
 */
public class Test {
    public static void main(String[] args) {
        TetrisModel model = new TetrisModelImpl();
//
//        model.start();
//        for (int i = 0; i < 300; i++) {
//            model.progress();
//            System.out.println(model);
//        }

        JFrame frame = new JFrame();
        JPanel panel = new TetrisPanel(model);
        frame.add(panel);
        frame.setSize(300, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        panel.setFocusable(true);
        panel.requestFocus();
        panel.grabFocus();
    }

//    public static void printModel() {
//        int[][] gameDisplayMatrix
//    }
}
