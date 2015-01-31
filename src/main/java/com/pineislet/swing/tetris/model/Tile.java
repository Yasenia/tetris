package com.pineislet.swing.tetris.model;

/**
 * Create on 2015/1/19
 *
 * @author Yasenia
 */
public enum Tile {
    L(new int[][]
            {
                    {0, 0, 0, 0},
                    {0, 1, 0, 0},
                    {0, 1, 0, 0},
                    {0, 1, 1, 0}
            }),
    J(new int[][]
            {
                    {0, 0, 0, 0},
                    {0, 0, 2, 0},
                    {0, 0, 2, 0},
                    {0, 2, 2, 0}
            }),
    S(new int[][]
            {
                    {0, 0, 0, 0},
                    {0, 3, 3, 0},
                    {3, 3, 0, 0},
                    {0, 0, 0, 0}
            }),
    Z(new int[][]
            {
                    {0, 0, 0, 0},
                    {4, 4, 0, 0},
                    {0, 4, 4, 0},
                    {0, 0, 0, 0}
            }),
    T(new int[][]
            {
                    {0, 0, 0, 0},
                    {5, 5, 5, 0},
                    {0, 5, 0, 0},
                    {0, 0, 0, 0}
            }),
    I(new int[][]
            {
                    {0, 6, 0, 0},
                    {0, 6, 0, 0},
                    {0, 6, 0, 0},
                    {0, 6, 0, 0}
            }),
    O(new int[][]
            {
                    {0, 0, 0, 0},
                    {0, 7, 7, 0},
                    {0, 7, 7, 0},
                    {0, 0, 0, 0}
            });

    /**
     *  砖块矩阵
     * */
    private int [][] tileMatrix;

    /**
     *  构造方法（私有）
     * */
    private Tile(int[][] tileMatrix) {
        this.tileMatrix = tileMatrix;
    }

    /**
     *  获取砖块矩阵
     *
     *  @return 砖块矩阵
     * */
    public int[][] getTileMatrix() {
        return getTileMatrix(Direction.NORTH);
    }

    /**
     *  获取指定方向的砖块矩阵
     *
     *  @param direction 砖块方向
     *  @return 砖块矩阵
     * */
    public int[][] getTileMatrix(Direction direction) {
        int[][] matrix = new int[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                switch(direction) {
                    case NORTH:
                        matrix[i][j] = tileMatrix[i][j];
                        break;
                    case EAST:
                        matrix[i][j] = tileMatrix[3 - j][i];
                        break;
                    case SOUTH:
                        matrix[i][j] = tileMatrix[3 - i][3 - j];
                        break;
                    case WEST:
                        matrix[i][j] = tileMatrix[j][3 - i];
                        break;
                    default :
                        assert false;
                }
            }
        }
        return matrix;
    }

    /**
     *  获取指定编号砖块
     *
     *  @return 指定砖块
     * */
    public static Tile getTile(int number) {
        Tile tile = null;
        switch (number) {
            case 0:
                tile = Tile.I;
                break;
            case 1:
                tile = Tile.L;
                break;
            case 2:
                tile = Tile.J;
                break;
            case 3:
                tile = Tile.S;
                break;
            case 4:
                tile = Tile.Z;
                break;
            case 5:
                tile = Tile.T;
                break;
            case 6:
                tile = Tile.O;
                break;
            default:
                assert false;
        }
        return tile;
    }
}