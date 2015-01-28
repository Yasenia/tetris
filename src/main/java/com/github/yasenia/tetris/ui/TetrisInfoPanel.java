package com.github.yasenia.tetris.ui;

import com.github.yasenia.tetris.model.TetrisModel;
import com.github.yasenia.tetris.ui.util.PanelRefreshThread;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.time.Duration;

/**
 * @author Yasenia
 * @since 2015/1/19.
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

        // 记录画笔颜色
        Color tempColor = g.getColor();

        // 绘制背景
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        int score = tetrisModel.getScore();
        Duration time = tetrisModel.getTime();
        int m = (int) time.getSeconds() / 60;
        int s = (int) time.getSeconds() % 60;
        int ss = time.getNano() / 10000000;

        long seconds = time.getSeconds();
        int nano = time.getNano();
        String timeStr = (m < 10 ? "0" : "") + m + ":" + (s < 10 ? "0" : "") + s +  ":" + (ss < 10 ? "0" : "") + ss;

        g.setColor(Color.BLACK);
        g.setFont(new Font("仿宋",Font.PLAIN, 18));
        FontMetrics fontMetrics = g.getFontMetrics();
        int strX = getWidth() / 5;
        int strY = getHeight() / 5;

        Rectangle2D rec  = fontMetrics.getStringBounds("得分：", g);
        g.drawString("得分：", strX, strY);
        strY += (int) rec.getHeight();
        g.drawString("" + score, strX, strY);

        strY = getHeight() * 2 / 5;
        rec  = fontMetrics.getStringBounds("用时：", g);
        g.drawString("用时：", strX, strY);

        strY += (int) rec.getHeight();
        g.drawString("" + timeStr, strX, strY);

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
        onRefreshing = false;
    }

    public boolean isOnRefreshing() {
        return onRefreshing;
    }
}
