package com.funguscow.rc3d.world.cell;

import com.funguscow.rc3d.gfx.Texture;
import com.funguscow.rc3d.physics.Vector2D;

public class RoundPillar extends Cell {

    private Vector2D center;
    private Vector2D axes;

    public RoundPillar(double floor,
                       double ceiling,
                       double propFloor,
                       double propCeiling,
                       Vector2D center,
                       Vector2D axes,
                       Texture... textures){
        super(floor, ceiling, textures);
        this.propFloor = propFloor;
        this.propCeiling = propCeiling;
        this.center = new Vector2D(center);
        this.axes = new Vector2D(axes);
    }

    @Override
    public boolean getIntersections(Vector2D enter,
                                    Vector2D exit,
                                    Vector2D direction,
                                    Vector2D cell,
                                    Vector2D first,
                                    Vector2D second,
                                    Vector2D distances,
                                    Vector2D textureXs,
                                    double norm) {
        double dx = enter.x - (center.x + cell.x), dy = enter.y - (center.y + cell.y);
        // Get quadratic coeffs
        double ax = (direction.x / axes.x), ay = (direction.y / axes.y);
        double a = ax * ax + ay * ay;
        double b = 2 * direction.x * dx / axes.x / axes.x + 2 * direction.y * dy / axes.y / axes.y;
        double cx = dx / axes.x, cy = dy / axes.y;
        double c = cx * cx + cy * cy - 1;
        double descriminant = b * b - 4 * a * c;
        if(descriminant < 0)
            return false;
        double real = -b / (a * 2);
        double pm = Math.sqrt(descriminant) / (a * 2);
        double t0 = real - pm, t1 = real + pm;
        if(t1 <= 0 || t0 * norm > distances.y - distances.x)
            return false;
        if(t0 < 0) {
            first.copy(enter);
            t0 = 0;
        }
        else{
            first.x = enter.x + direction.x * t0;
            first.y = enter.y + direction.y * t0;
        }
        if(t1 * norm >= distances.y - distances.x) {
            second.copy(exit);
            t1 = distances.y - distances.x;
        }
        else{
            second.x = enter.x + direction.x * t1;
            second.y = enter.y + direction.y * t1;
        }
        distances.x = t0 * norm;
        distances.y = t1 * norm;
        textureXs.x = Math.atan2(first.y - (center.y + cell.y), first.x - (center.x + cell.x)) / (2 * Math.PI) + .5;
        textureXs.y = Math.atan2(second.y - (center.y + cell.y), second.x - (center.x + cell.x)) / (2 * Math.PI) + .5;
        return true;
    }
}
