package com.pineislet.swing.tetris.ui;

import com.pineislet.swing.tetris.model.TetrisModel;

import javax.swing.*;

/**
 * Create on 2015/1/19
 *
 * @author Yasenia
 */
public class TetrisMenuBar extends JMenuBar {
    private TetrisModel tetrisModel;

    private JMenu gameMenu;
    private JMenuItem resetItem;
    private JMenuItem startItem;
    private JMenuItem pauseItem;

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
        pauseItem = new JMenuItem("游戏暂停/恢复");

        settingMenu = new JMenu("设置");
        aboutMenu = new JMenu("关于");

        setSpeedLevelItem = new JMenuItem("速度调节");
    }

    private void setupMenu() {
        gameMenu.add(resetItem);
        gameMenu.add(startItem);
        gameMenu.add(pauseItem);

        settingMenu.add(setSpeedLevelItem);

        add(gameMenu);
        add(settingMenu);
        add(aboutMenu);
    }

    private void addListener() {
        resetItem.addActionListener(e -> tetrisModel.changeGameStatus(TetrisModel.GameStatus.PREPARE));
        startItem.addActionListener(e -> tetrisModel.changeGameStatus(TetrisModel.GameStatus.PLAYING));
        pauseItem.addActionListener(e -> {
            switch (tetrisModel.getGameStatus()) {
                case PAUSE:
                    tetrisModel.changeGameStatus(TetrisModel.GameStatus.PLAYING);
                    break;
                case PLAYING:
                    tetrisModel.changeGameStatus(TetrisModel.GameStatus.PAUSE);
                    break;
            }
        });

        setSpeedLevelItem.addActionListener(e -> {
            String result = JOptionPane.showInputDialog(this, "请选择速度级别（0-9）", tetrisModel.getSpeedLevel());

            int speedLevel = Integer.valueOf(result);

            tetrisModel.setSpeedLevel(speedLevel);
            tetrisModel.changeGameStatus(TetrisModel.GameStatus.PREPARE);
        });
    }
}
