package com.github.yasenia.tetris.ui;

import com.github.yasenia.tetris.model.TetrisModel;
import com.github.yasenia.tetris.model.impl.TetrisModelImpl;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author Yasenia
 * @since 2015/1/18.
 */
public class TetrisFrame extends JFrame {
    /** 按键控制 */
    final int START_KEY = KeyEvent.VK_F1;               // 开始
    final int PAUSE_KEY = KeyEvent.VK_ESCAPE;           // 暂停/恢复暂停
    final int LEFT_KEY = KeyEvent.VK_A;                 // 左移
    final int RIGHT_KEY = KeyEvent.VK_D;                // 右移
    final int SPIN_POS_KEY = KeyEvent.VK_K;             // 顺时针旋转
    final int SPIN_NEG_KEY = KeyEvent.VK_J;             // 逆时针旋转
    final int HARD_DOWN_KEY = KeyEvent.VK_SPACE;        // 硬降

//    final int LEFT_KEY = KeyEvent.VK_LEFT;

    /** 游戏模型 */
    private TetrisModel tetrisModel;

    /** 游戏面板 */
    private TetrisPanel tetrisPanel;

    public TetrisFrame() {
        this.tetrisModel = new TetrisModelImpl();
        this.tetrisPanel = new TetrisPanel(tetrisModel);
        this.add(tetrisPanel);

        // 添加监听器
        addListener();
    }


    private void addListener() {
        // 监听键盘事件
        this.addKeyListener(new KeyListener() {
            // 是否持续移动
            private boolean keepMoveLeft;
            private boolean keepMoveRight;

            // 初始化
            {
                keepMoveLeft = false;
                keepMoveRight = false;
            }

            // 键盘点击事件
            @Override
            public void keyTyped(KeyEvent e) {

            }

            // 键盘按下事件
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    // 按下左移按键
                    case LEFT_KEY:
                        // 停止持续右移
                        if (keepMoveRight) {
                            tetrisModel.stopMoveRight();
                        }
                        // 若未处于持续左移状态，左移一格，标记为持续左移
                        if (!keepMoveLeft) {
                            tetrisModel.moveLeft();
                            keepMoveLeft = true;
                        }
                        // 若处于持续左移状态，持续左移
                        else {
                            tetrisModel.startMoveLeft();
                        }
                        break;
                    // 按下右移按键
                    case RIGHT_KEY:
                        // 停止持续左移
                        if (keepMoveLeft) {
                            tetrisModel.stopMoveLeft();
                        }
                        // 若未处于持续右移状态，右移一格，标记为持续右移
                        if (!keepMoveRight) {
                            tetrisModel.moveRight();
                            keepMoveRight = true;
                        }
                        // 若处于持续右移状态，持续右移
                        else {
                            tetrisModel.startMoveRight();
                        }
                        break;
                    case SPIN_POS_KEY:
                        tetrisModel.spinPos();
                        break;
                    case SPIN_NEG_KEY:
                        tetrisModel.spinNeg();
                        break;
                    case HARD_DOWN_KEY:
                        tetrisModel.hardDown();
                        break;
                }
            }

            // 键盘松开事件
            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    // 松开开始，游戏开始，游戏面板启动刷新线程
                    case START_KEY:
                        tetrisModel.start();
                        tetrisPanel.startRefresh();
                        break;
                    // 暂停
                    case PAUSE_KEY:
                        if (tetrisModel.getGameStatus() == TetrisModel.GameStatus.PLAYING) {
                            tetrisModel.pause();
                        }
                        else if (tetrisModel.getGameStatus() == TetrisModel.GameStatus.PAUSE) {
                            tetrisModel.resume();
                        }
                        break;
                    // 松开左移按键，停止持续左移
                    case LEFT_KEY:
                        if (keepMoveLeft) {
                            tetrisModel.stopMoveLeft();
                            keepMoveLeft = false;
                        }
                        break;
                    // 松开右移按键，停止持续右移
                    case RIGHT_KEY:
                        if (keepMoveRight) {
                            tetrisModel.stopMoveRight();
                            keepMoveRight = false;
                        }
                        break;
                }
            }
        });
    }
}
