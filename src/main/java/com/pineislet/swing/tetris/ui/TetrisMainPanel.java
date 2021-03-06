package com.pineislet.swing.tetris.ui;

import com.pineislet.swing.tetris.model.TetrisModel;
import com.pineislet.swing.tetris.ui.util.PaintUtil;
import com.pineislet.swing.tetris.ui.util.PanelRefreshThread;

import javax.swing.*;
import java.awt.*;

/**
 * Create on 2015/1/17
 *
 * @author Yasenia
 */
public class TetrisMainPanel extends JPanel {
    public static final long SLEEP_SPAN = 30;

    /**
     *  游戏模型
     * */
    private TetrisModel tetrisModel;

    /**
     *  刷新线程
     * */
    private PanelRefreshThread refreshThread;

    /**
     *  刷新状态
     * */
    private boolean onRefreshing;


    /**
     *  构造方法
     * */
    public TetrisMainPanel(TetrisModel tetrisModel) {
        this.tetrisModel = tetrisModel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 记录画笔颜色
        Color tempColor = g.getColor();

        g.setColor(Color.GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        // 确定绘制区域
        int width = Math.min(getWidth(), getHeight() / 2);
        int height = 2 * width;
        int baseX = (getWidth() - width) / 2;
        int baseY = (getHeight() - height) / 2;

        // 绘制背景
        g.setColor(Color.BLACK);
        g.fillRect(baseX, baseY, width, height);

        // 绘制图形
        if (null != tetrisModel) {
            if (tetrisModel.getGameStatus() == TetrisModel.GameStatus.OVER) {
                PaintUtil.paintMatrix(g, tetrisModel.getGameDisplayMatrix(), baseX, baseY, width, height, Color.BLACK, Color.GRAY);
            }
            else {
                PaintUtil.paintMatrix(g, tetrisModel.getGameDisplayMatrix(), baseX, baseY, width, height);
            }
        }

        // 恢复画笔颜色
        g.setColor(tempColor);
    }

    public void startRefresh() {
        // 停止原有刷新线程
        stopRefresh();
        // 创建并启动刷新线程
        refreshThread = new PanelRefreshThread(this, SLEEP_SPAN);
        refreshThread.start();
        // 更改刷新状态
        onRefreshing = true;
    }

    public void stopRefresh() {
        if (null != refreshThread) {
            refreshThread.callStop();
        }
        // 更改刷新状态
        onRefreshing = false;
    }

    /**
     *  获取刷新状态
     * */
    public boolean isOnRefreshing() {
        return onRefreshing;
    }
}
