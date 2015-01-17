package com.github.yasenia.tetris.model.event;

import java.util.EventObject;

/**
 * @author Yasenia
 * @since 2015/1/17.
 */
public class TileLockEvent extends EventObject {

    public TileLockEvent(Object source) {
        super(source);
    }
}
