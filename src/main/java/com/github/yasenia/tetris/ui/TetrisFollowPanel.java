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

        // 记录画笔颜色
        Color tempColor = g.getColor();

        // 绘制背景
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        // 获得随后砖块队列
        List<Tile> tileList = tetrisModel.getFollowingTileList();

        // 绘制砖块队列图形
        for (int i = 0; i < TetrisModel.FOLLOW_TILE_COUNTS; i++) {
            if (null != tileList) {
                Tile tile = tileList.get(i);
                if (tetrisModel.getGameStatus() == TetrisModel.GameStatus.OVER) {
                    PaintUtil.paintMatrix(g, null == tile ? null : tile.getTileMatrix(), getWidth() / 4, getWidth() * i + getWidth() / 4, getWidth() / 2, getWidth() / 2, Color.BLACK, Color.GRAY);
                }
                else {
                    PaintUtil.paintMatrix(g, null == tile ? null : tile.getTileMatrix(), getWidth() / 4, getWidth() * i + getWidth() / 4, getWidth() / 2, getWidth() / 2);
                }
            }
            else {
                PaintUtil.paintMatrix(g, null, getWidth() / 4, getWidth() * i + getWidth() / 4, getWidth() / 2, getWidth() / 2);
            }
        }

        // 恢复画笔颜色
        g.setColor(tempColor);
    }
}
