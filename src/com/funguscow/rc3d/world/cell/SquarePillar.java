package com.funguscow.rc3d.world.cell;

import com.funguscow.rc3d.gfx.Texture;
import com.funguscow.rc3d.physics.Vector2D;

public class SquarePillar extends Cell {

    private Vector2D p0, p1;

    public SquarePillar(double f, double c, double pf, double pc, Vector2D p0, Vector2D p1, Texture... tex){
        super(f, c, tex);
        propFloor = pf;
        propCeiling = pc;
        this.p0 = p0;
        this.p1 = p1;
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
        double x0 = cell.x + p0.x, x1 = cell.x + p1.x, y0 = cell.y + p0.y, y1 = cell.y + p1.y;
        double tx0 = (x0 - enter.x) / direction.x, tx1 = (x1 - enter.x) / direction.x;
        double ty0 = (y0 - enter.y) / direction.y, ty1 = (y1 - enter.y) / direction.y;
        double txMin = Math.min(tx0, tx1);
        double txMax = tx0 + tx1 - txMin;
        double tyMin = Math.min(ty0, ty1);
        double tyMax = ty0 + ty1 - tyMin;
        if(txMax <= tyMin)
            return false;
        if(tyMax <= txMin)
            return false;
        double t0 = Math.max(txMin, tyMin);
        double t1 = Math.min(txMax, tyMax);
        if(t1 < 0 || t0 * norm > distances.y)
            return false;
        first.x = enter.x + direction.x * t0;
        first.y = enter.y + direction.y * t0;
        second.x = enter.x + direction.x * t1;
        second.y = enter.y + direction.y * t1;
        if(txMin > tyMin){
            textureXs.x = first.y - cell.y;//(first.y - cell.y - p0.y) * dimReciprocal.y;
            if(direction.x < 0)
                textureXs.x = 1 - textureXs.x;
        }else{
            textureXs.x = first.x - cell.x;//(first.x - cell.x - p0.x) * dimReciprocal.x;
            if(direction.y > 0)
                textureXs.x = 1 - textureXs.x;
        }
        if(txMax < tyMax){
            textureXs.y = second.y - cell.y;//(second.y - cell.y - p0.y) * dimReciprocal.y;
            if(direction.x < 0)
                textureXs.y = 1 - textureXs.y;
        }else{
            textureXs.y = second.x - cell.x;//(second.x - cell.x - p0.x) * dimReciprocal.x;
            if(direction.y > 0)
                textureXs.y = 1 - textureXs.y;
        }
        distances.x = t0 * norm;
        distances.y = t1 * norm;
        return true;
    }
}
