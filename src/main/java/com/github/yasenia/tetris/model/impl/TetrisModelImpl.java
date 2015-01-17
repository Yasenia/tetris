package com.github.yasenia.tetris.model.impl;

import com.github.yasenia.tetris.model.TetrisConfig;
import com.github.yasenia.tetris.model.TetrisModel;
import com.github.yasenia.tetris.model.event.OnTileLockListener;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;

/**
 * @author Yasenia
 * @since 2015/1/17.
 */
public class TetrisModelImpl implements TetrisModel {

    /** 游戏配置 */
    private TetrisConfig config;

    /** 游戏状态 */
    private GameStatus gameStatus;

    /** 游戏基矩阵 */
    private int[][] gameBaseMatrix;

    /** 当前砖块 */
    private Tile currentTile;

    /** 当前砖块方向 */
    private Direction direction;

    /** 当前砖块左上角位置 */
    private int x;
    private int y;

    /** 砖块队列 */
    private Queue<Tile> tileQueue;

    /** 下落时间间隔，数值越小，下落速度越快 */
    private int downInterval = 1;
    /** 下落时间间隔计数 */
    private int currentDownInterval;

    /** 锁定延时间隔，数值越小，锁定越迅速 */
    private int lockInterval = 1;
    /** 锁定时间间隔计数 */
    private int currentLockInterval;

    /** 强制锁定延时间隔，数值越小，强制锁定越迅速 */
    private int hardLockInterval = 3;
    /** 强制锁定时间间隔计数 */
    private int currentHardLockInterval;

    public TetrisModelImpl() {
        config = TetrisConfig.getDefaultConfig();
        gameStatus = GameStatus.PREPARE;
        gameBaseMatrix = new int[config.getHeight()][config.getWidth()];

    }

    @Override
    public void start() {
        // TODO 完善方法
        if (gameStatus == GameStatus.PREPARE) {
            gameStatus = GameStatus.PLAYING;
            currentTile = nextTile();
            x = 3; y = 0;
            direction = Direction.NORTH;

            new TetrisGameThread().start();
        }
    }

    @Override
    public void pause() {
        if (gameStatus == GameStatus.PLAYING) {
            gameStatus = GameStatus.PAUSE;
        }
    }

    @Override
    public void resume() {
        if (gameStatus == GameStatus.PAUSE) {
            gameStatus = GameStatus.PLAYING;
        }
    }

    @Override
    public synchronized boolean moveLeft() {
        boolean flag = true;
        // 左移
        x--;
        // 冲突，则还原动作
        if (hasConflict()) {
            x++;
            flag = false;
        }
        // 移动成功，锁定计数清零
        else {
            currentLockInterval = 0;
        }
        return flag;
    }

    @Override
    public synchronized boolean moveRight() {
        boolean flag = true;
        // 右移
        x++;
        // 冲突，则还原动作
        if (hasConflict()) {
            x--;
            flag = false;
        }
        // 移动成功，锁定计数清零
        else {
            currentLockInterval = 0;
        }
        return flag;
    }

    @Override
    public synchronized boolean spinPos() {
        boolean flag = true;
        // 顺时针旋转90度
        direction = Direction.getDirection((direction.getNumber() + 1) % 4);
        // 冲突，则还原动作
        if (hasConflict()) {
            direction = Direction.getDirection((direction.getNumber() + 3) % 4);
            flag = false;
        }
        // 旋转成功，锁定计数清零
        else {
            currentLockInterval = 0;
        }
        return flag;
    }

    @Override
    public synchronized boolean spinNeg() {
        boolean flag = true;
        // 逆时针旋转90度
        direction = Direction.getDirection((direction.getNumber() + 3) % 4);
        // 冲突，则还原动作
        if (hasConflict()) {
            direction = Direction.getDirection((direction.getNumber() + 1) % 4);
            flag = false;
        }
        // 旋转成功，锁定计数清零
        else {
            currentLockInterval = 0;
        }
        return flag;
    }

    @Override
    public synchronized boolean spinRev() {
        boolean flag = true;
        // 旋转180度
        direction = Direction.getDirection((direction.getNumber() + 2) % 4);
        // 冲突，则还原动作
        if (hasConflict()) {
            direction = Direction.getDirection((direction.getNumber() + 2) % 4);
            flag = false;
        }
        // 旋转成功，锁定计数清零
        else {
            currentLockInterval = 0;
        }
        return flag;
    }

    @Override
    public void setSpeedUp(boolean isSpeedUp) {
        // TODO 实现方法
    }

    @Override
    public void straightDown() {
        // TODO 实现方法
    }

    @Override
    public void hold() {
        // TODO 实现方法
    }

