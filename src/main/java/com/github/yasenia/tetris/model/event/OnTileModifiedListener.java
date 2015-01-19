package com.github.yasenia.tetris.model.event;

import java.util.EventListener;

/**
 * @author Yasenia
 * @since 2015/1/17.
 */
public interface OnTileModifiedListener extends EventListener {

    /**
     *  砖块改变
     * */
    void onTileModified(TileModifiedEvent event);

}
