package com.funguscow.rc3d.gfx;

import com.funguscow.rc3d.gfx.texture.RgbImage;
import com.funguscow.rc3d.world.Camera;
import com.funguscow.rc3d.world.World;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class RaycastCanvas extends Canvas implements KeyListener {

    private BufferStrategy strategy;
    private BufferedImage image;
    private int[] buffer;
    private float[] zBuffer;

    private int screenWidth, screenHeight;
    private int imageWidth, imageHeight;
    private int sy0, sy1;

    public Camera camera = new Camera();
    public World world = new World();
    public Renderer renderer;

    private int frames = 0;
    private long start;

    public RaycastCanvas(int w, int h, int pixels){
        screenWidth = w;
        screenHeight = h;
        imageWidth = pixels;
        double aspectRatio = (double)screenHeight / screenWidth;
        imageHeight = (int)(imageWidth * aspectRatio);
        if(aspectRatio < 1) {
            sy0 = (imageWidth - imageHeight) / 2;
            sy1 = (imageWidth + imageHeight) / 2;
        }
        else {
            sy0 = 0;
            sy1 = (int) (imageWidth * aspectRatio);
        }
        image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        buffer = ((DataBufferInt)(image.getRaster().getDataBuffer())).getData();
        setBounds(0, 0, screenWidth, screenHeight);
        setIgnoreRepaint(true);
        addKeyListener(this);
        requestFocus();
        renderer = new Renderer(Math.max(imageHeight, imageWidth), 1);
        zBuffer = new float[imageHeight * imageWidth];
    }

    public void redraw(){
        if(strategy == null) {
            createBufferStrategy(2);
            strategy = getBufferStrategy();
            start = System.currentTimeMillis();
        }
        Graphics2D g = (Graphics2D)strategy.getDrawGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        Arrays.fill(zBuffer, Float.POSITIVE_INFINITY);
        renderer.render(buffer, zBuffer, camera, imageWidth, Math.max(imageWidth, imageHeight), world, sy0, sy1);
        renderer.renderSprites(buffer, zBuffer, camera, imageWidth, imageHeight);
        g.drawImage(image, 0, 0, screenWidth, screenHeight, 0, 0, imageWidth, imageHeight, this);
        g.dispose();
        if(frames++ % 100 == 0){
            System.out.println((frames * 1000.0 / (System.currentTimeMillis() - start)) + " FPS");
        }
        strategy.show();
    }

    /* TODO move input handling to a separate class */
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_W)
            camera.march();
        if(e.getKeyCode() == KeyEvent.VK_R)
            camera.turn();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
