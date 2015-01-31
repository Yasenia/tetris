package com.pineislet.swing.tetris.ui;

import com.pineislet.swing.tetris.model.TetrisModel;
import com.pineislet.swing.tetris.model.impl.TetrisModelImpl;

import javax.swing.*;
import java.awt.*;

/**
 * Create on 2015/1/17
 *
 * @author Yasenia
 */
public class Bootstrap {
    public static void main(String[] args) {
        // 设置 lookAndFeel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        // 启动游戏
        EventQueue.invokeLater(() -> {
            TetrisModel model = new TetrisModelImpl();
            JFrame frame = new TetrisFrame(model);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
