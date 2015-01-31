package com.pineislet.swing.tetris.ui;

import com.pineislet.swing.tetris.model.TetrisModel;
import com.pineislet.swing.tetris.ui.util.PanelRefreshThread;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;

/**
 * Create on 2015/1/19
 *
 * @author Yasenia
 */
public class TetrisInfoPanel extends JPanel {
    public static final long SLEEP_SPAN = 30;

    /** 模型 */
    private TetrisModel tetrisModel;

    /** 刷新线程 */
    private PanelRefreshThread refreshThread;

    /** 刷新状态 */
    private boolean onRefreshing;

    public TetrisInfoPanel(TetrisModel tetrisModel) {
        this.tetrisModel = tetrisModel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int score = tetrisModel.getScore();
        Duration time = tetrisModel.getTime();

        g.drawString("得分：\n" + score, getWidth() / 5, getHeight() / 5);
        g.drawString("用时：\n" + time, getWidth() / 5, getHeight() / 2);

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
        onRefreshing = false;
    }

    public boolean isOnRefreshing() {
        return onRefreshing;
    }
}