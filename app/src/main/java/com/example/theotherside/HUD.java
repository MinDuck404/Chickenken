/*
 * This file contains the HUD class which manages the game's heads-up display interface.
 * The class handles rendering game status information, controls, and visual effects
 * with a modern dynamic island style design.
 *
 * The class manages:
 * - Score display with coin icon
 * - Pause/play button functionality
 * - Countdown system (3, 2, 1, GO!)
 * - Semi-transparent overlay effects
 * - Touch detection for UI controls
 *
 */

package com.example.theotherside;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

public class HUD {
    // Paint objects for rendering
    private Paint paint, shadowPaint;
    private int screenWidth, screenHeight;
    private Bitmap playBitmap, pauseBitmap, coinBitmap;

    // Game state variables
    private boolean isPaused;
    private RectF hudBox;
    private int score;

    // Countdown variables
    private boolean isCountingDown;
    private int countdownValue; // 3, 2, 1, Go!
    private long lastCountdownTime;
    private int coinsCollected;


    private float currentDistance;

    /**
     * Constructs a new HUD with specified screen dimensions.
     * Initializes all UI elements including buttons, fonts, and the display box.
     *
     * @param context - The Android context for accessing resources
     * @param screenWidth - Width of the screen in pixels
     * @param screenHeight - Height of the screen in pixels
     */
    public HUD(Context context, int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        // Initialize paint objects
        paint = new Paint();
        paint.setTextSize(50);
        shadowPaint = new Paint();
        shadowPaint.setColor(Color.BLACK);
        shadowPaint.setTextSize(50);
        shadowPaint.setAlpha(120);

        // Load button images
        playBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.play);
        pauseBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pause);
        coinBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.coin);

        // Size and scale the button bitmaps
        int buttonSize = 60;
        playBitmap = Bitmap.createScaledBitmap(playBitmap, buttonSize, buttonSize, true);
        pauseBitmap = Bitmap.createScaledBitmap(pauseBitmap, buttonSize, buttonSize, true);
        coinBitmap = Bitmap.createScaledBitmap(coinBitmap, buttonSize, buttonSize, true);

        // Create HUD box
        int boxWidth = (screenWidth / 2) + 150;
        int boxHeight = 110;
        int boxX = (screenWidth - boxWidth) / 2;
        int boxY = 80;

        hudBox = new RectF(boxX, boxY, boxX + boxWidth, boxY + boxHeight);

        // Initialize state variables
        isPaused = false;
        score = 0;

        // Countdown initialization
        isCountingDown = false;
        countdownValue = 3;
    }

    /**
     * Updates the current score displayed in the HUD.
     *
     * @param distance - The new score value to display
     */
    public void setScore(float distance) {
        this.score = Math.round(distance/100);
    }



    /**
     * Returns the current pause state of the game.
     *
     * @return boolean indicating if the game is paused
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Returns the current countdown state.
     *
     * @return boolean indicating if countdown is active
     */
    public boolean isCountingDown() {
        return isCountingDown;
    }

    /**
     * Initiates the countdown sequence from 3 to "GO!".
     */
    public void startCountdown() {
        isCountingDown = true;
        countdownValue = 3;
        lastCountdownTime = System.currentTimeMillis();
    }

    /**
     * Updates the countdown timer, decrements the counter every second,
     * and handles the transition from countdown to game start.
     */
    public void updateCountdown() {
        if (!isCountingDown) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCountdownTime > 1000) { // 1 second intervals
            countdownValue--;
            lastCountdownTime = currentTime;

            if (countdownValue < 0) { // "Go!" is over
                isCountingDown = false;
                isPaused = false; // Resume game after countdown
            }
        }
    }

    /**
     * Toggles the pause state and initiates countdown when unpausing.
     */
    public void togglePause() {
        isPaused = !isPaused;
        if (!isPaused) {
            // Start countdown when unpausing
            startCountdown();
        }
    }

    /**
     * Checks if a touch event occurred within the pause/play button area.
     *
     * @param touchX - X coordinate of the touch event
     * @param touchY - Y coordinate of the touch event
     * @return boolean indicating if the button was pressed
     */
    public boolean checkButtonPress(float touchX, float touchY) {
        // Check if touch is within the pause/play button area (right side of HUD)
        float buttonX = hudBox.right - 80;
        float buttonY = hudBox.centerY() - 30;
        RectF buttonArea = new RectF(buttonX, buttonY, buttonX + 60, buttonY + 60);
        return buttonArea.contains(touchX, touchY);
    }

    /**
     * Draws the complete HUD including background, score, buttons, and countdown
     * if active. Implements visual effects like shadows and glows for enhanced
     * appearance.
     *
     * @param canvas - The Canvas object to draw on
     */
    /**
     * Draws the complete HUD including background, score, buttons, and countdown
     * if active. Implements visual effects like shadows and glows for enhanced
     * appearance.
     *
     * @param canvas - The Canvas object to draw on
     */
    public void draw(Canvas canvas) {
        // Draw HUD background
        paint.setColor(Color.argb(200, 30, 30, 30));
        canvas.drawRoundRect(hudBox, 40, 40, paint);

        // Border glow
        paint.setColor(Color.argb(60, 255, 255, 255));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRoundRect(hudBox, 40, 40, paint);
        paint.setStyle(Paint.Style.FILL);

        // ===== VERTICAL ALIGNMENT CALCULATIONS =====
        float centerY = hudBox.centerY();

        // ===== COIN SECTION =====
        float coinPadding = 30; // Space between left edge and coin
        float coinSpacing = 20; // Space between coin and text

        // Coin icon (centered vertically)
        float coinTop = centerY - (coinBitmap.getHeight() / 2f);
        canvas.drawBitmap(coinBitmap, hudBox.left + coinPadding, coinTop, paint);

        // Coin count text
        String coinText = String.valueOf(coinsCollected);
        float coinTextX = hudBox.left + coinPadding + coinBitmap.getWidth() + coinSpacing;

        // Measure text bounds directly
        Rect textBounds = new Rect();
        paint.getTextBounds(coinText, 0, coinText.length(), textBounds);
        float textHeight = textBounds.height();
        float textVerticalOffset = textHeight / 2f;
        float textY = centerY + textVerticalOffset;

        // Draw the text, now vertically centered
        canvas.drawText(coinText, coinTextX + 2, textY + 2, shadowPaint); // Shadow
        canvas.drawText(coinText, coinTextX, textY, paint); // Main text

        // ===== SCORE SECTION =====
        float scorePadding = 120; // space between right edge and score text
        String scoreText = "SCORE: " + score;
        float scoreTextWidth = paint.measureText(scoreText);

        // Measure text boundaries
        Rect textBoundsScore = new Rect();
        paint.getTextBounds(scoreText, 0, scoreText.length(), textBoundsScore);
        float textHeightScore = textBoundsScore.height();
        float textVerticalOffsetScore = textHeightScore / 2f;
        float textYScore = centerY + textVerticalOffsetScore;

        // Position score text
        float scoreX = hudBox.right - scorePadding - scoreTextWidth;
        canvas.drawText(scoreText, scoreX + 2, textYScore + 2, shadowPaint); // Shadow
        canvas.drawText(scoreText, scoreX, textYScore, paint); // Main text

        // ===== PAUSE/BUTTON =====
        float buttonSize = pauseBitmap.getWidth();
        float buttonPadding = 20;
        float buttonX = hudBox.right - buttonSize - buttonPadding;
        float buttonY = centerY - (buttonSize / 2f);
        Bitmap buttonBitmap;
        if (isPaused) {
            buttonBitmap = playBitmap;
        } else {
            buttonBitmap = pauseBitmap;
        }

        canvas.drawBitmap(buttonBitmap, buttonX, buttonY, paint);

        // ===== COUNTDOWN =====
        if (isCountingDown) {            // overlay
            paint.setColor(Color.argb(120, 0, 0, 0));
            canvas.drawRect(0, 0, screenWidth, screenHeight, paint);

            // Countdown text
            paint.setColor(Color.WHITE);
            paint.setTextSize(150);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setShadowLayer(15, 0, 0, Color.argb(180, 255, 165, 0));

            String countText;
            if (countdownValue > 0){
                countText = String.valueOf(countdownValue);
            } else
                countText = "GO!";
            canvas.drawText(countText, screenWidth/2f, screenHeight/2f, paint);

            // Reset paint
            paint.setShadowLayer(0, 0, 0, 0);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(50);
        }

        float dividerX = hudBox.left + (hudBox.width() * 0.35f); // start 35% from left
        canvas.drawLine(dividerX, hudBox.top, dividerX, hudBox.bottom, paint);
    }
    public void setDistance(float distance) {
        this.currentDistance = distance;
    }
    public void setCoins(int coins) {
        this.coinsCollected = coins;
    }

}