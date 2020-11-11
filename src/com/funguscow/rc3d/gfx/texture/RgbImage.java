package com.funguscow.rc3d.gfx.texture;

import com.funguscow.rc3d.gfx.Texture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class RgbImage implements Texture {

    private int rgb[];
    private int width, height;
    private boolean hasAlpha;

    public RgbImage(BufferedImage image){
        switch(image.getType()){
            case BufferedImage.TYPE_INT_RGB:
                hasAlpha = false; break;
            case BufferedImage.TYPE_INT_ARGB:
                hasAlpha = true; break;
            default:
                throw new RuntimeException("RgbImage must take an RGB or ARGB image");
        }
        rgb = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
        width = image.getWidth();
        height = image.getHeight();
    }

    public RgbImage(int w, int r, int g, int b){
        width = w;
        height = w;
        hasAlpha = false;
        rgb = new int[w * w];
        for(int x = 0; x < w; x ++){
            for(int y = 0; y < w; y++){
                double val = (x ^ y) * 1.0 / w;
                int color = ((int)(r * val) << 16) | ((int)(g * val) << 8) | (int)(b * val);
                rgb[y * w + x] = color;
            }
        }
    }

    public static final RgbImage RED = new RgbImage(64, 0xff, 0, 0);
    public static final RgbImage GREEN = new RgbImage(64, 0, 0xff, 0);
    public static final RgbImage BLUE = new RgbImage(64, 0, 0, 0xff);
    public static final RgbImage WHITE = new RgbImage(64, 0xff, 0xff, 0xff);
    public static final RgbImage GRAY = new RgbImage(64, 0x80, 0x80, 0x80);
    public static final RgbImage CYAN = new RgbImage(64, 0, 0xff, 0xff);
    public static final RgbImage MAGENTA = new RgbImage(64, 0xff, 0, 0x80);
    public static final RgbImage YELLOW = new RgbImage(64, 0xff, 0xff, 0);

    public int sample(double x, double y){
        x -= Math.floor(x);
        y -= Math.floor(y);
        int ix = (int)(x * width);
        int iy = (int)(y * height);
        int color = rgb[iy * width + ix];
        if(!hasAlpha)
            color |= 0xff000000;
        return color;
    }

    public SubTexture fullTexture(){
        return new SubTexture(this, 0, 0, 1, 1);
    }

}
