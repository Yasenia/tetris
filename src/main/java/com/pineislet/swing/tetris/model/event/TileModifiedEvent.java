package com.pineislet.swing.tetris.model.event;

import java.util.EventObject;

/**
 * Create on 2015/1/17
 *
 * @author Yasenia
 */
public class TileModifiedEvent extends EventObject {

    public TileModifiedEvent(Object source) {
        super(source);
    }
}
