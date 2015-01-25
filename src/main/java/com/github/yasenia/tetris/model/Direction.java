package com.github.yasenia.tetris.model;

/**
 * @author Yasenia
 * @since 2015/1/19.
 */
public enum Direction {
    /**
     *  北
     * */
    NORTH(0),
    /**
     *  东
     * */
    EAST(1),
    /**
     *  南
     * */
    SOUTH(2),
    /**
     *  西
     * */
    WEST(3);

    /**
     *  方位编号
     * */
    private int number;

    /**
     *  构造方法（私有）
     * */
    private Direction(int number) {
        this.number = number;
    }

    /**
     *  按方向编号获取方向
     * */
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

    /**
     *  获取方向编号
     * */
    public int getNumber() {
        return number;
    }
}