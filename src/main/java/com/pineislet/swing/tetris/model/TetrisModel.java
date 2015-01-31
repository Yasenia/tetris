package com.pineislet.swing.tetris.model;

import com.pineislet.swing.tetris.model.event.OnStatusChangedListener;
import com.pineislet.swing.tetris.model.event.OnTileModifiedListener;

import java.time.Duration;
import java.util.List;

/**
 * Create on 2015/1/17
 *
 * @author Yasenia
 */
public interface TetrisModel {

    /**
     *  游戏面板宽度、高度
     * */
    public static final int GAME_WIDTH = 10;
    public static final int GAME_HEIGHT = 20;

    /**
     *  获取后续方块数目
     * */
    public static final int FOLLOW_TILE_COUNTS = 5;

    /**
     *  切换游戏状态
     * */
    void changeGameStatus(GameStatus gameStatus);

    /**
     *  设置速度级别
     *
     * @param speedLevel 速度级别（0——9级，级别越高，下落速度越快）
     * */
    void setSpeedLevel(int speedLevel);

    /**
     *  设置速度级别
     *
     * @return 速度级别
     * */
    int getSpeedLevel();

    /**
     *  设置敏感度级别
     *
     *  @param sensitivityLevel 敏感度级别（0——9级，级别越高，左右移动越敏感）
     * */
    void setSensitivityLevel(int sensitivityLevel);

    /**
     *  获取敏感度级别
     *
     *  @return 敏感度级别
     * */
    int getSensitivityLevel();

    /**
     *  左移
     *
     * @return 操作是否成功
     * */
    boolean moveLeft();

    /**
     *  开始持续左移
     * */
    void startMoveLeft();

    /**
     *  结束持续左移
     * */
    void stopMoveLeft();

    /**
     *  右移
     *
     *  @return 操作是否成功
     * */
    boolean moveRight();

    /**
     *  开始持续右移
     * */
    void startMoveRight();

    /**
     *  结束持续右移
     * */
    void stopMoveRight();

    /**
     *  顺时针旋转90度
     *
     *  return 操作是否成功
     * */
    boolean spinPos();

    /**
     *  逆时针旋转90度
     *  @return 操作是否成功
     * */
    boolean spinNeg();

    /**
     * 旋转180度
     *
     * @return 操作是否成功
     * */
    boolean spinRev();

    /**
     *  开始软降（下落速度提高3倍）
     * */
    void startSoftDown();

    /**
     *  停止软降
     * */
    void stopSoftDown();

    /**
     *  硬降（直接下落到底）
     * */
    void hardDown();

    /**
     *  hold当前方块
     * */
    void hold();

    /**
     *  游戏推进
     * */
    void progress();

    /**
     *  获取游戏状态
     *
     *  @return 当前游戏状态
     * */
    GameStatus getGameStatus();

    /**
     *  获取游戏显示矩阵
     *
     *  @return 获取游戏显示矩阵
     * */
    int[][] getGameDisplayMatrix();

    /**
     *  获取后续砖块列表
     *
     *  @return 后续砖块列表
     * */
    List<Tile> getFollowingTileList();

    /**
     *  获取hold区方块
     *
     *  @return hold区砖块
     * */
    Tile getHoldTile();

    /**
     *  获取游戏分数
     *
     *  @return 游戏分数
     * */
    int getScore();

    /**
     *  获取游戏时间
     *
     *  @return 游戏时间
     * */
    Duration getTime();

    /**
     *  添加砖块锁定监听器
     *
     *  @param listener 监听器
     * */
    void addOnTileModifiedListener(OnTileModifiedListener listener);

    /**
     *  移除砖块锁定监听器
     *
     *  @param listener 监听器
     * */
    void removeOnTileModifiedListener(OnTileModifiedListener listener);

    /**
     *  添加游戏状态改变监听器
     *
     *  @param listener 监听器
     * */
    void addOnStatusChangedListener(OnStatusChangedListener listener);

    /**
     *  移除游戏状态改变监听器
     *
     *  @param listener 监听器
     * */
    void removeOnStatusChangedListener(OnStatusChangedListener listener);

    /**
     *  枚举 游戏状态
     * */
    enum GameStatus {
        /**
         *  准备
         * */
        PREPARE,

        /**
         *  进行中
         * */
        PLAYING,

        /**
         *  暂停
         * */
        PAUSE,

        /**
         *  结束
         * */
        OVER
    }
}
