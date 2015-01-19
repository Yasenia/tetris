package com.github.yasenia.tetris.ui;

import com.github.yasenia.tetris.model.TetrisModel;
import com.github.yasenia.tetris.model.impl.TetrisModelImpl;

import javax.swing.*;

/**
 * @author Yasenia
 * @since 2015/1/17.
 */
public class Bootstrap {
    public static void main(String[] args) {
        TetrisModel model = new TetrisModelImpl();

        JFrame frame = new TetrisFrame(model);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
