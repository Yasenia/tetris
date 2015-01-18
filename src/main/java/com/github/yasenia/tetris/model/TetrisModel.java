package com.github.yasenia.tetris.model;

import com.github.yasenia.tetris.model.event.OnTileLockListener;

import java.util.List;

/**
 * @author Yasenia
 * @since 2015/1/17.
 */
public interface TetrisModel {

    /** 开始游戏 */
    void start();

    /** 暂停游戏 */
    void pause();

    /** 恢复游戏 */
    void resume();

    /** 左移 */
    boolean moveLeft();

    /** 开始持续左移 */
    void startMoveLeft();

    /** 结束持续左移 */
    void stopMoveLeft();

    /** 右移 */
    boolean moveRight();

    /** 开始持续右移 */
    void startMoveRight();

    /** 结束持续右移 */
    void stopMoveRight();

    /** 顺时针旋转90度 */
    boolean spinPos();

    /** 逆时针旋转90度 */
    boolean spinNeg();

    /** 旋转180度 */
    boolean spinRev();

    /** 设置加速下落状态 */
    void setSpeedUp(boolean isSpeedUp);

    /** 直接下落 */
    void hardDown();

    /** hold当前方块 */
    void hold();

    /** 游戏推进 */
    void progress();

    /** 获取游戏状态 */
    GameStatus getGameStatus();

    /** 获取游戏显示矩阵 */
    int[][] getGameDisplayMatrix();

    /** 获取后续砖块列表 */
    List<Tile> getFollowingTileList();

    /** 获取hold区方块 */
    Tile getHoldTile();

    /** 添加砖块锁定监听器 */
    void setOnTileLockListener(OnTileLockListener listener);

    /**
     *  枚举 游戏状态
     * */
    enum GameStatus {
        PREPARE, PLAYING, PAUSE, OVER
    }

    /**
     *  枚举 砖块类型
     * */
    enum Tile {
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

        /** 砖块矩阵 */
        private int [][] tileMatrix;

        /** 构造方法 */
        private Tile(int[][] tileMatrix) {
            this.tileMatrix = tileMatrix;
        }

        /** 获取砖块矩阵 */
        public int[][] getTileMatrix() {
            return getTileMatrix(Direction.NORTH);
        }

        /** 获取指定方向的砖块矩阵 */
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

    /**
     *  枚举 砖块方向
     * */
    enum Direction {
        NORTH(0), EAST(1), SOUTH(2), WEST(3);
        private int number;

        private Direction(int number) {
            this.number = number;
        }

        public static Direction getDirection(int number) {
            switch (number) {
                case 0:
                    return NORTH;
                case 1:
                    return EAST;
                case 2:
                    return SOUTH;
                case 3:
                    return WEST;
                default:
                    return null;
            }
        }

        public int getNumber() {
            return number;
        }
    }
}
