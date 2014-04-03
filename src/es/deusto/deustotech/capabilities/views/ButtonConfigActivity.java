package es.deusto.deustotech.capabilities.views;

import java.util.List;
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
import es.deusto.deustotech.capabilities.UserMinimumPreferences;
import es.deusto.deustotech.capabilities.utils.OntologyManager;

/**
 * This activity configures the minimum visual interaction values
 * 
 * @author edlectrico
 * 
 */
public class ButtonConfigActivity extends AbstractActivity {

	private static final int VIEW_MAX_SIZE = 500;

	private static final String TAG = ButtonConfigActivity.class.getSimpleName();

	private OntologyManager ontManager;
	private static List<String> buttons;
	private static List<String> backgrounds;
	
	private Button testButton;
	private Button backgroundColorButton;
	private Button backColorButton;
	private Button textColorButton;
	private GridLayout grid;
	
	private int maxWidth = 0;
	private int maxHeight = 0;
	private int buttonBackgroundColor = 0;
	private int buttonTextColor = 0;
	private int backgroundColor = 0;

	private boolean buttonBackgroundColorChanged = false;

	private OnTouchListener onTouchListener;

	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Rect mBounds;
	
	int callerActivity = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.button_config);
		
		ontManager = super.getOntologyManager();

		testButton = (Button) findViewById(R.id.test_button);
		buttonTextColor = testButton.getTextColors().getDefaultColor();
		backgroundColorButton = (Button) findViewById(R.id.background_color_button);
		textColorButton = (Button) findViewById(R.id.text_color_button);
		backColorButton = (Button) findViewById(R.id.back_color_button);
		
		backgroundColorButton.setVisibility(View.INVISIBLE);
		textColorButton.setVisibility(View.INVISIBLE);
		backColorButton.setVisibility(View.INVISIBLE);

		Bundle bundle = getIntent().getExtras();
		
		callerActivity = bundle.getInt("caller");

		if (callerActivity != 2){ //2: VolumeActivity
			userPrefs = new UserMinimumPreferences();
			userPrefs.setSightProblem(bundle.getInt(getResources().getString(R.string.visual_impairment)));
			userPrefs.setHearingProblem(bundle.getInt(getResources().getString(R.string.hearing_impairment)));
			
			//TODO: by default (not working)
			userPrefs.setButtonBackgroundColor(getBackgroundColor(testButton));
			userPrefs.setButtonTextColor(testButton.getTextColors().getDefaultColor());
		} else {
			//TODO: Read instructions
			speakOut(getResources().getString(R.string.button_info_message));
		}

		grid = (GridLayout) findViewById(R.id.default_layout);
		onTouchListener = createOnTouchListener();
		
		initializeServices(TAG);
		addListeners();
	}

	@Override
	public void initializeServices(String TAG) {
		//If blind user, voice control
		if (userPrefs.getSightProblem() == 1){
			super.initializeServices(TAG);

			speakOut(getResources().getString(R.string.button_info_message));
		}
	}

	@Override
	public void addListeners() {
		grid.getChildAt(0).setOnTouchListener(onTouchListener);
		grid.getChildAt(1).setOnTouchListener(onTouchListener);
		grid.getChildAt(2).setOnTouchListener(onTouchListener);
		grid.getChildAt(3).setOnTouchListener(onTouchListener);

		findViewById(R.id.next_button).setOnClickListener(this);
		findViewById(R.id.background_color_button).setOnClickListener(this);
		findViewById(R.id.text_color_button).setOnClickListener(this);
		findViewById(R.id.back_color_button).setOnClickListener(this);
		testButton.setOnClickListener(this);
	}

	@Override
	public void redrawViews() {
		findViewById(R.id.next_button).setMinimumWidth(testButton.getWidth());
		findViewById(R.id.next_button).setMinimumHeight(testButton.getHeight());

		findViewById(R.id.background_color_button).setMinimumWidth(testButton.getWidth());
		findViewById(R.id.background_color_button).setMinimumHeight(testButton.getHeight());

		findViewById(R.id.text_color_button).setMinimumWidth(testButton.getWidth());
		findViewById(R.id.text_color_button).setMinimumHeight(testButton.getHeight());

		findViewById(R.id.back_color_button).setMinimumWidth(testButton.getWidth());
		findViewById(R.id.back_color_button).setMinimumHeight(testButton.getHeight());
	}


	private OnTouchListener createOnTouchListener(){
		return new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				final int width  = testButton.getWidth();
				final int height = testButton.getHeight();
				//TODO: And the Text size?
				if ((view.getWidth() > maxWidth) && (view.getHeight() > maxHeight)){
					if (testButton.getWidth() < VIEW_MAX_SIZE) {
						testButton.setWidth(width + 10);
						testButton.setHeight(height + 10);
					}
				}
				redrawViews();
				return true;
			}
		};
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.next_button) {
			//TODO: next activity for configuring TextEdit size and color
			if (userPrefs.getSightProblem() == 1){
				speakOut("Well done!");
			}
			Intent intent = new Intent(this, EditTextConfigActivity.class);

			if (buttonBackgroundColorChanged){
				userPrefs.setButtonBackgroundColor(getBackgroundColor(testButton));
				//				buttonBackgroundColorChanged = false;
			} else {
				userPrefs.setButtonBackgroundColor(0);
			}
			userPrefs.setButtonWidth(testButton.getWidth());
			userPrefs.setButtonHeight(testButton.getHeight());
			userPrefs.setButtonTextColor(buttonTextColor);
			userPrefs.setBackgroundColor(backgroundColor);

			intent.putExtra("viewParams", userPrefs);
			
			//Store in the ontology
			
			buttons = ontManager.getIndividualOfClass(super.getOntologyNamespace() + "Button");
			backgrounds = ontManager.getIndividualOfClass(super.getOntologyNamespace() + "Background");
			
			ontManager.addDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "userViewHasWidth", testButton.getWidth());
			ontManager.addDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "userViewHasHeight", testButton.getHeight());
			ontManager.addDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "userViewHasColor", buttonBackgroundColor);
			if (buttonTextColor > 0){
				ontManager.addDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "userViewHasTextColor", buttonTextColor);
			}
			ontManager.addDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "userViewHasTextSize", testButton.getTextSize());
			ontManager.addDataTypePropertyValue(backgrounds.get(0), super.getOntologyNamespace() + "userViewHasColor", buttonBackgroundColor);

			startActivity(intent);
		}				
		else if (view.getId() == R.id.background_color_button){
			buttonBackgroundColorChanged = true;
			Random randomBackColor = new Random(); 
			buttonBackgroundColor = Color.argb(255, randomBackColor.nextInt(256), randomBackColor.nextInt(256), randomBackColor.nextInt(256));   
			testButton.setBackgroundColor(buttonBackgroundColor);
			findViewById(R.id.next_button).setBackgroundColor(getBackgroundColor(this.testButton));
			findViewById(R.id.background_color_button).setBackgroundColor(buttonBackgroundColor);
			findViewById(R.id.text_color_button).setBackgroundColor(buttonBackgroundColor);
			findViewById(R.id.back_color_button).setBackgroundColor(buttonBackgroundColor);
		} else if (view.getId() == R.id.text_color_button){
			Random randomTextColor = new Random(); 
			int textColor = Color.argb(255, randomTextColor.nextInt(256), randomTextColor.nextInt(256), randomTextColor.nextInt(256));   
			testButton.setTextColor(textColor);
			this.buttonTextColor = textColor;
			((Button)findViewById(R.id.next_button)).setTextColor(textColor);
			((Button)findViewById(R.id.background_color_button)).setTextColor(textColor);
			((Button)findViewById(R.id.text_color_button)).setTextColor(textColor);
			((Button)findViewById(R.id.back_color_button)).setTextColor(textColor);
		} else if (view.getId() == R.id.back_color_button){
			Random randomColor = new Random(); 
			backgroundColor = Color.argb(255, randomColor.nextInt(256), randomColor.nextInt(256), randomColor.nextInt(256));   
			grid.setBackgroundColor(backgroundColor);
			Log.d(ButtonConfigActivity.class.getSimpleName(), "BackgroundColor: " + backgroundColor);
		} else if (view.getId() == R.id.test_button){
			textColorButton.setVisibility(View.VISIBLE);
			backgroundColorButton.setVisibility(View.VISIBLE);
			backColorButton.setVisibility(View.VISIBLE);
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
}
