package com.github.yasenia.tetris.ui;

import com.github.yasenia.tetris.model.TetrisModel;

import javax.swing.*;
import java.awt.*;

/**
 * @author Yasenia
 * @since 2015/1/17.
 */
public class TetrisPanel extends JPanel {
    public static final long SLEEP_SPAN = 30;

    /** 模型 */
    private TetrisModel tetrisModel;

    /** 刷新线程 */
    private TetrisPanelRefreshThread tetrisPanelRefreshThread;

    /** 刷新状态 */
    private boolean onRefreshing;

    public TetrisPanel(TetrisModel tetrisModel) {
        this.tetrisModel = tetrisModel;
        onRefreshing = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 确定绘制区域
        int baseX = 0;
        int baseY = 0;
        int width = getWidth();
        int height = getHeight();

        Color primaryColor = g.getColor();

        // 绘制背景
        g.setColor(Color.BLACK);
        g.fillRect(baseX, baseY, width, height);
        // 获得绘制数据矩阵
        int[][] gameDisplayMatrix = tetrisModel.getGameDisplayMatrix();
        if (null != gameDisplayMatrix) {
            int cellHeight= height / gameDisplayMatrix.length;
            for (int i = 0; i < gameDisplayMatrix.length; i++) {
                int cellWidth= width / gameDisplayMatrix[i].length;
                for (int j = 0; j < gameDisplayMatrix[i].length; j++) {
                    switch (gameDisplayMatrix[i][j]) {
                        case 1:
                            g.setColor(Color.RED);
                            break;
                        case 2:
                            g.setColor(Color.YELLOW);
                            break;
                        case 3:
                            g.setColor(Color.BLUE);
                            break;
                        case 4:
                            g.setColor(Color.CYAN);
                            break;
                        case 5:
                            g.setColor(Color.GRAY);
                            break;
                        case 6:
                            g.setColor(Color.GREEN);
                            break;
                        case 7:
                            g.setColor(Color.WHITE);
                            break;
                        default:
                            g.setColor(Color.BLACK);
                    }
                    g.fillRect(baseX + cellWidth * j, baseY + cellHeight * i, cellWidth, cellHeight);
                }
            }
        }

        // 恢复画笔颜色
        g.setColor(primaryColor);
    }

    public void startRefresh() {
        // 停止原有刷新线程
        stopRefresh();
        // 创建并启动刷新线程
        tetrisPanelRefreshThread = new TetrisPanelRefreshThread();
        tetrisPanelRefreshThread.start();
        // 更改刷新状态
        onRefreshing = true;
    }

    public void stopRefresh() {
        if (null != tetrisPanelRefreshThread) {
            tetrisPanelRefreshThread.callStop();
        }
        onRefreshing = false;
    }

    public boolean isOnRefreshing() {
        return onRefreshing;
    }

    private class TetrisPanelRefreshThread extends Thread {
        // 线程循环标识
        private boolean flag;

        public TetrisPanelRefreshThread() {
            flag = true;
        }

        @Override
        public void run() {
            super.run();
            while (flag) {
                // 重绘
                repaint();
                try {
                    sleep(SLEEP_SPAN);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // 请求停止
        public void callStop() {
            flag = false;
        }
    }
}
