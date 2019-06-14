package Rendering;

public class FPS {
    private long lastTime = System.currentTimeMillis();
    private int fpsCounter =0;
    private int lastFps=0;

    public void tick() {
        long timePassed = System.currentTimeMillis() - lastTime;
        if(timePassed > 1000){
            lastTime=System.currentTimeMillis();
            lastFps = fpsCounter;
            fpsCounter = 0;
        }
        fpsCounter++;
    }

    public int getFps() {
        return lastFps;
    }
}
