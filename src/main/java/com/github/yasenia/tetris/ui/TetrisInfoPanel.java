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

        String timeStr = (m < 10 ? "0" : "") + m + ":" + (s < 10 ? "0" : "") + s +  ":" + (ss < 10 ? "0" : "") + ss;

        g.setColor(Color.BLACK);
        g.setFont(new Font("宋体",Font.PLAIN, 16));
        FontMetrics fontMetrics = g.getFontMetrics();
        int strX = getWidth() / 10;
        int strY = getHeight() / 20;
        int lineHeight = (int) fontMetrics.getStringBounds("行", g).getHeight();

        String[] stringArray = new String[] {
                "得分：", "" + score, "用时：", "" + timeStr, "",
                "操作方法——",
                "F1：开始", "ESC：暂停",
                "A：左移", "D：右移",
                "S：加速下降", "SPACE：硬降",
                "J：顺时针旋转", "K：逆时针旋转", "L：180度旋转",
                "CTRL：hold",
        };

        for (String aStringArray : stringArray) {
            strY += lineHeight;
            g.drawString(aStringArray, strX, strY);
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
        onRefreshing = false;
    }

    public boolean isOnRefreshing() {
        return onRefreshing;
    }
}
