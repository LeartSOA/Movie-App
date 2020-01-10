package com.Util;

import java.io.Serializable;

public class YoutubeLink implements Serializable {
    private String key;

    public YoutubeLink() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
