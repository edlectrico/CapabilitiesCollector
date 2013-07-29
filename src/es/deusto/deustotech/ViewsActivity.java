package es.deusto.deustotech;

import java.util.HashMap;

import com.google.gson.Gson;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
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
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;
import es.deusto.deustotech.utils.ColorPickerDialog;

/**
 * This activity configures the minimum visual interaction values
 */
public class ViewsActivity extends Activity implements android.view.View.OnClickListener, 
OnCheckedChangeListener, TextToSpeech.OnInitListener, ColorPickerDialog.OnColorChangedListener {

	private static final String TAG = ViewsActivity.class.getSimpleName();
	private Button testButton;
	private Button testTextEdit; //Actually, it is a button with a transparent background
	private Button storeButton;
	private SharedPreferences minimunViewPreferences;
	private GridLayout grid;
	private AudioManager audioManager = null;
	private TextToSpeech tts;
	private int viewColor;
	
	private boolean buttonPressed = false;
	private boolean textEditPressed = false;
	
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Rect mBounds;
	
	//brightness
	float brightnessValue = 0.5f; // dummy default value
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.views_activity);

		testButton = (Button) findViewById(R.id.test_button);
		testButton.setOnClickListener(this);
		
		testTextEdit = (Button) findViewById(R.id.test_text_edit);
		testTextEdit.setOnClickListener(this);
		
		storeButton = (Button) findViewById(R.id.store_button);
		storeButton.setOnClickListener(this);
		
		tts = new TextToSpeech(this, this);
		
		SeekBar brightnessSeekBar = (SeekBar) findViewById(R.id.brightness_control);
		SeekBar volumeSeekBar = (SeekBar) findViewById(R.id.volume_control);
		
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		volumeSeekBar.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		volumeSeekBar.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));   
		
		Switch nightModeSwitch = (Switch) findViewById(R.id.night_mode_switch);
	    if (nightModeSwitch != null) {
	        nightModeSwitch.setOnCheckedChangeListener(this);
	    }

		brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				brightnessValue = (float) progress / 100;

				WindowManager.LayoutParams layoutParams = getWindow()
						.getAttributes();
				layoutParams.screenBrightness = brightnessValue;
				getWindow().setAttributes(layoutParams);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
		});
		
		volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
				
				tts.speak("Testing volume", TextToSpeech.QUEUE_FLUSH, null);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
		});
		
		this.minimunViewPreferences = getSharedPreferences(getResources().getString(R.string.preferences_name_minui), 0);
		this.grid = (GridLayout) findViewById(R.id.default_layout);
		
		this.grid.getChildAt(1).setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d(TAG, "Layout 1");
				
				float x = event.getRawX();
				float y = event.getRawY();
		
				testButton.setWidth((int) x);
				testButton.setHeight((int) y);
				testButton.invalidate();
				
				return true;
			}
		});
		
		this.grid.getChildAt(2).setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d(TAG, "Layout 2");
				
				float x = event.getRawX();
		
				testTextEdit.setTextSize((float) (x / 10.0));
				testTextEdit.invalidate();
				
				//TODO: Change also Button text size?
				
				return true;
			}
		});
	}

	private void enableNightMode(final boolean enable) {
		//TODO: Night mode is more than changing just the background color...
		View view = this.getWindow().getDecorView();
		if (enable){
			view.setBackgroundColor(Color.BLACK);
		} else {
			view.setBackgroundColor(Color.WHITE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		//Launch color dialog
		if (view.getId() == R.id.test_button){
			buttonPressed = true;
			int colorId = getBackgroundColor(this.testButton);
			new ColorPickerDialog(this, this, colorId).show();
		} else if (view.getId() == R.id.test_text_edit){
			textEditPressed = true;
			final ColorStateList colors = this.testTextEdit.getTextColors();
			new ColorPickerDialog(this, this, colors.getDefaultColor()).show();
			//TODO: also change button text color?
		} else if (view.getId() == R.id.store_button){
			//Store parameters
			SharedPreferences.Editor uiEditor = minimunViewPreferences.edit();
			final ViewParams buttonParams = new ViewParams(testButton.getWidth(), testButton.getHeight(), 
					getBackgroundColor(this.testButton), this.testButton.getTextColors().getDefaultColor());
			final ViewParams textEditParams = new ViewParams(testTextEdit.getWidth(), testTextEdit.getHeight(), 
					getBackgroundColor(this.testTextEdit), this.testTextEdit.getTextColors().getDefaultColor());
			
			Gson gson = new Gson();
			final String json = gson.toJson(generateUserConfig(buttonParams, textEditParams));
			
			uiEditor.putString(getResources().getString(R.string.adapted_configuration_ui), json);
			uiEditor.commit();
			
			Toast.makeText(this, "Preferences stored.", Toast.LENGTH_LONG).show();
			
			HashMap<String, Object> testUI = gson.fromJson(json, HashMap.class);
			testUI.get("Button");
			testUI.get("TextEdit");
			testUI.get("Brightness");
			testUI.get("Volume");
		}
	}

	private HashMap<String, Object> generateUserConfig(
			final ViewParams buttonParams, final ViewParams textEditParams) {
		HashMap<String, Object> viewConf = new HashMap<String, Object>();
		viewConf.put("Button", buttonParams);
		viewConf.put("TextEdit", textEditParams);
		viewConf.put("Brightness", this.brightnessValue);
		viewConf.put("Volume", (float)audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

		return viewConf;
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
	public void onCheckedChanged(CompoundButton arg0, boolean checked) {
			enableNightMode(checked);
	}

	@Override
	public void onInit(int status) { }

	@Override
	public void colorChanged(int color) {
		this.viewColor = color;
		if (buttonPressed){
			this.testButton.setBackgroundColor(this.viewColor);
			buttonPressed = false;
		} else if (textEditPressed){
			this.testTextEdit.setTextColor(this.viewColor);
			textEditPressed = false;
		}
	}
	
}
