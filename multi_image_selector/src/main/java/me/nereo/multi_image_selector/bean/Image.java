package me.nereo.multi_image_selector.bean;

import android.text.TextUtils;

/**
 * Image Entity
 */
public class Image {
    public String path;
    public String name;
    public long time;

    /**
     * get path, name and time for an image
     * @param path
     * @param name
     * @param time
     */
    public Image(String path, String name, long time){
        this.path = path;
        this.name = name;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        try {
            Image other = (Image) o;
            return TextUtils.equals(this.path, other.path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
