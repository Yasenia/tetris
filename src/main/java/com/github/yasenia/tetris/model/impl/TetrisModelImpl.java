package com.github.yasenia.tetris.model.impl;

import com.github.yasenia.tetris.model.Direction;
import com.github.yasenia.tetris.model.TetrisModel;
import com.github.yasenia.tetris.model.Tile;
import com.github.yasenia.tetris.model.event.OnStatusChangedListener;
import com.github.yasenia.tetris.model.event.OnTileModifiedListener;
import com.github.yasenia.tetris.model.event.StatusChangedEvent;
import com.github.yasenia.tetris.model.event.TileModifiedEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Yasenia
 * @since 2015/1/17.
 */
public class TetrisModelImpl implements TetrisModel {

    /**
     *  原子时间单位（游戏主线程推进周期）
     * */
    public static final int ATOMIC_TIME = 20;

    /**
     *  砖块切换监听器列表
     * */
    private List<OnTileModifiedListener> onTileModifiedListenerList;

    /**
     *  游戏状态改变监听器列表
     * */
    private List<OnStatusChangedListener> onStatusChangedListenerList;

    /**
     *  速度控制常量（对应速度级别0——9）
     * */
    private static final int[] SPEED_CONST = {25, 20, 16, 12, 9, 6, 4, 2, 1, 0};

    /**
     *  敏感度控制常量（对应敏感度级别0——9）
     * */
    private static final int[] SENSITIVITY_CONST = {17, 14, 11, 9, 7, 5, 4, 3, 2, 1};


    /**
     *  游戏状态
     * */
    private GameStatus gameStatus;

    /**
     *  速度等级
     * */
    private int speedLevel;

    /**
     *  敏感度等级
     * */
    private int sensitivityLevel;

    /**
     *  游戏基矩阵（记录已有砖块信息）
     * */
    private int[][] gameBaseMatrix;

    /**
     *  当前砖块
     * */
    private Tile currentTile;

    /**
     *  当前砖块方向
     * */
    private Direction direction;

    /**
     *  当前砖块左上角位置
     * */
    private int x;
    private int y;

    /**
     *  后续砖块队列
     * */
    private List<Tile> tileList;

    /**
     *  hold区砖块
     * */
    private Tile holdTile;

    /**
     *  软降标识
     * */
    private boolean softDownFlag;

    /**
     *  hold计数器
     *  0：可以进行hold操作，当前方块进入hold区后自增为1
     *  1：不可进行hold操作，hold区方块开始下落后自增为2
     *  2：不可进行hold操作，当前方块下落后重置为0
     * */
    private int holdCounter;

    /**
     *  下落计数标识
     *  游戏每推进一次，下落计数自增1，当下落计数到达阀值，执行下落操作
     *  下落阀值由下落速度级别决定，速度级别越高，阀值越小
     * */
    private int downCounter;

    /**
     *  锁定计数标识
     *  当执行下落操作失败时，锁定计数自增1
     *  当下落成功或任意旋转、移动操作被执行成功时，锁定计数清零
     *  当锁定计数到达阀值，当前砖块被锁定，开始下落下一砖块
     *  锁定阀值由下落速度级别决定（锁定延时为下落一格时间两倍）
     * */
    private int lockCounter;

    /**
     *  强制锁定计数标识
     *  （该标识目前无效，BUG原因为部分旋转操作后导致下落判定成功，强锁计数清零）
     * */
    private int hardLockCounter;

    /**
     *  累积时间
     * */
    private Duration accumulateTime;

    /**
     *  游戏时间戳
     * */
    private Instant gameInstant;

    /**
     *  游戏得分
     * */
    private int score;

    /**
     *  游戏核心线程
     *  控制游戏推进
     * */
    private TetrisMainThread tetrisMainThread;

    /**
     *  左移状态标识
     * */
    private boolean moveLeftFlag;

    /**
     *  左移控制线程
     * */
    private TetrisMoveThread moveLeftThread;

    /**
     *  右移状态标识
     * */
    private boolean moveRightFlag;

    /**
     *  右移控制线程
     * */
    private TetrisMoveThread moveRightThread;

    /**
     *  构造方法
     * */
    public TetrisModelImpl() {
        // 初始化游戏基矩阵（比游戏高度高出四格不予显示，仅用于计算判定）
        gameBaseMatrix = new int[GAME_HEIGHT + 4][GAME_WIDTH];
        // 初始化游戏速度，默认5级
        speedLevel = 5;
        // 初始化游戏敏感度级别，默认7级
        sensitivityLevel = 7;
        // 设置游戏状态为准备状态
        changeGameStatus(GameStatus.PREPARE);
    }

