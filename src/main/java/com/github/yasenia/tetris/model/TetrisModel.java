package com.github.yasenia.tetris.model;

import com.github.yasenia.tetris.model.event.OnStatusChangedListener;
import com.github.yasenia.tetris.model.event.OnTileModifiedListener;

import java.time.Duration;
import java.util.List;

/**
 * @author Yasenia
 * @since 2015/1/17.
 */
public interface TetrisModel {

    void changeGameStatus(GameStatus gameStatus);

    /** 设置速度级别 */
    void setSpeedLevel(int speedLevel);

    /** 设置敏感度级别 */
    void setSensitivityLevel(int sensitivityLevel);

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

    /** 开始软降 */
    void startSoftDown();

    /** 停止软降 */
    void stopSoftDown();

    /** 硬降 */
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

    /** 获取游戏分数 */
    int getScore();

    /** 获取游戏时间 */
    Duration getTime();

    /** 添加砖块锁定监听器 */
    void addOnTileModifiedListener(OnTileModifiedListener listener);

    /** 移除砖块锁定监听器 */
    void removeOnTileModifiedListener(OnTileModifiedListener listener);

    /** 添加游戏状态改变监听器 */
    void addOnStatusChangedListener(OnStatusChangedListener listener);

    /** 移除游戏状态改变监听器 */
    void removeOnStatusChangedListener(OnStatusChangedListener listener);

    /**
     *  枚举 游戏状态
     * */
    enum GameStatus {
        PREPARE, PLAYING, PAUSE, OVER
    }
}
