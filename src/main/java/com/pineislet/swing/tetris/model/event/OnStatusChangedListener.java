package com.pineislet.swing.tetris.model.event;

import java.util.EventListener;

/**
 * Create on 2015/1/17
 *
 * @author Yasenia
 */
public interface OnStatusChangedListener extends EventListener {

    /**
     *  游戏状态改变
     * */
    void onStatusChanged(StatusChangedEvent event);

}
