package com.funguscow.rc3d.physics;

public class Vector3D {

    public double x, y, z;

    public Vector3D(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D(Vector3D base){
        x = base.x;
        y = base.y;
        z = base.z;
    }

    public double magnitude(){
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3D normalize(){
        double norm = 1.0 / magnitude();
        x *= norm;
        y *= norm;
        z *= norm;
        return this;
    }

    public static Vector3D add(Vector3D left, Vector3D right){
        return new Vector3D(left.x + right.x, left.y + right.y, left.z + right.z);
    }

    public static Vector3D addScaled(Vector3D left, Vector3D right, double scale){
        return new Vector3D(left.x + right.x * scale, left.y + right.y * scale, left.z + right.z * scale);
    }

    public static Vector3D rotate(Vector3D vec, Quaternion rot){
        Quaternion p = new Quaternion(vec);
        p = Quaternion.times(rot, p);
        p = Quaternion.times(p, rot.conjugate());
        return p.vector();
    }

    public static double dot(Vector3D left, Vector3D right){
        return left.x * right.x + left.y * right.y + left.z * right.z;
    }

}
