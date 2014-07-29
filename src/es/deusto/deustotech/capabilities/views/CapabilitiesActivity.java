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
//				getOntologyManager().addDataTypePropertyValue(getDisplays().get(0), getOntologyNamespace() + "displayHasApplicable", true);
//				getOntologyManager().addDataTypePropertyValue(getDisplays().get(0), getOntologyNamespace() + "isStatic", false);
//				getOntologyManager().addDataTypePropertyValue(getAudios().get(0), getOntologyNamespace() + "audioHasApplicable", true);
				interactionIntent.setClass(this,  ButtonConfigActivity.class);
				interactionIntent.putExtra(getResources().getString(R.string.activity_caller), 1);
				tts.stop();
				startActivity(interactionIntent);
			} else if (longPush){
				//If longPush means that the user cannot see the screen properly
//				getOntologyManager().addDataTypePropertyValue(getDisplays().get(0), getOntologyNamespace() + "displayHasApplicable", false);
//				getOntologyManager().addDataTypePropertyValue(getDisplays().get(0), getOntologyNamespace() + "isStatic", true);
//				getOntologyManager().addDataTypePropertyValue(getAudios().get(0), getOntologyNamespace() + "audioHasApplicable", true);
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
		//Avoiding the configuration. Using the values stored in the ontology
		super.initOntology();
		
		final Collection<OWLLiteral> btnBackColor 	= getOntologyManager().getDataTypePropertyValue(getButtons().get(0), getOntologyNamespace() + "viewHasColor");
		final Collection<OWLLiteral> btnTextColor 	= getOntologyManager().getDataTypePropertyValue(getButtons().get(0), getOntologyNamespace() + "viewHasTextColor");
		final Collection<OWLLiteral> btnWidth 		= getOntologyManager().getDataTypePropertyValue(getButtons().get(0), getOntologyNamespace() + "viewHasWidth");
		final Collection<OWLLiteral> btnHeight 		= getOntologyManager().getDataTypePropertyValue(getButtons().get(0), getOntologyNamespace() + "viewHasHeight");
		
		final Collection<OWLLiteral> etBackColor 	= getOntologyManager().getDataTypePropertyValue(getEditTexts().get(0), getOntologyNamespace() + "viewHasColor");
		final Collection<OWLLiteral> etTextColor 	= getOntologyManager().getDataTypePropertyValue(getEditTexts().get(0), getOntologyNamespace() + "viewHasTextColor");
		final Collection<OWLLiteral> etWidth 		= getOntologyManager().getDataTypePropertyValue(getEditTexts().get(0), getOntologyNamespace() + "viewHasWidth");
		final Collection<OWLLiteral> etHeight 		= getOntologyManager().getDataTypePropertyValue(getEditTexts().get(0), getOntologyNamespace() + "viewHasHeight");
		final Collection<OWLLiteral> ettSize 		= getOntologyManager().getDataTypePropertyValue(getEditTexts().get(0), getOntologyNamespace() + "viewHasTextSize");
		
		final Collection<OWLLiteral> tvBackColor 	= etBackColor;
		final Collection<OWLLiteral> tvTextColor 	= etTextColor;
		final Collection<OWLLiteral> tvWidth 		= etWidth;
		final Collection<OWLLiteral> tvHeight 		= etHeight;
		final Collection<OWLLiteral> tvtSize 		= ettSize;
		
		final Collection<OWLLiteral> backColor = getOntologyManager().getDataTypePropertyValue(getBackgrounds().get(0), getOntologyNamespace() + "viewHasColor");
		final Collection<OWLLiteral> volume = getOntologyManager().getDataTypePropertyValue(getAudios().get(0), getOntologyNamespace() + "audioHasVolume");

		final String vol = ((OWLLiteral) volume.toArray()[0]).getLiteral();
		final String bwi = ((OWLLiteral) btnWidth.toArray()[0]).getLiteral();
		final String bhe = ((OWLLiteral) btnHeight.toArray()[0]).getLiteral();
		final String btc = ((OWLLiteral) btnTextColor.toArray()[0]).getLiteral();
		final String etc = ((OWLLiteral) etTextColor.toArray()[0]).getLiteral();
		final String tvtc = ((OWLLiteral) tvTextColor.toArray()[0]).getLiteral();
		
		final String etw = ((OWLLiteral) etWidth.toArray()[0]).getLiteral();
		final String tvw = ((OWLLiteral) tvWidth.toArray()[0]).getLiteral();
		final String eth = ((OWLLiteral) etHeight.toArray()[0]).getLiteral();
		final String tvh = ((OWLLiteral) tvHeight.toArray()[0]).getLiteral();

		String bco, bck, tvco, eco;
		bco = bck = tvco = eco = "";
		
		if (btnBackColor.size() > 0){
			bco = ((OWLLiteral) btnBackColor.toArray()[0]).getLiteral();
			userPrefs.setButtonBackgroundColor(Integer.parseInt(bco));
		}
		
		if (etBackColor.size() > 0){
			eco = ((OWLLiteral) etBackColor.toArray()[0]).getLiteral();
			tvco = ((OWLLiteral) tvBackColor.toArray()[0]).getLiteral();
			userPrefs.setEditTextBackgroundColor(Integer.parseInt(eco));
			userPrefs.setTextViewBackgroundColor(Integer.parseInt(tvco));
		}

		if (backColor.size() > 0){
			bck = ((OWLLiteral) backColor.toArray()[0]).getLiteral();
			userPrefs.setLayoutBackgroundColor(Integer.parseInt(bck));
		}

		interactionIntent.setClass(this,  MailSenderActivity.class);
		interactionIntent.putExtra(getResources().getString(R.string.activity_caller), 1);

		userPrefs.setVolume(Float.parseFloat(vol));
		userPrefs.setButtonWidth(Float.parseFloat(bwi));
		userPrefs.setButtonHeight(Float.parseFloat(bhe));
		userPrefs.setButtonTextColor(Integer.parseInt(btc));
		userPrefs.setEditTextTextColor(Integer.parseInt(etc));
		userPrefs.setEditTextWidth(Integer.parseInt(etw));
		userPrefs.setEditTextHeight(Integer.parseInt(eth));
		userPrefs.setTextViewTextColor(Integer.parseInt(tvtc));
		userPrefs.setTextViewWidth(Integer.parseInt(tvw));
		userPrefs.setTextViewHeight(Integer.parseInt(tvh));
		
		Collection<OWLLiteral> brightness = getOntologyManager().getDataTypePropertyValue(getDisplays().get(0), getOntologyNamespace() + "displayHasBrightness");
		String bri;
		
		if (brightness.size() < 1){
			//TODO: Simulating office light
			getOntologyManager().addDataTypePropertyValue(getContexts().get(0), getOntologyNamespace() + "contextAuxHasLightLevel", "office_hallway");
			
			brightness = getOntologyManager().getDataTypePropertyValue(getDisplays().get(0), getOntologyNamespace() + "adaptationBrightnessHasValue");
			
			System.out.println("Simulating Light");
		} else {
			System.out.println("Not simulating, using light = " + ((OWLLiteral) brightness.toArray()[0]).getLiteral());
		}
		
		bri = ((OWLLiteral) brightness.toArray()[0]).getLiteral();
		userPrefs.setBrightness(Float.parseFloat(bri));

		final String etts = ((OWLLiteral) ettSize.toArray()[0]).getLiteral();
		final String tvts = ((OWLLiteral) tvtSize.toArray()[0]).getLiteral();
		
		userPrefs.setEditTextTextSize(Float.parseFloat(etts));
		userPrefs.setTextViewTextSize(Float.parseFloat(tvts));
		
		interactionIntent.putExtra(getResources().getString(R.string.view_params), userPrefs);

		startActivity(interactionIntent);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
