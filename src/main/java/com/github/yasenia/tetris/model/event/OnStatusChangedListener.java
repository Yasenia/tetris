package com.github.yasenia.tetris.model.event;

import java.util.EventListener;

/**
 * @author Yasenia
 * @since 2015/1/17.
 */
public interface OnStatusChangedListener extends EventListener {

    /**
     *  游戏状态改变
     * */
    void onStatusChanged(TileModifiedEvent event);

}
