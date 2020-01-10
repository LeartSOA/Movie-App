package com.Util;


import java.io.Serializable;

public class MovieLength implements Serializable {
    private String runtime;

    public MovieLength() {
    }
    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getRuntime() {
        return runtime;
    }


}
