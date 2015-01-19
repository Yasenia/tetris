package com.github.yasenia.tetris.model;

/**
 * @author Yasenia
 * @since 2015/1/19.
 */
public enum Direction {
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