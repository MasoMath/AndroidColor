package cse340.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * This is a subclass of AbstractColorPickerView, that is, this View implements a ColorPicker.
 *
 * Creates a color picker which presents itself as 4 colored rectangles with drop shadows
 * that interact based on touch.
 */
public class MyColorPickerView extends ColorPickerView {

    /**
     * Update the local model (color) for this colorpicker view
     *
     * @param x The x location that the user selected
     * @param y The y location that the user selected
     */
    protected void updateModel(float x, float y) {
        int newColor;
        // Note that since updateModel is only ever called when the essential
        // geometry is INSIDE this tells us that we are inside the box,
        // meaning we only need to check which quadrant of the rectangle
        // we are in
        boolean left = x <= mLeft * SCALE_FACTOR;
        boolean upper = y <= mTop * SCALE_FACTOR;
        if (left) {
            if (upper) {
                pressedBox = Corner.TOPLEFT;
                newColor = Color.YELLOW;
            } else {
                pressedBox = Corner.BOTLEFT;
                newColor = Color.BLUE;
            }
        } else {
            if (upper) {
                pressedBox = Corner.TOPRIGHT;
                newColor = Color.RED;
            } else {
                pressedBox = Corner.BOTRIGHT;
                newColor = Color.GREEN;
            }
        }
        setColor(newColor);
    }

    /* ********************************************************************************************** *
     *                               <End of model declarations />
     * ********************************************************************************************** */

    /** The what fraction of the view the color picker will occupy
     * i.e. if DIVISION_FACTOR = n, then the color picker will occupy
     * (1-2/n)^2 of the view's bounding box's area
     * */
    private final float DIVISION_FACTOR = 8f;
    /** The middle of the colorpicker (DIVISON_FACTOR / 2) */
    private final int SCALE_FACTOR = 4;
    /** The width of a specific color box's shadow is */
    private final int SHADOW_WIDTH = 10;

    private int prevColor;

    /** The top, left, right, and bottom positions of the color picker in the view
     * from which everything is drawn and calculated from
     * */
    private float mTop, mLeft, mRight, mBot;
    /** The different colors positions are catagorized as the following */
    private enum Corner {TOPLEFT, TOPRIGHT, BOTLEFT, BOTRIGHT}
    /** Tracks which color is corrently being pressed*/
    private Corner pressedBox;
    // The paint object stored, and pre-allocated
    // on initialization for efficiency
    private Paint mBrush;


    /* ********************************************************************************************** *
     *                               <End of other fields and constants declarations />
     * ********************************************************************************************** */

    /**
     *
     * Constructor of the ColorPicker View
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view. This value may be null.
     */
    public MyColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBrush = new Paint();
        mBrush.setStyle(Paint.Style.FILL);

        prevColor = -1;
    }

    /**
     * Draw the ColorPicker on the Canvas
     * @param canvas the canvas that is drawn upon
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Creates the "Shadow box" for each color
        mBrush.setColor(Color.BLACK);
        canvas.drawRect(mLeft + SHADOW_WIDTH,
                mTop + SHADOW_WIDTH,
                mRight + SHADOW_WIDTH,
                mBot + SHADOW_WIDTH,
                mBrush
        );

        drawCorner(canvas, Corner.BOTLEFT);
        drawCorner(canvas, Corner.BOTRIGHT);
        drawCorner(canvas, Corner.TOPLEFT);
        drawCorner(canvas, Corner.TOPRIGHT);
    }

    /**
     * Called when this view should assign a size and position to all of its children.
     * @param changed This is a new size or position for this view
     * @param left Left position, relative to parent
     * @param top Top position, relative to parent
     * @param right Right position, relative to parent
     * @param bottom Bottom position, relative to parent
     */
    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int height = getHeight();
        int width = getWidth();

        int lastTerm = ((int) DIVISION_FACTOR) - 1;

        mBot = lastTerm * height / DIVISION_FACTOR;
        mTop = height / DIVISION_FACTOR;
        mLeft = width / DIVISION_FACTOR;
        mRight = lastTerm * width / DIVISION_FACTOR;
    }

    /**
     * Calculate the essential geometry given an event.
     *
     * @param event Motion event to compute geometry for, most likely a touch.
     * @return EssentialGeometry value.
     */
    @Override
    protected EssentialGeometry essentialGeometry(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if ((mLeft <= x) && (x <= mRight) && (mTop <= y) && (y <= mBot)) {
            return EssentialGeometry.INSIDE;
        } else {
            return EssentialGeometry.OUTSIDE;
        }
    }

    /* ********************************************************************************************** *
     *                               <Helper Functions />
     * ********************************************************************************************** */

    /**
     * Draws the appropriate color box in the correct position, and
     * pressed state, according to pressedBox and mState
     *
     * @param canvas the Canvas object on which the color boxes are drawn upon
     * @param corner the Corner enum indicating which color box is being drawn
     * */
    private void drawCorner( Canvas canvas, Corner corner) {
        switch (corner) {
            case TOPRIGHT:
                mBrush.setColor(Color.RED);
                if ((corner == pressedBox) && (mState == State.INSIDE)) {
                    canvas.drawRect(mLeft * SCALE_FACTOR + SHADOW_WIDTH,
                            mTop + SHADOW_WIDTH,
                            mRight,
                            mTop * SCALE_FACTOR,
                            mBrush);
                } else {
                    canvas.drawRect(mLeft * SCALE_FACTOR,
                            mTop,
                            mRight - SHADOW_WIDTH,
                            mTop * SCALE_FACTOR - SHADOW_WIDTH,
                            mBrush);
                }
                break;
            case TOPLEFT:
                mBrush.setColor(Color.YELLOW);
                if ((corner == pressedBox) && (mState == State.INSIDE)) {
                    canvas.drawRect(mLeft + SHADOW_WIDTH,
                            mTop + SHADOW_WIDTH,
                            mLeft * SCALE_FACTOR,
                            mTop * SCALE_FACTOR,
                            mBrush);
                } else {
                    canvas.drawRect(mLeft,
                            mTop,
                            mLeft * SCALE_FACTOR - SHADOW_WIDTH,
                            mTop * SCALE_FACTOR - SHADOW_WIDTH,
                            mBrush);
                }
                break;
            case BOTLEFT:
                mBrush.setColor(Color.BLUE);
                if ((corner == pressedBox) && (mState == State.INSIDE)) {
                    canvas.drawRect(mLeft + SHADOW_WIDTH,
                            mTop * SCALE_FACTOR + SHADOW_WIDTH,
                            mLeft * SCALE_FACTOR,
                            mBot,
                            mBrush);
                } else {
                    canvas.drawRect(mLeft,
                            mTop * SCALE_FACTOR,
                            mLeft * SCALE_FACTOR - SHADOW_WIDTH,
                            mBot - SHADOW_WIDTH,
                            mBrush);
                }
                break;
            case BOTRIGHT:
                mBrush.setColor(Color.GREEN);
                if ((corner == pressedBox) && (mState == State.INSIDE)) {
                    canvas.drawRect(mLeft * SCALE_FACTOR + SHADOW_WIDTH,
                            mTop * SCALE_FACTOR + SHADOW_WIDTH,
                            mRight,
                            mBot,
                            mBrush);
                } else {
                    canvas.drawRect(mLeft * SCALE_FACTOR,
                            mTop * SCALE_FACTOR,
                            mRight - SHADOW_WIDTH,
                            mBot - SHADOW_WIDTH,
                            mBrush);
                }
                break;
            default:
                break;
        }
    }
}
