package com.github.yasenia.tetris.model;

/**
 * @author Yasenia
 * @since 2015/1/17.
 */
public class TetrisConfig {

    /** 游戏矩阵宽度 */
    private int width;

    /** 游戏矩阵高度 */
    private int height;

    /** 后续砖块数目 */
    private int followingTileCounts;

    /** 构造方法 */
    public TetrisConfig() {
        this.width = 10;
        this.height = 20;
        this.followingTileCounts = 5;
    }

    public static TetrisConfig getDefaultConfig() {
        return new TetrisConfig();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFollowingTileCounts() {
        return followingTileCounts;
    }

    public void setFollowingTileCounts(int followingTileCounts) {
        this.followingTileCounts = followingTileCounts;
    }
}
