package es.deusto.deustotech.capabilities.views;

import java.util.HashMap;
import java.util.Map;

import yuku.ambilwarna.AmbilWarnaDialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;
import es.deusto.deustotech.R;
import es.deusto.deustotech.capabilities.UserMinimumPreferences;

/**
 * This activity configures the minimum visual interaction values
 * 
 * @author edlectrico
 * 
 */
public class ButtonConfigActivity extends AbstractActivity {

	private static final String TAG = ButtonConfigActivity.class.getSimpleName();

	private Button btnNext, btnBackgroundColor, btnColorButton,	btnTextColor;
	private GridLayout grid;

	private int buttonBackgroundColor = 0;
	private int textColor = 0;
//	private int layoutBackgroundColor = 0;

//	private int defaultButtonColor;
	private static final int DEFAULT_BACK_COLOR = Color.WHITE;

	private OnTouchListener onTouchListener;

	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Rect mBounds;

	int callerActivity = -1;

	private static boolean LAYOUT_BACKGROUND_COLOR_CHANGED = false;
	private static boolean BUTTON_BACKGROUND_COLOR_CHANGED = false;
	private static boolean BUTTON_TEXT_COLOR_CHANGED = false;
	private static boolean BUTTON_SIZE_CHANGED = false;
	
	private Map<Button, Float> textSizes;
	
