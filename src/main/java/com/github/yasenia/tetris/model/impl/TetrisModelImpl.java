package com.github.yasenia.tetris.model.impl;

import com.github.yasenia.tetris.model.Direction;
import com.github.yasenia.tetris.model.TetrisModel;
import com.github.yasenia.tetris.model.Tile;
import com.github.yasenia.tetris.model.event.OnTileModifiedListener;
import com.github.yasenia.tetris.model.event.TileModifiedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Yasenia
 * @since 2015/1/17.
 */
public class TetrisModelImpl implements TetrisModel {
    private static final Logger logger = LogManager.getLogger(TetrisModelImpl.class.getName());

    public static final int ATOMIC_TIME = 20;

    public static final int GAME_HEIGHT = 20;
    public static final int GAME_WIDTH = 10;
    public static final int FOLLOW_TILE_COUNTS = 6;

    private List<OnTileModifiedListener> onTileModifiedListenerList;

    /** 速度常量 */
    private static final int[] SPEED_CONST = {25, 20, 16, 12, 9, 6, 4, 2, 1, 0};

    /** 敏感度常量 */
    private static final int[] SENSITIVITY_CONST = {17, 14, 11, 9, 7, 5, 4, 3, 2, 1};

    /** 速度等级 */
    private int speedLevel;

    /** 敏感度等级 */
    private int sensitivityLevel;

    /** 软降标识 */
    private boolean onSoftDown;

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

    /** hold区砖块 */
    private Tile holdTile;

    /** 是否可以进行hold操作 */
    private int holdFlag;

    /** 下落计数标识 */
    private int downFlag;

    /** 锁定计数标识 */
    private int lockFlag;

    /** 强制锁定计数标识 */
    private int hardLockFlag;

    /** 累积时间 */
    private Duration accumulateTime;

    /** 游戏时间戳 */
    private Instant gameInstant;

    /** 游戏得分 */
    private int score;

    /** 游戏核心线程 */
    private TetrisMainThread tetrisMainThread;

    /** 左移控制线程 */
    private TetrisMoveThread moveLeftThread;
    private boolean moveLeftFlag;

    /** 右移控制线程 */
    private TetrisMoveThread moveRightThread;
    private boolean moveRightFlag;

    public TetrisModelImpl() {
        gameBaseMatrix = new int[GAME_HEIGHT + 4][GAME_WIDTH];
        speedLevel = 5;
        sensitivityLevel = 7;
        gameStatus = GameStatus.PREPARE;
    }

    @Override
    public void reset() {
        if (null != tetrisMainThread) {
            tetrisMainThread.callStop();
        }
        // 创建游戏线程
        tetrisMainThread = new TetrisMainThread();
        // 累积时间清零
        accumulateTime = Duration.ZERO;
        // 得分清零
        score = 0;

        // 更改游戏状态
        gameStatus = GameStatus.PREPARE;
    }

    @Override
    public void start() {
        if (gameStatus == GameStatus.PREPARE) {
            // 设置当前砖块
            nextTile();
            // 启动游戏线程
            tetrisMainThread.start();
            // 记录当前时间戳
            gameInstant = Instant.now();

            // 更改游戏状态
            gameStatus = GameStatus.PLAYING;
        }
    }

    @Override
    public void pause() {
        if (gameStatus == GameStatus.PLAYING) {
            // 终止游戏线程
            if (null != tetrisMainThread) {
                tetrisMainThread.callStop();
            }
            // 更新累积时间
            accumulateTime = accumulateTime.plus(Duration.between(gameInstant, Instant.now()));

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
            // 记录当前时间戳
            gameInstant = Instant.now();

            // 更改游戏状态
            gameStatus = GameStatus.PLAYING;
        }
    }

    @Override
    public void setSpeedLevel(int speedLevel) {
        speedLevel = Math.max(speedLevel, 0);
        speedLevel = Math.min(speedLevel, SPEED_CONST.length - 1);
        this.speedLevel = speedLevel;
    }

    @Override
    public void setSensitivityLevel(int sensitivityLevel) {
        sensitivityLevel = Math.max(sensitivityLevel, 0);
        sensitivityLevel = Math.min(sensitivityLevel, SENSITIVITY_CONST.length - 1);
        this.sensitivityLevel = sensitivityLevel;
    }

    @Override
    public synchronized boolean moveLeft() {
        boolean flag = true;
        if (gameStatus == GameStatus.PLAYING) {
            // 左移
            x--;
            // 冲突，则还原动作
            if (hasConflict()) {
                x++;
                flag = false;
            }
            // 移动成功，锁定计数清零
            else {
                lockFlag = 0;
            }
        }
        else {
            flag = false;
        }
        return flag;
    }

