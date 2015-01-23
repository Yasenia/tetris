package com.github.yasenia.tetris.ui;

import com.github.yasenia.tetris.model.TetrisModel;

import javax.swing.*;
import java.awt.*;
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
    final int SOFT_DOWN_KEY = KeyEvent.VK_S;            // 软降
    final int HARD_DOWN_KEY = KeyEvent.VK_SPACE;        // 硬降
    final int SPIN_POS_KEY = KeyEvent.VK_K;             // 顺时针旋转
    final int SPIN_NEG_KEY = KeyEvent.VK_J;             // 逆时针旋转
    final int SPIN_REV_KEY = KeyEvent.VK_L;             // 180度旋转
    final int HOLD_KEY = KeyEvent.VK_SHIFT;             // hold

    /** 游戏模型 */
    private TetrisModel tetrisModel;

    /** 游戏面板 */
    private TetrisMenuBar tetrisMenuBar;
    private TetrisMainPanel tetrisMainPanel;
    private TetrisFollowPanel tetrisFollowPanel;
    private TetrisHoldPanel tetrisHoldPanel;
    private TetrisInfoPanel tetrisInfoPanel;

    public TetrisFrame(TetrisModel tetrisModel) {
        this.tetrisModel = tetrisModel;

        initComponents();
        setupLayout();
        addListener();
    }

    /** 初始化组件 */
    private void initComponents() {
        this.tetrisMenuBar = new TetrisMenuBar(tetrisModel);
        this.tetrisMainPanel = new TetrisMainPanel(tetrisModel);
        this.tetrisFollowPanel = new TetrisFollowPanel(tetrisModel);
        this.tetrisHoldPanel = new TetrisHoldPanel(tetrisModel);
        this.tetrisInfoPanel = new TetrisInfoPanel(tetrisModel);
    }

    /** 设置布局 */
    private void setupLayout() {
        setSize(600, 602);
        tetrisFollowPanel.setPreferredSize(new Dimension(100, 600));
        tetrisInfoPanel.setPreferredSize(new Dimension(200, 400));
        tetrisHoldPanel.setPreferredSize(new Dimension(200, 200));
        JPanel westPanel = new JPanel();
        westPanel.setLayout(new BorderLayout());
        westPanel.add(tetrisHoldPanel, BorderLayout.NORTH);
        westPanel.add(tetrisInfoPanel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(tetrisMainPanel, BorderLayout.CENTER);
        add(tetrisFollowPanel, BorderLayout.EAST);
        add(westPanel, BorderLayout.WEST);
        setJMenuBar(tetrisMenuBar);

    }

    /** 添加监听器 */
    private void addListener() {
        // 监听键盘事件
        this.addKeyListener(new KeyListener() {

            // 键盘点击事件
            @Override
            public void keyTyped(KeyEvent e) {

            }

            // 键盘按下事件
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    // 按下左移键
                    case LEFT_KEY:
                        // 停止持续右移
                        tetrisModel.stopMoveRight();
                        // 持续左移
                        tetrisModel.startMoveLeft();
                        break;
                    // 按下右移键
                    case RIGHT_KEY:
                        // 停止持续左移
                        tetrisModel.stopMoveLeft();
                        // 持续右移
                        tetrisModel.startMoveRight();
                        break;
                    // 按下顺时针旋转键
                    case SPIN_POS_KEY:
                        // 顺时针旋转
                        tetrisModel.spinPos();
                        break;
                    // 按下逆时针旋转键
                    case SPIN_NEG_KEY:
                        // 逆时针旋转
                        tetrisModel.spinNeg();
                        break;
                    // 按下180度旋转键
                    case SPIN_REV_KEY:
                        // 180度旋转
                        tetrisModel.spinRev();
                        break;
                    // 按下硬降键
                    case HARD_DOWN_KEY:
                        // 硬降
                        tetrisModel.hardDown();
                        break;
                    // 按下软降键
                    case SOFT_DOWN_KEY:
                        // 开始软降
                        tetrisModel.startSoftDown();
                        break;
                    // 按下hold键
                    case HOLD_KEY:
                        // 开始软降
                        tetrisModel.hold();
                        break;
                }
            }

            // 键盘松开事件
            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    // 松开开始键
                    case START_KEY:
                        // 游戏开始，游戏面板启动刷新线程
                        if (tetrisModel.getGameStatus() == TetrisModel.GameStatus.PREPARE) {
                            tetrisModel.changeGameStatus(TetrisModel.GameStatus.PLAYING);
                        }
                        break;
                    // 松开暂停键
                    case PAUSE_KEY:
                        // 切换游戏状态
                        if (tetrisModel.getGameStatus() == TetrisModel.GameStatus.PLAYING) {
                            tetrisModel.changeGameStatus(TetrisModel.GameStatus.PAUSE);
                        }
                        else if (tetrisModel.getGameStatus() == TetrisModel.GameStatus.PAUSE) {
                            tetrisModel.changeGameStatus(TetrisModel.GameStatus.PLAYING);
                        }
                        break;
                    // 松开左移键
                    case LEFT_KEY:
                        // 停止持续左移
                        tetrisModel.stopMoveLeft();
                        break;
                    // 松开右移键
                    case RIGHT_KEY:
                        // 停止持续右移
                        tetrisModel.stopMoveRight();
                        break;
                    // 松开软降键
                    case SOFT_DOWN_KEY:
                        // 停止软降
                        tetrisModel.stopSoftDown();
                        break;
                }
            }
        });

        // 监听游戏状态改变事件
        tetrisModel.addOnStatusChangedListener(e -> {

            switch (e.getCurrentStatus()) {
                case PREPARE:
                    tetrisMainPanel.stopRefresh();
                    tetrisInfoPanel.stopRefresh();
                    break;
                case PLAYING:
                    tetrisMainPanel.startRefresh();
                    tetrisInfoPanel.startRefresh();
                    break;
                case PAUSE:
                    tetrisMainPanel.stopRefresh();
                    tetrisInfoPanel.stopRefresh();
                    break;
                case OVER:
                    tetrisMainPanel.stopRefresh();
                    tetrisInfoPanel.stopRefresh();
                    break;
            }

            tetrisFollowPanel.repaint();
            tetrisHoldPanel.repaint();
        });

        // 监听砖块改变事件
        tetrisModel.addOnTileModifiedListener(e -> {
            tetrisFollowPanel.repaint();
            tetrisHoldPanel.repaint();
        });
    }

    public void startRefreshMainPanel() {

    }

}
