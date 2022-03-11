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
import android.util.Log;
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
     * Holds a counter that stores the number of lemons created on level load
     */
    int lemonCount;

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
    int touchX = -100, touchY = -100;
    Rect touchRect;
    boolean DEBUG = true;
    /**
     * Similar to DeltaTime in Unity. Holds time it took to process the last frame
     */
    static float frame_time = 0;

    /**
     * Holds the reason for leaving the level. Can be either pass, fail or quit
     */
    ExitReason exitReason;

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

        touchRect = new Rect();

        if (level == null) {
            level = "TestLevel.txt";
        }
        //This will try to load the file it points to. If not, the thread will terminate and the level passed screen will show
        try {
            //Creates an InputStream using from the levels found in the assets folder
            InputStream is = context.getAssets().open(level);
            //size holds the number of bytes in the file
            int size = is.available();
            //buffer is a list of all the charatcers
            byte[] buffer = new byte[size];

            //checks if there are more than 0 lines in the file
            if (is.read(buffer) > 0) {
                //converts the buffer to a string
                String text = new String(buffer);
                text = text.trim();

                //splits each line into a separate index in the array
                String[] lines = text.split("\n");

                //cycle through each line
                for (String s : lines) {
                    //split the line up by a space
                    String[] parts = s.trim().split(" ");
                    switch (parts[0]) {
                        case "StandardLemon":
                            lemonsBuffer.add(new StandardLemon(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
                            break;
                        case "StopperLemon":
                            lemonsBuffer.add(new StopperLemon(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
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
                        case "LavaPit":
                            objects.add(new LavaPit(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4])));
                            break;
                        case "Button":
                            objects.add(new Button(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), objects.get(Integer.parseInt(parts[3]) - lemonsBuffer.size() - 1)));
                            break;
                    }
                }
            }
            //closes the stream
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        lemonCount = lemonsBuffer.size();
        //Add images for all relevant classes
        StandardLemon.image = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.lemon);

        StopperLemon.image = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.stopper);

        //Initialises the frame bitmap and creates a canvas so I can draw to the bitmap
        frame = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
        tmp = new Canvas(frame);
        frameDest = new Rect();
        frameDest.top = 0;
        frameDest.left = 0;
        frameDest.bottom = 720;
        frameDest.right = 1280;

        //Initialises the collision rectangles
        lemonRect = new Rect();
        objectRect = new Rect();

        //Sets score to 0
        score = 0;
        exitReason = null;

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


        if (DEBUG) {
            myPaint.setColor(Color.BLACK);
            //tmp.drawCircle(touchX, touchY, 40, myPaint);
            tmp.drawRect(touchRect, myPaint);
        }

        for (Object object : objects) {
            switch (object.tag) {
                case BUTTON:
                case LAVA:
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
                Bitmap image = null;
                switch (lemon.tag) {
                    case STANDARD_LEMON:
                        image = StandardLemon.image;
                        break;
                    case STOPPER_LEMON:
                        image = StopperLemon.image;
                        break;
                }
                tmp.drawBitmap(image, null, lemonRect, null);
                //canvas.drawBitmap(Lemon.image, lemon.x - 32f, lemon.y - 32f, null);
            }
            //canvas.drawBitmap(Lemon.image, object.x1 - object.w, object.y1 - object.h, null);
        }
        myPaint.setColor(Color.BLUE);
        myPaint.setTextSize(64f);
        tmp.drawText("Score: " + score, 25f, 75f, myPaint);
        myPaint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, getWidth(), getHeight(), myPaint);
        frameDest.bottom = getHeight();
        frameDest.right = (1080 / getHeight()) * 1920;
        canvas.drawBitmap(frame, null, frameDest, null);
        myPaint.setColor(Color.RED);
        //canvas.drawRect(getWidth(),0,getWidth() - 150 , 150, myPaint);
        //myPaint.setColor(Color.BLACK);
        canvas.drawText("Quit ", getWidth() - 140f, 100f, myPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = (int) event.getX();
        y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touching = true;
                this.touchX = x;
                this.touchY = y;

                touchRect.top = y - 20;
                touchRect.left = x - 20;
                touchRect.bottom = y + 20;
                touchRect.right = x + 20;

                if (x > getWidth() - 150f && y < 150f) {
                    running = false;
                    exitReason = ExitReason.QUIT;
                }
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

        String exitMessage = "";

        if (exitReason == ExitReason.PASSED)
            exitMessage = "Passed";
        else if (exitReason == ExitReason.FAILED)
            exitMessage = "Failed";
        else if (exitReason == ExitReason.QUIT)
            exitMessage = "Quit";

        switchActivityIntent.putExtra("pass_fail", exitMessage);
        context.startActivity(switchActivityIntent);
    }

    /**
     * Checks if the game has ended. If it has, the thread will be stopped
     */
    private void CheckWon() {
        if (lemons.size() == 0 && lemonsBuffer.size() == 0) {
            running = false;
            if (score >= lemonCount * 3 / 4)
                exitReason = ExitReason.PASSED;
            else
                exitReason = ExitReason.FAILED;
        }
    }

    /**
     * Checks for collision between elements
     */
    private void CheckCollision() {
        //Lemons do not collide with each other, not do objects. So only check for collision between lemons and objects exclusively
        for (Lemon lemon : lemons) {
            Rect lemonRect = new Rect((int) lemon.x, (int) lemon.y,
                    (int) lemon.x + lemon.w, (int) lemon.y + lemon.h);
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

        // Checks if the player is touching an object or lemon.
        // This can be used to activate switches and interact with lemons
        // Lemons take precedence over objects. Only one object can be touched
        // At a time
        if (touching) {
            boolean touchedItem = false;
            for (Lemon lemon : lemons) {
                Rect lemonRect = new Rect((int) lemon.x, (int) lemon.y,
                        (int) lemon.x + lemon.w, (int) lemon.y + lemon.h);
                if (lemonRect.intersect(touchRect)) {
                    lemon.OnTouch();
                    touchedItem = true;
                    break;
                }
            }

            if (!touchedItem)
                for (Object object : objects) {
                    Rect objectRect = new Rect((int) object.x, (int) object.y,
                            (int) object.x + object.w, (int) object.y + object.h);
                    if (objectRect.intersect(touchRect)) {
                        object.OnTouch();
                        break;
                    }
                }
        }
    }

    /**
     * Updates the game. Is called once per frame
     */
    private void DoUpdates() {
        for (int i = 0; i < lemons.size(); i++) {
            Lemon lemon = lemons.get(i);

            if (lemon.isLeft) {
                lemon.OnDeath();
                RemoveLemon(lemon);
                i--;
                score++;
                continue;
            }
            if (lemon.isDead) {
                lemon.OnDeath();
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
