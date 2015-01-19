package com.github.yasenia.tetris.model.event;

import java.util.EventObject;

/**
 * @author Yasenia
 * @since 2015/1/17.
 */
public class TileModifiedEvent extends EventObject {

    public TileModifiedEvent(Object source) {
        super(source);
    }
}
