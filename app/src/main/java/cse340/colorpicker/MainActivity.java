package cse340.colorpicker;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This subclasses AbstractMainActivity and as such is a AbstractMainActivity. It inherits a field
 * `colorPicker` which contains a pre-instantiated and positioned CircleColorPickerView. Do
 * NOT create your own CircleColorPickerView.
 *
 * We encourage you to read and understand AbstractMainActivity as it is fairly simple.
 *
 * Here you will attach a ColorListener callback and add bundle support.
 */
public class MainActivity extends AbstractMainActivity {

    /** The key used to store our bundle information */
    public static final String COLOR_BUNDLE_KEY = "color";

    /** The view that display the color that was selected by the ColorPicker */
    private View mColorView;

    /** The label that displays the string representation of the color selected by the ColorPicker */
    private TextView mLabelView;

    // Color change listener
    AbstractColorPickerView.ColorChangeListener mColorChangeListener = new AbstractColorPickerView.ColorChangeListener() {
        @Override
        public void onColorSelected(int color) {
            updateColor(color);
        }
    };


    /**
     * Callback that is called when the activity is first created.
     * @param savedInstanceState contains the activity's previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mColorView = findViewById(R.id.colorResult);
        mLabelView = findViewById(R.id.colorTextView);

        findViewById(R.id.root_layout).setOnClickListener((view) -> {
            if (mColorPicker.getVisibility() == View.VISIBLE) {
                Toast.makeText(this, R.string.click_outside,
                        Toast.LENGTH_SHORT).show();
                mColorPicker.setVisibility(View.INVISIBLE);
            }
        });

        // Shows the color picker wheel when the color tile is clicked on
        mColorView.setOnTouchListener((view, e) -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                if (mColorPicker.getVisibility() == View.VISIBLE) {
                    Toast.makeText(this,  R.string.click_outside,
                            Toast.LENGTH_SHORT).show();
                    mColorPicker.setVisibility(View.INVISIBLE);
                    view.performClick();
                    return true;
                } else {
                    mColorPicker.setVisibility(View.VISIBLE);
                    return true;
                }
            }
            return false;
        });

        // Registering color change listener to views
        ((AbstractColorPickerView) findViewById(R.id.circleColorPicker)
            ).addColorChangeListener(mColorChangeListener);
        ((AbstractColorPickerView) findViewById(R.id.myColorPicker)
            ).addColorChangeListener(mColorChangeListener);

        setStartingColor(savedInstanceState);
    }

    // Removes registered Listener
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((AbstractColorPickerView) findViewById(R.id.circleColorPicker)
            ).removeColorChangeListener(mColorChangeListener);
        ((AbstractColorPickerView) findViewById(R.id.myColorPicker)
            ).removeColorChangeListener(mColorChangeListener);
    }

    /**
     * Private helper function to update the view after the color in the model has changed
     *
     * @param color The new color
     */
    private void updateColor(int color) {
        mColorView.setBackgroundColor(color);
        mLabelView.setText(colorToString(color));
        mColorModel.setColor(color);
    }

    /**
     * Sets the starting color of this Activity's ColorPicker.
     *
     * @param state Bundled state to extract previous color from or null for default.
     */
    @Override
    protected void setStartingColor(Bundle state) {
        int startingColor;
        if (state == null) {
            startingColor = AbstractColorPickerView.DEFAULT_COLOR;
        } else {
            startingColor = state.getInt(COLOR_BUNDLE_KEY);
        }
        ((AbstractColorPickerView) findViewById(R.id.circleColorPicker)
            ).setColor(startingColor);
        ((AbstractColorPickerView) findViewById(R.id.myColorPicker)
            ).setColor(startingColor);
        updateColor(startingColor);
    }

    /**
     * Invoked when the activity may be temporarily destroyed, save the instance state here.
     * @param outState State to save out through the bundle
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(COLOR_BUNDLE_KEY, mColorModel.getColor());
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        int startingColor = savedInstanceState.getInt(COLOR_BUNDLE_KEY);
        updateColor(startingColor);
        ((AbstractColorPickerView) findViewById(R.id.circleColorPicker)
            ).setColor(startingColor);
        ((AbstractColorPickerView) findViewById(R.id.myColorPicker)
            ).setColor(startingColor);
    }
}
