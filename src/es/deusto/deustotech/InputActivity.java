package es.deusto.deustotech;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

/**
 * @author edlectrico
 * This activity checks user basic input capabilities
 */
public class InputActivity extends Activity implements TextToSpeech.OnInitListener{

	//variable for checking Voice Recognition support on user device
	private static final int VR_REQUEST = 999;
	
	private TextToSpeech tts;
	private Vibrator vibrator;
	private boolean longPush = false;
	private boolean voiceRecognition = false;
	private Intent interactionIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_activity);
		
		interactionIntent = new Intent(this, ViewsActivity.class);
	
		voiceRecognition = checkVoiceRecognition();
		
		tts = new TextToSpeech(this, this);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		findViewById(R.id.grid_layout).setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				onLongClickView();
				return true;
			}
		});
		
		findViewById(R.id.input_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!longPush){
					vibrator.vibrate(500);
					speakOut("Well done!");
					startActivity(getDefaultIntent());
				}
			}
		});
		
		findViewById(R.id.input_button).setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				onLongClickView();
				return true;
			}
		});
	}
	
	private Intent getDefaultIntent() {
		interactionIntent.putExtra(getResources().getString(R.string.visual_impairment), false);
		interactionIntent.putExtra(getResources().getString(R.string.hearing_impairment), false);
		
		return interactionIntent;
	}
	
	//Check if voice recognition is present
	public boolean checkVoiceRecognition() {
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			Log.d(InputActivity.class.getSimpleName(), "Voice recognizer not present");
			return false;
		} 
		return true;
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
	
	private void onLongClickView() {
		longPush = true;
		speakOut("If you prefer an audio based interaction, please say YES. If not, please say NO.");
		
		if (voiceRecognition){
			listenToSpeech();
		}
	}
	
	private void listenToSpeech() {
		Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
		listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a word!");
		listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);

		//start listening
		startActivityForResult(listenIntent, VR_REQUEST);
	}
	
	public void speak(View view) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		// Specify the calling package to identify your application
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
				.getPackage().getName());

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//check speech recognition result 
        if (requestCode == VR_REQUEST && resultCode == RESULT_OK) {
        	//store the returned word list as an ArrayList
            ArrayList<String> suggestedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (suggestedWords.contains("yes")){
            	//TODO: Communication by audio (blind user)
            	interactionIntent.putExtra(getResources().getString(R.string.hearing_impairment), true);
            } else if (suggestedWords.contains("no")){
            	//Communication by visual interaction, but probably with a visual difficulty
            	
            	interactionIntent.putExtra(getResources().getString(R.string.visual_impairment), true);
            	interactionIntent.putExtra(getResources().getString(R.string.hearing_impairment), false);
            }
            //TODO: If no answer, hearing_impairments = true
            startActivity(interactionIntent);
        }
	}

}
