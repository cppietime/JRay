package com.funguscow.rc3d.physics;

public class Ray {

    public Vector2D origin, direction;

    private static final double EPSILON = 0.5;

    public Ray(Vector2D origin, Vector2D direction){
        this.origin = origin;
        this.direction = direction;
    }

    public Ray(Vector2D direction){
        origin = new Vector2D();
        this.direction = direction;
    }

    public double nextIntersection(Vector2D position, Vector2D out, Vector2D cell){
        double tx = tIntersection(position.x, direction.x);
        double ty = tIntersection(position.y, direction.y);
        double t = tx;
        boolean hity = false;
        if(ty < t){
            t = ty;
            hity = true;
        }
        Vector2D intersection = Vector2D.add(position, direction, t);
        if(out != null){
            out.copy(intersection);
        }
        if(cell != null){
            if(!hity) {
                cell.y = Math.floor(intersection.y);
                if (direction.x > 0)
                    cell.x = Math.floor(intersection.x + EPSILON);
                else
                    cell.x = Math.floor(intersection.x - EPSILON);
            }
            else{
                cell.x = Math.floor(intersection.x);
                if(direction.y > 0)
                    cell.y = Math.floor(intersection.y + EPSILON);
                else
                    cell.y = Math.floor(intersection.y - EPSILON);
            }
        }
        return t;
    }

    public static double tIntersection(double start, double direction){
        if(direction == 0)
            return Double.POSITIVE_INFINITY;
        double target;
        if(direction > 0)
            target = Math.floor(start) + 1;
        else
            target = Math.ceil(start) - 1;
        return (target - start) / direction;
    }

}
