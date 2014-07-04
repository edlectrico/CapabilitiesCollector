package es.deusto.deustotech.capabilities.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.semanticweb.owlapi.model.OWLLiteral;

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
	/** 
	 * This variable checks if the corresponding individuals
	 * have been loaded before, so there is no need of doing the same operation again
	 */
	private boolean loaded				= false;
	private static List<String> audios, displays;
	private Intent interactionIntent;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.capabilities_activity);

		initializeServices(TAG);
		addListeners();

		interactionIntent = new Intent(this, ButtonConfigActivity.class);
		interactionIntent.putExtra(getResources().getString(R.string.visual_impairment), 0);
		interactionIntent.putExtra(getResources().getString(R.string.hearing_impairment), 0);
//		
//		setVoiceRecognition(checkVoiceRecognition());
	}

	@Override
	public void addListeners() {
		//OnLongClick -> audio-based interaction
		GridLayout grid = (GridLayout) findViewById(R.id.grid_layout);
		grid.setOnLongClickListener(this);

		findViewById(R.id.button_input).setOnLongClickListener(this);
		findViewById(R.id.button_input).setOnClickListener(this);
		findViewById(R.id.mail_activity_button).setOnClickListener(this);
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

	private void onLongClickView() {
		longPush = true;

		if (voiceRecognition){
			listenToSpeech();
		}
	}

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
		audios = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Audio");
		if (view.getId() == R.id.grid_layout) {
			onLongClickView();
		} else if (view.getId() == R.id.button_input){
			onLongClickView();
		}

		super.getOntologyManager().addDataTypePropertyValue(audios.get(0), 	super.getOntologyNamespace() + "audioHasApplicabe", true);
		longPush = false;

		Intent intent = new Intent(this, VolumeConfigActivity.class);
		intent.putExtra("caller", 0); //0 - MainActivity; 1 - BrightnessAtivity
		startActivity(intent);
		
		return super.onLongClick(view);
	}

	@Override
	public void onClick(View view) {
		if (!loaded){
			audios 	 = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Audio");
			displays = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Display");			
			loaded 	 = true;
		}
		
		if (view.getId() == R.id.button_input){
			deletePreviousValues();
			if (!longPush){
				vibrator.vibrate(500);
//				speakOut("Visual based interaction selected");
				super.getOntologyManager().addDataTypePropertyValue(displays.get(0), super.getOntologyNamespace() + "userDisplayHasApplicable", true);
				super.getOntologyManager().addDataTypePropertyValue(displays.get(0), super.getOntologyNamespace() + "userDisplayApplicableIsStatic", false);
				super.getOntologyManager().addDataTypePropertyValue(audios.get(0), 	 super.getOntologyNamespace() + "userAudioHasApplicabe", true);
				interactionIntent.setClass(this,  ButtonConfigActivity.class);
				interactionIntent.putExtra("caller", 1);
				startActivity(interactionIntent);
			} else if (longPush){
				//If longPush means that the user cannot see the screen properly
				super.getOntologyManager().addDataTypePropertyValue(displays.get(0), super.getOntologyNamespace() + "userDisplayHasApplicable", false);
				super.getOntologyManager().addDataTypePropertyValue(displays.get(0), super.getOntologyNamespace() + "userDisplayApplicableIsStatic", true);
				super.getOntologyManager().addDataTypePropertyValue(audios.get(0), 	 super.getOntologyNamespace() + "userAudioHasApplicabe", true);
				longPush = false;
				interactionIntent.setClass(this,  VolumeConfigActivity.class);
				interactionIntent.putExtra("caller", 0); //0 - MainActivity; 1 - BrightnessAtivity
				startActivity(interactionIntent);
			}
		} else if (view.getId() == R.id.mail_activity_button){
			onBackPressed();
		}
	}

	/**
	 * To maintain the consistency of the ontology we are storing just one value per property.
	 * To do so, it is necessary to delete the corresponding property value before inserting
	 * a new one.
	 */
	private void deletePreviousValues() {
		super.getOntologyManager().deleteAllValuesOfProperty(displays.get(0), super.getOntologyNamespace() + "userDisplayHasApplicable");
		super.getOntologyManager().deleteAllValuesOfProperty(displays.get(0), super.getOntologyNamespace() + "userDisplayApplicableIsStatic");
		super.getOntologyManager().deleteAllValuesOfProperty(audios.get(0), super.getOntologyNamespace() + "userAudioHasApplicabe");
	}

	public boolean isVoiceRecognition() {
		return voiceRecognition;
	}

	public void setVoiceRecognition(boolean voiceRecognition) {
		this.voiceRecognition = voiceRecognition;
	}

	@Override
	public void onBackPressed() {
		//Avoiding the configuration, as it is stored in the ontology
		final List<String> buttons	 = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Button");
		final List<String> editTexts = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "EditText");
		final List<String> textViews = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "TextView");
		final List<String> backgrounds = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Background");

		final Collection<OWLLiteral> volume = super.getOntologyManager().getDataTypePropertyValue(audios.get(0), super.getOntologyNamespace() + "audioHasVolume");
		final Collection<OWLLiteral> brightness = super.getOntologyManager().getDataTypePropertyValue(displays.get(0), super.getOntologyNamespace() + "displayHasBrightness");
		final Collection<OWLLiteral> buttonWidth = super.getOntologyManager().getDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasWidth");
		final Collection<OWLLiteral> buttonHeiht = super.getOntologyManager().getDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasHeight");
		final Collection<OWLLiteral> buttonColor = super.getOntologyManager().getDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasColor");
		final Collection<OWLLiteral> buttonTextColor = super.getOntologyManager().getDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasTextColor");

		final Collection<OWLLiteral> editColor 	= super.getOntologyManager().getDataTypePropertyValue(editTexts.get(0), super.getOntologyNamespace() + "viewHasColor");
		final Collection<OWLLiteral> editTextColor 	= super.getOntologyManager().getDataTypePropertyValue(editTexts.get(0), super.getOntologyNamespace() + "viewHasTextColor");
		final Collection<OWLLiteral> editTextSize 	= super.getOntologyManager().getDataTypePropertyValue(editTexts.get(0), super.getOntologyNamespace() + "viewHasTextSize");

		final Collection<OWLLiteral> textViewColor		= super.getOntologyManager().getDataTypePropertyValue(textViews.get(0), super.getOntologyNamespace() + "viewHasColor");
		final Collection<OWLLiteral> textViewTextColor 	= super.getOntologyManager().getDataTypePropertyValue(textViews.get(0), super.getOntologyNamespace() + "viewHasTextColor");
		final Collection<OWLLiteral> textViewTextSize 	= super.getOntologyManager().getDataTypePropertyValue(textViews.get(0), super.getOntologyNamespace() + "viewHasTextSize");

		final Collection<OWLLiteral> backgroundColor 	= super.getOntologyManager().getDataTypePropertyValue(backgrounds.get(0), super.getOntologyNamespace() + "viewHasColor");

		final String vol = ((OWLLiteral) volume.toArray()[0]).getLiteral();
		final String bri = ((OWLLiteral) brightness.toArray()[0]).getLiteral();
		final String bwi = ((OWLLiteral) buttonWidth.toArray()[0]).getLiteral();
		final String bhe = ((OWLLiteral) buttonHeiht.toArray()[0]).getLiteral();
		final String bco = ((OWLLiteral) buttonColor.toArray()[0]).getLiteral();
		final String btc = ((OWLLiteral) buttonTextColor.toArray()[0]).getLiteral();
		final String eco = ((OWLLiteral) editColor.toArray()[0]).getLiteral();
		final String etc = ((OWLLiteral) editTextColor.toArray()[0]).getLiteral();
		final String ets = ((OWLLiteral) editTextSize.toArray()[0]).getLiteral();
		final String tvco = ((OWLLiteral) textViewColor.toArray()[0]).getLiteral();
		final String tvtc = ((OWLLiteral) textViewTextColor.toArray()[0]).getLiteral();
		final String tvts = ((OWLLiteral) textViewTextSize.toArray()[0]).getLiteral();
		final String bck = ((OWLLiteral) backgroundColor.toArray()[0]).getLiteral();

		interactionIntent.setClass(this,  MailSenderActivity.class);
		interactionIntent.putExtra("caller", 1);

		userPrefs.setVolume(Float.parseFloat(vol));
		userPrefs.setBrightness(Float.parseFloat(bri));
		userPrefs.setButtonWidth(Float.parseFloat(bwi));
		userPrefs.setButtonHeight(Float.parseFloat(bhe));
		userPrefs.setButtonBackgroundColor(Integer.parseInt(bco));
		userPrefs.setButtonTextColor(Integer.parseInt(btc));
		userPrefs.setTextEditSize(Float.parseFloat(ets));
		userPrefs.setTextEditBackgroundColor(Integer.parseInt(eco));
		userPrefs.setTextEditTextColor(Integer.parseInt(etc));
		userPrefs.setTextViewBackgroundColor(Integer.parseInt(tvco));
		userPrefs.setTextViewTextColor(Integer.parseInt(tvtc));
		userPrefs.setTextViewTextSize(Float.parseFloat(tvts));
		userPrefs.setLayoutBackgroundColor(Integer.parseInt(bck));

		interactionIntent.putExtra("viewParams", userPrefs);

		startActivity(interactionIntent);
	}

}
