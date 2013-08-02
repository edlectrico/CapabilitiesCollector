package es.deusto.deustotech;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.GridLayout;
import es.deusto.deustotech.utils.ColorPickerDialog;

/**
 * This activity configures the minimum visual interaction values
 */
public class ViewsActivity extends Activity implements android.view.View.OnClickListener, 
	TextToSpeech.OnInitListener, ColorPickerDialog.OnColorChangedListener {

	private static final String TAG = ViewsActivity.class.getSimpleName();
	private Button testButton;
	private GridLayout grid;
	private AudioManager audioManager = null;
	private TextToSpeech tts;
	private int viewColor;
	
	private OnTouchListener onTouchListener;
	
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Rect mBounds;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.views_activity);

		this.testButton = (Button) findViewById(R.id.test_button);
		this.testButton.setOnClickListener(this);
		
		this.tts = new TextToSpeech(this, this);
		
		this.audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		this.grid = (GridLayout) findViewById(R.id.default_layout);
		
		this.onTouchListener = createOnTouchListener();
		
		this.grid.getChildAt(0).setOnTouchListener(this.onTouchListener);
		this.grid.getChildAt(1).setOnTouchListener(this.onTouchListener);
		this.testButton.setOnTouchListener(this.onTouchListener);
	}
	
	public OnTouchListener createOnTouchListener(){
		return new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				final int width  = testButton.getWidth();
				final int height = testButton.getHeight();
//				final float textSize = testButton.getTextSize();
				
				testButton.setWidth(width + 10);
				testButton.setHeight(height + 10);
//				testButton.setTextSize(textSize + 0.1F);
				
				return true;
			}
		};
	}

	@Override
	public void onClick(View view) {
		//Launch color dialog
		if (view.getId() == R.id.test_button){
//			buttonPressed = true;
			int colorId = getBackgroundColor(this.testButton);
			new ColorPickerDialog(this, this, colorId).show();
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
	public void onInit(int status) { }

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