    @Override
    public void startMoveLeft() {
        if (gameStatus == GameStatus.PLAYING && !moveLeftFlag) {
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
                moveLeftThread.callStop();
            }
            moveLeftFlag = false;
        }
    }

    @Override
    public synchronized boolean moveRight() {
        boolean flag = true;
        if (gameStatus == GameStatus.PLAYING) {
            // 右移
            x++;
            // 冲突，则还原动作
            if (hasConflict()) {
                x--;
                flag = false;
            }
            // 移动成功，锁定计数清零
            else {
                lockFlag = 0;
            }
        }
        else {
            flag = false;
        }
        return flag;
    }

    @Override
    public void startMoveRight() {
        if (gameStatus == GameStatus.PLAYING && !moveRightFlag) {
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
                moveRightThread.callStop();
            }
            moveRightFlag = false;
        }
    }

    @Override
    public synchronized boolean spinPos() {
        boolean flag;
        if (gameStatus == GameStatus.PLAYING) {
            // 顺时针旋转90度
            direction = Direction.getDirection((direction.getNumber() + 1) % 4);

            flag = adaptTile();
            // 旋转成功，锁定计数清零
            if (flag) {
                lockFlag = 0;
            }
            // 旋转失败，还原动作
            else {
                direction = Direction.getDirection((direction.getNumber() + 3) % 4);
            }
        }
        else {
            flag = false;
        }
        return flag;
    }

    @Override
    public synchronized boolean spinNeg() {
        boolean flag;
        if (gameStatus == GameStatus.PLAYING) {
            // 逆时针旋转90度
            direction = Direction.getDirection((direction.getNumber() + 3) % 4);

            flag = adaptTile();
            // 旋转成功，锁定计数清零
            if (flag) {
                lockFlag = 0;
            }
            // 旋转失败，还原动作
            else {
                direction = Direction.getDirection((direction.getNumber() + 1) % 4);
            }
        }
        else {
            flag = false;
        }
        return flag;
    }

    @Override
    public synchronized boolean spinRev() {
        boolean flag;
        if (gameStatus == GameStatus.PLAYING) {
            // 旋转180度
            direction = Direction.getDirection((direction.getNumber() + 2) % 4);

            flag = adaptTile();
            // 旋转成功，锁定计数清零
            if (flag) {
                lockFlag = 0;
            }
            // 旋转失败，还原动作
            else {
                direction = Direction.getDirection((direction.getNumber() + 2) % 4);
            }
        }
        else {
            flag = false;
        }
        return flag;
    }

    @Override
    public void startSoftDown() {
        onSoftDown = true;
    }

    @Override
    public void stopSoftDown() {
        onSoftDown = false;
    }

    @Override
    public void hardDown() {
        if (gameStatus == GameStatus.PLAYING) {
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
    }

    @Override
    public void hold() {
        if (gameStatus == GameStatus.PLAYING && holdFlag == 0) {
            // 若hold区为空，将当前下落方块置入hold区
            if (null == holdTile) {
                holdTile = currentTile;
            }
            // 若hold区非空，将当前下落方块置入hold区,将hold区砖块置于砖块队列列首
            else {
                Tile temp = holdTile;
                holdTile = currentTile;
                tileList.add(0, temp);
            }
            // 更改hold标识位
            holdFlag++;
            // 开始掉落下一砖块
            nextTile();
        }
    }

    @Override
    public void progress() {
        if (gameStatus == GameStatus.PLAYING) {
            // 判断是否下落
            if (downFlag >= (onSoftDown ? SPEED_CONST[speedLevel] / 3 : SPEED_CONST[speedLevel])) {
                // 尝试下落
                boolean isDown = moveDown();
                // 若成功下落，下落、锁定、强制锁定计数均归零
                if (isDown) {
                    logger.debug("downFlag: " + downFlag);
                    downFlag = 0;
                    lockFlag = 0;
                    hardLockFlag = 0;
                }
                // 若无法下落
                else {

                    boolean isLock = false;
                    // 判断是否被强制锁定
                    if (hardLockFlag >= SPEED_CONST[speedLevel] * 5) {
                        isLock = true;
                    }
                    else {
                        hardLockFlag++;
                        // 判断是否被锁定
                        if (lockFlag >= SPEED_CONST[speedLevel] * 2) {
                            isLock = true;
                        }
                        else{
                            lockFlag++;
                        }
                    }
                    if (isLock) {
                        // 下落、锁定、强制锁定计数均归零
                        downFlag = 0;
                        lockFlag = 0;
                        hardLockFlag = 0;

                        // 锁定砖块
                        lockTile();
                        // 消除满行
                        clearTile();
                        // 开始下落下一砖块
                        nextTile();
                    }
                }
            }
            // 若不下落，则下落标识计数自增
            else {
                downFlag++;
            }
        }
    }

    @Override
    public GameStatus getGameStatus() {
        return gameStatus;
    }

    @Override
    public synchronized int[][] getGameDisplayMatrix() {
        // 新建显示矩阵
        int[][] displayMatrix = new int[gameBaseMatrix.length - 4][];
        // 复制基矩阵（不包括前四行）
        for (int i = 4; i < gameBaseMatrix.length; i++) {
            displayMatrix[i - 4] = Arrays.copyOf(gameBaseMatrix[i], gameBaseMatrix[i].length);
        }

        // 复制投影矩阵
        if (null != currentTile) {
            // 记录砖块原始高度
            int tempY = y;
            // 计算投影位置
            // 下落到底
            while (true) {
                y++;
                if (hasConflict()) {
                    y--;
                    break;
                }
            }
            int[][] tileMatrix = currentTile.getTileMatrix(direction);
            for (int i = 0; i < tileMatrix.length; i++) {
                for (int j = 0; j < tileMatrix[i].length; j++) {
                    if (tileMatrix[i][j] != 0
                            && (y - 4 + i) >= 0 && (y - 4 + i) < displayMatrix.length
                            && (x + j) >= 0 && (x + j) < displayMatrix[y - 4 + i].length) {
                        displayMatrix[y - 4 + i][x + j] = -tileMatrix[i][j];
                    }
                }
            }
            // 还原砖块位置
            y = tempY;
        }

        // 复制砖块矩阵
        if (null != currentTile) {
            int[][] tileMatrix = currentTile.getTileMatrix(direction);
            for (int i = 0; i < tileMatrix.length; i++) {
                for (int j = 0; j < tileMatrix[i].length; j++) {
                    if (tileMatrix[i][j] != 0
                            && (y - 4 + i) >= 0 && (y - 4 + i) < displayMatrix.length
                            && (x + j) >= 0 && (x + j) < displayMatrix[y - 4 + i].length) {
                        displayMatrix[y - 4 + i][x + j] = tileMatrix[i][j];
                    }
                }
            }
        }

        return displayMatrix;
    }

    @Override
    public List<Tile> getFollowingTileList() {
        List<Tile> followingTileList = null;
        if (gameStatus != GameStatus.PREPARE) {
            // 确定最大数目
            int tileCounts = Math.min(FOLLOW_TILE_COUNTS, 7);
            // 返回队列中前 tileCounts 个砖块
            if (null != tileList) {
                followingTileList = tileList.stream().limit(tileCounts).collect(Collectors.toList());
            }
        }
        return followingTileList;
    }

    @Override
    public Tile getHoldTile() {
        if (gameStatus != GameStatus.PREPARE) {
            return holdTile;
        }
        else {
            return null;
        }
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public Duration getTime() {
        Duration time = Duration.ZERO;
        switch (gameStatus) {
            case PLAYING:
                time = accumulateTime.plus(Duration.between(gameInstant, Instant.now()));
                break;
            case PAUSE: case OVER:
                time = accumulateTime;
                break;
        }

        return time;
    }

    @Override
    public void addOnTileModifiedListener(OnTileModifiedListener listener) {
        if (null == onTileModifiedListenerList) {
            onTileModifiedListenerList = new ArrayList<>();
        }
        // 如果监听队列中不包含该监听器，则添加该监听器进入监听队列
        if (onTileModifiedListenerList.stream().filter(l -> l == listener).count() == 0) {
            onTileModifiedListenerList.add(listener);
        }
    }

    @Override
    public void removeOnTileModifiedListener(OnTileModifiedListener listener) {
        if (null != onTileModifiedListenerList) {
            onTileModifiedListenerList.remove(listener);
        }
    }

    /**
     *  判断砖块位置是否存在冲突
     * */
    private synchronized boolean hasConflict() {
        boolean flag = false;
        int[][] tileMatrix = currentTile.getTileMatrix(direction);
        for (int i = 0; i < tileMatrix.length; i++) {
            for (int j = 0; j < tileMatrix[i].length; j++) {
                if ((tileMatrix[i][j] != 0)                                     // 砖块不为空
                        &&
                        ((x + j < 0 || x + j >= gameBaseMatrix[i].length)       // 越过左右边界
                        || (y + i >= gameBaseMatrix.length)                     // 越过下边界
                        || (y + i >= 0 && gameBaseMatrix[y + i][x + j] != 0))   // 与已有砖块冲突
                        ) {
                    flag = true; break;
                }
            }
        }
        return flag;
    }

    /**
     * 调整砖块位置以适应周围环境
     * */
    private synchronized boolean adaptTile() {
        boolean flag = false;

        // 记录砖块原始位置
        int tempX = x;
        int tempY = y;

        // 设定砖块调整范围
        int[] xRange;
        int[] yRange;
        // 对于 I 型砖块，最大调整范围为2格
        if (currentTile == Tile.I) {
            xRange = new int[]{x, x - 1, x + 1, x - 2, x + 2};
            yRange = new int[]{y, y - 1, y + 1, y - 2, y + 2};
        }
        // 对于其它砖块，最大调整范围为1格
        else {
            xRange = new int[]{x, x - 1, x + 1};
            yRange = new int[]{y, y - 1, y + 1};
        }
        // 调整砖块位置
        for (int x : xRange) {
            for (int y : yRange) {
                this.x = x;
                this.y = y;
                // 调整成功，跳出循环
                if (!hasConflict()) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                break;
            }
        }

        if (!flag) {
            x = tempX;
            y = tempY;
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
                if (tileMatrix[i][j] != 0
                        && y + i >= 0 && y + i < gameBaseMatrix.length
                        && x + j >= 0 && x + j < gameBaseMatrix[y + i].length) {
                    gameBaseMatrix[y + i][x + j] = tileMatrix[i][j];
                }
            }
        }
    }

    /** 消除方块 */
    private synchronized int clearTile() {
        int lineCounts = 0;

        for (int i = gameBaseMatrix.length - 1; i >= 0; i--) {
            boolean isFullRow = true;
            for (int j = 0; j < gameBaseMatrix[i].length; j++) {
                if (gameBaseMatrix[i][j] == 0) {
                    isFullRow = false;
                }
            }
            // 若为满行，消除该行，i 自增，lineCounts 自增
            if (isFullRow) {
                System.arraycopy(gameBaseMatrix, 0, gameBaseMatrix, 1, i);
                gameBaseMatrix[0] = new int[gameBaseMatrix[0].length];
                i++;
                lineCounts++;
            }
        }

        switch (lineCounts) {
            case 1:
                score += 100;
                break;
            case 2:
                score += 300;
                break;
            case 3:
                score += 500;
                break;
            case 4:
                score += 1000;
                break;
        }
        return lineCounts;
    }

    /** 获取下一方块 */
    private void nextTile() {
        if (null == tileList) {
            tileList = new ArrayList<>();
        }
        // 填充砖块队列至两组
        while (tileList.size() <= 7) {
            List<Tile> tileBag = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                tileBag.add(Tile.getTile(i));
            }
            // 乱序
            Collections.shuffle(tileBag);
            tileList.addAll(tileBag);
        }
        // 设置队首砖块为当前砖块
        currentTile = tileList.remove(0);
        // 设置方块初始位置
        x = 3; y = 0;
        direction = Direction.NORTH;

        // 存在冲突，游戏结束
        if (hasConflict()) {
            // 更新累积时间
            accumulateTime = accumulateTime.plus(Duration.between(gameInstant, Instant.now()));

            // 更改游戏状态
            gameStatus = GameStatus.OVER;
        }
        // 无冲突，切换成功
        else {
            // 重设hold
            if (holdFlag == 1) {
                holdFlag++;
            }
            else if (holdFlag == 2) {
                holdFlag = 0;
            }
        }

        // 触发砖块切换事件
        onTileModifiedListenerList.forEach(listener -> {
            TileModifiedEvent event = new TileModifiedEvent(this);
            listener.onTileModified(event);
        });
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

        public synchronized void callStop() {
            this.flag = false;
        }
    }

    private class TetrisMoveThread extends Thread {
        private boolean flag;

        private int loopCounts;

        // 移动方向标识
        private boolean Toward;

        public TetrisMoveThread(boolean Toward) {
            this.Toward = Toward;
            this.loopCounts = 0;
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

                long sleepSpan = ATOMIC_TIME * SENSITIVITY_CONST[sensitivityLevel] * (loopCounts == 0 ? 2 : 1);
                try {
                    sleep(sleepSpan);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                loopCounts++;
            }
        }

        public synchronized void callStop() {
            flag = false;
        }
    }
}
