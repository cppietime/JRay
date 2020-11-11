package com.funguscow.rc3d.gfx.texture;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

public class PaletteTexture {

    private PaletteImage image;
    private IndexColorModel palette;

    public PaletteTexture(BufferedImage image){
        this.image = new PaletteImage(image);
        palette = (IndexColorModel)image.getColorModel();
    }

    public int sample(double x, double y){
        int index = image.sample(x, y);
        return palette.getRGB(index);
    }

}
