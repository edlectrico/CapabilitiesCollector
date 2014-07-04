package es.deusto.deustotech.capabilities.views;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import es.deusto.deustotech.R;
import es.deusto.deustotech.capabilities.UserMinimumPreferences;
import es.deusto.deustotech.pellet4android.MainActivity;
import es.deusto.deustotech.pellet4android.OntologyManager;

@SuppressLint("NewApi")
public abstract class AbstractActivity extends Activity implements View.OnClickListener, View.OnLongClickListener, TextToSpeech.OnInitListener {

	//variable for checking Voice Recognition support on user device
	public static final int VR_REQUEST = 999;
	public TextToSpeech tts;
	public UserMinimumPreferences userPrefs;
	public Vibrator vibrator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		    WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	public void initializeServices(final String TAG){
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		tts 	 = new TextToSpeech(this, this);
		userPrefs = new UserMinimumPreferences();
		//TODO: why is not working the onDone call? This part of code was after
		//the speakOut() call within the onInit method
		tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
			@Override
			public void onStart(String message) {
				Log.d(TAG, "onStart " + message);
			}

			@Override
			public void onError(String message) { }

			@Override
			public void onDone(String message) {
				Log.d(TAG, "onDone " + message);
				listenToSpeech();
			}
		});
	}
	
	public static OntologyManager getOntologyManager() {
		return MainActivity.getOntologyManager();
	}
	
	public String getOntologyNamespace() {
		return getResources().getString(R.string.ontology_namespace);
	}
	
	public String getOntologyFilename() {
		return getResources().getString(R.string.ontology_filename);
	}
	
	public void addListeners(){ }
	
	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(new Locale("spa", "ES"));
 
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
            	speakOut(getResources().getString(R.string.basic_input_message_es));
            	//TODO: This method should be called once the tts finished reading the basic_input_message
            	listenToSpeech();
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
		
	}
	
	public void listenToSpeech() {
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
	public void onClick(View view) { }
	
	@Override
	public boolean onLongClick(View view) {
		return false;
	}
	
	public void speakOut(final String text) {
		if (tts == null){
			tts = new TextToSpeech(this, this);
		}
      tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
  }
	
	public void redrawViews(){ }

	public String getOntologyPath() {
		return getResources().getString(R.string.ontology_path);
	}
	
}
