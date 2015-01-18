package com.github.yasenia.tetris.model.impl;

import com.github.yasenia.tetris.model.TetrisConfig;
import com.github.yasenia.tetris.model.TetrisModel;
import com.github.yasenia.tetris.model.event.OnTileLockListener;

import java.util.*;

/**
 * @author Yasenia
 * @since 2015/1/17.
 */
public class TetrisModelImpl implements TetrisModel {
    public static final int ATOMIC_TIME = 20;

    /** 速度级别 */
    public static final int[] SPEED_LEVEL = {25, 20, 16, 12, 9, 6, 4, 2, 1, 0};
    /** 敏感度级别 */
    public static final double[] SENSITIVITY_LEVEL = {20, 17, 14, 11, 9, 7, 5, 4, 3, 2};
    /** 锁定时长 */

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
    private List<Tile> tileList;

    /** 下落时间间隔，数值越小，下落速度越快 */
    private int downInterval = 15;
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

    /** 游戏核心线程 */
    private TetrisMainThread tetrisMainThread;

    /** 左移控制线程 */
    private TetrisMoveThread moveLeftThread;
    private boolean moveLeftFlag;
    /** 右移控制线程 */
    private TetrisMoveThread moveRightThread;
    private boolean moveRightFlag;

    public TetrisModelImpl() {
        config = TetrisConfig.getDefaultConfig();
        gameStatus = GameStatus.PREPARE;
        gameBaseMatrix = new int[config.getHeight()][config.getWidth()];
        tileList = new ArrayList<Tile>();
    }

    @Override
    public void start() {
        if (gameStatus == GameStatus.PREPARE) {
            // 设置当前砖块
            nextTile();
            // 启动游戏线程
            tetrisMainThread = new TetrisMainThread();
            tetrisMainThread.start();

            // 更改游戏状态
            gameStatus = GameStatus.PLAYING;
        }
    }

    @Override
    public void pause() {
        if (gameStatus == GameStatus.PLAYING) {
            // 终止游戏线程
            if (null != tetrisMainThread) {
                tetrisMainThread.setFlag(false);
            }

            // 更改游戏状态
            gameStatus = GameStatus.PAUSE;
        }
    }

    @Override
    public void resume() {
        if (gameStatus == GameStatus.PAUSE) {
            // 启动游戏线程
            tetrisMainThread = new TetrisMainThread();
            tetrisMainThread.start();

            // 更改游戏状态
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
    public void startMoveLeft() {
        if (!moveLeftFlag) {
            // 创建并开始左移进程
            moveLeftThread = new TetrisMoveThread(true);
            moveLeftThread.start();
            moveLeftFlag = true;
        }
    }

    @Override
    public void stopMoveLeft() {
        if (moveLeftFlag) {
            // 终止左移进程
            if (null != moveLeftThread) {
                moveLeftThread.setFlag(false);
            }
            moveLeftFlag = false;
        }
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
    public void startMoveRight() {
        if (!moveRightFlag) {
            // 创建并开始右移进程
            moveRightThread = new TetrisMoveThread(false);
            moveRightThread.start();
            moveRightFlag = true;
        }
    }

    @Override
    public void stopMoveRight() {
        if (moveRightFlag) {
            // 终止右移进程
            if (null != moveRightThread) {
                moveRightThread.setFlag(false);
            }
            moveRightFlag = false;
        }
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
    public void hardDown() {
        // 直落到底
        boolean flag = true;
        while (flag) {
            flag = moveDown();
        }
        // 锁定砖块
        lockTile();
        // 消除满行
        clearTile();
        // 开始下一砖块
        nextTile();
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
                        clearTile();

                        // 开始下落下一砖块
                        nextTile();
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
        if (null != currentTile) {
            int[][] tileMatrix = currentTile.getTileMatrix(direction);
            for (int i = 0; i < tileMatrix.length; i++) {
                for (int j = 0; j < tileMatrix[i].length; j++) {
                    if (tileMatrix[i][j] != 0 && (y + i) < displayMatrix.length && (x + j) < displayMatrix[y + i].length) {
                        displayMatrix[y + i][x + j] = tileMatrix[i][j];
                    }
                }
            }
        }
        return displayMatrix;
    }

    @Override
    public List<Tile> getFollowingTileList() {
        List<Tile> tileList = new ArrayList<Tile>();
        int lastIndex = Math.min(config.getFollowingTileCounts(), this.tileList.size());
        for (int i = 0; i < lastIndex; i++) {
            tileList.add(this.tileList.get(i));
        }
        return tileList;
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
        for (int i = gameBaseMatrix.length - 1; i >= 0; i--) {
            boolean isFullRow = true;
            for (int j = 0; j < gameBaseMatrix[i].length; j++) {
                if (gameBaseMatrix[i][j] == 0) {
                    isFullRow = false;
                }
            }
            // 若为满行，消除该行
            if (isFullRow) {
                System.arraycopy(gameBaseMatrix, 0, gameBaseMatrix, 1, i);
                gameBaseMatrix[0] = new int[gameBaseMatrix[0].length];
            }
        }
    }

    /** 获取下一方块 */
    private void nextTile() {
        // 填充砖块队列至两组
        while (tileList.size() <= 7) {
            List<Tile> tileBag = new ArrayList<Tile>();
            for (int i = 0; i < 7; i++) {
                tileBag.add(Tile.getTile(i));
            }
            // 乱序
            Collections.shuffle(tileBag);
            tileList.addAll(tileBag);
        }
        // 设置队首砖块为当前砖块
        currentTile = tileList.remove(0);
        x = 3; y = 0;
        direction = Direction.NORTH;
        // 若冲突，游戏结束
        if (hasConflict()) {
            gameStatus = GameStatus.OVER;
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


    private class TetrisMainThread extends Thread {
        private boolean flag;

        @Override
        public void run() {
            super.run();
            flag = true;
            while (flag) {
                progress();
                try {
                    sleep(ATOMIC_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }
    }

    private class TetrisMoveThread extends Thread {
        private boolean flag;

        // 移动方向标识
        private boolean Toward;

        public TetrisMoveThread(boolean Toward) {
            this.Toward = Toward;
        }

        @Override
        public void run() {
            super.run();
            flag = true;
            while (flag) {
                if (Toward) {
                    moveLeft();
                }
                else {
                    moveRight();
                }
                try {
                    sleep(60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }
    }
}
