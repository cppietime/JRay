package com.funguscow.rc3d.gfx.texture;

import com.funguscow.rc3d.gfx.Texture;

public class SubTexture implements Texture {

    private Texture image;
    private double x0, y0, width, height;

    public SubTexture(Texture parent, double x, double y, double w, double h){
        image = parent;
        x0 = x;
        y0 = y;
        width = w;
        height = h;
    }

    public int sample(double x, double y){
        return image.sample(x0 + x * width, y0 + y * height);
    }

}
