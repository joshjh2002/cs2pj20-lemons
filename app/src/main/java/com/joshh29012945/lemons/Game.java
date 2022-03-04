package com.joshh29012945.lemons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Game extends SurfaceView implements Runnable {

    /**
     * Lemon list of current "alive" lemons
     */
    ArrayList<Lemon> lemons;
    /**
     * Lemon list of all lemons currently not on the screen. These will gradually be moved to "lemons" by the spawner
     */
    ArrayList<Lemon> lemonsBuffer;
    /**
     * A list of all "alive" objects
     */
    ArrayList<Object> objects;

    /**
     * Paint object to draw text to screen
     */
    Paint myPaint;

    /**
     * Determines the state of the run thread. If false, the thread will stop and the game will end
     */
    Boolean running;
    /**
     * Temporary canvas to draw each frame to
     */
    Canvas tmp;
    /**
     * The bitmap tmp will draw to
     */
    Bitmap frame;
    /**
     * The destination of the frame, so if the screen is smaller than 1920*1080, I can scale the image down
     */
    Rect frameDest;
    /**
     * Collision rectangle for the lemon
     */
    Rect lemonRect;
    /**
     * Collision rectangle for the object
     */
    Rect objectRect;
    /**
     * Score
     */
    int score;

    /**
     * Default starting position for any object that has not got a position
     */
    static int x = -50, y = -50;
    /**
     * True when someone is touching the screen
     */
    static Boolean touching = false;
    /**
     * Similar to DeltaTime in Unity. Holds time it took to process the last frame
     */
    static float frame_time = 0;

    /**
     * Takes Context as a parameter. Creates a new "Game" and adds in all objects stored in asset file
     *
     * @param context the current context the game is ran in
     */
    public Game(Context context, String level) {
        super(context);
        //Initialises the paint, and arrays
        myPaint = new Paint();

        lemons = new ArrayList<>();
        lemonsBuffer = new ArrayList<>();
        objects = new ArrayList<>();
        if (level == null) {
            level = "TestLevel.txt";
        }
        //This will try to load the file it points to. If not, the thread will terminate and the level passed screen will show
        try {
            InputStream is = context.getAssets().open(level);
            int size = is.available();
            byte[] buffer = new byte[size];
            if (is.read(buffer) > 0) {
                String text = new String(buffer);
                text = text.trim();
                String[] lines = text.split("\n");

                for (String s : lines) {
                    String[] parts = s.trim().split(" ");
                    switch (parts[0]) {
                        case "StandardLemon":
                            lemonsBuffer.add(new StandardLemon(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
                            break;
                        case "Platform":
                            objects.add(new Platform(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                                    Integer.parseInt(parts[3]), Integer.parseInt(parts[4])));
                            break;
                        case "JumpPad":
                            objects.add(new JumpPad(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                                    Integer.parseInt(parts[3]), Integer.parseInt(parts[4])));
                            break;
                        case "Exit":
                            CreateExit(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                            break;
                        case "Spawner":
                            objects.add(new Spawner(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), this, Integer.parseInt(parts[3])));
                            break;
                    }
                }
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Add images for all relevant classes
        StandardLemon.image = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.lemon);

        //Initialises the frame bitmap and creates a canvas so I can draw to the bitmap
        frame = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
        tmp = new Canvas(frame);
        frameDest = new Rect();
        frameDest.top = 0;
        frameDest.left = 0;
        frameDest.bottom = 1080;
        frameDest.right = 1920;

        //Initialises the collision rectangles
        lemonRect = new Rect();
        objectRect = new Rect();

        //Sets score to 0
        score = 0;

        running = true;
    }

    /**
     * Creates an exit as the coordinates specified. An exit consists of an "Exit" and "Door"
     *
     * @param x coordinate of the exit
     * @param y coordinate of the exit
     */
    private void CreateExit(int x, int y) {
        objects.add(new Exit(x, y));
        objects.add(new Door(x + 32, y + 64));
    }

    /**
     * Removes the lemon from the list
     *
     * @param lemon that is to be removed
     */
    public void RemoveLemon(Lemon lemon) {
        lemons.remove(lemon);
    }

    /**
     * Overridden function to draw the graphics to the screen
     *
     * @param canvas that will be drawn on
     */
    protected void onDraw(Canvas canvas) {
        myPaint.setColor(Color.WHITE);
        myPaint.setStrokeWidth(3);

        //Clears the Canvas
        tmp.drawRect(0, 0, 1920, 1080, myPaint);

        for (Object object : objects) {
            switch (object.tag) {
                case DOOR:
                case JUMP:
                case EXIT:
                case PLATFORM:
                    objectRect.left = (int) object.x;
                    objectRect.top = (int) object.y;
                    objectRect.right = (int) object.x + object.w;
                    objectRect.bottom = (int) object.y + object.h;
                    tmp.drawRect(objectRect, object.paint);
                    break;
            }

            for (int i = 0; i < lemons.size(); i++) {
                Lemon lemon = lemons.get(i);
                lemonRect.left = (int) lemon.x;
                lemonRect.top = (int) lemon.y;
                lemonRect.right = (int) lemon.x + lemon.w;
                lemonRect.bottom = (int) lemon.y + lemon.h;
                tmp.drawBitmap(StandardLemon.image, null, lemonRect, null);
                //canvas.drawBitmap(Lemon.image, lemon.x - 32f, lemon.y - 32f, null);
            }
            //canvas.drawBitmap(Lemon.image, object.x1 - object.w, object.y1 - object.h, null);
        }
        myPaint.setColor(Color.BLUE);
        myPaint.setTextSize(64f);
        tmp.drawText("Score: " + score, 25f, 75f, myPaint);
        myPaint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, getWidth(), getHeight(), myPaint);
        canvas.drawBitmap(frame, null, frameDest, null);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = (int) event.getX();
        y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touching = true;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                touching = false;
                break;
        }
        return true;
    }

    /**
     * Added through the implementation of "Runnable". This is the game thread
     */
    public void run() {
        do {
            long startTime = System.currentTimeMillis();
            CheckCollision();
            DoUpdates();
            CheckWon();
            invalidate();
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime);
            frame_time = duration / 1000.0f;
        } while (running);

        Context context = getContext();
        Intent switchActivityIntent = new Intent(context, LevelFinished.class);
        switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        switchActivityIntent.putExtra("pass_fail", "Passed");
        context.startActivity(switchActivityIntent);
    }

    /**
     * Checks if the game has ended. If it has, the thread will be stopped
     */
    private void CheckWon() {
        if (lemons.size() == 0 && lemonsBuffer.size() == 0) {
            running = false;
        }
    }

    /**
     * Checks for collision between elements
     */
    private void CheckCollision() {
        //Lemons do not collide with each other, not do objects. So only check for collision between lemons and objects exclusively
        for (Lemon lemon : lemons) {
            Rect lemonRect = new Rect((int) lemon.x, (int) lemon.y, (int) lemon.x + lemon.w, (int) lemon.y + lemon.h);
            boolean collisionFound = false;
            for (Object object : objects) {
                Rect objectRect = new Rect((int) object.x, (int) object.y,
                        (int) object.x + object.w, (int) object.y + object.h);
                if (objectRect.intersect(lemonRect)) {
                    object.OnCollide(lemon);
                    collisionFound = true;
                }
            }
            lemon.isColliding = collisionFound;
        }
    }

    /**
     * Updates the game. Is called once per frame
     */
    private void DoUpdates() {
        for (int i = 0; i < lemons.size(); i++) {
            Lemon lemon = lemons.get(i);

            if (lemon.isDead) {
                lemon.OnDeath();
                score++;
                RemoveLemon(lemon);
                i--;
                continue;
            }
            lemon.Update();
        }

        for (Object object : objects)
            object.Update();
    }
}