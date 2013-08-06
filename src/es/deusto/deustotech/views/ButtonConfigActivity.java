package es.deusto.deustotech.views;

import java.util.Locale;

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
import es.deusto.deustotech.utils.ViewParams;

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
	
	private OnTouchListener onTouchListener;
	
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Rect mBounds;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.button_config_activity);

		testButton = (Button) findViewById(R.id.test_button);
		testButton.setOnClickListener(this);
		findViewById(R.id.next_button).setOnClickListener(this);
		
		Bundle bundle = getIntent().getExtras();
		//If blind user, voice control
		if (bundle.getBoolean(getResources().getString(R.string.visual_impairment))){
			tts = new TextToSpeech(this, this);
			
			speakOut(getResources().getString(R.string.button_info_message));
		}
		
		grid = (GridLayout) findViewById(R.id.default_layout);
		
		onTouchListener = createOnTouchListener();
		
		grid.getChildAt(0).setOnTouchListener(this.onTouchListener);
		grid.getChildAt(1).setOnTouchListener(this.onTouchListener);
		grid.getChildAt(2).setOnTouchListener(this.onTouchListener);
		
		testButton.setOnTouchListener(this.onTouchListener);
	}
	
	public OnTouchListener createOnTouchListener(){
		return new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				final int width  = testButton.getWidth();
				final int height = testButton.getHeight();
				
				if ((view.getWidth() > maxWidth) && (view.getHeight() > maxHeight)){
					testButton.setWidth(width + 10);
					testButton.setHeight(height + 10);
				}
				
				return true;
			}
		};
	}

	@Override
	public void onClick(View view) {
		//TODO: next activity for configuring TextEdit size and color
		if (view.getId() == R.id.test_button){
			speakOut("Well done!");
		}
		
		Intent intent = new Intent(this, EditTextConfigActivity.class);

		ViewParams viewParams = new ViewParams();
		viewParams.setButtonBackgroundColor(getBackgroundColor(this.testButton));
		viewParams.setButtonWidth(testButton.getWidth());
		viewParams.setButtonHeight(testButton.getHeight());

		intent.putExtra("viewParams", viewParams);

		startActivity(intent);
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
