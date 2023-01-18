package ru.shanin.workwithsurfaceviewmy.surfaceview;

import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import ru.shanin.workwithsurfaceviewmy.surfaceview.sprite.Field;
import ru.shanin.workwithsurfaceviewmy.surfaceview.timers.TimerCounterFramesIncrements;
import ru.shanin.workwithsurfaceviewmy.surfaceview.timers.TimerDrawCanvasOnDisplay;
import ru.shanin.workwithsurfaceviewmy.surfaceview.timers.TimerWorkWithCoordinateThread;

public class ViewDrawThread extends Thread {
    private final TimerCounterFramesIncrements timerCounter;
    private final TimerDrawCanvasOnDisplay timerDrawOnCanvas;
    private final TimerWorkWithCoordinateThread timerWorkWithCoordinate;
    private final Field field;
    boolean running;


    public ViewDrawThread(Context context, SurfaceHolder holder) {
        timerDrawOnCanvas = new TimerDrawCanvasOnDisplay(context, holder);
        timerCounter = new TimerCounterFramesIncrements();
        timerWorkWithCoordinate = new TimerWorkWithCoordinateThread(context);
        field=new Field(context);
    }

    @Override
    public synchronized void start() {
        running = true;
        timerCounter.start();
        timerDrawOnCanvas.start();
        timerWorkWithCoordinate.start();
        super.start();
    }

    @Override
    public void run() {
        super.run();
    }

    @Override
    public void interrupt() {
        running=false;
        super.interrupt();
    }
    public boolean getRetry(){
        return timerCounter.stopWork() || timerDrawOnCanvas.stopWork()
                || timerWorkWithCoordinate.stopWork();
    }
    public void onTouch(MotionEvent event){
        timerDrawOnCanvas.updateTouchCoordinate((int) event.getX(), (int) event.getY());
        timerWorkWithCoordinate.updateTouchCoordinate((int) event.getX(), (int) event.getY());
    }
}
