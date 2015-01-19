package com.github.yasenia.tetris.ui;

import com.github.yasenia.tetris.model.TetrisModel;
import com.github.yasenia.tetris.model.Tile;
import com.github.yasenia.tetris.ui.util.PaintUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author Yasenia
 * @since 2015/1/19.
 */
public class TetrisHoldPanel extends JPanel {
    private TetrisModel tetrisModel;

    public TetrisHoldPanel(TetrisModel tetrisModel) {
        this.tetrisModel = tetrisModel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 绘制背景
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        // 获得hold区砖块
        Tile holdTile = tetrisModel.getHoldTile();
        // 绘制hold区砖块图形
        PaintUtil.paintMatrix(g, null == holdTile ? null : holdTile.getTileMatrix(), getWidth() / 6, getWidth() / 6, getWidth() * 2 / 3, getWidth() * 2 / 3);

    }
}
