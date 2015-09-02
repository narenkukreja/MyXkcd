package com.example.naren.xkcdapp.model;

/*

   Comic Model class to get relevant data

    */
public class Comic {


    protected String title, img, alt_text;
    protected int num;
    public static final String COMIC_URL = "http://xkcd.com/";
    public static final String COMIC_URL_ENDPOINT = "/info.0.json";
    public static final String DEFAULT_COMIC_URL = "http://xkcd.com/info.0.json";

    public String getAlt_text() {
        return alt_text;
    }

    public void setAlt_text(String alt_text) {
        this.alt_text = alt_text;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public String getImg() {
        return img;
    }


}
