package ru.shanin.workwithsurfaceviewmy.surfaceview.timers;

import java.util.Timer;
import java.util.TimerTask;

import ru.shanin.workwithsurfaceviewmy.surfaceview.sprite.Sprite;

public class TimerCounterFramesIncrements extends Thread {
    private final Timer timer;
    private final TimerTaskCounterIncrements task;
    private boolean running;

    public TimerCounterFramesIncrements() {
        timer = new Timer();
        task = new TimerTaskCounterIncrements();
    }

    @Override
    public synchronized void start() {
        int PERIOD = 16;// ms, for 60 tic/sec
        int DELAYED_START = 0;// ms
        running = true;
        timer.scheduleAtFixedRate(task, DELAYED_START, PERIOD);
super.start();
    }

    @Override
    public void run() {
super.run();
    }

    public boolean stopWork() {
        running = false;
        timer.cancel();
        return running;
    }

    private class TimerTaskCounterIncrements extends TimerTask {
        @Override
        public void run() {
            if (running) setCurrentFrameCounter();
        }
    }

    private void setCurrentFrameCounter() {
        Sprite.updateNextFrame();
    }
}