package com.funguscow.rc3d.physics;

public class Vector2D {

    public double x, y;

    public Vector2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Vector2D(){
        this(0, 0);
    }

    public Vector2D(Vector2D other){
        this(other.x, other.y);
    }

    public double magnitude(){
        return Math.sqrt(x * x + y * y);
    }

    public Vector2D normalized(){
        double norm = 1.0 / magnitude();
        return new Vector2D(x * norm, y * norm);
    }

    public Vector2D scaled(double scale){
        return new Vector2D(x * scale, y * scale);
    }

    public Vector2D rotated(double angle){
        double cos = Math.cos(angle), sin = Math.sin(angle);
        return new Vector2D(x * cos - y * sin, y * cos + x * sin);
    }

    public Vector2D copy(Vector2D other){
        x = other.x;
        y = other.y;
        return this;
    }

    public static Vector2D add(Vector2D left, Vector2D right, double scale){
        return new Vector2D(left.x + right.x * scale, left.y + right.y * scale);
    }

    public static Vector2D add(Vector2D left, Vector2D right){
        return add(left, right, 1);
    }

    public static Vector2D linInterp(Vector2D a, Vector2D b, double z){
        return new Vector2D(a.x + (b.x - a.x) * z, a.y + (b.y - a.y) * z);
    }

    public static void linInterp(Vector2D out, Vector2D a, Vector2D b, double z){
        out.x = a.x + (b.x - a.x) * z;
        out.y = a.y + (b.y - a.y) * z;
    }

}
