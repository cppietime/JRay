package com.funguscow.rc3d.gfx.texture;

import com.funguscow.rc3d.gfx.Texture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class PaletteImage implements Texture {

    private byte[] pixels;
    private int width, height;

    public PaletteImage(BufferedImage image){
        if(image.getType() != BufferedImage.TYPE_BYTE_INDEXED)
            throw new RuntimeException("Non-paletted image cannot make a PaletteImage");
        width = image.getWidth();
        height = image.getHeight();
        pixels = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
    }

    public int sample(double x, double y){
        x -= Math.floor(x);
        y -= Math.floor(y);
        int ix = (int)(width * x), iy = (int)(height * y);
        return pixels[iy * width + ix];
    }

}
