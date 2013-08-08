package es.deusto.deustotech.views;

import java.util.Random;

import com.google.gson.Gson;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import es.deusto.deustotech.R;
import es.deusto.deustotech.utils.UserMinimumPreferences;

/**
 * This activity allows the user to configure the minimum volume
 * level for interacting with the device just by tapping the
 * screen 
 * 
 * @author edlectrico
 *
 */

public class VolumeConfigActivity extends Activity implements OnClickListener, TextToSpeech.OnInitListener {

	private GridLayout grid;
	private AudioManager audioManager = null;
	private TextToSpeech tts;
	private UserMinimumPreferences userPrefs;
	private int volumeLevel = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.volume_config);
		
		grid = (GridLayout) findViewById(R.id.volume_layout);
		
		grid.getChildAt(0).setOnClickListener(this);
		grid.getChildAt(1).setOnClickListener(this);
		
		Bundle bundle = getIntent().getExtras();
		userPrefs = bundle.getParcelable("viewParams");
		
		tts = new TextToSpeech(this, this);
		if (userPrefs.getSightProblem() == 1){
			
			speakOut(getResources().getString(R.string.volume_message));
		}
		
		WindowManager.LayoutParams layoutParams = getWindow()
				.getAttributes();
		layoutParams.screenBrightness = userPrefs.getBrightness();
		getWindow().setAttributes(layoutParams);
		
		findViewById(R.id.end_button).setOnClickListener(this);
		findViewById(R.id.end_button).setMinimumWidth((int)userPrefs.getButtonWidth());
		findViewById(R.id.end_button).setMinimumHeight((int) userPrefs.getButtonHeight());
		((Button)findViewById(R.id.end_button)).setBackgroundColor(userPrefs.getButtonBackgroundColor());
		((Button)findViewById(R.id.end_button)).setTextColor(userPrefs.getButtonTextColor());
		
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
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
		} else {
			Random randomGenerator = new Random();
			volumeLevel = randomGenerator.nextInt(100);
			
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					volumeLevel, 0);
			
			tts.speak("Testing volume", TextToSpeech.QUEUE_FLUSH, null);
		}
	}

	@Override
	public void onInit(int status) { }
	
	private void speakOut(final String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

}