    @Override
    public void changeGameStatus(GameStatus gameStatus) {
        GameStatus tempStatus = this.gameStatus;
        // 更改游戏状态
        this.gameStatus = gameStatus;

        if (null != gameStatus && tempStatus != gameStatus) {
            switch (gameStatus) {
                // 切换至准备状态
                case PREPARE:
                    // 停止已有游戏线程
                    if (null != tetrisMainThread) {
                        tetrisMainThread.callStop();
                    }
                    if (null != moveLeftThread) {
                        moveLeftThread.callStop();
                    }
                    if (null != moveRightThread) {
                        moveRightThread.callStop();
                    }
                    // 重置各状态标识
                    softDownFlag = false;
                    moveLeftFlag = false;
                    moveRightFlag = false;
                    // 各计数器清零
                    downCounter = 0;
                    lockCounter = 0;
                    hardLockCounter = 0;
                    holdCounter = 0;
                    // 清空基矩阵
                    for (int i = 0; i < gameBaseMatrix.length; i++) {
                        for (int j = 0; j < gameBaseMatrix[i].length; j++) {
                            gameBaseMatrix[i][j] = 0;
                        }
                    }
                    // 清空砖块列表
                    if (null != tileList) {
                        tileList.clear();
                    }
                    // 清空当前砖块
                    currentTile = null;
                    // 重置hold区
                    holdTile = null;
                    // 累积时间清零
                    accumulateTime = Duration.ZERO;
                    // 得分清零
                    score = 0;
                    break;
                // 切换至游戏中状态
                case PLAYING:
                    // 若上亿状态为准备状态，则开始下落第一块砖块准备
                    if (tempStatus == GameStatus.PREPARE) {
                        nextTile();
                    }
                    // 创建并启动游戏线程
                    tetrisMainThread = new TetrisMainThread();
                    tetrisMainThread.start();
                    // 记录当前时间戳
                    gameInstant = Instant.now();
                    break;
                // 切换至暂停状态
                case PAUSE:
                    // 停止已有游戏线程
                    if (null != tetrisMainThread) {
                        tetrisMainThread.callStop();
                    }
                    if (null != moveLeftThread) {
                        moveLeftThread.callStop();
                    }
                    if (null != moveRightThread) {
                        moveRightThread.callStop();
                    }
                    // 重置各状态标识
                    softDownFlag = false;
                    moveLeftFlag = false;
                    moveRightFlag = false;
                    // 更新累积时间
                    if (null != accumulateTime) {
                        accumulateTime = accumulateTime.plus(Duration.between(gameInstant, Instant.now()));
                    }
                    else {
                        accumulateTime = Duration.between(gameInstant, Instant.now());
                    }
                    break;
                case OVER:
                    // 停止已有游戏线程
                    if (null != tetrisMainThread) {
                        tetrisMainThread.callStop();
                    }
                    if (null != moveLeftThread) {
                        moveLeftThread.callStop();
                    }
                    if (null != moveRightThread) {
                        moveRightThread.callStop();
                    }
                    // 重置各状态标识
                    softDownFlag = false;
                    moveLeftFlag = false;
                    moveRightFlag = false;
                    // 各计数器清零
                    downCounter = 0;
                    lockCounter = 0;
                    hardLockCounter = 0;
                    holdCounter = 0;
                    // 更新累积时间
                    if (null != accumulateTime) {
                        accumulateTime = accumulateTime.plus(Duration.between(gameInstant, Instant.now()));
                    }
                    else {
                        accumulateTime = Duration.between(gameInstant, Instant.now());
                    }
                    break;
                default:
                    assert false;
            }

            // 触发游戏状态改变事件
            if (null != onStatusChangedListenerList) {
                StatusChangedEvent event = new StatusChangedEvent(this, gameStatus, tempStatus);
                onStatusChangedListenerList.forEach(l -> l.onStatusChanged(event));
            }
        }
    }

    @Override
    public void setSpeedLevel(int speedLevel) {
        speedLevel = Math.max(speedLevel, 0);
        speedLevel = Math.min(speedLevel, SPEED_CONST.length - 1);
        this.speedLevel = speedLevel;
    }

