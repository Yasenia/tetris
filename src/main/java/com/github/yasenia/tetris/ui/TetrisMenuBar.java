package com.github.yasenia.tetris.ui;

import com.github.yasenia.tetris.model.TetrisModel;

import javax.swing.*;

/**
 * @author Yasenia
 * @since 2015/1/19.
 */
public class TetrisMenuBar extends JMenuBar {
    private TetrisModel tetrisModel;

    private JMenu gameMenu;
    private JMenuItem resetItem;
    private JMenuItem startItem;

    private JMenu settingMenu;
    private JMenu aboutMenu;

    private JMenuItem setSpeedLevelItem;

    public TetrisMenuBar(TetrisModel tetrisModel) {
        this.tetrisModel = tetrisModel;
        initComponents();
        setupMenu();
        addListener();
    }

    private void initComponents() {
        gameMenu = new JMenu("游戏");
        resetItem = new JMenuItem("游戏重置");
        startItem = new JMenuItem("游戏开始");

        settingMenu = new JMenu("设置");
        aboutMenu = new JMenu("关于");

        setSpeedLevelItem = new JMenuItem("速度调节");
    }

    private void setupMenu() {
        gameMenu.add(resetItem);
        gameMenu.add(startItem);

        settingMenu.add(setSpeedLevelItem);

        add(gameMenu);
        add(settingMenu);
        add(aboutMenu);
    }

    private void addListener() {
        resetItem.addActionListener(e -> {

            tetrisModel.reset();
//            tetrisModel.start();
        });

        startItem.addActionListener(e -> {
            tetrisModel.start();
        });

        setSpeedLevelItem.addActionListener(e -> {


        });
    }
}
