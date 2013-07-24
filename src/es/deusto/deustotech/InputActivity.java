/**
 * 
 */
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

/**
 * @author edlectrico
 * This activity checks user basic input capabilities
 */
public class InputActivity extends Activity implements TextToSpeech.OnInitListener{

	private TextToSpeech tts;
	private Vibrator vibrator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_activity);
	
		tts = new TextToSpeech(this, this);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		
		findViewById(R.id.input_button).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				//TODO: 1. Vibrate 2. Go to ViewsActivity
				vibrator.vibrate(500);
				speakOut("Well done!");
				startActivity(new Intent(getApplicationContext(), ViewsActivity.class));
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
                speakOut("Can you push the button in the screen?");
            }
 
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
	}
	
	private void speakOut(final String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
	
}
