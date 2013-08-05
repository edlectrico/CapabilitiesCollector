package es.deusto.deustotech.views;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import es.deusto.deustotech.R;

/**
 * This activity allows the user to configure the minimum volume
 * level for interacting with the device just by tapping the
 * screen 
 * 
 * @author edlectrico
 *
 */

public class VolumeConfigActivity extends Activity implements OnClickListener, TextToSpeech.OnInitListener {

	private LinearLayout layout;
	private AudioManager audioManager = null;
	private TextToSpeech tts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.volume_config);
		
		layout = (LinearLayout) findViewById(R.id.volume_layout);
		layout.setOnClickListener(this);
		
		tts = new TextToSpeech(this, this);

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
	}

	@Override
	public void onClick(View view) {
		Random randomGenerator = new Random();
	    int randomInt = randomGenerator.nextInt(100);
		
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				randomInt, 0);

		tts.speak("Testing volume", TextToSpeech.QUEUE_FLUSH, null);
	}

	@Override
	public void onInit(int status) { }

}
