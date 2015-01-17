package com.github.yasenia.tetris.ui;

import com.github.yasenia.tetris.model.TetrisModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author Yasenia
 * @since 2015/1/17.
 */
public class TetrisPanel extends JPanel implements KeyListener {
    private TetrisModel tetrisModel;

    public TetrisPanel(TetrisModel tetrisModel) {
        this.tetrisModel = tetrisModel;
        requestFocus();
        addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int baseX = getWidth() / 10;
        int baseY = getHeight() / 10;
        int width = getWidth() * 4 / 5;
        int height = getHeight() * 4 / 5;



        Color primaryColor = g.getColor();

        // 绘制背景
        g.setColor(Color.BLACK);
        g.fillRect(baseX, baseY, width, height);

        if (tetrisModel.getGameStatus() == TetrisModel.GameStatus.PLAYING) {
            int[][] gameDisplayMatrix = tetrisModel.getGameDisplayMatrix();
            int cellHeight= height / gameDisplayMatrix.length;
            for (int i = 0; i < gameDisplayMatrix.length; i++) {
                int cellWidth= width / gameDisplayMatrix[i].length;
                for (int j = 0; j < gameDisplayMatrix[i].length; j++) {
                    if (gameDisplayMatrix[i][j] != 0) {
                        g.setColor(Color.RED);
                        g.fillRect(baseX + cellWidth * j, baseY + cellHeight * i, cellWidth, cellHeight);
                    }
                }
            }
        }


        g.setColor(primaryColor);
    }

    private class TetrisUIRefreshThread extends Thread {
        @Override
        public void run() {
            super.run();

            while (true) {
                repaint();

                try {
                    sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_F5:
                System.out.println("=============");
                tetrisModel.start();
                new TetrisUIRefreshThread().start();
                break;
            case KeyEvent.VK_LEFT:
                tetrisModel.moveLeft();
                break;
            case KeyEvent.VK_RIGHT:
                tetrisModel.moveRight();
                break;
            case KeyEvent.VK_UP:
                break;
            case KeyEvent.VK_SPACE:
                tetrisModel.spinPos();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
