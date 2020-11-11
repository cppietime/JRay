package com.funguscow.rc3d.world;

import com.funguscow.rc3d.gfx.QueuedSprite;
import com.funguscow.rc3d.gfx.Texture;
import com.funguscow.rc3d.gfx.texture.RgbImage;
import com.funguscow.rc3d.physics.Vector2D;
import com.funguscow.rc3d.world.cell.Cell;

public class Entity {

    public double x, y, z;
    public double width, height;
    public double velX, velY;
    public double hitboxHeight, climbHeight;

    public boolean isVisible(){
        return true;
    }

    public Texture getTexture(){
        return null;
    }

    public QueuedSprite transform(Vector2D origin, double cHeight, double cFov, double sin, double cos){
        QueuedSprite qs = new QueuedSprite();
        qs.texture = getTexture();
        double dx = x - origin.x, dy = y - origin.y;
        qs.distance = dx * cos + dy * sin;
        double dScale = 0.5 / (qs.distance * cFov);
        qs.x = (dy * cos - dx * sin) * dScale;
        qs.y = (z - cHeight) * dScale;
        qs.width = width * 2 * dScale;
        qs.height = height * 2 * dScale;
        return qs;
    }

    public boolean getPhysicsHitbox(Vector2D center, Vector2D corner0, Vector2D corner1){
        return false;
    }

    public void resolveCollisions(World world, double deltaTime){
        Vector2D corner0 = new Vector2D(), corner1 = new Vector2D(), center = new Vector2D();
        Vector2D propC0 = new Vector2D(), propC1 = new Vector2D();
        if(!getPhysicsHitbox(center, corner0, corner1))
            return;
        Vector2D startPos = new Vector2D(Math.floor(center.x), Math.floor(center.y));
        int[] coordDeltas = {0, 0, -1, 0, 1, 0, 0, -1, 0, 1, -1, -1, 1, -1, -1, 1, 1, 1};
        for(int i = 0; i < coordDeltas.length; i+= 2) {
            Vector2D cellPos = new Vector2D(startPos.x + coordDeltas[i], startPos.y + coordDeltas[i + 1]);
            Cell current = world.getCell(cellPos);
            if (current != null) {
                z = current.floor;
                if (resolveCollision(new Vector2D(cellPos.x - corner0.x + center.x, cellPos.y - corner0.y + center.y),
                        new Vector2D(cellPos.x + corner1.x - center.x, cellPos.y + corner1.y - center.y),
                        new Vector2D(deltaTime * velX, deltaTime * velY),
                        current.floor,
                        current.ceiling)) {
                    if (current.getPropHitbox(propC0, propC1)) {
                        resolveCollision(new Vector2D(propC0.x - corner0.x + center.x, propC0.y - corner0.y + center.y),
                                new Vector2D(propC1.x + corner1.x - center.x, propC1.y + corner1.y - center.y),
                                new Vector2D(deltaTime * velX, deltaTime * velY),
                                current.propFloor,
                                current.propCeiling);
                    }
                }
            }
        }
    }

    public boolean resolveCollision(Vector2D corner0, Vector2D corner1, Vector2D motion, double floor, double ceiling){
        if(x >= corner0.x && x <= corner1.x && y >= corner0.y && y <= corner1.y) {
            if(floor > z)
                z = floor;
        }
        else{
            double tx0 = (corner0.x - x) / motion.x, tx1 = (corner1.x - x) / motion.x;
            double ty0 = (corner0.y - y) / motion.y, ty1 = (corner1.y - y) / motion.y;
            double txMin = Math.min(tx0, tx1), tyMin = Math.min(ty0, ty1);
            double txMax = tx0 + tx1 - txMin, tyMax = ty0 + ty1 - tyMin;
            if (txMax < tyMin || tyMax < txMin)
                return false;
            boolean yFirst = false;
            double t0;
            if(txMin >= tyMin){
                t0 = txMin;
            }
            else{
                t0 = tyMin;
                yFirst = true;
            }
            double t1 = Math.min(txMax, tyMax);
            if (t0 > 1 || t1 < 0)
                return false;
            if(floor - z > climbHeight || ceiling - floor < hitboxHeight) {
                if (yFirst)
                    velY *= t0;
                else
                    velX *= t0;
            }
            else if(floor > z)
                z = floor;
        }
        return true;
    }

    public static final Entity TESTENTITY = new Entity(){
        public boolean isVisible(){return true;}
        public Texture getTexture(){return RgbImage.YELLOW.fullTexture();}
        };
    static{
        TESTENTITY.width = .5;
        TESTENTITY.height = .5;
        TESTENTITY.x = 3;
        TESTENTITY.y = 2;
        TESTENTITY.z = .5;
    }

}
