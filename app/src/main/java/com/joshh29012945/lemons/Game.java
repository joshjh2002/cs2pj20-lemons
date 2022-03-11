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
import java.text.DecimalFormat;
import java.util.ArrayList;

@SuppressLint("ViewConstructor")
public class Game extends SurfaceView implements Runnable {

    /**
     * The amount of time you have to complete the level
     */
    private float time_limit;
    /**
     * Lemon list of current "alive" lemons
     */
    ArrayList<Lemon> lemons;
    /**
     * Lemon list of all lemons currently not on the screen. These will gradually be moved to "lemons" by the spawner
     */
    ArrayList<Lemon> lemons_buffer;
    /**
     * A list of all "alive" objects
     */
    ArrayList<Object> objects;

    /**
     * Holds a counter that stores the number of lemons created on level load
     */
    int lemon_count;

    /**
     * Paint object to draw text to screen
     */
    Paint paint;

    /**
     * Determines the state of the run thread. If false, the thread will stop and the game will end
     */
    Boolean is_running;
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
    Rect frame_dest_rect;
    /**
     * Collision rectangle for the lemon
     */
    Rect lemon_rect;
    /**
     * Collision rectangle for the object
     */
    Rect object_rect;
    /**
     * Score
     */
    int score;

    /**
     * True when someone is touching the screen
     */
    static Boolean touching = false;

    /**
     * Current x and y position of the touch
     */
    int touchX = -100, touchY = -100;

    /**
     * A rectangle that will hold the x and y touch position.
     * If this rectangle collides with an object, then you have touched that object
     */
    Rect touch_rect;

    /**
     * If true, then will be used to debug certain elements. Like drawing the touch location
     */
    boolean DEBUG = true;
    /**
     * Similar to DeltaTime in Unity. Holds time it took to process the last frame
     */
    static float frame_time = 0;

    /**
     * Holds how long the game has currently been running for
     */
    private float run_time;

    /**
     * Holds the reason for leaving the level. Can be either pass, fail or quit
     */
    ExitReason exit_reason;

    /**
     * Used to format the time remaining
     */
    private static final DecimalFormat time_formatter = new DecimalFormat("00");

    /**
     * Takes Context as a parameter. Creates a new "Game" and adds in all objects stored in asset file
     *
     * @param context the current context the game is ran in
     */
    public Game(Context context, String level) {
        super(context);
        //Initialises the paint, and arrays
        paint = new Paint();

        lemons = new ArrayList<>();
        lemons_buffer = new ArrayList<>();
        objects = new ArrayList<>();

        touch_rect = new Rect();
        run_time = 0;

        if (level == null) {
            level = "TestLevel.txt";
        }
        //This will try to load the file it points to. If not, the thread will terminate and the level passed screen will show
        try {
            //Creates an InputStream using from the levels found in the assets folder
            InputStream is = context.getAssets().open(level);
            //size holds the number of bytes in the file
            int size = is.available();
            //buffer is a list of all the characters
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
                            lemons_buffer.add(new StandardLemon(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
                            break;
                        case "StopperLemon":
                            lemons_buffer.add(new StopperLemon(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
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
                            objects.add(new Button(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), objects.get(Integer.parseInt(parts[3]) - lemons_buffer.size() - 1)));
                            break;
                        case "TimeLimit":
                            this.time_limit = Float.parseFloat(parts[1]);
                            break;
                    }
                }
            }
            //closes the stream
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        lemon_count = lemons_buffer.size();
        //Add images for all relevant classes
        StandardLemon.image = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.lemon);

        StopperLemon.image = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.stopper);

        //Initialises the frame bitmap and creates a canvas so I can draw to the bitmap
        frame = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
        tmp = new Canvas(frame);
        frame_dest_rect = new Rect();
        frame_dest_rect.top = 0;
        frame_dest_rect.left = 0;
        frame_dest_rect.bottom = 720;
        frame_dest_rect.right = 1280;

        //Initialises the collision rectangles
        lemon_rect = new Rect();
        object_rect = new Rect();

        //Sets score to 0
        score = 0;
        exit_reason = null;

        is_running = true;
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
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(3);

        //Clears the Canvas
        tmp.drawRect(0, 0, 1920, 1080, paint);


        if (DEBUG) {
            paint.setColor(Color.BLACK);
            //tmp.drawCircle(touchX, touchY, 40, myPaint);
            tmp.drawRect(touch_rect, paint);
        }

