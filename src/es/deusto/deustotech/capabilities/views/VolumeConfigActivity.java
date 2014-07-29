package es.deusto.deustotech.capabilities.views;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
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
			speakOut(getResources().getString(R.string.volume_longpush_es) + volumeLevel);
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
				
				System.out.println("Brightness: " + userPrefs.getBrightness());
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
			grid.setBackgroundColor(userPrefs.getLayoutBackgroundColor());

			((TextView)findViewById(R.id.volume_message)).setTextSize(userPrefs.getEditTextTextSize() / 2);

			if (userPrefs.getEditTextTextColor() != 0){
				((TextView)findViewById(R.id.volume_message)).setTextColor(userPrefs.getEditTextTextColor());
			}

			if (userPrefs.getEditTextBackgroundColor() != 0){
				((TextView)findViewById(R.id.volume_message)).setBackgroundColor(userPrefs.getEditTextBackgroundColor());
			}

			findViewById(R.id.end_button).setMinimumWidth((int)userPrefs.getButtonWidth());
			findViewById(R.id.end_button).setMinimumHeight((int) userPrefs.getButtonHeight());

			((Button)findViewById(R.id.end_button)).setTextColor(userPrefs.getButtonTextColor());
			((TextView)findViewById(R.id.volume_message)).setTextSize(userPrefs.getEditTextTextSize() / 2);

			if (userPrefs.getButtonBackgroundColor() != DEFAULT_BUTTON_COLOR){
				((Button)findViewById(R.id.end_button)).setBackgroundColor(userPrefs.getButtonBackgroundColor());
			}

			if (userPrefs.getEditTextTextColor() != 0){
				((TextView)findViewById(R.id.volume_message)).setTextColor(userPrefs.getEditTextTextColor());
			}
		} else {
			Random randomGenerator = new Random();
			volumeLevel = randomGenerator.nextInt(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					volumeLevel, 0);

			speakOut(getResources().getString(R.string.volume_es) + volumeLevel);
		}
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.end_button){
			//Store
			if (userPrefs == null){
				userPrefs = new UserMinimumPreferences();
			}

			userPrefs.setVolume(volumeLevel);
			
			super.initOntology();
			super.removeAllValuesFromOntology();
			storeUserPreferencesIntoOntology();
			
			//Saving the ontology
			try {
				super.getOntologyManager().saveOntologyAs(Environment.getExternalStorageDirectory() + "/ontologies/" + super.getOntologyFilename());
			} catch (OntologySavingException e) {
				e.printStackTrace();
			}

			SharedPreferences  preferences = getPreferences(MODE_PRIVATE);
			Editor prefsEditor = preferences.edit();

			Gson gson = new Gson();
			String json = gson.toJson(userPrefs);
			prefsEditor.putString(getResources().getString(R.string.view_params), json);
			prefsEditor.commit();

			if (callerActivity == 1){ //BrightnessActivity
				Intent intent = new Intent(this, MailSenderActivity.class);
				intent.putExtra(getResources().getString(R.string.view_params), userPrefs);

				if (userPrefs.getDisplayHasApplicable() == 0){
					speakOut("Now try to send an email!");
				}
				intent.putExtra(getResources().getString(R.string.activity_caller), 2);
				startActivity(intent);
			} else { //MainActivity
				Intent intent = new Intent(this, ButtonConfigActivity.class);
				intent.putExtra(getResources().getString(R.string.activity_caller), 2); //0: MainActivity; 1: BrightnessActivity; 2: VolumeActivity
				startActivity(intent);
			}
		} else {
			Random randomGenerator = new Random();
			volumeLevel = randomGenerator.nextInt(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

			System.out.println("Max volume: " + audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					volumeLevel, 0);

			speakOut(getResources().getString(R.string.volume_es) + volumeLevel);
		}
	}
	
	private void storeUserPreferencesIntoOntology() {
		System.out.println("Storing in the ontology...");
		
		getOntologyManager().addDataTypePropertyValue(getAudios().get(0), 		getOntologyNamespace() + "audioHasVolume", 			volumeLevel);
		getOntologyManager().addDataTypePropertyValue(getDisplays().get(0), 	getOntologyNamespace() + "displayHasBrightness", 	userPrefs.getBrightness());
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
		getOntologyManager().addDataTypePropertyValue(getButtons().get(0), 		getOntologyNamespace() + "viewHasColor", 			userPrefs.getButtonBackgroundColor());
		getOntologyManager().addDataTypePropertyValue(getButtons().get(0), 		getOntologyNamespace() + "viewHasWidth", 			(int)userPrefs.getButtonWidth());
		getOntologyManager().addDataTypePropertyValue(getButtons().get(0), 		getOntologyNamespace() + "viewHasHeight", 			(int)userPrefs.getButtonHeight());
		getOntologyManager().addDataTypePropertyValue(getButtons().get(0), 		getOntologyNamespace() + "viewHasTextSize", 		userPrefs.getEditTextTextSize());
		getOntologyManager().addDataTypePropertyValue(getButtons().get(0), 		getOntologyNamespace() + "viewHasTextColor", 		userPrefs.getButtonTextColor());
		getOntologyManager().addDataTypePropertyValue(getBackgrounds().get(0), 	getOntologyNamespace() + "viewHasColor", 			userPrefs.getLayoutBackgroundColor());
	
		getOntologyManager().addDataTypePropertyValue(getDisplays().get(0), 	getOntologyNamespace() + "displayHasApplicable", 	(userPrefs.getDisplayHasApplicable() == 1) ? true : false);
		getOntologyManager().addDataTypePropertyValue(getAudios().get(0), 	getOntologyNamespace() + "audioHasBrightness", 			(userPrefs.getAudioHasApplicable() == 1) ? true : false);
		
		System.out.println("userPrefs.getLayoutBackgroundColor(): " + userPrefs.getLayoutBackgroundColor());
		System.out.println("backgroundColor: " + getOntologyManager().getDataTypePropertyValue(getBackgrounds().get(0), getOntologyNamespace() + "viewHasColor"));
	}

	@Override
	public void onBackPressed() {
		if (callerActivity == 1){
			startActivity(new Intent(this, BrightnessConfigActivity.class));
		} else {
			startActivity(new Intent(this, CapabilitiesActivity.class));
		}
	}
}
