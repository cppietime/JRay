package com.funguscow.rc3d.physics;

public class Quaternion {

    public double r, i, j, k;

    public Quaternion(double a, double b, double c, double d){
        r = a;
        i = b;
        j = c;
        k = d;
    }

    public Quaternion(Vector3D axis, double angle){
        r = Math.cos(angle / 2);
        double s = Math.sin(angle / 2);
        i = axis.x * s;
        j = axis.y * s;
        k = axis.z * s;
    }

    public Quaternion(Vector3D vec){
        r = 0;
        i = vec.x;
        j = vec.y;
        k = vec.z;
    }

    public Quaternion conjugate(){
        return new Quaternion(r, -i, -j, -k);
    }

    public Vector3D vector(){
        return new Vector3D(i, j, k);
    }

    public static Quaternion times(Quaternion left, Quaternion right){
        double r = left.r * right.r - left.i * right.i - left.j * right.j - left.k * right.k;
        double i = left.r * right.i + left.i * right.r + left.j * right.k - left.k * right.j;
        double j = left.r * right.j + left.j * right.r + left.k * right.i - left.i * right.k;
        double k = left.r * right.k + left.k * right.r + left.i * right.j - left.j * right.i;
        return new Quaternion(r, i, j, k);
    }

}