	int color = Color.WHITE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.button_config_activity);

		grid = (GridLayout) findViewById(R.id.default_gridlayout);
		drawButtons();

		Bundle bundle = getIntent().getExtras();
		userPrefs = new UserMinimumPreferences();
		callerActivity = bundle.getInt(getResources().getString(R.string.activity_caller));

		if (callerActivity != 2){ //2: VolumeActivity
			userPrefs.setButtonBackgroundColor(getBackgroundColor(btnNext));
			userPrefs.setButtonTextColor(btnNext.getTextColors().getDefaultColor());

		} else {
			speakOut(getResources().getString(R.string.button_info_message_es));
		}

		onTouchListener = createOnTouchListener();

		initializeServices(TAG);
		addListeners();

		//Default values
		userPrefs.setButtonBackgroundColor(Color.LTGRAY);
		userPrefs.setLayoutBackgroundColor(DEFAULT_BACK_COLOR);
		userPrefs.setButtonTextColor(Color.BLACK);
		userPrefs.setButtonTextSize(btnColorButton.getTextSize() / 2);
	}

	private void drawButtons() {
		textSizes = new HashMap<Button, Float>();
		
		btnColorButton = (Button) findViewById(R.id.button_color);
		textColor = btnColorButton.getTextColors().getDefaultColor();

		btnBackgroundColor = (Button) findViewById(R.id.button_background_color);
		btnTextColor = (Button) findViewById(R.id.button_text_color);
		btnNext = (Button) findViewById(R.id.button_next_bt);

//		defaultButtonColor = getBackgroundColor(btnNext);
		
		textSizes.put(btnColorButton, btnColorButton.getTextSize() / 2);
		textSizes.put(btnBackgroundColor, btnColorButton.getTextSize() / 2);
		textSizes.put(btnTextColor, btnColorButton.getTextSize() / 2);
		textSizes.put(btnNext, btnColorButton.getTextSize() / 2);
	}

	@Override
	public void initializeServices(String TAG) {
		//If blind user, voice control
		if (userPrefs.getDisplayHasApplicable() == 0){
			super.initializeServices(TAG);
			speakOut(getResources().getString(R.string.button_info_message_es));
		}
	}

	@Override
	public void addListeners() {
		grid.setOnTouchListener(onTouchListener);
		grid.getChildAt(1).setBackgroundColor(Color.LTGRAY);

		btnNext.setOnClickListener(this);
		btnBackgroundColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openDialog(false, v);
			}
		});
		btnTextColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openDialog(false, v);
			}
		});
		btnColorButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openDialog(false, v);
			}
		});
	}

	private OnTouchListener createOnTouchListener(){
		return new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				BUTTON_SIZE_CHANGED = true;
				float x = event.getRawX();

				btnBackgroundColor.setTextSize((float) (x / 10.0));
				btnColorButton.setTextSize((float) (x / 10.0));
				btnTextColor.setTextSize((float) (x / 10.0));
				btnNext.setTextSize((float) (x / 10.0));

				btnBackgroundColor.invalidate();
				btnColorButton.invalidate();
				btnTextColor.invalidate();
				btnNext.invalidate();

				return true;
			}
		};
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.button_next_bt) {
			//TODO: next activity for configuring TextEdit size and color
			if (CapabilitiesActivity.getDisplayIsApplicable() == 0){
				speakOut("Well done!");
			}
			Intent intent = new Intent(this, EditTextConfigActivity.class);

			userPrefs.setButtonWidth(btnNext.getWidth());
			userPrefs.setButtonHeight(btnNext.getHeight());
//			userPrefs.setButtonTextColor(textColor);
			userPrefs.setButtonTextSize(btnNext.getTextSize());

			intent.putExtra(getResources().getString(R.string.activity_caller), 1);

			intent.putExtra(getResources().getString(R.string.view_params), userPrefs);

			startActivity(intent);
		} 
	}

	private int getBackgroundColor(View view) {
		// The actual color, not the id.
		int color = Color.BLACK;

		if(view.getBackground() instanceof ColorDrawable) {
			if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				initIfNeeded();
				// If the ColorDrawable makes use of its bounds in the draw method,
				// we may not be able to get the color we want. This is not the usual
				// case before Ice Cream Sandwich (4.0.1 r1).
				// Yet, we change the bounds temporarily, just to be sure that we are
				// successful.
				ColorDrawable colorDrawable = (ColorDrawable)view.getBackground();

				mBounds.set(colorDrawable.getBounds()); // Save the original bounds.
				colorDrawable.setBounds(0, 0, 1, 1); // Change the bounds.

				colorDrawable.draw(mCanvas);
				color = mBitmap.getPixel(0, 0);

				colorDrawable.setBounds(mBounds); // Restore the original bounds.
			}
			else {
				color = ((ColorDrawable)view.getBackground()).getColor();
			}
		}
		return color;
	}

	private void initIfNeeded() {
		if(mBitmap == null) {
			mBitmap = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);
			mBounds = new Rect();
		}
	}

	public static boolean getButtonBackgroundColorChanged(){
		return BUTTON_BACKGROUND_COLOR_CHANGED;
	}
	
	public static boolean getButtonTextColorChanged(){
		return BUTTON_TEXT_COLOR_CHANGED;
	}

	public static boolean getButtonSizeChanged(){
		return BUTTON_SIZE_CHANGED;
	}

	public static boolean getLayoutBackgroundColorChanged(){
		return LAYOUT_BACKGROUND_COLOR_CHANGED;
	}
	
	void openDialog(boolean supportsAlpha, final View v) {
		AmbilWarnaDialog dialog = new AmbilWarnaDialog(ButtonConfigActivity.this, color, supportsAlpha, new AmbilWarnaDialog.OnAmbilWarnaListener() {
			@Override
			public void onOk(AmbilWarnaDialog dialog, int color) {
				ButtonConfigActivity.this.color = color;
				System.out.println(color);
				
				if (v.getId() == R.id.button_background_color){
					LAYOUT_BACKGROUND_COLOR_CHANGED = true;
					
					grid.setBackgroundColor(color);
					userPrefs.setLayoutBackgroundColor(color);
					
				} else if (v.getId() == R.id.button_color){
					BUTTON_BACKGROUND_COLOR_CHANGED = true;

					buttonBackgroundColor = color;

					btnNext.setBackgroundColor(buttonBackgroundColor);
					btnBackgroundColor.setBackgroundColor(buttonBackgroundColor);
					btnTextColor.setBackgroundColor(buttonBackgroundColor);
					btnColorButton.setBackgroundColor(buttonBackgroundColor);
					
					userPrefs.setButtonBackgroundColor(buttonBackgroundColor);
					
				} else if (v.getId() == R.id.button_text_color){
					BUTTON_TEXT_COLOR_CHANGED = true;
					
					textColor = color;
					
					btnNext.setTextColor(textColor);
					btnBackgroundColor.setTextColor(textColor);
					btnTextColor.setTextColor(textColor);
					btnColorButton.setTextColor(textColor);
					
					userPrefs.setButtonTextColor(textColor);
				} 
				
			}

			@Override
			public void onCancel(AmbilWarnaDialog dialog) {
//				Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_SHORT).show();
			}
		});
		dialog.show();
	}
	
	void displayColor() {
		Toast.makeText(getApplicationContext(), String.format("Current color: 0x%08x", color), Toast.LENGTH_LONG).show();
	}
}
