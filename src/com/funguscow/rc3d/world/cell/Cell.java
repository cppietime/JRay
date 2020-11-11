package com.funguscow.rc3d.world.cell;

import com.funguscow.rc3d.gfx.texture.RgbImage;
import com.funguscow.rc3d.gfx.Texture;
import com.funguscow.rc3d.physics.Vector2D;

public class Cell {

    public static final int[] TEXTURE;
    static{
        TEXTURE = new int[64 * 64];
        for(int x = 0; x < 64; x++){
            for(int y = 0 ;y < 64; y++){
                TEXTURE[y * 64 + x] = x ^ y;
            }
        }
    }

    public double floor = .25, ceiling = .75;
    public double propFloor, propCeiling;

    private Texture[] textures;

    public Cell(double f, double c, Texture... tex){
        textures = new Texture[8]; // Floor, Ceiling, Low wall, High wall, and for prop
        floor = f;
        ceiling = c;
        System.arraycopy(tex, 0, textures, 0, tex.length);
    }

    public boolean getIntersections(Vector2D enter,
                                    Vector2D exit,
                                    Vector2D direction,
                                    Vector2D cell,
                                    Vector2D first,
                                    Vector2D second,
                                    Vector2D distances,
                                    Vector2D textureXs,
                                    double norm){
        return false;
    }

    public int getColor(double x, double y, int index){
        if(textures[index] == null)
            return 0;
        return textures[index].sample(x, y);
    }

    public boolean getPropHitbox(Vector2D corner0, Vector2D corner1){
        return false;
    }

    public static final Cell GREEN = new RoundPillar(.2,
                            2,
                            0,
                            .6,
                            new Vector2D(.25, .25),
                            new Vector2D(.25, .1),
                            RgbImage.CYAN.fullTexture(),
                            RgbImage.CYAN.fullTexture(),
                            RgbImage.GREEN.fullTexture(),
                            RgbImage.GREEN.fullTexture(),
                            RgbImage.MAGENTA.fullTexture(),
                            RgbImage.MAGENTA.fullTexture(),
                            RgbImage.GREEN.fullTexture(),
                            RgbImage.GREEN.fullTexture()),
                    RED = new SquarePillar(.25, 2, 0,
                            .6,
                            new Vector2D(.1, .25),
                            new Vector2D(.6, .76),
                            RgbImage.BLUE.fullTexture(),
                            RgbImage.BLUE.fullTexture(),
                            RgbImage.RED.fullTexture(),
                            RgbImage.RED.fullTexture(),
                            RgbImage.GRAY.fullTexture(),
                            RgbImage.GRAY.fullTexture(),
                            RgbImage.GREEN.fullTexture(),
                            RgbImage.GREEN.fullTexture()),
                    BLUE = new PlaneDivided(.4,
                            2,
                            .1,
                            .9,
                            1,
                            3,
                            3,
                            RgbImage.BLUE.fullTexture(),
                            RgbImage.BLUE.fullTexture(),
                            RgbImage.BLUE.fullTexture(),
                            RgbImage.BLUE.fullTexture(),
                            RgbImage.WHITE.fullTexture(),
                            RgbImage.WHITE.fullTexture(),
                            RgbImage.MAGENTA.fullTexture(),
                            RgbImage.MAGENTA.fullTexture()),
                    EMPTY = new Cell(0,
                            2,
                            RgbImage.WHITE.fullTexture(),
                            RgbImage.WHITE.fullTexture());

}
