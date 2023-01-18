package ru.shanin.workwithsurfaceviewmy.surfaceview;

import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class ViewDraw extends SurfaceView implements SurfaceHolder.Callback {
    private ViewDrawThread thread;

    public ViewDraw(Context context) {
        super(context);
        getHolder().addCallback(this);

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        thread= new ViewDrawThread(getContext(), getHolder());
        thread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                retry = thread.getRetry();
            } catch (Exception ignored) {
                retry = false;
            }
        }
    }
    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN) {
            //TODO method to work with coordinate
            thread.onTouch(event);
            //Log.w("onTouchEvent", "Click on coord : " + (int) event.getX() + ", " + (int) event.getY());
        }
        return true;
    }

}