        //Draws all objects, provided that object is not "dead"
        for (Object object : objects) {
            if (!object.isDead) {
                switch (object.tag) {
                    case BUTTON:
                    case LAVA:
                    case DOOR:
                    case JUMP:
                    case EXIT:
                    case PLATFORM:
                        object_rect.left = (int) object.x;
                        object_rect.top = (int) object.y;
                        object_rect.right = (int) object.x + object.w;
                        object_rect.bottom = (int) object.y + object.h;
                        tmp.drawRect(object_rect, object.paint);
                        break;
                }
            }

            //draws all lemons
            for (int i = 0; i < lemons.size(); i++) {
                Lemon lemon = lemons.get(i);
                lemon_rect.left = (int) lemon.x;
                lemon_rect.top = (int) lemon.y;
                lemon_rect.right = (int) lemon.x + lemon.w;
                lemon_rect.bottom = (int) lemon.y + lemon.h;
                Bitmap image = null;
                switch (lemon.tag) {
                    case STANDARD_LEMON:
                        image = StandardLemon.image;
                        break;
                    case STOPPER_LEMON:
                        image = StopperLemon.image;
                        break;
                }
                tmp.drawBitmap(image, null, lemon_rect, null);
                //canvas.drawBitmap(Lemon.image, lemon.x - 32f, lemon.y - 32f, null);
            }
            //canvas.drawBitmap(Lemon.image, object.x1 - object.w, object.y1 - object.h, null);
        }
        // Draws the score to the top left of the screen
        paint.setColor(Color.BLUE);
        paint.setTextSize(64f);
        tmp.drawText("Score: " + score, 25f, 75f, paint);

        //Fills in the background to easily distinguish between background and the level bitmap
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        frame_dest_rect.bottom = getHeight();
        frame_dest_rect.right = (1080 / getHeight()) * 1920;
        canvas.drawBitmap(frame, null, frame_dest_rect, null);

        // Draws the exit button
        paint.setColor(Color.RED);
        canvas.drawText("Quit ", getWidth() - 140f, 100f, paint);

        // Draws the time remaining
        paint.setColor(Color.BLACK);
        canvas.drawText("Time Remaining: " + time_formatter.format(time_limit - run_time), 0, getHeight() - 50f, paint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // The user has tapped the screen
                touching = true;
                this.touchX = (int) event.getX();

                this.touchY = (int) event.getY();

                touch_rect.top = touchY - 20;
                touch_rect.left = touchX - 20;
                touch_rect.bottom = touchY + 20;
                touch_rect.right = touchX + 20;

                // Checks if the user has pressed the Quit button
                if (touchX > getWidth() - 150f && touchY < 150f) {
                    is_running = false;
                    exit_reason = ExitReason.QUIT;
                }
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                // When the user is no longer touching the screen
                touching = false;
                touch_rect.top = -100;
                touch_rect.left = -100;
                touch_rect.bottom = -90;
                touch_rect.right = -90;
                break;
        }
        return true;
    }

    /**
     * Added through the implementation of "Runnable". This is the game thread
     */
    public void run() {
        // The game thread loop. Will continue until the game is no longer running
        do {
            long startTime = System.currentTimeMillis();
            CheckCollision();
            DoUpdates();
            CheckWon();
            invalidate();
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime);
            frame_time = duration / 1000.0f;
        } while (is_running);

        // Sets up the activity change to go to the level finished screen
        Context context = getContext();
        Intent switchActivityIntent = new Intent(context, LevelFinished.class);
        switchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Sets the exit message
        String exitMessage = "";

        if (exit_reason == ExitReason.PASSED)
            exitMessage = "Passed";
        else if (exit_reason == ExitReason.FAILED)
            exitMessage = "Failed";
        else if (exit_reason == ExitReason.QUIT)
            exitMessage = "Quit";
        else if (exit_reason == ExitReason.TIMEOUT)
            exitMessage = "Not Completed";

        // Actually switches activity
        switchActivityIntent.putExtra("pass_fail", exitMessage);
        context.startActivity(switchActivityIntent);
    }

    /**
     * Checks if the game has ended. If it has, the thread will be stopped
     */
    private void CheckWon() {
        // if there are no more lemons alive and in the buffer, the game has finished
        if (lemons.size() == 0 && lemons_buffer.size() == 0) {
            is_running = false;
            // to win, at least 75% of the total lemons must reach the exit
            if (score >= lemon_count * 3 / 4)
                exit_reason = ExitReason.PASSED;
            else
                exit_reason = ExitReason.FAILED;
        }

        // if run_time is larger then time_limit then it will exit the level
        if (run_time > time_limit) {
            is_running = false;
            exit_reason = ExitReason.TIMEOUT;
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
                if (objectRect.intersect(lemonRect) && !object.isDead) {
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
                if (lemonRect.intersect(touch_rect)) {
                    lemon.OnTouch();
                    touchedItem = true;
                    break;
                }
            }

            if (!touchedItem)
                for (Object object : objects) {
                    Rect objectRect = new Rect((int) object.x, (int) object.y,
                            (int) object.x + object.w, (int) object.y + object.h);
                    if (objectRect.intersect(touch_rect)) {
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
        // increases run_time
        run_time += Game.frame_time;

        // updates all lemons
        for (int i = 0; i < lemons.size(); i++) {
            Lemon lemon = lemons.get(i);

            // if lemon has left the level then it will remove it from thr list and increase score
            if (lemon.isLeft) {
                RemoveLemon(lemon);
                i--;
                score++;
                continue;
            }
            // if lemon died, it will remove it from the list but not increase the score
            if (lemon.isDead) {
                lemon.OnDeath();
                RemoveLemon(lemon);
                i--;
                continue;
            }
            // if the lemon is still in the level, it will update
            lemon.Update();
        }

        // update all objects
        for (Object object : objects)
            object.Update();
    }
}
