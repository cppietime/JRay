package com.funguscow.rc3d.world;

import com.funguscow.rc3d.gfx.Frame;
import com.funguscow.rc3d.physics.Ray;
import com.funguscow.rc3d.physics.Vector2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Camera {

    public Vector2D position;
    public double angle;
    public double fov;

    public double near = .1;
    public double far;
    public double height = 0.5;

    public Camera(){
        position = new Vector2D(0, 0);
        angle = 0;
        far = 100;
        fov = 1.5;
    }

    public void march(){
        double cos = Math.cos(angle), sin = Math.sin(angle);
        position.x += cos * .2;
        position.y += sin * .2;
    }

    public void turn(){
        angle += 0.1;
    }
}
