package com.funguscow.rc3d.gfx;

public class QueuedSprite implements Comparable<QueuedSprite> {

    public double distance;
    public double x, y, width, height;
    public Texture texture;

    public int compareTo(QueuedSprite other){
        double diff = distance - other.distance;
        return diff == 0 ? 0 : diff > 0 ? -1 : 1;
    }

}
