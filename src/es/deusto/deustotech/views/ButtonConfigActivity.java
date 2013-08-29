package es.deusto.deustotech.views;

import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.GridLayout;
import es.deusto.deustotech.R;
import es.deusto.deustotech.utils.ColorPickerDialog;
import es.deusto.deustotech.utils.UserMinimumPreferences;

/**
 * This activity configures the minimum visual interaction values
 * 
 * @author edlectrico
 * 
 */
public class ButtonConfigActivity extends Activity implements android.view.View.OnClickListener, 
	TextToSpeech.OnInitListener, ColorPickerDialog.OnColorChangedListener {

	private Button testButton;
	private GridLayout grid;
	private TextToSpeech tts;
//	private int viewColor;
	
	private int maxWidth;
	private int maxHeight;
	private int buttonTextColor;
	
	private OnTouchListener onTouchListener;
	
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Rect mBounds;
	
	private UserMinimumPreferences userPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.button_config_activity);

		testButton = (Button) findViewById(R.id.test_button);
		testButton.setOnClickListener(this);
		buttonTextColor = testButton.getTextColors().getDefaultColor();
		
		findViewById(R.id.next_button).setOnClickListener(this);
		
		Bundle bundle = getIntent().getExtras();
		
		userPrefs = new UserMinimumPreferences();
		userPrefs.setSightProblem(bundle.getInt(getResources().getString(R.string.visual_impairment)));
		userPrefs.setHearingProblem(bundle.getInt(getResources().getString(R.string.hearing_impairment)));
		
		//TODO: by default (not working)
		userPrefs.setButtonBackgroundColor(getBackgroundColor(testButton));
		userPrefs.setButtonTextColor(testButton.getTextColors().getDefaultColor());
		
		//If blind user, voice control
		if (userPrefs.getSightProblem() == 1){
			tts = new TextToSpeech(this, this);
			
			speakOut(getResources().getString(R.string.button_info_message));
		}
		
		grid = (GridLayout) findViewById(R.id.default_layout);
		
		onTouchListener = createOnTouchListener();
		
		grid.getChildAt(0).setOnTouchListener(this.onTouchListener);
		grid.getChildAt(1).setOnTouchListener(this.onTouchListener);
		grid.getChildAt(2).setOnTouchListener(this.onTouchListener);
		grid.getChildAt(3).setOnTouchListener(this.onTouchListener);
		
		testButton.setOnTouchListener(this.onTouchListener);
		
		findViewById(R.id.background_color_button).setOnClickListener(this);
		findViewById(R.id.text_color_button).setOnClickListener(this);
	}
	
	public OnTouchListener createOnTouchListener(){
		return new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				final int width  = testButton.getWidth();
				final int height = testButton.getHeight();
				//TODO: And the Text size?
				if ((view.getWidth() > maxWidth) && (view.getHeight() > maxHeight)){
					testButton.setWidth(width + 10);
					testButton.setHeight(height + 10);
				}
				
				resizeButtons();
				
				return true;
			}

			private void resizeButtons() {
				findViewById(R.id.next_button).setMinimumWidth(testButton.getWidth());
				findViewById(R.id.next_button).setMinimumHeight(testButton.getHeight());
				
				findViewById(R.id.background_color_button).setMinimumWidth(testButton.getWidth());
				findViewById(R.id.background_color_button).setMinimumHeight(testButton.getHeight());
				
				findViewById(R.id.text_color_button).setMinimumWidth(testButton.getWidth());
				findViewById(R.id.text_color_button).setMinimumHeight(testButton.getHeight());
			}
		};
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.next_button:
			//TODO: next activity for configuring TextEdit size and color
			if (userPrefs.getSightProblem() == 1){
				speakOut("Well done!");
			}
			Intent intent = new Intent(this, EditTextConfigActivity.class);

			userPrefs.setButtonBackgroundColor(getBackgroundColor(this.testButton));
			userPrefs.setButtonWidth(testButton.getWidth());
			userPrefs.setButtonHeight(testButton.getHeight());
			userPrefs.setButtonTextColor(buttonTextColor);

			intent.putExtra("viewParams", userPrefs);

			startActivity(intent);
			break;
			
		case R.id.background_color_button:
			Random randomBackColor = new Random(); 
			int backgroundColor = Color.argb(255, randomBackColor.nextInt(256), randomBackColor.nextInt(256), randomBackColor.nextInt(256));   
			testButton.setBackgroundColor(backgroundColor);
			findViewById(R.id.next_button).setBackgroundColor(getBackgroundColor(this.testButton));
			findViewById(R.id.background_color_button).setBackgroundColor(backgroundColor);
			findViewById(R.id.text_color_button).setBackgroundColor(backgroundColor);
			
		case R.id.text_color_button:
			Random randomTextColor = new Random(); 
			int textColor = Color.argb(255, randomTextColor.nextInt(256), randomTextColor.nextInt(256), randomTextColor.nextInt(256));   
			testButton.setTextColor(textColor);
			this.buttonTextColor = textColor;
			((Button)findViewById(R.id.next_button)).setTextColor(textColor);
			((Button)findViewById(R.id.background_color_button)).setTextColor(textColor);
			((Button)findViewById(R.id.text_color_button)).setTextColor(textColor);
			
		default:
			break;
		}
	}

	public int getBackgroundColor(View view) {
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

	public void initIfNeeded() {
		if(mBitmap == null) {
			mBitmap = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);
			mBounds = new Rect();
		}
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
 
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
            	speakOut(getResources().getString(R.string.button_info_message));
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
	}
	
	private void speakOut(final String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

	@Override
	public void colorChanged(int color) {
//		this.viewColor = color;
//		if (buttonPressed){
//			this.testButton.setBackgroundColor(this.viewColor);
//			buttonPressed = false;
//		} else if (textEditPressed){
////			this.testTextEdit.setTextColor(this.viewColor);
////			textEditPressed = false;
//		}
	}
	
}
