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

		checkPreviousAdaptations();
		
		initializeServices(TAG);
		addListeners();
		
		interactionIntent = new Intent(this, ButtonConfigActivity.class);
		interactionIntent.putExtra(getResources().getString(R.string.visual_impairment), 0);
		interactionIntent.putExtra(getResources().getString(R.string.hearing_impairment), 0);
		
		setVoiceRecognition(checkVoiceRecognition());
		
		audios 	 = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Audio");
		displays = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Display");
		
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
		final String btc = ((OWLLiteral) buttonTextColor.toArray()[0]).getLiteral();
		final String etc = ((OWLLiteral) editTextColor.toArray()[0]).getLiteral();
		final String ets = ((OWLLiteral) editTextSize.toArray()[0]).getLiteral();
		final String tvtc = ((OWLLiteral) textViewTextColor.toArray()[0]).getLiteral();
		final String tvts = ((OWLLiteral) textViewTextSize.toArray()[0]).getLiteral();

		String bco, bck, tvco, eco;
		
		if (buttonColor.size() > 0){
			bco = ((OWLLiteral) buttonColor.toArray()[0]).getLiteral();
			userPrefs.setButtonBackgroundColor(Integer.parseInt(bco));
		}
		
		if (editColor.size() > 0){
			eco = ((OWLLiteral) editColor.toArray()[0]).getLiteral();
			tvco = ((OWLLiteral) textViewColor.toArray()[0]).getLiteral();
			userPrefs.setTextEditBackgroundColor(Integer.parseInt(eco));
			userPrefs.setTextViewBackgroundColor(Integer.parseInt(tvco));
		}

		if (backgroundColor.size() > 0){
			bck = ((OWLLiteral) backgroundColor.toArray()[0]).getLiteral();
			userPrefs.setLayoutBackgroundColor(Integer.parseInt(bck));
		}

		interactionIntent.setClass(this,  MailSenderActivity.class);
		interactionIntent.putExtra(getResources().getString(R.string.activity_caller), 1);

		userPrefs.setVolume(Float.parseFloat(vol));
		userPrefs.setBrightness(Float.parseFloat(bri));
		userPrefs.setButtonWidth(Float.parseFloat(bwi));
		userPrefs.setButtonHeight(Float.parseFloat(bhe));
		userPrefs.setButtonTextColor(Integer.parseInt(btc));
		userPrefs.setTextEditSize(Float.parseFloat(ets));
		userPrefs.setTextEditTextColor(Integer.parseInt(etc));
		userPrefs.setTextViewTextColor(Integer.parseInt(tvtc));
		userPrefs.setTextViewTextSize(Float.parseFloat(tvts));

		interactionIntent.putExtra(getResources().getString(R.string.view_params), userPrefs);

		startActivity(interactionIntent);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	private void checkPreviousAdaptations(){
		List<String> adaptations = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Adaptation");
		List<String> devices = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "DeviceAux");
//		super.getOntologyManager().addDataTypePropertyValue(devices.get(0), super.getOntologyNamespace() + "deviceAuxBatteryIsSufficient" , true);
		Collection<OWLLiteral> battery = super.getOntologyManager().getDataTypePropertyValue(devices.get(0), super.getOntologyNamespace() + "deviceAuxBatteryIsSufficient");
		Collection<OWLLiteral> brightness = super.getOntologyManager().getDataTypePropertyValue(devices.get(0), super.getOntologyNamespace() + "deviceAuxHasBrightness");
		
		Collection<OWLLiteral> btnBackColor = super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasButtonBackgroundColor");
		Collection<OWLLiteral> btnTextColor = super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasButtonTextColor");
		Collection<OWLLiteral> btnWidth = super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasButtonWidth");
		Collection<OWLLiteral> btnHeight = super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasButtonHeight");
		
		Collection<OWLLiteral> tvBackColor = super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasTextViewBackgroundColor");
		Collection<OWLLiteral> tvTextColor = super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasTextViewTextColor");
		Collection<OWLLiteral> tvWidth = super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasTextViewWidth");
		Collection<OWLLiteral> tvHeight = super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasTextViewHeight");
		
		Collection<OWLLiteral> etBackColor = super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasEditTextBackgroundColor");
		Collection<OWLLiteral> etTextColor = super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasEditTextTextColor");
		Collection<OWLLiteral> etWidth = super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasEditTextWidth");
		Collection<OWLLiteral> etHeight = super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasEditTextHeight");
		
		Collection<OWLLiteral> backColor = super.getOntologyManager().getDataTypePropertyValue(adaptations.get(0), super.getOntologyNamespace() + "adaptationHasBackgroundColor");
		
		System.out.println("Previous adaptations");
		
		System.out.println("battery: " 		+ battery );
		System.out.println("brightness: " 	+ brightness);
		
		System.out.println("btnBackColor: " + btnBackColor);
		System.out.println("btnTextColor: " + btnTextColor);
		System.out.println("btnWidth: " 	+ btnWidth);
		System.out.println("btnHeight: " 	+ btnHeight);
		
		System.out.println("tvBackColor: " 	+ tvBackColor);
		System.out.println("tvTextColor: " 	+ tvTextColor);
		System.out.println("tvWidth: " 		+ tvWidth);
		System.out.println("tvHeight: " 	+ tvHeight);
		
		System.out.println("etBackColor: " 	+ etBackColor);
		System.out.println("etTextColor: " 	+ etTextColor);
		System.out.println("etWidth: " 		+ etWidth);
		System.out.println("etHeight: " 	+ etHeight);
		
		System.out.println("backColor: " 	+ backColor);
	}
	
}
