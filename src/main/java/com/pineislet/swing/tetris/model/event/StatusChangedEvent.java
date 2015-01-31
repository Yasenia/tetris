package com.pineislet.swing.tetris.model.event;

import com.pineislet.swing.tetris.model.TetrisModel;

import java.util.EventObject;

/**
 * Create on 2015/1/17
 *
 * @author Yasenia
 */
public class StatusChangedEvent extends EventObject {

    private TetrisModel.GameStatus currentStatus;
    private TetrisModel.GameStatus lastStatus;

    public StatusChangedEvent(Object source, TetrisModel.GameStatus currentStatus, TetrisModel.GameStatus lastStatus) {
        super(source);
        this.currentStatus = currentStatus;
        this.lastStatus = lastStatus;
    }

    public TetrisModel.GameStatus getCurrentStatus() {
        return currentStatus;
    }

    public TetrisModel.GameStatus getLastStatus() {
        return lastStatus;
    }
}
