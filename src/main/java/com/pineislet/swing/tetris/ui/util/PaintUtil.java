package com.pineislet.swing.tetris.ui.util;


import java.awt.*;

/**
 * Create on 2015/1/19
 *
 * @author Yasenia
 */
public class PaintUtil {

    /**
     *  绘制数据矩阵对应图像
     * */
    public static void paintMatrix(Graphics g, int[][] matrix, int x, int y, int width, int height) {
        paintMatrix(g, matrix, x, y, width, height, Color.black);
    }

    public static void paintMatrix(Graphics g, int[][] matrix, int x, int y, int width, int height, Color background) {
        // 记录画笔颜色
        Color primaryColor = g.getColor();

        // 绘制背景
        g.setColor(background);
        g.fillRect(x, y, width, height);

        if (null != matrix) {
            int cellHeight= height / matrix.length;
            for (int i = 0; i < matrix.length; i++) {
                if (null != matrix[i]) {
                    int cellWidth= width / matrix[i].length;
                    for (int j = 0; j < matrix[i].length; j++) {
                        switch (Math.abs(matrix[i][j])) {
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
                                g.setColor(Color.MAGENTA);
                                break;
                            case 6:
                                g.setColor(Color.GREEN);
                                break;
                            case 7:
                                g.setColor(Color.WHITE);
                                break;
                            default:
                                g.setColor(background);
                        }
                        if (matrix[i][j] > 0) {
                            g.fillRect(x + cellWidth * j + 1, y + cellHeight * i + 1, cellWidth - 2, cellHeight - 2);
                        }
                        if (matrix[i][j] < 0) {
                            g.drawRect(x + cellWidth * j + 1, y + cellHeight * i + 1, cellWidth - 2, cellHeight - 2);
                        }
                    }
                }
            }
        }

        // 恢复画笔颜色
        g.setColor(primaryColor);
    }
}
