package es.deusto.deustotech.capabilities.views;

import java.util.Random;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

	private static final int VIEW_MAX_SIZE = 500;

	private static final String TAG = ButtonConfigActivity.class.getSimpleName();

	private Button btnResize, btnBackgroundColor, btnColorButton, 
	btnTextColor, btnInvert, btnRestore;
	private GridLayout grid;
	
	private int maxWidth = 0;
	private int maxHeight = 0;
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

	private static boolean LAYOUT_BACKGROUND_COLOR_CHANGED = false;
	private static boolean BUTTON_BACKGROUND_COLOR_CHANGED = false;
	private static boolean BUTTON_TEXT_COLOR_CHANGED = false;
	private static boolean BUTTON_SIZE_CHANGED = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.button_config_activity);
		
		grid = (GridLayout) findViewById(R.id.default_layout);
		drawButtons();

		Bundle bundle = getIntent().getExtras();
		userPrefs = new UserMinimumPreferences();
		callerActivity = bundle.getInt(getResources().getString(R.string.activity_caller));

		if (callerActivity != 2){ //2: VolumeActivity
//			userPrefs.setSightProblem(bundle.getInt(getResources().getString(R.string.visual_impairment)));
//			userPrefs.setHearingProblem(bundle.getInt(getResources().getString(R.string.hearing_impairment)));
			
			userPrefs.setButtonBackgroundColor(getBackgroundColor(btnResize));
			userPrefs.setButtonTextColor(btnResize.getTextColors().getDefaultColor());
		} else {
			speakOut(getResources().getString(R.string.button_info_message_es));
		}
	
		onTouchListener = createOnTouchListener();
		
		initializeServices(TAG);
		addListeners();
		
		userPrefs.setButtonBackgroundColor(defaultButtonColor);
		userPrefs.setLayoutBackgroundColor(DEFAULT_BACK_COLOR);
	}

	private void drawButtons() {
		btnResize = (Button) findViewById(R.id.button_resize);
		textColor = btnResize.getTextColors().getDefaultColor();
		btnBackgroundColor = (Button) findViewById(R.id.button_background_color);
		btnTextColor = (Button) findViewById(R.id.button_text_color);
		btnColorButton = (Button) findViewById(R.id.button_color);
		btnRestore = (Button) findViewById(R.id.button_restore);
		btnInvert = (Button) findViewById(R.id.button_invert);
		
		btnBackgroundColor.setVisibility(View.INVISIBLE);
		btnTextColor.setVisibility(View.INVISIBLE);
		btnColorButton.setVisibility(View.INVISIBLE);
		
		defaultButtonColor = getBackgroundColor(btnResize);
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
		grid.getChildAt(0).setOnTouchListener(onTouchListener);
		grid.getChildAt(1).setOnTouchListener(onTouchListener);
		grid.getChildAt(2).setOnTouchListener(onTouchListener);
		grid.getChildAt(3).setOnTouchListener(onTouchListener);

		findViewById(R.id.buttonact_next).setOnClickListener(this);
		findViewById(R.id.button_background_color).setOnClickListener(this);
		findViewById(R.id.button_text_color).setOnClickListener(this);
		findViewById(R.id.button_color).setOnClickListener(this);
		btnResize.setOnClickListener(this);
		
		btnInvert.setOnClickListener(this);
		btnRestore.setOnClickListener(this);
	}

	@Override
	public void redrawViews() {
		findViewById(R.id.buttonact_next).setMinimumWidth(btnResize.getWidth());
		findViewById(R.id.buttonact_next).setMinimumHeight(btnResize.getHeight());
		findViewById(R.id.button_background_color).setMinimumWidth(btnResize.getWidth());
		findViewById(R.id.button_background_color).setMinimumHeight(btnResize.getHeight());
		findViewById(R.id.button_text_color).setMinimumWidth(btnResize.getWidth());
		findViewById(R.id.button_text_color).setMinimumHeight(btnResize.getHeight());
		findViewById(R.id.button_color).setMinimumWidth(btnResize.getWidth());
		findViewById(R.id.button_color).setMinimumHeight(btnResize.getHeight());
	}

	private OnTouchListener createOnTouchListener(){
		return new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				BUTTON_SIZE_CHANGED = true;
				final int width  = btnResize.getWidth();
				final int height = btnResize.getHeight();
				//TODO: And the Text size?
				if ((view.getWidth() > maxWidth) && (view.getHeight() > maxHeight)){
					if (btnResize.getWidth() < VIEW_MAX_SIZE) {
						btnResize.setWidth(width + 10);
						btnResize.setHeight(height + 10);
					}
				}
				redrawViews();
				return true;
			}
		};
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.buttonact_next) {
			//TODO: next activity for configuring TextEdit size and color
			if (userPrefs.getDisplayHasApplicable() == 0){
				speakOut("Well done!");
			}
			Intent intent = new Intent(this, EditTextConfigActivity.class);

			userPrefs.setButtonWidth(btnResize.getWidth());
			userPrefs.setButtonHeight(btnResize.getHeight());
			userPrefs.setButtonTextColor(textColor);
			intent.putExtra(getResources().getString(R.string.activity_caller), 1);

			intent.putExtra(getResources().getString(R.string.view_params), userPrefs);
			
			//Store in the ontology
			if (BUTTON_BACKGROUND_COLOR_CHANGED){
				userPrefs.setButtonBackgroundColor(getBackgroundColor(btnResize));
			}
			
			if (LAYOUT_BACKGROUND_COLOR_CHANGED ){
				userPrefs.setLayoutBackgroundColor(getBackgroundColor(grid));
			}

			startActivity(intent);
		}				
		
		else if (view.getId() == R.id.button_color){
			BUTTON_BACKGROUND_COLOR_CHANGED = true;
			
			Random randomBackColor = new Random(); 
			buttonBackgroundColor = Color.argb(255, randomBackColor.nextInt(256), randomBackColor.nextInt(256), randomBackColor.nextInt(256));
			btnResize.setBackgroundColor(buttonBackgroundColor);
			findViewById(R.id.buttonact_next).setBackgroundColor(getBackgroundColor(this.btnResize));
			findViewById(R.id.button_background_color).setBackgroundColor(buttonBackgroundColor);
			findViewById(R.id.button_text_color).setBackgroundColor(buttonBackgroundColor);
			findViewById(R.id.button_color).setBackgroundColor(buttonBackgroundColor);
		
		} else if (view.getId() == R.id.button_text_color){
			BUTTON_TEXT_COLOR_CHANGED = true;
			
			Random randomTextColor = new Random(); 
			int textColor = Color.argb(255, randomTextColor.nextInt(256), randomTextColor.nextInt(256), randomTextColor.nextInt(256));   
			btnResize.setTextColor(textColor);
			this.textColor = textColor;
			((Button)findViewById(R.id.buttonact_next)).setTextColor(textColor);
			((Button)findViewById(R.id.button_background_color)).setTextColor(textColor);
			((Button)findViewById(R.id.button_text_color)).setTextColor(textColor);
			((Button)findViewById(R.id.button_color)).setTextColor(textColor);
		
		} else if (view.getId() == R.id.button_background_color){
			LAYOUT_BACKGROUND_COLOR_CHANGED = true;
			
			Random randomColor = new Random(); 
			layoutBackgroundColor = Color.argb(255, randomColor.nextInt(256), randomColor.nextInt(256), randomColor.nextInt(256));   
			grid.setBackgroundColor(layoutBackgroundColor);
			Log.d(ButtonConfigActivity.class.getSimpleName(), "BackgroundColor: " + layoutBackgroundColor);
		} else if (view.getId() == R.id.button_resize){
			btnTextColor.setVisibility(View.VISIBLE);
			btnBackgroundColor.setVisibility(View.VISIBLE);
			btnColorButton.setVisibility(View.VISIBLE);
		} else if (view.getId() == R.id.button_restore){
			findViewById(R.id.buttonact_next).setBackgroundColor(Color.GRAY);
			findViewById(R.id.button_background_color).setBackgroundColor(Color.GRAY);
			findViewById(R.id.button_text_color).setBackgroundColor(Color.GRAY);
			findViewById(R.id.button_color).setBackgroundColor(Color.GRAY);
			findViewById(R.id.button_resize).setBackgroundColor(Color.GRAY);
			
			((Button)findViewById(R.id.buttonact_next)).setTextColor(Color.BLACK);
			((Button)findViewById(R.id.button_background_color)).setTextColor(Color.BLACK);
			((Button)findViewById(R.id.button_text_color)).setTextColor(Color.BLACK);
			((Button)findViewById(R.id.button_color)).setTextColor(Color.BLACK);
			
			grid.setBackgroundColor(Color.WHITE);
		} else if (view.getId() == R.id.button_invert){
			findViewById(R.id.buttonact_next).setBackgroundColor(Color.GRAY);
			findViewById(R.id.button_background_color).setBackgroundColor(Color.GRAY);
			findViewById(R.id.button_text_color).setBackgroundColor(Color.GRAY);
			findViewById(R.id.button_color).setBackgroundColor(Color.GRAY);
			findViewById(R.id.button_resize).setBackgroundColor(Color.GRAY);
			
			((Button)findViewById(R.id.buttonact_next)).setTextColor(Color.WHITE);
			((Button)findViewById(R.id.button_background_color)).setTextColor(Color.WHITE);
			((Button)findViewById(R.id.button_text_color)).setTextColor(Color.WHITE);
			((Button)findViewById(R.id.button_color)).setTextColor(Color.WHITE);
			
			grid.setBackgroundColor(Color.BLACK);
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
