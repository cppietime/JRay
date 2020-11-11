package com.funguscow.rc3d.gfx;

import com.funguscow.rc3d.physics.Ray;
import com.funguscow.rc3d.physics.Vector2D;
import com.funguscow.rc3d.world.Camera;
import com.funguscow.rc3d.world.Entity;
import com.funguscow.rc3d.world.cell.Cell;
import com.funguscow.rc3d.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Renderer {

    private double[] floorPos;
    private ExecutorService es;
    private int threads;
    private List<Entity> entities;

    public Renderer(int height, int threads){
        this.threads = threads;
        floorPos = new double[height / 2 + 1];
        for(int i = 0; i < floorPos.length; i++){
            floorPos[i] = height / (height - 2.0 * i);
        }
        es = Executors.newFixedThreadPool(threads);
        entities = new ArrayList<>();
        entities.add(Entity.TESTENTITY);
    }

    public void renderSprites(int[] rgbBuffer,
                              float[] zBuffer,
                              Camera camera,
                              int width,
                              int height){
        PriorityQueue<QueuedSprite> queue = new PriorityQueue<>();
        double sin = Math.sin(camera.angle), cos = Math.cos(camera.angle);
        for(Entity e : entities)
            queue.add(e.transform(camera.position, camera.height, camera.fov, sin, cos));
        for(QueuedSprite qs : queue){
            if(qs.distance <= camera.near)
                break;
            renderSprite(rgbBuffer, zBuffer, qs, width, height);
        }
    }

    public void renderSprite(int[] rgbBuffer,
                             float[] zBuffer,
                             QueuedSprite qs,
                             int width,
                             int height){
        int x0 = (int)(width * (.5 + qs.x) - height * qs.width / 2);
        int x1 = (int)(x0 + qs.width * height);
        int y0 = (int)(height * (.5 + qs.y) - height * qs.height / 2);
        int y1 = (int)(y0 + qs.height * height);
        for(int px = x0; px < x1; px++){
            if(px < 0 || px >= width)
                continue;
            for(int py = y0; py < y1; py++){
                if(py < 0 || py >= height)
                    continue;
                int index = py * width + px;
                if(zBuffer[index] < qs.distance)
                    continue;
                int pixel = qs.texture.sample((double)(px - x0) / (x1 - x0), (double)(py - y0) / (y1 - y0));
                if((pixel & 0xff000000) == 0)
                    continue;
                rgbBuffer[index] = pixel;
                zBuffer[index] = (float)qs.distance;
            }
        }
    }

    public void render(int[] rgbBuffer,
                       float[] zBuffer,
                       Camera view,
                       int width,
                       int height,
                       World world,
                       int y0,
                       int y1){
        Arrays.fill(rgbBuffer, 0);
        if(threads == 1){
            for(int x = 0; x < width; x++)
                castRay(rgbBuffer,
                        zBuffer,
                        view,
                        (x * 2.0 - width) / width * view.fov,
                        x,
                        width,
                        height,
                        world,
                        y0,
                        y1);
        }else {
            List<Callable<Void>> tasks = new ArrayList<>();
            for (int x = 0; x < width; x += width / threads) {
                final int x0 = x, x1 = x + width / threads;
                tasks.add(() -> {
                    for(int fx = x0; fx < x1; fx++)
                        castRay(rgbBuffer,
                                zBuffer,
                                view,
                                (fx * 2.0 - width) / width * view.fov,
                                fx,
                                width,
                                height,
                                world,
                                y0,
                                y1);
                    return null;
                });
            }
            try {
                List<Future<Void>> awaits = es.invokeAll(tasks);
                for (Future<Void> future : awaits)
                    future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void castRay(int[] rgbBuffer,
                        float[] zBuffer,
                        Camera camera,
                        double column,
                        int offset,
                        int stride,
                        int height,
                        World world,
                        int y0,
                        int y1){
        double halfOverFov = 0.5 / camera.fov;
        Vector2D dir = new Vector2D(1, column).normalized();//.rotated(angle);
        double norm = dir.x;
        dir = dir.rotated(camera.angle);
        Ray ray = new Ray(camera.position, dir);
        double floor = 0, ceiling = 1;
        int low = y0, high = y1 - 1;
        double t = camera.near * norm, swapT;
        Vector2D current = Vector2D.add(camera.position, dir, t);
        Vector2D next = new Vector2D();
        Vector2D propTex = new Vector2D();
        Vector2D cellPos = new Vector2D(Math.floor(camera.position.x), Math.floor(camera.position.y));
        Vector2D nextCell = new Vector2D();
        Vector2D lastPos = new Vector2D();
        Vector2D propEnter = new Vector2D();
        Vector2D propExit = new Vector2D();
        Vector2D propTs = new Vector2D();
        Vector2D texPos = new Vector2D();
        Cell lastCell = world.getCell(cellPos);
        lastPos.copy(cellPos); // The previous cell coords are used for drawing the floor and ceiling
        t += ray.nextIntersection(current, current, cellPos) * norm; // Perform first intersection check outside loop
        double  rt;
        while(t < camera.far && low <= high){
            swapT = t + ray.nextIntersection(current, next, nextCell) * norm; /* Distance of next intersection
                Performing this check now so we can use the exit point later and then just switch to it */
            Cell cell = world.getCell(cellPos);
            if(cell != null){
                double tRecip = 1.0 / t;
                // May need to invert this for certain rays. .0001 is added to avoid rounding errors
                double xDif = current.x - Math.floor(current.x + .0001);
                double yDif = current.y - Math.floor(current.y + .0001);
                double xTex;
                if(xDif > yDif){
                    xTex = xDif;
                    if(dir.y > 0)
                        xTex = 1 - xTex;
                }
                else{
                    xTex = yDif;
                    if(dir.x < 0)
                        xTex = 1 - xTex;
                }
                double yRange = height / t * halfOverFov;
                double minY = height * (.5 - tRecip * (camera.height - floor) * halfOverFov);
                // Draw floor
                for(; low < height * (0.5 - tRecip * (camera.height - floor) * halfOverFov) && low < high; low++){
                    double ft = floorPos[low] * 2 * (camera.height - floor) * halfOverFov;
                    Vector2D.linInterp(texPos, camera.position, current, ft * tRecip);
                    renderAndBuffer(rgbBuffer,
                            zBuffer,
                            offset + (height - 1 - low - y0) * stride,
                            lastCell.getColor(texPos.x, texPos.y, 0),
                            (float)ft);
                }
                // Ceiling
                for(; high >= height * (0.5 + tRecip * (ceiling - camera.height) * halfOverFov) && high >= low; high--){
                    double ct = floorPos[height - high - 1] * 2 * (ceiling - camera.height) * halfOverFov;
                    Vector2D.linInterp(texPos, camera.position, current, ct * tRecip);
                    renderAndBuffer(rgbBuffer,
                            zBuffer,
                            offset + (height - 1 - high - y0) * stride,
                            lastCell.getColor(texPos.x, texPos.y, 0),
                            (float)ct);
                }
                // Wall
                for(; low <= height * (.5 - tRecip * (camera.height - cell.floor) * halfOverFov) && low < high; low++) {
//                    rgbBuffer[offset + (height - 1 - low) * stride] = cell.getColor(xTex, (low - minY) / yRange, 2);
//                    if(zBuffer != null)
//                        zBuffer[offset + (height - 1 - low) * stride] = (float)t;
                    renderAndBuffer(rgbBuffer,
                            zBuffer,
                            offset + (height - 1 - low - y0) * stride,
                            cell.getColor(xTex, (low - minY) / yRange, 2),
                            (float)t);
                }
                for(; high >= height * (.5 + tRecip * (cell.ceiling - camera.height) * halfOverFov) && high >= low; high--) {
//                    rgbBuffer[offset + (height - 1 - high) * stride] = cell.getColor(xTex, (high - minY) / yRange, 3);
//                    if(zBuffer != null)
//                        zBuffer[offset + (height - 1 - high) * stride] = (float)t;
                    renderAndBuffer(rgbBuffer,
                            zBuffer,
                            offset + (height - 1 - high - y0) * stride,
                            cell.getColor(xTex, (high - minY) / yRange, 3),
                            (float)t);
                }
                // Update to the new floor/ceiling values
                floor = cell.floor;
                ceiling = cell.ceiling;
                propTs.x = t;
                propTs.y = swapT;
                if(cell.getIntersections(current, next, dir, cellPos, propEnter, propExit, propTs, propTex, norm)){
                    rt = t + propTs.x;
                    double rtRecip = 1 / rt;
                    yRange = height * rtRecip;
                    minY = height * (.5 - (camera.height - floor) * rtRecip * halfOverFov);
                    // Draw raised floor
                    for(; low < height * (.5 - rtRecip * (camera.height - floor) * halfOverFov) && low < high; low++){
                        double ft = floorPos[low] * 2 * (camera.height - floor) * halfOverFov;
                        Vector2D.linInterp(texPos, camera.position, propEnter, ft * rtRecip);
                        renderAndBuffer(rgbBuffer,
                                zBuffer,
                                offset + (height - 1 - low - y0) * stride,
                                cell.getColor(texPos.x, texPos.y, 0),
                                (float)ft);
                    }
                    // Lowered ceiling
                    for(; high >= height * (.5 + rtRecip * (ceiling - camera.height) * halfOverFov) && high >= low; high--){
                        double ct = floorPos[height - high - 1] * 2 * (ceiling - camera.height) * halfOverFov;
                        Vector2D.linInterp(texPos, camera.position, propEnter, ct * rtRecip);
                        renderAndBuffer(rgbBuffer,
                                zBuffer,
                                offset + (height - 1 - high - y0) * stride,
                                cell.getColor(texPos.x, texPos.y, 1),
                                (float)ct);
                    }
                    // Low prop wall
                    for(; low < height * (.5 - rtRecip * (camera.height - cell.propFloor) * halfOverFov) && low < high; low++){
                        renderAndBuffer(rgbBuffer,
                                zBuffer,
                                offset + (height - 1 - low - y0) * stride,
                                cell.getColor(propTex.x, (low - minY) / yRange, 4),
                                (float)rt);
                    }
                    // High prop wall
                    for(; high >= height * (.5 + rtRecip * (cell.propCeiling - camera.height) * halfOverFov) && high >= low; high--){
                        renderAndBuffer(rgbBuffer,
                                zBuffer,
                                offset + (height - 1 - high - y0) * stride,
                                cell.getColor(propTex.x, (high - minY) / yRange, 5),
                                (float)rt);
                    }
                    rt = t + propTs.y;
                    rtRecip = 1 / rt;
                    yRange = height * rtRecip;
                    minY = height * (.5 - (camera.height - cell.propFloor) * rtRecip * halfOverFov);
                    // Draw prop floor
                    for (; low < height * (.5 - rtRecip * (camera.height - cell.propFloor) * halfOverFov) && low < high; low++) {
                        double ct = floorPos[low] * 2 * (camera.height - cell.propFloor) * halfOverFov;
                        Vector2D.linInterp(texPos, camera.position, propExit, ct * rtRecip);
//                        if (zBuffer != null)
//                            zBuffer[offset + (height - 1 - low - y0) * stride] = (float) ct;
//                        rgbBuffer[offset + (height - 1 - low - y0) * stride] = cell.getColor(texPos.x, texPos.y, 6);
                        renderAndBuffer(rgbBuffer,
                                zBuffer,
                                offset + (height - 1 - low - y0) * stride,
                                cell.getColor(texPos.x, texPos.y, 6),
                                (float)ct);
                    }
                    // Draw prop ceiling
                    for (; high >= height * (.5 + rtRecip * (cell.propCeiling - camera.height) * halfOverFov) && high >= low; high--) {
                        double ct = floorPos[height - high - 1] * 2 * (cell.propCeiling - camera.height)* halfOverFov;
                        Vector2D.linInterp(texPos, camera.position, propExit, ct * rtRecip);
//                        if (zBuffer != null)
//                            zBuffer[offset + (height - 1 - high - y0) * stride] = (float) ct;
//                        rgbBuffer[offset + (height - 1 - high - y0) * stride] = cell.getColor(texPos.x, texPos.y, 7);
                        renderAndBuffer(rgbBuffer,
                                zBuffer,
                                offset + (height - 1 - high - y0) * stride,
                                cell.getColor(texPos.x, texPos.y, 7),
                                (float)ct);
                    }
                    // Draw far walls
                    for(; low < height * (.5 - rtRecip * (camera.height - floor) * halfOverFov) && low < high; low++){
//                        rgbBuffer[offset + (height - 1 - low) * stride] = cell.getColor(propTex.y, (low - minY) / yRange, 4);
//                        if(zBuffer != null)
//                            zBuffer[offset + (height - 1 - low) * stride] = (float)rt;
                        renderAndBuffer(rgbBuffer,
                                zBuffer,
                                offset + (height - 1 - low - y0) * stride,
                                cell.getColor(propTex.y, (low - minY) / yRange, 4),
                                (float)rt);
                    }
                    for(; high >= height * (.5 + rtRecip * (ceiling - camera.height) * halfOverFov) && high >= low; high--){
//                        rgbBuffer[offset + (height - 1 - high) * stride] = cell.getColor(propTex.y, (high - minY) / yRange, 5);
//                        if(zBuffer != null)
//                            zBuffer[offset + (height - 1 - high) * stride] = (float)rt;
                        renderAndBuffer(rgbBuffer,
                                zBuffer,
                                offset + (height - 1 - high - y0) * stride,
                                cell.getColor(propTex.y, (high - minY) / yRange, 5),
                                (float)rt);
                    }
                }
                lastCell = cell;
            }
            t = swapT;
            current.copy(next);
            cellPos.copy(nextCell);
        }
    }

    private static void renderAndBuffer(int[] rgbBuffer, float[] zBuffer, int offset, int rgb, float z){
        rgbBuffer[offset] = rgb;
        if(zBuffer != null)
            zBuffer[offset] = z;
    }

}
