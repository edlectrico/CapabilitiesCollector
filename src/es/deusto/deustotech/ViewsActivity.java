package es.deusto.deustotech;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
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

/**
 * This activity configures the minimum visual interaction values
 */
public class ViewsActivity extends Activity implements android.view.View.OnClickListener, 
OnCheckedChangeListener, TextToSpeech.OnInitListener {

	private static final String TAG = ViewsActivity.class.getSimpleName();
	private Button previewButton;
	private Button previewTextEdit;
	private SharedPreferences minimunViewPreferences;
	private GridLayout grid;
	private AudioManager audioManager = null;
	private TextToSpeech tts;
	
	//brightness
	float brightnessValue = 0.5f; // dummy default value
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.views_activity);

		previewButton = (Button) findViewById(R.id.test_button);
		previewButton.setOnClickListener(this);
		
		previewTextEdit = (Button) findViewById(R.id.test_text_edit);
		previewTextEdit.setOnClickListener(this);
		
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
		
		this.grid.getChildAt(0).setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d(TAG, "Layout 0");
				
				return false;
			}
		});
		
		this.grid.getChildAt(1).setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d(TAG, "Layout 1");
				
				float x = event.getRawX();
				float y = event.getRawY();
		
				previewButton.setWidth((int) x);
				previewButton.setHeight((int) y);
				previewButton.invalidate();
				
				if (event.getAction() == MotionEvent.ACTION_UP){
					//store
					SharedPreferences.Editor uiEditor = minimunViewPreferences.edit();
					Set<String> values = new HashSet<String>();
					values.add(String.valueOf(x));
					values.add(String.valueOf(y));
					uiEditor.putStringSet(getResources().getString(R.string.adapted_configuration_ui), values);
					uiEditor.commit();
					
					Log.d(TAG, "Stored!");
				}
				
				return true;
			}
		});
		
		this.grid.getChildAt(2).setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d(TAG, "Layout 2");
				
				float x = event.getRawX();
		
				previewTextEdit.setTextSize((float) (x / 10.0));
				
				previewTextEdit.invalidate();
				
				if (event.getAction() == MotionEvent.ACTION_UP){
					//store
//					SharedPreferences.Editor uiEditor = minimunViewPreferences.edit();
					//TODO: store only font size
//					Set<String> values = new HashSet<String>();
//					values.add(String.valueOf(x));
//					values.add(String.valueOf(y));
//					uiEditor.putStringSet(getResources().getString(R.string.adapted_configuration_ui), values);
//					uiEditor.commit();
					
					Log.d(TAG, "Stored!");
				}
				
				return true;
			}
		});
	}

	private void enableNightMode(final boolean enable) {
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
		Toast.makeText(getApplicationContext(), "This is a test!", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean checked) {
			enableNightMode(checked);
	}

	@Override
	public void onInit(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
