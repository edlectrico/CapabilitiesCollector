package es.deusto.deustotech.capabilities.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.semanticweb.owlapi.model.OWLLiteral;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import es.deusto.deustotech.R;

/**
 * This activity checks user basic input capabilities
 * 
 * @author edlectrico
 * 
 */
public class CapabilitiesActivity extends AbstractActivity {

	private static final String TAG = CapabilitiesActivity.class.getSimpleName();

	private boolean longPush 			= false;
	private boolean voiceRecognition 	= false;
	private Intent interactionIntent;
	
	private static List<String> displays;
	private static List<String> audios;

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_activity);

		initializeServices(TAG);
		addListeners();

		interactionIntent = new Intent(this, ButtonConfigActivity.class);
		setVoiceRecognition(checkVoiceRecognition());
	}

	@SuppressLint("NewApi")
	@Override
	public void addListeners() {
		//OnLongClick -> audio-based interaction
		GridLayout grid = (GridLayout) findViewById(R.id.grid_layout);
		grid.setOnLongClickListener(this);

		findViewById(R.id.input_button).setOnLongClickListener(this);
		findViewById(R.id.input_button).setOnClickListener(this);
		findViewById(R.id.mail_activity_button).setOnClickListener(this);
	}

	private Intent getDefaultIntent() {
		interactionIntent.putExtra(getResources().getString(R.string.visual_impairment), 0);
		interactionIntent.putExtra(getResources().getString(R.string.hearing_impairment), 0);

		return interactionIntent;
	}

	//Check if voice recognition is present
	public boolean checkVoiceRecognition() {
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			Log.d(CapabilitiesActivity.class.getSimpleName(), "Voice recognizer not present");
			return false;
		} 
		return true;
	}

//	private void onLongClickView() {
//		longPush = true;
//
//		if (voiceRecognition){
//			listenToSpeech();
//		}
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//check speech recognition result 
		if (requestCode == VR_REQUEST && resultCode == RESULT_OK) {
			//store the returned word list as an ArrayList
			ArrayList<String> suggestedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if (suggestedWords.contains("yes")){
				//TODO: Communication by audio (blind user)
				interactionIntent.putExtra(getResources().getString(R.string.visual_impairment), 1);
			} else if (suggestedWords.contains("no")){
				//TODO: Communication by visual interaction, but probably with a visual difficulty
				super.getOntologyManager().addDataTypePropertyValue(displays.get(0), "displayHasApplicabe", true);
				
				interactionIntent.putExtra(getResources().getString(R.string.visual_impairment), 0);
				interactionIntent.putExtra(getResources().getString(R.string.hearing_impairment), 0);
			}
			//TODO: If no answer, hearing_impairments = true
			startActivity(interactionIntent);
		}
	}

	@Override
	public boolean onLongClick(View view) {
//		if (view.getId() == R.id.grid_layout) {
//			onLongClickView();
//		} else if (view.getId() == R.id.input_button){
//			onLongClickView();
//		}

		super.getOntologyManager().addDataTypePropertyValue(audios.get(0), 	super.getOntologyNamespace() + "audioHasApplicabe", true);
		longPush = false;

		Intent intent = new Intent(this, VolumeConfigActivity.class);
		intent.putExtra("caller", 0); //0 - MainActivity; 1 - BrightnessAtivity
		startActivity(intent);
		
		return super.onLongClick(view);
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.input_button){
			if (!longPush){
				vibrator.vibrate(500);
				speakOut("Visual based interaction selected");
				startActivity(getDefaultIntent());
			} else if (longPush){
//				MainActivity.getOntologyManager().addDataTypePropertyValue(audios.get(0), 	ADAPT_UI + "userAudioHasApplicabe", true);
				longPush = false;

//				Intent intent = new Intent(this, VolumeConfigActivity.class);
//				intent.putExtra("caller", 0); //0 - MainActivity; 1 - BrightnessAtivity
//				startActivity(intent);
			}
		} else if (view.getId() == R.id.mail_activity_button){
			onBackPressed();
		}
	}

	public boolean isVoiceRecognition() {
		return voiceRecognition;
	}

	public void setVoiceRecognition(boolean voiceRecognition) {
		this.voiceRecognition = voiceRecognition;
	}

	@Override
	public void onBackPressed() {
//		super.onBackPressed();
		//Avoiding the configuration, as it is stored in the ontology
		List<String> audios 	= super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Audio");
		List<String> displays 	= super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Display");
		List<String> buttons	= super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Button");
		List<String> editTexts	= super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "EditText");
		

		final Collection<OWLLiteral> volume = super.getOntologyManager().getDataTypePropertyValue(audios.get(0), super.getOntologyNamespace() + "audioHasVolume");
		final Collection<OWLLiteral> brightness = super.getOntologyManager().getDataTypePropertyValue(displays.get(0), super.getOntologyNamespace() + "displayHasBrightness");
		final Collection<OWLLiteral> buttonWidth = super.getOntologyManager().getDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasWidth");
		final Collection<OWLLiteral> buttonHeiht = super.getOntologyManager().getDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasHeight");
		final Collection<OWLLiteral> buttonColor = super.getOntologyManager().getDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasColor");
		final Collection<OWLLiteral> buttonTextColor = super.getOntologyManager().getDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasTextColor");
