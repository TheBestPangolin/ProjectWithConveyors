package ru.shanin.workwithsurfaceviewmy.surfaceview.sprite;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import java.util.Arrays;

import ru.shanin.workwithsurfaceviewmy.R;
import ru.shanin.workwithsurfaceviewmy.surfaceview.timers.TimerWorkWithCoordinateThread;

public class Field extends Thread  {
    private static final String LOG_TAG = Field.class.getSimpleName();

    private static final int spriteCount = 8;
    private final int countOfTypeBelt = 3;
    private final int SPEED_FAST = 1;
    private final int SPEED_NORMAL = 2;
    private final int SPEED_SLOW = 3;

    private static Sprite[] line;
    private static Sprite[] trailer_b;
    private static Sprite[] trailer_e;
    private static Sprite[] turn_0123;
    private static Sprite[] turn_3210;

    private static Sprite[][] field;

    public Field(Context context) {
        field = new Sprite[spriteCount][];
        start();
        for (int i = 0; i < field.length; i++) {
            field[i] = new Sprite[spriteCount];
            Arrays.fill(field[i], null);
        }
        initSprite(context);
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {

    }

    private void initSprite(Context context) {
        int[] speed = {SPEED_SLOW, SPEED_NORMAL, SPEED_FAST};
        int[] lineId = {R.drawable.mk0_line, R.drawable.mk1_line, R.drawable.mk2_line};
        int[] turn0123Id = {R.drawable.mk0_turn_0123, R.drawable.mk1_turn_0123, R.drawable.mk2_turn_0123};
        int[] turn3210Id = {R.drawable.mk0_turn_3210, R.drawable.mk1_turn_3210, R.drawable.mk2_turn_3210};
        int[] trailerBId = {R.drawable.mk0_trailer_b, R.drawable.mk1_trailer_b, R.drawable.mk2_trailer_b};
        int[] trailerEId = {R.drawable.mk0_trailer_e, R.drawable.mk1_trailer_e, R.drawable.mk2_trailer_e};
        line = new Sprite[countOfTypeBelt];
        turn_0123 = new Sprite[countOfTypeBelt];
        turn_3210 = new Sprite[countOfTypeBelt];
        trailer_b = new Sprite[countOfTypeBelt];
        trailer_e = new Sprite[countOfTypeBelt];
        for (int i = 0; i < countOfTypeBelt; i++) {
            line[i] = new Sprite(
                    BitmapFactory.decodeResource(context.getResources(), lineId[i]), speed[i]);
            turn_0123[i] = new Sprite(
                    BitmapFactory.decodeResource(context.getResources(), turn0123Id[i]), speed[i]);
            turn_3210[i] = new Sprite(
                    BitmapFactory.decodeResource(context.getResources(), turn3210Id[i]), speed[i]);
            trailer_b[i] = new Sprite(
                    BitmapFactory.decodeResource(context.getResources(), trailerBId[i]), speed[i]);
            trailer_e[i] = new Sprite(
                    BitmapFactory.decodeResource(context.getResources(), trailerEId[i]), speed[i]);
        }
    }

    public static void drawFieldLines(Canvas canvas) {
        for (int i = 1; i < field.length; i++) {
            for (int j = 1; j < field[i].length; j++) {
                if (field[i][j] != null) {
                    Rect rect = new Rect(
                            TimerWorkWithCoordinateThread.coordinate[0][i - 1],
                            TimerWorkWithCoordinateThread.coordinate[1][j - 1],
                            TimerWorkWithCoordinateThread.coordinate[0][i + 1],
                            TimerWorkWithCoordinateThread.coordinate[1][j + 1]
                    );
                        field[i][j].draw(canvas, rect, TimerWorkWithCoordinateThread.ward[i][j]);
                } else continue;
            }
        }
    }

    public static void updateSprite(int x, int y, int type) {
        field[x][y] = line[type];
    }

    public static void updateSpriteTurn0123(int x, int y, int type) {
        field[x][y] = turn_0123[type];
    }

    public static void updateSpriteTurn3210(int x, int y, int type) {
        field[x][y] = turn_3210[type];
    }

    private static void showLog(String message) {
        Log.d(LOG_TAG, LOG_TAG + ": " + message);
    }

    public static void clearSprite(int x, int y) {
        field[x][y] = null;
    }

    public static Sprite getField(int i, int j) {
        return field[i][j];
    }
    public static boolean getSmthUp(int i, int j, int type){
        if ((field[i][j]== turn_0123[type]
                && TimerWorkWithCoordinateThread.ward[i][j]==3
                && TimerWorkWithCoordinateThread.realWard[i][j]==0)
                ||(field[i][j]==turn_3210[type]
                && TimerWorkWithCoordinateThread.realWard[i][j]==2
                && TimerWorkWithCoordinateThread.ward[i][j]==1)){
            return false;
        } else return true;
    }
    public static boolean getSmthDown(int i, int j, int type){
        if ((field[i][j]== turn_0123[type]
                && TimerWorkWithCoordinateThread.ward[i][j]==1
                && TimerWorkWithCoordinateThread.realWard[i][j]==2)
                ||(field[i][j]==turn_3210[type]
                && TimerWorkWithCoordinateThread.realWard[i][j]==0
                && TimerWorkWithCoordinateThread.ward[i][j]==3)){
            return false;
        } else return true;
    }
}
