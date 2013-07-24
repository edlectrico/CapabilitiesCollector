package es.deusto.deustotech;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.GridLayout;

/**
 * @author edlectrico
 * This activity checks user basic input capabilities
 */
public class InputActivity extends Activity implements TextToSpeech.OnInitListener{

	private GridLayout gl;
	private TextToSpeech tts;
	private Vibrator vibrator;
	private boolean longPush = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_activity);
	
		gl = (GridLayout) findViewById(R.id.grid_layout);
		gl.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				longPush = true;
				speakOut("If you prefer an audio based interaction, please say YES. If not, please say NO.");
				return true;
			}
		});
		
		tts = new TextToSpeech(this, this);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		findViewById(R.id.input_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				//TODO: 1. Vibrate 2. Go to ViewsActivity
				if (!longPush){
					vibrator.vibrate(500);
					speakOut("Well done!");
					startActivity(new Intent(getApplicationContext(), ViewsActivity.class));
				}
			}
		});
		
		findViewById(R.id.input_button).setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				longPush = true;
				speakOut("If you prefer an audio based interaction, please say YES. If not, please say NO.");
				return true;
			}
		});
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			 
            int result = tts.setLanguage(Locale.US);
 
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut("Can you push the button in the screen? " +
                		"If you can not, then hold your finger over the screen for a while.");
            }
 
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
	}
	
	private void speakOut(final String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

}
