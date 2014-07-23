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

	private static final String TAG = CapabilitiesActivity.class.getSimpleName();
	/** 
	 * This variable checks if the corresponding individuals
	 * have been loaded before, so there is no need of doing the same operation again
	 */
	private boolean loaded 				= false;
	private boolean longPush 			= false;
	private boolean voiceRecognition 	= false;
	
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
		
		setVoiceRecognition(checkVoiceRecognition());
		
		audios 	 = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Audio");
		displays = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Display");
		
		removeAllValuesFromOntology();
		
		listenToSpeech();
	}
	
	private void removeAllValuesFromOntology() {
		//Get all the individuals from the ontology and remove previous values
		//The only remaining values are the ones in the Adaptation individual
		final List <String> buttons 	= super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Button");
		final List <String> edits 		= super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "EditText");
		final List <String> textViews 	= super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "TextView");
		final List <String> backgrounds	= super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Background");
		final List <String> audios 		= super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Audio");
		final List <String> displays 	= super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Display");
		final List <String> devices 	= super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "DeviceAux");
		final List <String> contexts 	= super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "ContextAux");
		final List <String> lights 		= super.getOntologyManager().getIndividualOfClass("http://u2m.org/2003/02/UserModelOntology.rdf#Light");
		final List <String> noises		= super.getOntologyManager().getIndividualOfClass("http://u2m.org/2003/02/UserModelOntology.rdf#Noise");
		
		final List <String> views = new ArrayList<String>();
		views.add(buttons.get(0));
		views.add(edits.get(0));
		views.add(textViews.get(0));
		views.add(backgrounds.get(0));
		
		for (int i = 0; i < views.size(); i++){
			super.getOntologyManager().removeIndividualMembership(views.get(i), super.getOntologyNamespace() + "viewHasColor");
			super.getOntologyManager().removeIndividualMembership(views.get(i), super.getOntologyNamespace() + "viewHasTextColor");
			super.getOntologyManager().removeIndividualMembership(views.get(i), super.getOntologyNamespace() + "viewHasTextSize");
			super.getOntologyManager().removeIndividualMembership(views.get(i), super.getOntologyNamespace() + "viewHasWidth");
			super.getOntologyManager().removeIndividualMembership(views.get(i), super.getOntologyNamespace() + "viewHasHeight");
		}
		
		super.getOntologyManager().removeIndividualMembership(audios.get(0), super.getOntologyNamespace() + "audioHasVolume");
		super.getOntologyManager().removeIndividualMembership(audios.get(0), super.getOntologyNamespace() + "audioHasApplicable");

		super.getOntologyManager().removeIndividualMembership(displays.get(0), super.getOntologyNamespace() + "displayHasBrightness");
		super.getOntologyManager().removeIndividualMembership(displays.get(0), super.getOntologyNamespace() + "displayHasApplicable");
		
		super.getOntologyManager().removeIndividualMembership(devices.get(0), super.getOntologyNamespace() + "deviceAuxBatteryIsSufficient");
		super.getOntologyManager().removeIndividualMembership(devices.get(0), super.getOntologyNamespace() + "deviceAuxHasBrightness");
		
		super.getOntologyManager().removeIndividualMembership(contexts.get(0), super.getOntologyNamespace() + "contextAuxHasLightLevel");
		super.getOntologyManager().removeIndividualMembership(contexts.get(0), super.getOntologyNamespace() + "contextAuxHasNoiseLevel");
		
		super.getOntologyManager().removeIndividualMembership(lights.get(0), super.getOntologyNamespace() + "contextHasLight");
		super.getOntologyManager().removeIndividualMembership(noises.get(0), super.getOntologyNamespace() + "contextHasNoise");
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
				super.getOntologyManager().addDataTypePropertyValue(displays.get(0), "displayHasApplicabe", false);
				interactionIntent.setClass(this,  VolumeConfigActivity.class);
				interactionIntent.putExtra(getResources().getString(R.string.activity_caller), 0); //0 - MainActivity; 1 - BrightnessAtivity
				speakOut(getResources().getString(R.string.volume_info_message_es));
				speakOut(getResources().getString(R.string.volume_longpush_es));
			} else if (suggestedWords.contains(getResources().getString(R.string.no))){
				//TODO: Communication by visual interaction, but probably with a visual difficulty
				tts.stop();
				super.getOntologyManager().addDataTypePropertyValue(displays.get(0), "displayHasApplicabe", true);
				
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
		
		audios = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Audio");
		super.getOntologyManager().addDataTypePropertyValue(audios.get(0), 	super.getOntologyNamespace() + "audioHasApplicabe", true);
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
			deletePreviousValues();
			if (!longPush){
				vibrator.vibrate(500);
				speakOut(getResources().getString(R.string.message_visual_interaction_es));
				super.getOntologyManager().addDataTypePropertyValue(displays.get(0), super.getOntologyNamespace() + "displayHasApplicable", true);
				super.getOntologyManager().addDataTypePropertyValue(displays.get(0), super.getOntologyNamespace() + "isStatic", false);
				super.getOntologyManager().addDataTypePropertyValue(audios.get(0), 	 super.getOntologyNamespace() + "audioHasApplicable", true);
				interactionIntent.setClass(this,  ButtonConfigActivity.class);
				interactionIntent.putExtra(getResources().getString(R.string.activity_caller), 1);
				tts.stop();
				startActivity(interactionIntent);
			} else if (longPush){
				//If longPush means that the user cannot see the screen properly
				super.getOntologyManager().addDataTypePropertyValue(displays.get(0), super.getOntologyNamespace() + "displayHasApplicable", false);
				super.getOntologyManager().addDataTypePropertyValue(displays.get(0), super.getOntologyNamespace() + "isStatic", true);
				super.getOntologyManager().addDataTypePropertyValue(audios.get(0), 	 super.getOntologyNamespace() + "audioHasApplicable", true);
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

	/**
	 * To maintain the consistency of the ontology we are storing just one value per property.
	 * To do so, it is necessary to delete the corresponding property value before inserting
	 * a new one.
	 */
	private void deletePreviousValues() {
		super.getOntologyManager().deleteAllValuesOfProperty(displays.get(0), super.getOntologyNamespace() + "displayHasApplicable");
		super.getOntologyManager().deleteAllValuesOfProperty(displays.get(0), super.getOntologyNamespace() + "isStatic");
		super.getOntologyManager().deleteAllValuesOfProperty(audios.get(0), super.getOntologyNamespace() + "audioHasApplicable");
	}

	public boolean isVoiceRecognition() {
		return voiceRecognition;
	}

	public void setVoiceRecognition(boolean voiceRecognition) {
		this.voiceRecognition = voiceRecognition;
	}

	public void launchMailSenderActivity() {
		//Avoiding the configuration. Using the values stored in the ontology
//		final Collection<OWLLiteral> volume = super.getOntologyManager().getDataTypePropertyValue(audios.get(0), super.getOntologyNamespace() + "audioHasVolume");
		List<String> adaptations = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Adaptation");
		
		Collection<OWLLiteral> btnBackColor = super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasButtonBackgroundColor");
		Collection<OWLLiteral> btnTextColor = super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasButtonTextColor");
		Collection<OWLLiteral> btnWidth 	= super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasButtonWidth");
		Collection<OWLLiteral> btnHeight 	= super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasButtonHeight");
		
		Collection<OWLLiteral> tvBackColor 	= super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasTextViewBackgroundColor");
		Collection<OWLLiteral> tvTextColor 	= super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasTextViewTextColor");
		Collection<OWLLiteral> tvWidth 		= super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasTextViewWidth");
		Collection<OWLLiteral> tvHeight 	= super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasTextViewHeight");
		Collection<OWLLiteral> tvtSize 		= super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasTextViewTextSize");
		
		Collection<OWLLiteral> etBackColor 	= super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasEditTextBackgroundColor");
		Collection<OWLLiteral> etTextColor 	= super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasEditTextTextColor");
		Collection<OWLLiteral> etWidth 		= super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasEditTextWidth");
		Collection<OWLLiteral> etHeight 	= super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasEditTextHeight");
		Collection<OWLLiteral> ettSize 		= super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasEditTextTextSize");
		
		Collection<OWLLiteral> backColor 	= super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasBackgroundColor");

//		final String vol = ((OWLLiteral) volume.toArray()[0]).getLiteral();
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
			userPrefs.setTextEditBackgroundColor(Integer.parseInt(eco));
			userPrefs.setTextViewBackgroundColor(Integer.parseInt(tvco));
		}

		if (backColor.size() > 0){
			bck = ((OWLLiteral) backColor.toArray()[0]).getLiteral();
			userPrefs.setLayoutBackgroundColor(Integer.parseInt(bck));
		}

		interactionIntent.setClass(this,  MailSenderActivity.class);
		interactionIntent.putExtra(getResources().getString(R.string.activity_caller), 1);

//		userPrefs.setVolume(Float.parseFloat(vol));
		userPrefs.setButtonWidth(Float.parseFloat(bwi));
		userPrefs.setButtonHeight(Float.parseFloat(bhe));
		userPrefs.setButtonTextColor(Integer.parseInt(btc));
		userPrefs.setEditTextTextColor(Integer.parseInt(etc));
		userPrefs.setEditTextWidth(Integer.parseInt(etw));
		userPrefs.setEditTextHeight(Integer.parseInt(eth));
		userPrefs.setTextViewTextColor(Integer.parseInt(tvtc));
		userPrefs.setTextViewWidth(Integer.parseInt(tvw));
		userPrefs.setTextViewHeight(Integer.parseInt(tvh));

		
		Collection<OWLLiteral> brightness = super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationBrightnessHasValue");
		String bri;
		
		if (brightness.size() < 1){
			//TODO: Simulating office light
			List<String> contextAux = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "ContextAux");
			super.getOntologyManager().addDataTypePropertyValue(contextAux.get(0), super.getOntologyNamespace() + "contextAuxHasLightLevel", "office_hallway");
			
			brightness = super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationBrightnessHasValue");
			
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
