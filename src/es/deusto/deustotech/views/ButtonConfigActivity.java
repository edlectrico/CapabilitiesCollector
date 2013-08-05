package es.deusto.deustotech.views;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
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

/**
 * This activity configures the minimum visual interaction values
 */
public class ButtonConfigActivity extends Activity implements android.view.View.OnClickListener, 
	TextToSpeech.OnInitListener, ColorPickerDialog.OnColorChangedListener {

	private Button testButton;
	private GridLayout grid;
	private AudioManager audioManager = null;
	private TextToSpeech tts;
	private int viewColor;
	
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

		this.testButton = (Button) findViewById(R.id.test_button);
		this.testButton.setOnClickListener(this);
		findViewById(R.id.next_button).setOnClickListener(this);
		
		this.tts = new TextToSpeech(this, this);
		
		this.audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		this.grid = (GridLayout) findViewById(R.id.default_layout);
		
		this.onTouchListener = createOnTouchListener();
		
		this.grid.getChildAt(0).setOnTouchListener(this.onTouchListener);
		this.grid.getChildAt(1).setOnTouchListener(this.onTouchListener);
		this.grid.getChildAt(2).setOnTouchListener(this.onTouchListener);
		
		this.testButton.setOnTouchListener(this.onTouchListener);
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
		//Launch color dialog
		switch (view.getId()) {
		case R.id.test_button:
			int colorId = getBackgroundColor(this.testButton);
			new ColorPickerDialog(this, this, colorId).show();
			break;
			
		case R.id.next_button:
			//TODO: next activity for configuring TextEdit size and color
			Intent intent = new Intent(this, TextEditConfigActivity.class);
			intent.putExtra(getResources().getString(R.string.button_size), testButton.getScaleX());
			intent.putExtra(getResources().getString(R.string.button_background_color), getBackgroundColor(this.testButton));
			
			startActivity(intent);
			break;

		default:
			break;
		}
		
	}

//	private HashMap<String, Object> generateUserConfig(
//			final ViewParams buttonParams, final ViewParams textEditParams) {
//		HashMap<String, Object> viewConf = new HashMap<String, Object>();
//		viewConf.put("Button", buttonParams);
//		viewConf.put("TextEdit", textEditParams);
//		viewConf.put("Brightness", this.brightnessValue);
//		viewConf.put("Volume", (float)audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
//
//		return viewConf;
//	}
	
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
		this.viewColor = color;
//		if (buttonPressed){
//			this.testButton.setBackgroundColor(this.viewColor);
//			buttonPressed = false;
//		} else if (textEditPressed){
////			this.testTextEdit.setTextColor(this.viewColor);
////			textEditPressed = false;
//		}
	}
	
}