//		final Collection<OWLLiteral> buttonTextSize = super.getOntologyManager().getDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasTextSize");

//		final Collection<OWLLiteral> editHeight = super.getOntologyManager().getDataTypePropertyValue(editTexts.get(0), super.getOntologyNamespace() + "viewHasHeight");
		final Collection<OWLLiteral> editColor 	= super.getOntologyManager().getDataTypePropertyValue(editTexts.get(0), super.getOntologyNamespace() + "viewHasColor");
		final Collection<OWLLiteral> editTextColor 	= super.getOntologyManager().getDataTypePropertyValue(editTexts.get(0), super.getOntologyNamespace() + "viewHasTextColor");
		final Collection<OWLLiteral> editTextSize 	= super.getOntologyManager().getDataTypePropertyValue(editTexts.get(0), super.getOntologyNamespace() + "viewHasTextSize");
		
		
		final String vol = ((OWLLiteral) volume.toArray()[0]).getLiteral();
		final String bri = ((OWLLiteral) brightness.toArray()[0]).getLiteral();
		final String bwi = ((OWLLiteral) buttonWidth.toArray()[0]).getLiteral();
		final String bhe = ((OWLLiteral) buttonHeiht.toArray()[0]).getLiteral();
		final String bco = ((OWLLiteral) buttonColor.toArray()[0]).getLiteral();
		final String btc = ((OWLLiteral) buttonTextColor.toArray()[0]).getLiteral();
//		final String bts = ((OWLLiteral) buttonTextSize.toArray()[0]).getLiteral();
//		final String ehe = ((OWLLiteral) editHeight.toArray()[0]).getLiteral();
		final String eco = ((OWLLiteral) editColor.toArray()[0]).getLiteral();
		final String etc = ((OWLLiteral) editTextColor.toArray()[0]).getLiteral();
		final String ets = ((OWLLiteral) editTextSize.toArray()[0]).getLiteral();
		
		
//		Intent intent = new Intent(this,  MailSenderActivity.class);
		
		getDefaultIntent().setClass(this,  MailSenderActivity.class);
		getDefaultIntent().putExtra("caller", "CapabilitiesActivity");
		
		userPrefs.setVolume(Float.parseFloat(vol));
		userPrefs.setBrightness(Float.parseFloat(bri));
		userPrefs.setButtonWidth(Float.parseFloat(bwi));
		userPrefs.setButtonHeight(Float.parseFloat(bhe));
		userPrefs.setButtonBackgroundColor(Integer.parseInt(bco));
		userPrefs.setButtonTextColor(Integer.parseInt(btc));
		userPrefs.setTextEditSize(Float.parseFloat(ets));
		userPrefs.setTextEditBackgroundColor(Integer.parseInt(eco));
		userPrefs.setTextEditTextColor(Integer.parseInt(etc));
		
//		intent.putExtra("buttonTextSize", bts);
//		intent.putExtra("editHeight", ehe);
		
		getDefaultIntent().putExtra("viewParams", userPrefs);
		
		startActivity(getDefaultIntent());
	}
	
	
	

}
