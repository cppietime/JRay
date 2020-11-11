package com.funguscow.rc3d.world.cell;

import com.funguscow.rc3d.gfx.Texture;
import com.funguscow.rc3d.physics.Vector2D;

public class PlaneDivided extends Cell{

    private static final double EPSILON = .0001;

    private double[] coefficients;
    private double cOverB, bOverA;

    public PlaneDivided(double floor,
                        double ceiling,
                        double pFloor,
                        double pCeiling,
                        double a,
                        double b,
                        double c,
                        Texture... textures){
        super(floor, ceiling, textures);
        coefficients = new double[]{a, b, c};
        propFloor = pFloor;
        propCeiling = pCeiling;
        cOverB = c / b;
        bOverA = b / a;
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
        double slopeDot = coefficients[0] * direction.x + coefficients[1] * direction.y;
        double dx = enter.x - cell.x, dy = enter.y - cell.y;
        double lineDot = coefficients[0] * dx + coefficients[1] * dy;

        double t;
        if(slopeDot == 0)
            t = Double.NEGATIVE_INFINITY;
        else {
            t = (coefficients[2] - lineDot) / slopeDot;
        }
        if(lineDot >= coefficients[2]){ // Starts in prop
            first.copy(enter);
            if(t < 0 || t * norm > distances.y - distances.x){ // Intersection outside of block
                second.copy(exit);
                distances.y -= distances.x;
            }
            else {
                second.x = enter.x + direction.x * t;
                second.y = enter.y + direction.y * t;
                distances.y = t * norm;
            }
            distances.x = 0;
        }
        else{ // Ends in prop
            second.copy(exit);
            distances.y -= distances.x;
            if(t < 0 || t * norm > distances.y){
                return false;
            }
            else{
                first.x = enter.x + direction.x * t;
                first.y = enter.y + direction.y * t;
                distances.x = t * norm;
            }
        }
        // Entrance tex
        if(Math.abs(first.x - cell.x) < EPSILON)
            textureXs.x = first.y;
        else if(Math.abs(first.x - cell.x - 1)< EPSILON)
            textureXs.x = 1 - first.y;
        else if(Math.abs(first.y - cell.y)< EPSILON)
            textureXs.x = 1 - first.x;
        else if(Math.abs(first.y - cell.y - 1)< EPSILON)
            textureXs.x = first.x;
        else if(coefficients[0] == 0)
            textureXs.x = first.x;
        else {
            double yOfX0 = cOverB;
            textureXs.x = (first.y - cell.y - yOfX0) * bOverA;
        }
        // Exit tex
        if(Math.abs(second.x - cell.x) < EPSILON)
            textureXs.y = second.y;
        else if(Math.abs(second.x - cell.x - 1)< EPSILON)
            textureXs.y = 1 - second.y;
        else if(Math.abs(second.y - cell.y)< EPSILON)
            textureXs.y = 1 - second.x;
        else if(Math.abs(second.y - cell.y - 1)< EPSILON)
            textureXs.y = second.x;
        else if(coefficients[0] == 0)
            textureXs.y = second.x;
        else {
            double yOfX0 = cOverB;
            textureXs.y = (second.y - cell.y - yOfX0) * bOverA;
        }
        return true;
    }
}
