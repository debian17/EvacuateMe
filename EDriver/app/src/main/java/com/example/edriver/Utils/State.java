package com.example.edriver.Utils;

/**
 * Created by Андрей Кравченко on 23-Apr-17.
 */

public class State {
    private static State state = new State();
    private State(){}
    public static State getInstance(){
        return state;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status;

}
