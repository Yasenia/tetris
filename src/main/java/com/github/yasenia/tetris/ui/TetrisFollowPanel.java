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
public class TetrisFollowPanel extends JPanel {
    private TetrisModel tetrisModel;

    public TetrisFollowPanel(TetrisModel tetrisModel) {
        this.tetrisModel = tetrisModel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 绘制背景
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        // 获得随后砖块队列
        List<Tile> tileList = tetrisModel.getFollowingTileList();
        if (null != tileList) {
            // 绘制砖块队列图形
            for (int i = 0; i < tileList.size(); i++) {
                Tile tile = tileList.get(i);
                PaintUtil.paintMatrix(g, null == tile ? null : tile.getTileMatrix(), getWidth() / 4, getWidth() * i, getWidth() / 2, getWidth() / 2);
            }
        }
    }
}
