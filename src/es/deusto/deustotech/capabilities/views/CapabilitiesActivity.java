package es.deusto.deustotech.capabilities.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;

import com.google.gson.Gson;

import es.deusto.deustotech.R;
import es.deusto.deustotech.capabilities.UserMinimumPreferences;
import es.deustotech.piramide.activities.location.Categories;

/**
 * This activity checks user basic input capabilities
 * 
 * @author edlectrico
 * 
 */
public class CapabilitiesActivity extends AbstractActivity {

	public static final String TAG = CapabilitiesActivity.class.getSimpleName();
	/** 
	 * This variable checks if the corresponding individuals
	 * have been loaded before, so there is no need of doing the same operation again
	 */
	private boolean loaded 				= false;
	private boolean longPush 			= false;
	private boolean voiceRecognition 	= false;
	private Intent interactionIntent;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.capabilities_activity);
		
		initializeServices(TAG);
		addListeners();
		
		interactionIntent = new Intent(this, ButtonConfigActivity.class);
		interactionIntent.putExtra(getResources().getString(R.string.visual_impairment), 0);
		interactionIntent.putExtra(getResources().getString(R.string.hearing_impairment), 0);
		
		super.initOntology();
//		getOntologyManager().removeIndividualMembership(getContexts().get(0), getOntologyNamespace() + "contextAuxHasLightLevel");
//		System.out.println(getOntologyManager().getDataTypePropertyValue(getContexts().get(0), getOntologyNamespace() + "contextAuxHasLightLevel"));
		
		setVoiceRecognition(checkVoiceRecognition());
		
		listenToSpeech();
	}
	
	@Override
	public void addListeners() {
		//OnLongClick -> audio-based interaction
		GridLayout grid = (GridLayout) findViewById(R.id.grid_layout);
		grid.setOnLongClickListener(this);

		findViewById(R.id.button_input).setOnClickListener(this);
		findViewById(R.id.mail_activity_button).setOnClickListener(this);
		findViewById(R.id.navigate_button).setOnClickListener(this);
		
		findViewById(R.id.button_input).setOnLongClickListener(this);
		findViewById(R.id.mail_activity_button).setOnLongClickListener(this);
		findViewById(R.id.navigate_button).setOnLongClickListener(this);
	}

	//Check if voice recognition is present
	public boolean checkVoiceRecognition() {
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			Log.d(CapabilitiesActivity.class.getSimpleName(), getResources().getString(R.string.recognizer_error));
			return false;
		} 
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//check speech recognition result 
		if (requestCode == VR_REQUEST && resultCode == RESULT_OK) {
			//store the returned word list as an ArrayList
			ArrayList<String> suggestedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if (suggestedWords.contains(getResources().getString(R.string.si))){
				tts.stop();
				//TODO: Communication by audio (blind user)
				interactionIntent.putExtra(getResources().getString(R.string.visual_impairment), 1);
				getOntologyManager().addDataTypePropertyValue(getDisplays().get(0), "displayHasApplicabe", false);
				interactionIntent.setClass(this, VolumeConfigActivity.class);
				interactionIntent.putExtra(getResources().getString(R.string.activity_caller), 0); //0 - MainActivity; 1 - BrightnessAtivity
				speakOut(getResources().getString(R.string.volume_info_message_es));
				speakOut(getResources().getString(R.string.volume_longpush_es));
			} else if (suggestedWords.contains(getResources().getString(R.string.no))){
				//TODO: Communication by visual interaction, but probably with a visual difficulty
				tts.stop();
				getOntologyManager().addDataTypePropertyValue(getDisplays().get(0), "displayHasApplicabe", true);
				
				interactionIntent.putExtra(getResources().getString(R.string.visual_impairment), 0);
				interactionIntent.putExtra(getResources().getString(R.string.hearing_impairment), 0);
			}
			//TODO: If no answer, hearing_impairments = true
			tts.stop();
			startActivity(interactionIntent);
		}
	}

	@Override
	public boolean onLongClick(View view) {
		
		listenToSpeech();
		
		getOntologyManager().addDataTypePropertyValue(getAudios().get(0), 	getOntologyNamespace() + "audioHasApplicabe", true);
		longPush = false;

		Intent intent = new Intent(this, VolumeConfigActivity.class);
		intent.putExtra(getResources().getString(R.string.activity_caller), 0); //0 - MainActivity; 1 - BrightnessAtivity
		
		speakOut(getResources().getString(R.string.volume_info_message_es));
		speakOut(getResources().getString(R.string.volume_longpush_es));
		
		startActivity(intent);
		
		return super.onLongClick(view);
	}

	@Override
	public void onClick(View view) {
		if (!loaded){
			loaded 	 = true;
		}
		
		if (view.getId() == R.id.button_input){
			if (!longPush){
				vibrator.vibrate(500);
				speakOut(getResources().getString(R.string.message_visual_interaction_es));
				userPrefs.setDisplayHasApplicable(1);
				userPrefs.setAudioHasApplicable(1);
				interactionIntent.setClass(this,  ButtonConfigActivity.class);
				interactionIntent.putExtra(getResources().getString(R.string.activity_caller), 1);
				tts.stop();
				startActivity(interactionIntent);
			} else if (longPush){
				userPrefs.setDisplayHasApplicable(0);
				userPrefs.setAudioHasApplicable(1);
				//If longPush means that the user cannot see the screen properly
				longPush = false;
				interactionIntent.setClass(this,  VolumeConfigActivity.class);
				interactionIntent.putExtra(getResources().getString(R.string.activity_caller), 0); //0 - MainActivity; 1 - BrightnessAtivity
				tts.stop();
				startActivity(interactionIntent);
			}
		} else if (view.getId() == R.id.mail_activity_button){
			tts.stop();
			launchMailSenderActivity();
		} else if (view.getId() == R.id.navigate_button){
			tts.stop();
			startActivity(new Intent(this, Categories.class));
		}
	}

	public boolean isVoiceRecognition() {
		return voiceRecognition;
	}

	public void setVoiceRecognition(boolean voiceRecognition) {
		this.voiceRecognition = voiceRecognition;
	}

	public void launchMailSenderActivity() {
		Gson gson = new Gson();
		SharedPreferences  preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		userPrefs = gson.fromJson(preferences.getString(getResources().getString(R.string.view_params), ""), UserMinimumPreferences.class);
		
		interactionIntent.setClass(this,  MailSenderActivity.class);
		interactionIntent.putExtra(getResources().getString(R.string.activity_caller), 1);
		interactionIntent.putExtra(getResources().getString(R.string.view_params), userPrefs);

		startActivity(interactionIntent);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
