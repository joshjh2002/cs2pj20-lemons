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
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private final ArrayList<Lemon> lemons;
    /**
     * Lemon list of all lemons currently not on the screen. These will gradually be moved to "lemons" by the spawner
     */
    private final ArrayList<Lemon> lemons_buffer;

    public void AddLemon(float x, float y) {
        if (lemons_buffer.size() > 0) {
            Lemon lemon = lemons_buffer.get(0);
            lemon.x = x;
            lemon.y = y;
            lemons.add(lemon);
            lemons_buffer.remove(0);
        }
    }

    /**
     * A list of all "alive" objects
     */
    private final ArrayList<Object> objects;

    /**
     * Holds a counter that stores the number of lemons created on level load
     */
    private final int lemon_count;

    /**
     * Paint object to draw text to screen
     */
    private final Paint paint;

    /**
     * Determines the state of the run thread. If false, the thread will stop and the game will end
     */
    private Boolean is_running;
    /**
     * Temporary canvas to draw each frame to
     */
    private final Canvas tmp;
    /**
     * The bitmap tmp will draw to
     */
    private final Bitmap frame;
    /**
     * The destination of the frame, so if the screen is smaller than 1920*1080, I can scale the image down
     */
    private final Rect frame_dest_rect;
    /**
     * Collision rectangle for the lemon
     */
    private final Rect lemon_rect;
    /**
     * Collision rectangle for the object
     */
    private final Rect object_rect;
    /**
     * Score
     */
    private int score;

    /**
     * True when someone is touching the screen
     */
    private static Boolean touching = false;

    /**
     * Current x and y position of the touch
     */
    private int touchX = -100, touchY = -100;

    /**
     * A rectangle that will hold the x and y touch position.
     * If this rectangle collides with an object, then you have touched that object
     */
    private final Rect touch_rect;

    /**
     * If true, then will be used to debug certain elements. Like drawing the touch location
     */
    private final boolean DEBUG = true;
    /**
     * Similar to DeltaTime in Unity. Holds time it took to process the last frame
     */
    private static float frame_time = 0;

    public static float FrameTime() {
        return frame_time;
    }

    /**
     * Holds how long the game has currently been running for
     */
    private float run_time;

    /**
     * Holds the reason for leaving the level. Can be either pass, fail or quit
     */
    private ExitReason exit_reason;

    /**
     * Used to format the time remaining
     */
    private static final DecimalFormat time_formatter = new DecimalFormat("00");

    private final String name;
    private int highScore;

    /**
     * Takes Context as a parameter. Creates a new "Game" and adds in all objects stored in asset file
     *
     * @param context the current context the game is ran in
     */
    public Game(Context context, String level, boolean isExternal, String name) {
        super(context);

        this.name = name;
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://lemons-80393-default-rtdb.firebaseio.com/");
        DatabaseReference myRef;
        myRef = database.getReference(name).child("score");

        myRef.get().addOnCompleteListener(y -> {
            if (y.isSuccessful()) {
                if (y.getResult() != null && y.getResult().exists()) {
                    if (y.getResult() != null && y.getResult().getValue() != null)
                        highScore = Integer.parseInt(y.getResult().getValue().toString());
                }
            }

        });


        //Initialises the paint, and arrays
        paint = new Paint();

        lemons = new ArrayList<>();
        lemons_buffer = new ArrayList<>();
        objects = new ArrayList<>();

        touch_rect = new Rect();
        run_time = 0;

        //Initialises the frame bitmap and creates a canvas so I can draw to the bitmap
        frame = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
        tmp = new Canvas(frame);
        frame_dest_rect = new Rect();
        frame_dest_rect.top = 0;
        frame_dest_rect.left = 0;

        //Initialises the collision rectangles
        lemon_rect = new Rect();
        object_rect = new Rect();

        //Sets score to 0
        score = 0;
        exit_reason = null;

        is_running = true;

        LoadLevel(level, context, isExternal);

        lemon_count = lemons_buffer.size();

        LoadImages();
        LoadSounds();
    }

    private void LoadSounds() {
        JumpPad.jumpSound = MediaPlayer.create(getContext(), R.raw.jump);
        Door.exitEffect = MediaPlayer.create(getContext(), R.raw.exit);
        LavaPit.burnEffect = MediaPlayer.create(getContext(), R.raw.lava);
        TouchedButton.clickEffect = MediaPlayer.create(getContext(), R.raw.click);
        Button.clickEffect = MediaPlayer.create(getContext(), R.raw.click);
    }

    private void LoadLevel(String level, Context context, boolean isExternal) {
        if (level == null) {
            level = "TestLevel.txt";
        }
        //This will try to load the file it points to. If not, the thread will terminate and the level passed screen will show
        try {
            //Creates an InputStream using from the levels found in the assets folder
            InputStream is = null;
            //size holds the number of bytes in the file
            int size;
            //buffer is a list of all the characters
            byte[] buffer = null;

            if (!isExternal) {
                is = context.getAssets().open(level);
                //size holds the number of bytes in the file
                size = is.available();
                //buffer is a list of all the characters
                buffer = new byte[size];
            }


            //checks if there are more than 0 lines in the file
            if (isExternal || is.read(buffer) > 0) {
                //converts the buffer to a string
                String text;
                if (isExternal)
                    text = level;
                else
                    text = new String(buffer);

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
                            objects.add(new JumpPad(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Float.parseFloat(parts[3])));
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
                        case "TouchedButton":
                            objects.add(new TouchedButton(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), objects.get(Integer.parseInt(parts[3]) - lemons_buffer.size() - 1)));
                            break;
                        case "TimeLimit":
                            this.time_limit = Float.parseFloat(parts[1]);
                            break;
                    }
                }
            }
            //closes the stream
            if (is != null)
                is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void LoadImages() {
        //Add images for all relevant classes
        StandardLemon.image = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.lemon);

        StopperLemon.image = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.stopper);

        Exit.image = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.exit);

        Button.image = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.button);
        Button.image_pressed = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.button_pressed);

        LavaPit.image = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.lava);

        JumpPad.image = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.jump);
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
                object_rect.left = (int) object.x;
                object_rect.top = (int) object.y;
                object_rect.right = (int) object.x + object.w;
                object_rect.bottom = (int) object.y + object.h;
                switch (object.tag) {
                    case EXIT:
                        tmp.drawBitmap(Exit.image, null, object_rect, null);
                        break;
                    case BUTTON:
                        if (!((Button) object).isPressed())
                            tmp.drawBitmap(Button.image, null, object_rect, null);
                        else
                            tmp.drawBitmap(Button.image_pressed, null, object_rect, null);
                        break;
                    case LAVA:
                        tmp.drawBitmap(LavaPit.image, object_rect, object_rect, null);
                        break;
                    case JUMP:
                        tmp.drawBitmap(JumpPad.image, null, object_rect, null);
                        break;
                    case PLATFORM:
                    case TOUCH_BUTTON:
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
        frame_dest_rect.right = getHeight() / 9 * 16;
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
                this.touchX = (int) (((float) this.touchX / (float) frame_dest_rect.right) * 1920f);


                this.touchY = (int) event.getY();
                this.touchY = (int) (((float) this.touchY / (float) frame_dest_rect.bottom) * 1080f);

                touch_rect.top = touchY - 30;
                touch_rect.left = touchX - 30;
                touch_rect.bottom = touchY + 30;
                touch_rect.right = touchX + 30;

                // Checks if the user has pressed the Quit button
                if (touchX > getWidth() - 150f && touchY < 150f) {
                    is_running = false;
                    exit_reason = ExitReason.QUIT;
                }
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                // When the user is no longer touching the screen
                touch_rect.top = -100;
                touch_rect.left = -100;
                touch_rect.bottom = -101;
                touch_rect.right = -101;
                this.touchX = this.touchY = -100;

                touching = false;
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

        if (score > highScore && !name.equals("")) {
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://lemons-80393-default-rtdb.firebaseio.com/");
            DatabaseReference myRef = database.getReference(name).child("score");
            myRef.setValue(score);
        }
        // Actually switches activity
        switchActivityIntent.putExtra("pass_fail", exitMessage);
        switchActivityIntent.putExtra("score", score + "");
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
            for (Object object : objects) {
                Rect objectRect = new Rect((int) object.x, (int) object.y,
                        (int) object.x + object.w, (int) object.y + object.h);
                if (objectRect.intersect(touch_rect)) {
                    object.OnTouch();
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
