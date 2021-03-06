package com.pineislet.swing.tetris.ui.util;

import javax.swing.*;

/**
 * Create on 2015/1/19
 *
 * @author Yasenia
 */
public class PanelRefreshThread extends Thread {

    /**
     *  刷新面板
     * */
    private JPanel panel;

    /**
     *  刷帧时间
     * */
    private long sleepSpan;

    /**
     *  循环标识
     * */
    private boolean flag;

    public PanelRefreshThread(JPanel panel, long sleepSpan) {
        this.panel = panel;
        this.sleepSpan = sleepSpan;
        this.flag = true;
    }

    @Override
    public void run() {
        super.run();
        while (flag) {
            panel.repaint();
            try {
                sleep(sleepSpan);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *  请求线程停止
     * */
    public synchronized void callStop() {
        this.flag = false;
    }
}
