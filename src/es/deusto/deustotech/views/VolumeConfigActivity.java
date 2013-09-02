package es.deusto.deustotech.views;

import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import es.deusto.deustotech.R;

/**
 * This activity allows the user to configure the minimum volume
 * level for interacting with the device just by tapping the
 * screen 
 * 
 * @author edlectrico
 *
 */

public class VolumeConfigActivity extends AbstractActivity {

	private static final String TAG = VolumeConfigActivity.class.getSimpleName();
	
	private GridLayout grid;
	private AudioManager audioManager = null;
	private int volumeLevel = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.volume_config);
		
		Bundle bundle = getIntent().getExtras();
		userPrefs = bundle.getParcelable("viewParams");
		
		grid = (GridLayout) findViewById(R.id.volume_layout);
		
		redrawViews();
		initializeServices(TAG);
		addListeners();
	}
	
	@Override
	public void initializeServices(String TAG) {
		if (userPrefs.getSightProblem() == 1){
			super.initializeServices(TAG);
			
			speakOut(getResources().getString(R.string.edit_text_info_message));
		}
		if (userPrefs.getBrightness() != 0){
			WindowManager.LayoutParams layoutParams = getWindow()
					.getAttributes();
			layoutParams.screenBrightness = userPrefs.getBrightness();
			getWindow().setAttributes(layoutParams);
		}
		
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
	}
	
	@Override
	public void addListeners() {
		findViewById(R.id.end_button).setOnClickListener(this);
		findViewById(R.id.end_button).setOnClickListener(this);
		
		grid.getChildAt(0).setOnClickListener(this);
		grid.getChildAt(1).setOnClickListener(this);
	}
	
	@Override
	public void redrawViews() {
		grid.setBackgroundColor(userPrefs.getBackgroundColor());
		
		findViewById(R.id.end_button).setMinimumWidth((int)userPrefs.getButtonWidth());
		findViewById(R.id.end_button).setMinimumHeight((int) userPrefs.getButtonHeight());
		
		((Button)findViewById(R.id.end_button)).setTextColor(userPrefs.getButtonTextColor());
		((TextView)findViewById(R.id.volume_message)).setTextSize(userPrefs.getTextEditSize());

		if (userPrefs.getBackgroundColor() != 0){
			((Button)findViewById(R.id.end_button)).setBackgroundColor(userPrefs.getButtonBackgroundColor());
		}
		
		if (userPrefs.getTextEditTextColor() != 0){
			((TextView)findViewById(R.id.volume_message)).setTextColor(userPrefs.getTextEditTextColor());
		}
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.end_button){
			//Store
			userPrefs.setVolume(volumeLevel);
			
			SharedPreferences  preferences = getPreferences(MODE_PRIVATE);
			Editor prefsEditor = preferences.edit();
			
			Gson gson = new Gson();
			String json = gson.toJson(userPrefs);
			prefsEditor.putString("viewParams", json);
			prefsEditor.commit();
			
			Intent intent = new Intent(this, MailSenderActivity.class);
			
			intent.putExtra("viewParams", userPrefs);
			
			startActivity(intent);
		} else {
			Random randomGenerator = new Random();
			volumeLevel = randomGenerator.nextInt(100);
			
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					volumeLevel, 0);
			
			tts.speak("Testing volume", TextToSpeech.QUEUE_FLUSH, null);
		}
	}

}
