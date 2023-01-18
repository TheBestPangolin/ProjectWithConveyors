package ru.shanin.workwithsurfaceviewmy.surfaceview.timers;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import ru.shanin.workwithsurfaceviewmy.surfaceview.sprite.Field;

public class TimerWorkWithCoordinateThread extends Thread {
    private final String LOG_TAG = TimerWorkWithCoordinateThread.class.getSimpleName();
    private final Timer timer;
    private final TimerWorkWithCoordinateThread.TimerTaskUpdateCoordinate task;
    private static boolean running;
    public static int[][] clickCounter;
    public static int[][] ward;
    public static int[][] coordinate;
    public static int[][] realWard;

    public TimerWorkWithCoordinateThread(Context context) {
        timer = new Timer();
        task = new TimerWorkWithCoordinateThread.TimerTaskUpdateCoordinate(context);
    }


    @Override
    public synchronized void start() {
        int PERIOD = 50;// ms, for 20 tic/sec
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

    public void updateTouchCoordinate(int newX, int newY) {
        task.updateTouchCoordinate(newX, newY);
    }

    private void showLog(String message) {
        Log.d(LOG_TAG, LOG_TAG + ": " + message);
    }

    private static class TimerTaskUpdateCoordinate extends TimerTask {
        private final String LOG_TAG = TimerTaskUpdateCoordinate.class.getSimpleName();
        private final int COUNT_SPRITE_TYPE = 13;
        private final int COUNT_WARD_TYPE = 4;

        private int step;
        private int startX;
        private int stopX;
        private int start;
        private int stop;
        private int startY;
        private int stopY;
        private int delta;
        private int touchX;
        private int touchY;
        private int touchX_old;
        private int touchY_old;

        private int sizeClickableSpritesField = 8;


        private boolean showClickLog;


        public TimerTaskUpdateCoordinate(Context context) {
            initDisplayMetrics(context);
            initMath();
            initCoordinate(start + step, start + step, step + 0);
        }

        private void showLog(String message) {
            Log.d(LOG_TAG, LOG_TAG + ": " + message);
        }

        private void initDisplayMetrics(Context context) {
            showLog("initDisplayMetrics");
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            showLog("ScreenSize : x =  " + dm.widthPixels + ", y = " + dm.heightPixels);
            int minPx = Math.min(dm.widthPixels, dm.heightPixels);
            step = minPx > 1000 ? 128 : 80;
            showLog("step = " + step);
            delta = (minPx - (step << 3) >> 1);
            showLog("delta = " + delta);
            start = minPx * 0 + delta;
            stop = minPx * 1 - delta;
            startX = startY = start + (step >> 1);
            stopX = stopY = stop - (step >> 1);

        }

        private void initMath() {
            ward = new int[sizeClickableSpritesField][sizeClickableSpritesField];
            realWard = new int[sizeClickableSpritesField][sizeClickableSpritesField];
            clickCounter = new int[sizeClickableSpritesField][sizeClickableSpritesField];
            coordinate = new int[2][sizeClickableSpritesField];
            for (int i = 0; i < sizeClickableSpritesField; i++) {
                Arrays.fill(ward[i], 4);
                Arrays.fill(realWard[i], 4);
                Arrays.fill(clickCounter[i], 0xC);
            }
        }

        public void initCoordinate(int x0, int y0, int step) {
            for (int i = 0; i < sizeClickableSpritesField; i++) {
                coordinate[0][i] = x0 + i * step - (step >> 1);
                coordinate[1][i] = y0 + i * step - (step >> 1);
                showLog("x = " + coordinate[0][i] + ", y = " + coordinate[1][i]);
            }
        }

        @Override
        public void run() {
            if (running) {
                if (isChangeTouchCoordinate()) {
                    updateEventCoordinateAfterWorkWithEvent();
                    updateCoor();
                }
            }
        }

        private boolean isChangeTouchCoordinate() {
            return !(touchX == touchX_old && touchY == touchY_old);
        }

        public void updateTouchCoordinate(int newX, int newY) {
            touchX_old = touchX;
            touchX = newX;
            touchY_old = touchY;
            touchY = newY;
            showClickLog = true;
        }

        private void updateEventCoordinateAfterWorkWithEvent() {
            touchX_old = touchX;
            touchY_old = touchY;
        }

        private void updateCoor() {
            int temp_x = (touchX - delta) / step;
            int temp_y = (touchY - delta) / step;
            int flag = 0;
            if (temp_x > 0 && temp_x < 7) flag++;
            if (temp_y > 0 && temp_y < 7) flag++;
            // it's work
            if (showClickLog) {
                Log.w("Click", "Click on square : " + temp_x + ", " + temp_y);
            }
            if (flag == 2) updateWard(temp_x, temp_y);
            showClickLog = false;
        }

        private void updateWard(int i, int j) {
            clickCounter[i][j] = (++clickCounter[i][j]) % COUNT_SPRITE_TYPE;
            if (clickCounter[i][j] == (COUNT_SPRITE_TYPE - 1)) {
                Field.clearSprite(i, j);
                if ((realWard[i][j - 1] == 0 || realWard[i][j - 1] == 2) && ward[i][j - 1] != realWard[i][j - 1]) {
                    if (realWard[i][j - 2] == 1) {
                        if (realWard[i][j - 1] == 0) {
                            if (realWard[i - 1][j - 1] == 0) {
                                ward[i][j - 1] = realWard[i][j - 1];
                                Field.updateSprite(i, j - 1, clickCounter[i][j - 1] / 4);
                            } else {
                                ward[i][j - 1] = 3;
                                Field.updateSpriteTurn3210(i, j - 1, clickCounter[i][j - 1] / 4);
                            }
                        } else if (realWard[i][j - 1] == 2) {
                            if (realWard[i + 1][j - 1] == 2) {
                                ward[i][j - 1] = realWard[i][j - 1];
                                Field.updateSprite(i, j - 1, clickCounter[i][j - 1] / 4);
                            } else {
                                ward[i][j - 1] = 1;
                                Field.updateSpriteTurn0123(i, j - 1, clickCounter[i][j - 1] / 4);
                            }
                        }
                    } else {
                        ward[i][j - 1] = realWard[i][j - 1];
                        Field.updateSprite(i, j - 1, clickCounter[i][j - 1] / 4);
                    }
                } else if ((realWard[i][j - 1] == 0 || realWard[i][j - 1] == 2)) {
                    if (realWard[i][j - 2] == 1) {
                        if (realWard[i][j - 1] == 0) {
                            if (realWard[i - 1][j - 1] == 0) {
                                Field.updateSprite(i, j - 1, clickCounter[i][j - 1] / 4);
                            } else {
                                ward[i][j - 1] = 3;
                                Field.updateSpriteTurn3210(i, j - 1, clickCounter[i][j - 1] / 4);
                            }
                        } else if (realWard[i][j - 1] == 2) {
                            if (realWard[i + 1][j - 1] == 2) {
                                Field.updateSprite(i, j - 1, clickCounter[i][j - 1] / 4);
                            } else {
                                ward[i][j - 1] = 1;
                                Field.updateSpriteTurn0123(i, j - 1, clickCounter[i][j - 1] / 4);
                            }
                        }
                    }
                }
            } else {
                ward[i][j] = clickCounter[i][j] % COUNT_WARD_TYPE;
                realWard[i][j] = ward[i][j];
                switch (ward[i][j]) {
                    case 0:
                    case 2:
                        //making myself turn
                        if (Field.getField(i, j + 1) != null
                                && (Field.getField(i, j - 1) == null
                                || realWard[i][j - 1] != 1)
                                && realWard[i][j + 1] == 3) {
                            if((ward[i][j] == 0 && realWard[i - 1][j] == 0)
                                    ||(realWard[i + 1][j] == 2 && ward[i][j] == 2)){
                                Field.updateSprite(i, j, clickCounter[i][j] / 4);
                            }
                            if (ward[i][j] == 0 && realWard[i - 1][j] != 0) {
                                Field.updateSpriteTurn0123(i, j, clickCounter[i][j] / 4);
                                ward[i][j] = realWard[i][j + 1];
                            } else if (realWard[i + 1][j] != 2 && ward[i][j] == 2) {
                                Field.updateSpriteTurn3210(i, j, clickCounter[i][j] / 4);
                                ward[i][j] = realWard[i][j + 1] - 2;
                            }

                        } else if (Field.getField(i, j - 1) != null
                                && (Field.getField(i, j + 1) == null
                                || realWard[i][j + 1] != 3)
                                && realWard[i][j - 1] == 1) {
                            if((ward[i][j] == 0 && realWard[i - 1][j] == 0)
                                    ||(realWard[i + 1][j] == 2 && ward[i][j] == 2)){
                                Field.updateSprite(i, j, clickCounter[i][j] / 4);
                            }
                            if (ward[i][j] == 0 && realWard[i - 1][j] != 0) {
                                Field.updateSpriteTurn3210(i, j, clickCounter[i][j] / 4);
                                ward[i][j] = realWard[i][j - 1] + 2;
                            } else if (realWard[i + 1][j] != 2 && ward[i][j] == 2) {
                                Field.updateSpriteTurn0123(i, j, clickCounter[i][j] / 4);
                                ward[i][j] = realWard[i][j - 1];
                            }

                        } else {
                            Field.updateSprite(i, j, clickCounter[i][j] / 4);
                        }
                        //making others to turn
                        if (realWard[i][j] == 0) {
                            if (Field.getField(i + 1, j) != null
                                    && realWard[i + 1][j] != ward[i + 1][j]
                                    && ward[i + 1][j] != realWard[i][j]
                                    && realWard[i + 1][j] != 2) {
                                ward[i + 1][j] = realWard[i + 1][j];
                                Field.updateSprite(i + 1, j, clickCounter[i + 1][j] / 4);
                                break;
                            }
                            if (Field.getField(i + 1, j) != null
                                    && realWard[i + 1][j] == ward[i + 1][j]
                                    && realWard[i + 2][j] != 2) {
                                if (realWard[i + 1][j] == 1 && realWard[i + 1][j - 1] != 1) {
                                    ward[i + 1][j] = 0;
                                    Field.updateSpriteTurn0123(i + 1, j, clickCounter[i + 1][j] / 4);
                                } else if (realWard[i + 1][j] == 3 && realWard[i + 1][j + 1] != 3) {
                                    ward[i + 1][j] = 0;
                                    Field.updateSpriteTurn3210(i + 1, j, clickCounter[i + 1][j] / 4);
                                }
                            }
                        } else {
                            if (Field.getField(i - 1, j) != null
                                    && realWard[i - 1][j] != ward[i - 1][j]
                                    && ward[i - 1][j] != realWard[i][j]
                                    && realWard[i - 1][j] != 0) {
                                ward[i - 1][j] = realWard[i - 1][j];
                                Field.updateSprite(i - 1, j, clickCounter[i - 1][j] / 4);
                                break;
                            }
                            if (Field.getField(i - 1, j) != null
                                    && realWard[i - 1][j] == ward[i - 1][j]
                                    && realWard[i - 2][j] != 0) {
                                if (realWard[i - 1][j] == 1 && realWard[i - 1][j - 1] != 1) {
                                    ward[i - 1][j] = 2;
                                    Field.updateSpriteTurn3210(i - 1, j, clickCounter[i - 1][j] / 4);
                                } else if (realWard[i - 1][j] == 3 && realWard[i - 1][j + 1] != 3) {
                                    ward[i - 1][j] = 2;
                                    Field.updateSpriteTurn0123(i - 1, j, clickCounter[i - 1][j] / 4);
                                }
                            }
                        }
                        //cancelling of turn
                        if (realWard[i][j] == 2) {
                            if (ward[i][j + 1] == realWard[i][j + 1]) {
                                if (realWard[i][j + 1] == 1) {
                                    if (realWard[i - 1][j + 1] == 0) {
                                        ward[i][j + 1] = 0;
                                        Field.updateSpriteTurn0123(i, j + 1, clickCounter[i][j + 1] / 4);
                                    } else if (realWard[i + 1][j + 1] == 2) {
                                        ward[i][j + 1] = 2;
                                        Field.updateSpriteTurn3210(i, j + 1, clickCounter[i][j + 1] / 4);
                                    }
                                }

                            }
                            if ((realWard[i][j + 1] == 0
                                    || realWard[i][j + 1] == 2)) {
                                if (realWard[i][j + 2] != 3) {
                                    ward[i][j + 1] = realWard[i][j + 1];
                                    Field.updateSprite(i, j + 1, clickCounter[i][j + 1] / 4);

                                } else {

                                    if (realWard[i][j + 1] == 0 && realWard[i][j+2]==3) {
                                        ward[i][j + 1] = 3;
                                        Field.updateSpriteTurn0123(i, j + 1, clickCounter[i][j + 1] / 4);
                                    } else if(realWard[i][j+2]==3){
                                        ward[i][j + 1] = 1;
                                        Field.updateSpriteTurn3210(i, j + 1, clickCounter[i][j + 1] / 4);
                                    }
                                }
                            }
                        } else if (clickCounter[i][j] / 4 != 0) {
                            if (ward[i][j - 1] == realWard[i][j - 1]) {
                                if (realWard[i][j - 1] == 3) {
                                    if (realWard[i - 1][j - 1] == 0) {
                                        ward[i][j - 1] = 0;
                                        Field.updateSpriteTurn3210(i, j - 1, clickCounter[i][j - 1] / 4);
                                    } else if (realWard[i + 1][j - 1] == 2) {
                                        ward[i][j - 1] = 2;
                                        Field.updateSpriteTurn0123(i, j - 1, clickCounter[i][j - 1] / 4);
                                    }
                                }

                            }
                            if ((realWard[i][j - 1] == 0
                                    || realWard[i][j - 1] == 2)) {
                                if (realWard[i][j - 2] != 1) {
                                    ward[i][j - 1] = realWard[i][j - 1];
                                    Field.updateSprite(i, j - 1, clickCounter[i][j - 1] / 4);

                                } else {
                                    if (realWard[i][j - 1] == 0 && realWard[i-1][j-1]!=0) {
                                        ward[i][j - 1] = 3;
                                        Field.updateSpriteTurn3210(i, j - 1, clickCounter[i][j - 1] / 4);
                                    } else if(realWard[i+1][j-1]!=2&& realWard[i][j-1]==2) {
                                        ward[i][j - 1] = 1;
                                        Field.updateSpriteTurn0123(i, j - 1, clickCounter[i][j - 1] / 4);
                                    }
                                }
                            }
                        }
                        break;
                    case 1:
                    case 3:
                        //making myself turn
                        if (Field.getField(i - 1, j) != null
                                && (Field.getField(i + 1, j) == null
                                || realWard[i + 1][j] != 2)
                                && realWard[i - 1][j] == 0) {
                            if (ward[i][j] == 1 && realWard[i][j - 1] != 1) {
                                Field.updateSpriteTurn0123(i, j, clickCounter[i][j] / 4);
                                ward[i][j] = realWard[i - 1][j];
                            } else if (ward[i][j] == 3 && realWard[i][j + 1] != 3) {
                                Field.updateSpriteTurn3210(i, j, clickCounter[i][j] / 4);
                                ward[i][j] = realWard[i - 1][j];
                            } else {
                                Field.updateSprite(i, j, clickCounter[i][j] / 4);
                            }
                        } else if (Field.getField(i + 1, j) != null
                                && (Field.getField(i - 1, j) == null
                                || realWard[i - 1][j] != 0)
                                && realWard[i + 1][j] == 2) {
                            if (ward[i][j] == 1 && realWard[i][j - 1] != 1) {
                                Field.updateSpriteTurn3210(i, j, clickCounter[i][j] / 4);
                                ward[i][j] = realWard[i + 1][j];
                            } else if (ward[i][j] == 3 && realWard[i][j + 1] != 3) {
                                Field.updateSpriteTurn0123(i, j, clickCounter[i][j] / 4);
                                ward[i][j] = realWard[i + 1][j];
                            } else {
                                Field.updateSprite(i, j, clickCounter[i][j] / 4);
                            }

                        } else {
                            Field.updateSprite(i, j, clickCounter[i][j] / 4);
                        }
                        //making others to turn
                        if (realWard[i][j] == 1) {
                            if (Field.getField(i, j + 1) != null
                                    && realWard[i][j + 1] != ward[i][j + 1]
                                    && realWard[i][j + 1] != 3
                                    && Field.getSmthDown(i, j + 1, clickCounter[i][j + 1] / 4)) {
                                ward[i][j + 1] = realWard[i][j + 1];
                                Field.updateSprite(i, j + 1, clickCounter[i][j + 1] / 4);
                                break;
                            }
                            if (Field.getField(i, j + 1) != null
                                    && realWard[i][j + 1] == ward[i][j + 1]
                                    && realWard[i][j + 2] != 3) {
                                if (realWard[i][j + 1] == 0 && realWard[i - 1][j + 1] != 0) {
                                    ward[i][j + 1] = 3;
                                    Field.updateSpriteTurn3210(i, j + 1, clickCounter[i][j + 1] / 4);
                                } else if (realWard[i][j + 1] == 2 && realWard[i + 1][j + 1] != 2) {
                                    ward[i][j + 1] = 1;
                                    Field.updateSpriteTurn0123(i, j + 1, clickCounter[i][j + 1] / 4);
                                }
                            }
                        } else {
                            if (Field.getField(i, j - 1) != null
                                    && realWard[i][j - 1] != ward[i][j - 1]
                                    && realWard[i][j - 1] != 1
                                    && Field.getSmthUp(i, j - 1, clickCounter[i][j - 1] / 4)) {
                                ward[i][j - 1] = realWard[i][j - 1];
                                Field.updateSprite(i, j - 1, clickCounter[i][j - 1] / 4);
                                break;
                            }
                            if (Field.getField(i, j - 1) != null
                                    && realWard[i][j - 1] == ward[i][j - 1]
                                    && realWard[i][j - 2] != 1) {
                                if (realWard[i][j - 1] == 0
                                        && realWard[i - 1][j - 1] != 0) {
                                    ward[i][j - 1] = 3;
                                    Field.updateSpriteTurn0123(i, j - 1, clickCounter[i][j - 1] / 4);
                                } else if (realWard[i][j - 1] == 2 && realWard[i + 1][j - 1] != 2) {
                                    ward[i][j - 1] = 1;
                                    Field.updateSpriteTurn3210(i, j - 1, clickCounter[i][j - 1] / 4);
                                }
                            }
                        }
                        //cancelling of turn
                        if (realWard[i][j] == 1) {
                            if (ward[i + 1][j] == realWard[i + 1][j]) {
                                if (realWard[i + 1][j] == 0) {
                                    if (realWard[i + 1][j + 1] == 3) {
                                        ward[i + 1][j] = 3;
                                        Field.updateSpriteTurn0123(i + 1, j, clickCounter[i + 1][j] / 4);
                                    } else if (realWard[i + 1][j - 1] == 1) {
                                        ward[i + 1][j] = 3;
                                        Field.updateSpriteTurn3210(i + 1, j, clickCounter[i + 1][j] / 4);
                                    }
                                }
                            }
                            if ((realWard[i + 1][j] == 1
                                    || realWard[i + 1][j] == 3)) {
                                if (realWard[i + 2][j] != 2) {
                                    ward[i + 1][j] = realWard[i + 1][j];
                                    Field.updateSprite(i + 1, j, clickCounter[i + 1][j] / 4);

                                } else if ((realWard[i - 1][j] == 1 ? realWard[i + 1][j + 1] != 1 : realWard[i + 1][j - 1] != 3)) {
                                    ward[i + 1][j] = 2;
                                    if (realWard[i + 1][j] == 1) {
                                        Field.updateSpriteTurn3210(i + 1, j, clickCounter[i + 1][j] / 4);
                                    } else {
                                        Field.updateSpriteTurn0123(i + 1, j, clickCounter[i + 1][j] / 4);
                                    }
                                }
                            }
                        } else {
                            if (realWard[i - 1][j] == ward[i - 1][j]) {
                                if (realWard[i - 1][j] == 2) {
                                    if (realWard[i - 1][j + 1] == 3) {
                                        ward[i - 1][j] = 1;
                                        Field.updateSpriteTurn3210(i - 1, j, clickCounter[i - 1][j] / 4);
                                    } else if (realWard[i - 1][j - 1] == 1) {
                                        ward[i - 1][j] = 1;
                                        Field.updateSpriteTurn0123(i - 1, j, clickCounter[i - 1][j] / 4);
                                    }
                                }
                            }
                            if (realWard[i - 1][j] == 1
                                    || realWard[i - 1][j] == 3) {
                                if (realWard[i - 2][j] != 0) {
                                    ward[i - 1][j] = realWard[i - 1][j];
                                    Field.updateSprite(i - 1, j, clickCounter[i - 1][j] / 4);
                                    break;
                                } else if ((realWard[i - 1][j] == 1 ? realWard[i - 1][j - 1] != 1 : realWard[i - 1][j + 1] != 3)) {
                                    ward[i - 1][j] = 0;
                                    if (realWard[i - 1][j] == 3) {
                                        Field.updateSpriteTurn3210(i - 1, j, clickCounter[i - 1][j] / 4);
                                    } else {
                                        Field.updateSpriteTurn0123(i - 1, j, clickCounter[i - 1][j] / 4);
                                    }
                                }
                            }
                        }
                        break;
                }

                Log.w("Click", "Ward on square : " + ward[i][j] + ";");
                Log.w("Click", "clickCounter on square : " + clickCounter[i][j] + ";");
            }
        }
    }
}
