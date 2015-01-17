package com.github.yasenia.tetris.model.event;

import java.util.EventListener;

/**
 * @author Yasenia
 * @since 2015/1/17.
 */
public interface OnTileLockListener extends EventListener {

    /**
     *  砖块锁定
     * */
    void onTileLock(TileLockEvent event);

}