    @Override
    public void progress() {
        if (gameStatus == GameStatus.PLAYING) {
            // 判断是否下落
            if (currentDownInterval == downInterval) {
                // 尝试下落
                boolean isDown = moveDown();
                // 若成功下落，下落、锁定、强制锁定计数均归零
                if (isDown) {
                    currentDownInterval = 0;
                    currentLockInterval = 0;
                    currentHardLockInterval = 0;
                }
                // 若无法下落
                else {
                    boolean isLock = false;
                    // 判断是否被强制锁定
                    if (currentHardLockInterval == hardLockInterval) {
                        isLock = true;
                    }
                    else {
                        currentHardLockInterval++;
                        // 判断是否被锁定
                        if (currentLockInterval == lockInterval) {
                            isLock = true;
                        }
                        else{
                            currentLockInterval++;
                        }
                    }
                    if (isLock) {
                        // 下落、锁定、强制锁定计数均归零
                        currentDownInterval = 0;
                        currentLockInterval = 0;
                        currentHardLockInterval = 0;

                        lockTile();

                        // 开始下落下一砖块
                        currentTile = nextTile();
                        x = 3;
                        y = 0;
                        direction = Direction.NORTH;
                        // 若冲突，游戏结束
                        if (hasConflict()) {
                            gameStatus = GameStatus.OVER;
                        }
                    }
                }
            }
            // 若不下落，则下落标识计数自增
            else {
                currentDownInterval++;
            }
        }
    }

    @Override
    public GameStatus getGameStatus() {
        // TODO 实现方法
        return gameStatus;
    }

    @Override
    public synchronized int[][] getGameDisplayMatrix() {
        // 新建显示矩阵
        int[][] displayMatrix = new int[gameBaseMatrix.length][];
        // 复制基矩阵
        for (int i = 0; i < gameBaseMatrix.length; i++) {
            displayMatrix[i] = Arrays.copyOf(gameBaseMatrix[i], gameBaseMatrix[i].length);
        }
        // 复制砖块矩阵
        int[][] tileMatrix = currentTile.getTileMatrix(direction);
        for (int i = 0; i < tileMatrix.length; i++) {
            for (int j = 0; j < tileMatrix[i].length; j++) {
                if (tileMatrix[i][j] != 0 && (y + i) < displayMatrix.length && (x + j) < displayMatrix[y + i].length) {
                    displayMatrix[y + i][x + j] = tileMatrix[i][j];
                }
            }
        }
        return displayMatrix;
    }

    @Override
    public List<Tile> getFollowingTileList() {
        // TODO 实现方法
        return null;
    }

    @Override
    public Tile getHoldTile() {
        // TODO 实现方法
        return null;
    }

    @Override
    public void setOnTileLockListener(OnTileLockListener listener) {
        // TODO 实现方法
    }


    /**
     *  判断砖块位置是否存在冲突
     * */
    private boolean hasConflict() {
        boolean flag = false;
        int[][] tileMatrix = currentTile.getTileMatrix(direction);
        for (int i = 0; i < tileMatrix.length; i++) {
            for (int j = 0; j < tileMatrix[i].length; j++) {
                // 砖格不为空时，进行检查。越过边界或冲突则检查不通过
                if (tileMatrix[i][j] != 0
                        && (x + j < 0 || x + j >= gameBaseMatrix[i].length
                        || y + i >= gameBaseMatrix.length
                        || gameBaseMatrix[y + i][x + j] != 0)) {
                    flag = true; break;
                }
            }
        }
        return flag;
    }

    /**
     *  向下移动
     * */
    private synchronized boolean moveDown() {
        boolean flag = true;
        // 下移
        y++;
        // 冲突，则还原动作
        if (hasConflict()) {
            y--;
            flag = false;
        }
        return flag;
    }

    /** 锁定方块 */
    private synchronized void lockTile() {
        // 复制砖块矩阵
        int[][] tileMatrix = currentTile.getTileMatrix(direction);
        for (int i = 0; i < tileMatrix.length; i++) {
            for (int j = 0; j < tileMatrix[i].length; j++) {
                if (tileMatrix[i][j] != 0 && (y + i) < gameBaseMatrix.length && (x + j) < gameBaseMatrix[y + i].length) {
                    gameBaseMatrix[y + i][x + j] = tileMatrix[i][j];
                }
            }
        }
    }

    /** 消除方块 */
    private synchronized void clearTile() {
        // TODO
    }

    /** 获取下一方块 */
    private Tile nextTile() {
        Tile tile = null;
        switch ((int) (Math.random() * 7)) {
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

    private class TetrisGameThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (gameStatus == GameStatus.PLAYING) {
                progress();
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public String toString() {
        StringBuilder strBd = new StringBuilder();

        int[][] gameDisplayMatrix = getGameDisplayMatrix();

        for (int[] aGameDisplayMatrix : gameDisplayMatrix) {
            strBd.append("\t");
            for (int anAGameDisplayMatrix : aGameDisplayMatrix) {
                strBd.append(anAGameDisplayMatrix).append(" ");
            }
            strBd.append("\n");
        }
        return strBd.toString();
    }
}
