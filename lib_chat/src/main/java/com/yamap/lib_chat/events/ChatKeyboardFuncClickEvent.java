package com.yamap.lib_chat.events;


public class ChatKeyboardFuncClickEvent {

    private int position;

    public ChatKeyboardFuncClickEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

}
