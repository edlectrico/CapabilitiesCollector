package es.deusto.deustotech.capabilities.views;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

	private Button btnInvertColors, btnRestore, btnNext, 
	btnBackgroundColor, btnColorButton,	btnTextColor;
	private GridLayout grid;

	private int buttonBackgroundColor = 0;
	private int textColor = 0;
	private int layoutBackgroundColor = 0;

	private int defaultButtonColor;
	private static final int DEFAULT_BACK_COLOR = Color.WHITE;

	private OnTouchListener onTouchListener;

	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Rect mBounds;

	int callerActivity = -1;

	private boolean inverted = false;

	private static boolean LAYOUT_BACKGROUND_COLOR_CHANGED = false;
	private static boolean BUTTON_BACKGROUND_COLOR_CHANGED = false;
	private static boolean BUTTON_TEXT_COLOR_CHANGED = false;
	private static boolean BUTTON_SIZE_CHANGED = false;
	
	private Map<Button, Float> textSizes;

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
			userPrefs.setButtonBackgroundColor(getBackgroundColor(btnInvertColors));
			userPrefs.setButtonTextColor(btnInvertColors.getTextColors().getDefaultColor());

		} else {
			speakOut(getResources().getString(R.string.button_info_message_es));
		}

		onTouchListener = createOnTouchListener();

		initializeServices(TAG);
		addListeners();

		//Default values
		userPrefs.setButtonBackgroundColor(defaultButtonColor);
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
		btnRestore = (Button) findViewById(R.id.button_restore);
		btnInvertColors = (Button) findViewById(R.id.button_invert);
		btnNext = (Button) findViewById(R.id.button_next_bt);

		defaultButtonColor = getBackgroundColor(btnInvertColors);
		
		textSizes.put(btnColorButton, btnColorButton.getTextSize() / 2);
		textSizes.put(btnBackgroundColor, btnColorButton.getTextSize() / 2);
		textSizes.put(btnTextColor, btnColorButton.getTextSize() / 2);
		textSizes.put(btnRestore, btnColorButton.getTextSize() / 2);
		textSizes.put(btnInvertColors, btnColorButton.getTextSize() / 2);
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
		btnBackgroundColor.setOnClickListener(this);
		btnTextColor.setOnClickListener(this);
		btnColorButton.setOnClickListener(this);
		btnInvertColors.setOnClickListener(this);
		btnRestore.setOnClickListener(this);
	}

	private OnTouchListener createOnTouchListener(){
		return new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				BUTTON_SIZE_CHANGED = true;
				float x = event.getRawX();

				btnRestore.setTextSize((float) (x / 10.0));
				btnInvertColors.setTextSize((float) (x / 10.0));
				btnBackgroundColor.setTextSize((float) (x / 10.0));
				btnColorButton.setTextSize((float) (x / 10.0));
				btnTextColor.setTextSize((float) (x / 10.0));
				btnNext.setTextSize((float) (x / 10.0));

				btnRestore.invalidate();
				btnInvertColors.invalidate();
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

			userPrefs.setButtonWidth(btnInvertColors.getWidth());
			userPrefs.setButtonHeight(btnInvertColors.getHeight());
			userPrefs.setButtonTextColor(textColor);
			userPrefs.setButtonTextSize(btnInvertColors.getTextSize());

			intent.putExtra(getResources().getString(R.string.activity_caller), 1);

			intent.putExtra(getResources().getString(R.string.view_params), userPrefs);

			if (BUTTON_BACKGROUND_COLOR_CHANGED){
				userPrefs.setButtonBackgroundColor(getBackgroundColor(btnInvertColors));
			}

			if (LAYOUT_BACKGROUND_COLOR_CHANGED ){
				userPrefs.setLayoutBackgroundColor(getBackgroundColor(grid));
			} else {
				userPrefs.setLayoutBackgroundColor(Color.WHITE);
			}

			if (inverted){
				userPrefs.setLayoutBackgroundColor(Color.BLACK);
			}
			
			System.out.println("BUttonColor: " + getBackgroundColor(btnInvertColors) );
			System.out.println("userPrefsBUttonColor: " + userPrefs.getButtonBackgroundColor() );

			startActivity(intent);
		} 
		else if (view.getId() == R.id.button_color){
			BUTTON_BACKGROUND_COLOR_CHANGED = true;

			Random randomBackColor = new Random(); 
			buttonBackgroundColor = Color.argb(255, randomBackColor.nextInt(256), randomBackColor.nextInt(256), randomBackColor.nextInt(256));

			btnNext.setBackgroundColor(buttonBackgroundColor);
			btnBackgroundColor.setBackgroundColor(buttonBackgroundColor);
			btnTextColor.setBackgroundColor(buttonBackgroundColor);
			btnColorButton.setBackgroundColor(buttonBackgroundColor);
			btnInvertColors.setBackgroundColor(buttonBackgroundColor);
			btnRestore.setBackgroundColor(buttonBackgroundColor);
		} 
		else if (view.getId() == R.id.button_text_color){
			BUTTON_TEXT_COLOR_CHANGED = true;

			Random randomTextColor = new Random(); 
			int textColor = Color.argb(255, randomTextColor.nextInt(256), randomTextColor.nextInt(256), randomTextColor.nextInt(256));   
			this.textColor = textColor;
			btnNext.setTextColor(textColor);
			btnBackgroundColor.setTextColor(textColor);
			btnTextColor.setTextColor(textColor);
			btnColorButton.setTextColor(textColor);
			btnInvertColors.setTextColor(textColor);
			btnRestore.setTextColor(textColor);
		} 
		else if (view.getId() == R.id.button_background_color){
			LAYOUT_BACKGROUND_COLOR_CHANGED = true;
			inverted = false;

			Random randomColor = new Random(); 
			layoutBackgroundColor = Color.argb(255, randomColor.nextInt(256), randomColor.nextInt(256), randomColor.nextInt(256));   
			grid.setBackgroundColor(layoutBackgroundColor);
		} 
		else if (view.getId() == R.id.button_restore){
			LAYOUT_BACKGROUND_COLOR_CHANGED = false;
			grid.setBackgroundColor(Color.WHITE);

			btnNext.setBackgroundColor(Color.LTGRAY);
			btnBackgroundColor.setBackgroundColor(Color.LTGRAY);
			btnTextColor.setBackgroundColor(Color.LTGRAY);
			btnColorButton.setBackgroundColor(Color.LTGRAY);
			btnInvertColors.setBackgroundColor(Color.LTGRAY);
			btnRestore.setBackgroundColor(Color.LTGRAY);

			textColor = Color.BLACK;

			btnNext.setTextColor(textColor);
			btnBackgroundColor.setTextColor(textColor);
			btnTextColor.setTextColor(textColor);
			btnColorButton.setTextColor(textColor);
			btnInvertColors.setTextColor(textColor);
			btnRestore.setTextColor(textColor);
			
			btnNext.setTextSize(textSizes.get(btnNext));
			btnBackgroundColor.setTextSize(textSizes.get(btnBackgroundColor));
			btnTextColor.setTextSize(textSizes.get(btnTextColor));
			btnColorButton.setTextSize(textSizes.get(btnColorButton));
			btnInvertColors.setTextSize(textSizes.get(btnInvertColors));
			btnRestore.setTextSize(textSizes.get(btnRestore));
			
			userPrefs.setLayoutBackgroundColor(Color.WHITE);
		} 
		else if (view.getId() == R.id.button_invert){
			LAYOUT_BACKGROUND_COLOR_CHANGED = true;
			grid.setBackgroundColor(Color.BLACK);
			inverted = true;

			btnNext.setBackgroundColor(Color.LTGRAY);
			btnBackgroundColor.setBackgroundColor(Color.LTGRAY);
			btnTextColor.setBackgroundColor(Color.LTGRAY);
			btnColorButton.setBackgroundColor(Color.LTGRAY);
			btnInvertColors.setBackgroundColor(Color.LTGRAY);
			btnRestore.setBackgroundColor(Color.LTGRAY);

			textColor = Color.WHITE;

			btnNext.setTextColor(textColor);
			btnBackgroundColor.setTextColor(textColor);
			btnTextColor.setTextColor(textColor);
			btnColorButton.setTextColor(textColor);
			btnInvertColors.setTextColor(textColor);
			btnRestore.setTextColor(textColor);
			
			userPrefs.setLayoutBackgroundColor(Color.BLACK);
			userPrefs.setButtonBackgroundColor(Color.LTGRAY);
			userPrefs.setButtonTextColor(textColor);
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
}
