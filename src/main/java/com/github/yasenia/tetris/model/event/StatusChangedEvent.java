package com.github.yasenia.tetris.model.event;

import com.github.yasenia.tetris.model.TetrisModel;

import java.util.EventObject;

/**
 * @author Yasenia
 * @since 2015/1/17.
 */
public class StatusChangedEvent extends EventObject {

    private TetrisModel.GameStatus currentStatus;
    private TetrisModel.GameStatus lastStatus;

    public StatusChangedEvent(Object source, TetrisModel.GameStatus currentStatus, TetrisModel.GameStatus lastStatus) {
        super(source);
        this.currentStatus = currentStatus;
        this.lastStatus = lastStatus;
    }
}
