package com.pineislet.swing.tetris.model.event;

import java.util.EventListener;

/**
 * Create on 2015/1/17
 *
 * @author Yasenia
 */
public interface OnTileModifiedListener extends EventListener {

    /**
     *  砖块改变
     * */
    void onTileModified(TileModifiedEvent event);

}