    @Override
    public int getSpeedLevel() {
        return speedLevel;
    }

    @Override
    public void setSensitivityLevel(int sensitivityLevel) {
        sensitivityLevel = Math.max(sensitivityLevel, 0);
        sensitivityLevel = Math.min(sensitivityLevel, SENSITIVITY_CONST.length - 1);
        this.sensitivityLevel = sensitivityLevel;
    }

    @Override
    public int getSensitivityLevel() {
        return sensitivityLevel;
    }

    @Override
    public synchronized boolean moveLeft() {
        boolean flag = false;
        if (gameStatus == GameStatus.PLAYING) {
            // 左移
            x--;
            // 冲突，则还原动作
            if (hasConflict()) {
                x++;
            }
            // 移动成功，锁定计数清零
            else {
                flag = true;
                lockCounter = 0;
            }
        }
        return flag;
    }

    @Override
    public void startMoveLeft() {
        if (gameStatus == GameStatus.PLAYING && !moveLeftFlag) {
            // 创建并开始左移进程
            moveLeftThread = new TetrisMoveThread(true);
            moveLeftThread.start();
            // 更改左移状态标识
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
            // 更改左移状态标识
            moveLeftFlag = false;
        }
    }

    @Override
    public synchronized boolean moveRight() {
        boolean flag = false;
        if (gameStatus == GameStatus.PLAYING) {
            // 右移
            x++;
            // 冲突，则还原动作
            if (hasConflict()) {
                x--;
            }
            // 移动成功，锁定计数清零
            else {
                lockCounter = 0;
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public void startMoveRight() {
        if (gameStatus == GameStatus.PLAYING && !moveRightFlag) {
            // 创建并开始右移进程
            moveRightThread = new TetrisMoveThread(false);
            moveRightThread.start();
            // 更改右移状态标识
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
            // 更改右移状态标识
            moveRightFlag = false;
        }
    }

    @Override
    public synchronized boolean spinPos() {
        boolean flag = false;
        if (gameStatus == GameStatus.PLAYING) {
            // 顺时针旋转90度
            direction = Direction.getDirection((direction.getNumber() + 1) % 4);

            // 旋转后进行自适应尝试，若自适应成功，则成功旋转，锁定计数清零
            if (adaptTile()) {
                lockCounter = 0;
                flag = true;
            }
            // 旋转失败，还原动作
            else {
                direction = Direction.getDirection((direction.getNumber() + 3) % 4);
            }
        }
        return flag;
    }

    @Override
    public synchronized boolean spinNeg() {
        boolean flag = false;
        if (gameStatus == GameStatus.PLAYING) {
            // 逆时针旋转90度
            direction = Direction.getDirection((direction.getNumber() + 3) % 4);

            // 旋转后进行自适应尝试，若自适应成功，则成功旋转，锁定计数清零
            if (adaptTile()) {
                lockCounter = 0;
                flag = true;
            }
            // 旋转失败，还原动作
            else {
                direction = Direction.getDirection((direction.getNumber() + 1) % 4);
            }
        }
        return flag;
    }

    @Override
    public synchronized boolean spinRev() {
        boolean flag = false;
        if (gameStatus == GameStatus.PLAYING) {
            // 旋转180度
            direction = Direction.getDirection((direction.getNumber() + 2) % 4);

            // 旋转后进行自适应尝试，若自适应成功，则成功旋转，锁定计数清零
            if (adaptTile()) {
                lockCounter = 0;
                flag = true;
            }
            // 旋转失败，还原动作
            else {
                direction = Direction.getDirection((direction.getNumber() + 2) % 4);
            }
        }
        return flag;
    }

    @Override
    public void startSoftDown() {
        softDownFlag = true;
    }

    @Override
    public void stopSoftDown() {
        softDownFlag = false;
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
        if (gameStatus == GameStatus.PLAYING && holdCounter == 0) {
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
            holdCounter++;
            // 开始掉落下一砖块
            nextTile();
        }
    }

    @Override
    public void progress() {
        if (gameStatus == GameStatus.PLAYING) {
            // 判断是否下落
            if (downCounter >= (softDownFlag ? SPEED_CONST[speedLevel] / 3 : SPEED_CONST[speedLevel])) {
                // 尝试下落
                boolean isDown = moveDown();
                // 若成功下落，下落、锁定、强制锁定计数均归零
                if (isDown) {
                    downCounter = 0;
                    lockCounter = 0;
                    hardLockCounter = 0;
                }
                // 若无法下落
                else {

                    boolean isLock = false;
                    // 判断是否被强制锁定
                    if (hardLockCounter >= SPEED_CONST[speedLevel] * 5) {
                        isLock = true;
                    }
                    else {
                        hardLockCounter++;
                        // 判断是否被锁定
                        if (lockCounter >= SPEED_CONST[speedLevel] * 2) {
                            isLock = true;
                        }
                        else{
                            lockCounter++;
                        }
                    }
                    if (isLock) {
                        // 下落、锁定、强制锁定计数均归零
                        downCounter = 0;
                        lockCounter = 0;
                        hardLockCounter = 0;

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
                downCounter++;
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
        if (gameStatus != GameStatus.PREPARE && null != tileList) {
            // 返回队列中前 FOLLOW_TILE_COUNTS 个砖块
            followingTileList = tileList.stream().limit(FOLLOW_TILE_COUNTS).collect(Collectors.toList());
        }
        return followingTileList;
    }

    @Override
    public Tile getHoldTile() {
        return gameStatus == GameStatus.PREPARE ? null : holdTile;
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
        boolean flag = onTileModifiedListenerList.stream().anyMatch(l -> l == listener);
        if (!flag) {
            onTileModifiedListenerList.add(listener);
        }
    }

    @Override
    public void removeOnTileModifiedListener(OnTileModifiedListener listener) {
        if (null != onTileModifiedListenerList) {
            onTileModifiedListenerList.remove(listener);
        }
    }

    @Override
    public void addOnStatusChangedListener(OnStatusChangedListener listener) {
        if (null == onStatusChangedListenerList) {
            onStatusChangedListenerList = new ArrayList<>();
        }
        // 如果监听队列中不包含该监听器，则添加该监听器进入监听队列
        boolean flag = onStatusChangedListenerList.stream().anyMatch(l -> l == listener);
        if (!flag) {
            onStatusChangedListenerList.add(listener);
        }
    }

    @Override
    public void removeOnStatusChangedListener(OnStatusChangedListener listener) {
        if (null != onStatusChangedListenerList) {
            onStatusChangedListenerList.remove(listener);
        }
    }

    /**
     *  判断砖块位置是否存在冲突
     *
     *  @return 是否存在冲突
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
     *
     * @return 是否适应成功
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

        // 调整失败，还原砖块位置
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

    /**
     *  锁定当前方块
     * */
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

    /**
     *  消除满行方块
     *
     *  @return 消除行数
     * */
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

        // 计算得分
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

    /**
     *  切换下一方块
     * */
    private synchronized void nextTile() {
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
            changeGameStatus(GameStatus.OVER);
        }
        // 无冲突，切换成功
        else {
            // 重设hold
            if (holdCounter == 1) {
                holdCounter++;
            }
            else if (holdCounter == 2) {
                holdCounter = 0;
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
        // 打印游戏显示矩阵（简单调试用）
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

    /**
     *  游戏核心线程
     * */
    private class TetrisMainThread extends Thread {
        /**
         *  循环标识
         * */
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

        /**
         *  请求线程停止
         * */
        public synchronized void callStop() {
            this.flag = false;
        }
    }

    /**
     *  水平移动控制线程
     * */
    private class TetrisMoveThread extends Thread {
        /**
         *  循环标识
         * */
        private boolean flag;

        /**
         *  循环计数
         * */
        private int loopCounts;

        /**
         *  移动方向标识
         *  true 左移
         *  false 右移
         * */
        private boolean toward;

        public TetrisMoveThread(boolean Toward) {
            this.toward = Toward;
            this.loopCounts = 0;
        }

        @Override
        public void run() {
            super.run();
            flag = true;
            while (flag) {
                // 移动
                if (toward) {
                    moveLeft();
                }
                else {
                    moveRight();
                }

                // 计算间隔时间
                // 移动间隔时间由敏感度级别决定
                // 第一次移动间隔时间为后续移动间隔时间两倍
                long sleepSpan = ATOMIC_TIME * SENSITIVITY_CONST[sensitivityLevel] * (loopCounts == 0 ? 2 : 1);
                try {
                    sleep(sleepSpan);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 循环计数自增
                loopCounts++;
            }
        }

        /**
         *  请求线程停止
         * */
        public synchronized void callStop() {
            flag = false;
        }
    }
}
