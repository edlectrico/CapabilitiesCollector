package es.deusto.deustotech.capabilities.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.semanticweb.owlapi.model.OWLLiteral;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

import com.google.gson.Gson;

import es.deusto.deustotech.R;
import es.deusto.deustotech.capabilities.UserMinimumPreferences;
import es.deusto.deustotech.pellet4android.exceptions.OntologySavingException;

/**
 * This activity allows the user to configure the minimum volume
 * level for interacting with the device just by tapping the
 * screen 
 * 
 * @author edlectrico
 *
 */

public class VolumeConfigActivity extends AbstractActivity implements TextToSpeech.OnInitListener {

	private static final String TAG = VolumeConfigActivity.class.getSimpleName();

	private ProgressDialog dialog;
	
	private GridLayout grid;
	private NumberPicker volumePicker;
	private AudioManager audioManager = null;
	private int volumeLevel = 10;
	private int callerActivity = -1;
	
	private static final int DEFAULT_BUTTON_COLOR = -16777216;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.volume_config_activity);

		Bundle bundle = getIntent().getExtras();

		callerActivity = bundle.getInt(getResources().getString(R.string.activity_caller));

		grid = (GridLayout) findViewById(R.id.volume_layout);
		grid.setOnClickListener(this);
		
		if (callerActivity == 1){ //BrightnessActivity
			audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			
			userPrefs = bundle.getParcelable(getResources().getString(R.string.view_params));
			volumePicker = (NumberPicker) findViewById(R.id.volume_picker);
			volumePicker.setMinValue(0);
			volumePicker.setMaxValue(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
			volumePicker.setBackgroundColor(userPrefs.getEditTextBackgroundColor());
		} else {
//			speakOut(getResources().getString(R.string.volume_longpush_es) + volumeLevel);
			grid.setOnLongClickListener(this);
		}

		initializeServices(TAG);
		
		redrawViews();
		addListeners();
	}
	
	@Override
	public boolean onLongClick(View view) {
		listenToSpeech();
		
		return super.onLongClick(view);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VR_REQUEST && resultCode == RESULT_OK) {
			//store the returned word list as an ArrayList
			ArrayList<String> suggestedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if (suggestedWords.contains("siguiente")){
				tts.stop();
				startActivity(new Intent(this, MailSenderActivity.class));
			}
		}
	}

	@Override
	public void initializeServices(String TAG) {
		if (callerActivity == 1){ //BrightnessActivity
			if (userPrefs.getDisplayHasApplicable() == 0){
				super.initializeServices(TAG);
			}
			if (userPrefs.getBrightness() != 0){
				WindowManager.LayoutParams layoutParams = getWindow()
						.getAttributes();
				layoutParams.screenBrightness = userPrefs.getBrightness();
				getWindow().setAttributes(layoutParams);
			}
		} else {
			audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		}

	}

	@Override
	public void addListeners() {
		findViewById(R.id.end_button).setOnClickListener(this);
		findViewById(R.id.end_button).setOnClickListener(this);

		grid.getChildAt(0).setOnClickListener(this);
		grid.getChildAt(1).setOnClickListener(this);

		if (callerActivity == 1){
			volumePicker.setOnValueChangedListener(new OnValueChangeListener() {
				@Override
				public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
					volumeLevel = newVal;
					
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
							volumeLevel, 0);
					
					speakOut(getResources().getString(R.string.volume_es) + volumeLevel);
				}
			});
		}
	}

	@Override
	public void redrawViews() {
		if (callerActivity == 1){ //BrightnessActivity
			if (ButtonConfigActivity.getLayoutBackgroundColorChanged()){
			grid.setBackgroundColor(userPrefs.getLayoutBackgroundColor());
		}

			((TextView)findViewById(R.id.volume_message)).setTextSize(userPrefs.getEditTextTextSize() / 2);
			((TextView)findViewById(R.id.volume_title)).setTextSize(userPrefs.getEditTextTextSize() / 2);
			
			if (userPrefs.getEditTextTextColor() != 0){
				((TextView)findViewById(R.id.volume_message)).setTextColor(userPrefs.getEditTextTextColor());
				((TextView)findViewById(R.id.volume_title)).setTextColor(userPrefs.getEditTextTextColor());
			}

			if (userPrefs.getEditTextBackgroundColor() != 0){
				((TextView)findViewById(R.id.volume_message)).setBackgroundColor(userPrefs.getEditTextBackgroundColor());
				((TextView)findViewById(R.id.volume_title)).setBackgroundColor(userPrefs.getEditTextBackgroundColor());
			}

			findViewById(R.id.end_button).setMinimumWidth((int)userPrefs.getButtonWidth());
			findViewById(R.id.end_button).setMinimumHeight((int) userPrefs.getButtonHeight());

			((Button)findViewById(R.id.end_button)).setTextSize(userPrefs.getButtonTextSize() / 2);
			((Button)findViewById(R.id.end_button)).setTextColor(userPrefs.getButtonTextColor());
			((TextView)findViewById(R.id.volume_message)).setTextSize(userPrefs.getEditTextTextSize() / 2);
			((TextView)findViewById(R.id.volume_title)).setTextSize(userPrefs.getEditTextTextSize() / 2);
			
			
			if (userPrefs.getButtonBackgroundColor() != DEFAULT_BUTTON_COLOR){
				((Button)findViewById(R.id.end_button)).setBackgroundColor(userPrefs.getButtonBackgroundColor());
			}

			if (userPrefs.getEditTextTextColor() != 0){
				((TextView)findViewById(R.id.volume_message)).setTextColor(userPrefs.getEditTextTextColor());
				((TextView)findViewById(R.id.volume_title)).setTextColor(userPrefs.getEditTextTextColor());
			}
		} else {
			Random randomGenerator = new Random();
			volumeLevel = randomGenerator.nextInt(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					volumeLevel, 0);

			speakOut(getResources().getString(R.string.volume_es) + volumeLevel);
		}
	}
	
	private void showDialog() {
		dialog = ProgressDialog.show(VolumeConfigActivity.this, "", "Storing in the ontology. Please wait.", true);        
		dialog.show();
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.end_button){
			showDialog();
			new ProcessOntology().execute("adaptui"); // The name is not important
		}
	}
	
	class ProcessOntology extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... urls) {
			//Store
			if (userPrefs == null){
				userPrefs = new UserMinimumPreferences();
			}

			userPrefs.setVolume(volumeLevel);
			
			initOntology();
			storeUserPreferencesIntoOntology();
			storeUserPreferencesInMobile();
			
			//Saving the ontology
			try {
				getOntologyManager().saveOntologyAs(Environment.getExternalStorageDirectory() + "/ontologies/" + getOntologyFilename());
			} catch (OntologySavingException e) {
				e.printStackTrace();
			}

//		} 
//		else {
//			Random randomGenerator = new Random();
//			volumeLevel = randomGenerator.nextInt(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
//
//			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
//					volumeLevel, 0);
//
//			speakOut(getResources().getString(R.string.volume_es) + volumeLevel);
//		}
			
			return null;
		}

		protected void onPostExecute(String string) {
			dialog.dismiss();
			if (callerActivity == 1){ //BrightnessActivity
				Intent intent = new Intent(VolumeConfigActivity.this, MailSenderActivity.class);
				intent.putExtra(getResources().getString(R.string.view_params), userPrefs);

				if (CapabilitiesActivity.getDisplayIsApplicable() == 0){
					speakOut("Prepárese para enviar email");
				}
				intent.putExtra(getResources().getString(R.string.activity_caller), 2);
				startActivity(intent);
			} else { //MainActivity
				Intent intent = new Intent(VolumeConfigActivity.this, ButtonConfigActivity.class);
				intent.putExtra(getResources().getString(R.string.activity_caller), 2); //0: MainActivity; 1: BrightnessActivity; 2: VolumeActivity
				startActivity(intent);
			}
			
			CapabilitiesActivity.setDEMOS_AVAILABLE(true);
		}
	}
	
	private void storeUserPreferencesInMobile() {
		SharedPreferences  preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor prefsEditor = preferences.edit();

		//Store last UI configuration in the mobile
		Gson gson = new Gson();
		String json = gson.toJson(userPrefs);
		prefsEditor.putString(getResources().getString(R.string.view_params), json);
		prefsEditor.commit();
		prefsEditor.apply();
		
		System.out.println("Stored in UserPrefs");
		
		getOntologyManager().addDataTypePropertyValue(getContextJSON().get(0), getOntologyNamespace() + "contextJSONHasValue", json);
		
		System.out.println("contextJSONHasValue: " + getOntologyManager().getDataTypePropertyValue(getContextJSON().get(0), getOntologyNamespace() + "contextJSONHasValue"));
	}

	private void storeUserPreferencesIntoOntology() {
		getOntologyManager().addDataTypePropertyValue(getAudios().get(0), 		getOntologyNamespace() + "audioHasVolume", 			volumeLevel);
		getOntologyManager().addDataTypePropertyValue(getDisplays().get(0), 	getOntologyNamespace() + "displayHasBrightness", 	userPrefs.getBrightness());
		
		//TODO: remove
//		System.out.println("Checking Brightness");
//		final Collection<OWLLiteral> lightConditions = getOntologyManager().getDataTypePropertyValue(getDisplays().get(0), getOntologyNamespace() + "displayHasBrightness");
//		for (OWLLiteral brightness : lightConditions){
//			System.out.println("VolumeActivity: " + String.valueOf(((OWLLiteral) brightness).getLiteral()));
//		}
		
		getOntologyManager().addDataTypePropertyValue(getLights().get(0), 		getOntologyNamespace() + "contextHasLight", 		userPrefs.getLuxes());
		getOntologyManager().addDataTypePropertyValue(getEditTexts().get(0), 	getOntologyNamespace() + "viewHasColor", 			userPrefs.getEditTextBackgroundColor());
		getOntologyManager().addDataTypePropertyValue(getEditTexts().get(0), 	getOntologyNamespace() + "viewHasWidth", 			userPrefs.getEditTextWidth());
		getOntologyManager().addDataTypePropertyValue(getEditTexts().get(0), 	getOntologyNamespace() + "viewHasHeight", 			userPrefs.getEditTextHeight());
		getOntologyManager().addDataTypePropertyValue(getEditTexts().get(0), 	getOntologyNamespace() + "viewHasTextSize", 		userPrefs.getEditTextTextSize());
		getOntologyManager().addDataTypePropertyValue(getEditTexts().get(0), 	getOntologyNamespace() + "viewHasTextColor", 		userPrefs.getEditTextTextColor());
		getOntologyManager().addDataTypePropertyValue(getTextViews().get(0), 	getOntologyNamespace() + "viewHasColor", 			userPrefs.getTextViewBackgroundColor());
		getOntologyManager().addDataTypePropertyValue(getTextViews().get(0), 	getOntologyNamespace() + "viewHasWidth", 			userPrefs.getTextViewWidth());
		getOntologyManager().addDataTypePropertyValue(getTextViews().get(0), 	getOntologyNamespace() + "viewHasHeight", 			userPrefs.getTextViewHeight());
		getOntologyManager().addDataTypePropertyValue(getTextViews().get(0), 	getOntologyNamespace() + "viewHasTextSize", 		userPrefs.getTextViewTextSize());
		getOntologyManager().addDataTypePropertyValue(getTextViews().get(0), 	getOntologyNamespace() + "viewHasTextColor", 		userPrefs.getTextViewTextColor());
		
		System.out.println("ButtonColorVolumeConfig: " + userPrefs.getButtonBackgroundColor());
		getOntologyManager().addDataTypePropertyValue(getButtons().get(0), 		getOntologyNamespace() + "viewHasColor", 			userPrefs.getButtonBackgroundColor());
		
		getOntologyManager().addDataTypePropertyValue(getButtons().get(0), 		getOntologyNamespace() + "viewHasWidth", 			(int)userPrefs.getButtonWidth());
		getOntologyManager().addDataTypePropertyValue(getButtons().get(0), 		getOntologyNamespace() + "viewHasHeight", 			(int)userPrefs.getButtonHeight());
		getOntologyManager().addDataTypePropertyValue(getButtons().get(0), 		getOntologyNamespace() + "viewHasTextSize", 		userPrefs.getButtonTextSize());
		getOntologyManager().addDataTypePropertyValue(getButtons().get(0), 		getOntologyNamespace() + "viewHasTextColor", 		userPrefs.getButtonTextColor());
		getOntologyManager().addDataTypePropertyValue(getBackgrounds().get(0), 	getOntologyNamespace() + "viewHasColor", 			userPrefs.getLayoutBackgroundColor());
	
		userPrefs.setDisplayHasApplicable(CapabilitiesActivity.getDisplayIsApplicable());
		
		getOntologyManager().addDataTypePropertyValue(getDisplays().get(0), 	getOntologyNamespace() + "displayHasApplicable", 	(userPrefs.getDisplayHasApplicable() == 1) ? true : false);
		getOntologyManager().addDataTypePropertyValue(getAudios().get(0), 		getOntologyNamespace() + "audioHasBrightness", 		(userPrefs.getAudioHasApplicable() == 1) ? true : false);
	
		final Collection<OWLLiteral> contextAuxHasLightLevel = getOntologyManager().getDataTypePropertyValue(getContexts().get(0), getOntologyNamespace() + "contextAuxHasLightLevel");
		final Collection<OWLLiteral> storedJSON = getOntologyManager().getDataTypePropertyValue(getContextJSON().get(0), getOntologyNamespace() + "contextJSONHasValue");
		
		boolean stored = false;
		for (OWLLiteral lightLevel : contextAuxHasLightLevel){
			if (!storedJSON.toString().contains(lightLevel.getLiteral())){
				userPrefs.setContextAuxLight(lightLevel.getLiteral());
				stored = true;
				break;
			} 
		}
		
		if (!stored){
			userPrefs.setContextAuxLight(String.valueOf(((OWLLiteral) contextAuxHasLightLevel.toArray()[0]).getLiteral()));
		}
		
		System.out.println(String.valueOf(((OWLLiteral) contextAuxHasLightLevel.toArray()[0]).getLiteral()));
	}
}
