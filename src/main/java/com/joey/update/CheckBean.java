package com.joey.update;

/**
 * Created by Joey on 2016/3/17 0017.
 */
public class CheckBean {
    private String id;
    private String o_v;
    private String n_v;
    private String url;
    private String time;
    private String content;
    private int size;

    public void setId(String id) {
        this.id = id;
    }

    public void setO_v(String o_v) {
        this.o_v = o_v;
    }

    public void setN_v(String n_v) {
        this.n_v = n_v;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String getO_v() {
        return o_v;
    }

    public String getN_v() {
        return n_v;
    }

    public String getUrl() {
        return url;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
