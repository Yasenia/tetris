package com.github.yasenia.tetris.ui;

import com.github.yasenia.tetris.model.TetrisModel;

import javax.swing.*;

/**
 * @author Yasenia
 * @since 2015/1/19.
 */
public class TetrisMenuBar extends JMenuBar {
    private TetrisModel tetrisModel;

    private JMenu settingMenu;
    private JMenu aboutMenu;


    public TetrisMenuBar(TetrisModel tetrisModel) {
        this.tetrisModel = tetrisModel;
        initComponents();
        setupMenu();
        addListener();
    }

    private void initComponents() {
        settingMenu = new JMenu("设置");
        aboutMenu = new JMenu("关于");
    }

    private void setupMenu() {
        add(settingMenu);
        add(aboutMenu);
    }

    private void addListener() {

    }
}
