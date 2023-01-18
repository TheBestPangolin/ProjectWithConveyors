package ru.shanin.workwithsurfaceviewmy.surfaceview.sprite;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Sprite  {
    public static long stepCount = 0;

    private final int FRAME_SIZE_PIXELS = 128;
    private final Bitmap bitmap;

    private final int framesCountRowInSprite = 4;
    private final int frameCountColInSprite;
    private final Rect[][] frames;
    private int currentFrame;
    private int speed;

    /**
     * speed :
     * 1 - fast
     * 2 - normal
     * 3 - slow
     */

    public Sprite(Bitmap bitmap, int speed) {
        this.bitmap = bitmap;
        this.speed = speed;
        frameCountColInSprite = bitmap.getWidth() / FRAME_SIZE_PIXELS;
        frames = new Rect[framesCountRowInSprite][frameCountColInSprite];

        for (int i = 0; i < framesCountRowInSprite; i++)
            for (int j = 0; j < frameCountColInSprite; j++)
                frames[i][j] = new Rect(
                        FRAME_SIZE_PIXELS * j, FRAME_SIZE_PIXELS * i,
                        FRAME_SIZE_PIXELS * (j + 1), FRAME_SIZE_PIXELS * (i + 1)
                );
    }


    public void draw(Canvas canvas, Rect rect, int ward) {
        currentFrame = (int) (stepCount / speed) % frameCountColInSprite;
        canvas.drawBitmap(bitmap, frames[ward][currentFrame], rect, new Paint());
    }

    public static void updateNextFrame() {
        stepCount++;
    }
}