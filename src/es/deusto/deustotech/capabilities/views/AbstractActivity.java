package es.deusto.deustotech.capabilities.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public abstract class AbstractActivity extends Activity implements View.OnClickListener, View.OnLongClickListener, TextToSpeech.OnInitListener {

	//variable for checking Voice Recognition support on user device
	public static final int VR_REQUEST = 999;
	public TextToSpeech tts;
	public UserMinimumPreferences userPrefs;
	public Vibrator vibrator;
	
	private SharedPreferences  storedPreferences;
	
	//ontology individuals
	private List<String> buttons, edits, textViews,
	audios, displays, noises, lights, backgrounds,
	devices, contexts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tts = new TextToSpeech(this, this);
		tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
			@Override
			public void onStart(String message) { }
			
			@Override
			public void onError(String message) { }
			
			@Override
			public void onDone(String message) {
				listenToSpeech();
			}
		});
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		    WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
//		initOntology();
	}
	
	protected void initOntology() {
		buttons 	= getOntologyManager().getIndividualOfClass(getOntologyNamespace() + "Button");
		edits 		= getOntologyManager().getIndividualOfClass(getOntologyNamespace() + "EditText");
		textViews 	= getOntologyManager().getIndividualOfClass(getOntologyNamespace() + "TextView");
		backgrounds	= getOntologyManager().getIndividualOfClass(getOntologyNamespace() + "Background");
		audios 		= getOntologyManager().getIndividualOfClass(getOntologyNamespace() + "Audio");
		displays 	= getOntologyManager().getIndividualOfClass(getOntologyNamespace() + "Display");
		devices 	= getOntologyManager().getIndividualOfClass(getOntologyNamespace() + "DeviceAux");
		contexts 	= getOntologyManager().getIndividualOfClass(getOntologyNamespace() + "ContextAux");
		lights 		= getOntologyManager().getIndividualOfClass("http://u2m.org/2003/02/UserModelOntology.rdf#Light");
		noises		= getOntologyManager().getIndividualOfClass("http://u2m.org/2003/02/UserModelOntology.rdf#Noise");
	}

	public void initializeServices(final String TAG){
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		if (userPrefs == null){
			userPrefs = new UserMinimumPreferences();
		}
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
            	//TODO: This method should be called once the tts finished reading the basic_input_message
//            	listenToSpeech();
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
		
	}
	
	protected void removeAllValuesFromOntology() {
		//Get all the individuals from the ontology and remove previous values
		//The only remaining values are the ones in the Adaptation individual
		final List <String> views = new ArrayList<String>();
		views.add(getButtons().get(0));
		views.add(getEditTexts().get(0));
		views.add(getTextViews().get(0));
		views.add(getBackgrounds().get(0));
		
		for (int i = 0; i < views.size(); i++){
			getOntologyManager().removeIndividualMembership(views.get(i), getOntologyNamespace() + "viewHasColor");
			getOntologyManager().removeIndividualMembership(views.get(i), getOntologyNamespace() + "viewHasTextColor");
			getOntologyManager().removeIndividualMembership(views.get(i), getOntologyNamespace() + "viewHasTextSize");
			getOntologyManager().removeIndividualMembership(views.get(i), getOntologyNamespace() + "viewHasWidth");
			getOntologyManager().removeIndividualMembership(views.get(i), getOntologyNamespace() + "viewHasHeight");
		}
		
		getOntologyManager().removeIndividualMembership(getAudios().get(0), getOntologyNamespace() + "audioHasVolume");
		getOntologyManager().removeIndividualMembership(getAudios().get(0), getOntologyNamespace() + "audioHasApplicable");

		getOntologyManager().removeIndividualMembership(getDisplays().get(0), getOntologyNamespace() + "displayHasBrightness");
		getOntologyManager().removeIndividualMembership(getDisplays().get(0), getOntologyNamespace() + "displayHasApplicable");
		
		getOntologyManager().removeIndividualMembership(getDevices().get(0), getOntologyNamespace() + "deviceAuxBatteryIsSufficient");
		getOntologyManager().removeIndividualMembership(getDevices().get(0), getOntologyNamespace() + "deviceAuxHasBrightness");
		
		getOntologyManager().removeIndividualMembership(getContexts().get(0), getOntologyNamespace() + "contextAuxHasLightLevel");
		getOntologyManager().removeIndividualMembership(getContexts().get(0), getOntologyNamespace() + "contextAuxHasNoiseLevel");
		
		getOntologyManager().removeIndividualMembership(getLights().get(0), getOntologyNamespace() + "contextHasLight");
		getOntologyManager().removeIndividualMembership(getLights().get(0), getOntologyNamespace() + "contextHasNoise");
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
	
	@Override
	public void onClick(View view) { }
	
	@Override
	public boolean onLongClick(View view) {
		return false;
	}
	
	public void speakOut(final String text) {
		if (tts == null){
			tts = new TextToSpeech(this, this);
			tts.setLanguage(new Locale("spa", "ES"));
		}
      tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
  }
	
	public void redrawViews(){ }

	public String getOntologyPath() {
		return getResources().getString(R.string.ontology_path);
	}
	
	@Override
	public void onBackPressed() {
		if (vibrator != null){
			vibrator.cancel();
		}
		
		if (tts != null){
			tts.stop();
			tts.shutdown();
		}
		
		super.onBackPressed();
	}

	public List<String> getButtons() {
		return buttons;
	}

	public List<String> getEditTexts() {
		return edits;
	}

	public List<String> getTextViews() {
		return textViews;
	}

	public List<String> getAudios() {
		return audios;
	}

	public List<String> getDisplays() {
		return displays;
	}

	public List<String> getNoises() {
		return noises;
	}

	public List<String> getLights() {
		return lights;
	}

	public List<String> getBackgrounds() {
		return backgrounds;
	}

	public List<String> getDevices() {
		return devices;
	}

	public List<String> getContexts() {
		return contexts;
	}
	
	public SharedPreferences getStoredPreferences() {
		return storedPreferences;
	}
	
}
