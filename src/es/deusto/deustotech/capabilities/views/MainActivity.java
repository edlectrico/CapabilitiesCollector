package es.deusto.deustotech.capabilities.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import es.deusto.deustotech.capabilities.utils.OntologyLoadException;
import es.deusto.deustotech.capabilities.utils.OntologyManager;

/**
 * This activity checks user basic input capabilities
 * 
 * @author edlectrico
 * 
 */
public class MainActivity extends AbstractActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private boolean longPush 			= false;
	private boolean voiceRecognition 	= false;
	private Intent interactionIntent;
	
	private OntologyManager ontManager;
	private static String ontFilename;
	private static String adaptui;
	
	private static List<String> displays;
	private static List<String> users;
	private static List<String> audios;

	private ProgressDialog dialog;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_activity);

		dialog = ProgressDialog.show(MainActivity.this, "", 
				"Loading. Please wait...", true);        
		dialog.show();
		
		ontManager = super.getOntologyManager();
		adaptui = super.getOntologyNamespace();
		ontFilename = super.getOntologyFilename();
		
		new OntologyImports().execute("pepe");

		initializeServices(TAG);
		addListeners();

		interactionIntent = new Intent(this, ButtonConfigActivity.class);
		voiceRecognition = checkVoiceRecognition();
	}

	class OntologyImports extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... urls) {
			try {
				try {
					ontManager.loadOntologyFromFile(getAssets().open(ontFilename));
					insertDefaultUser();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (OntologyLoadException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String string) {
			System.out.println("userDisplayApplicableIsStatic: " + ontManager.getPropertyValue(displays.get(0), adaptui + "userDisplayApplicableIsStatic"));
			System.out.println("userDisplayHasApplicable: " 	+ ontManager.getPropertyValue(displays.get(0), adaptui + "userDisplayHasApplicable"));
			System.out.println("userAudioHasApplicable: " 		+ ontManager.getPropertyValue(audios.get(0), adaptui + "userAudioHasApplicable"));
			System.out.println("userDisplayHasBrightness: " 	+ ontManager.getPropertyValue(displays.get(0), adaptui + "userDisplayHasBrightness"));

			dialog.dismiss();
		}
	}

	private void insertDefaultUser(){
		System.out.println("\n Input users");
		System.out.println("-----------");

		ontManager.addIndividualMembership(adaptui + "defaultUser", adaptui + "User");
		ontManager.addIndividualMembership(adaptui + "display", 	 adaptui + "Display");
		ontManager.addIndividualMembership(adaptui + "audio", 		 adaptui + "Audio");

		users 	 = ontManager.getIndividualOfClass(adaptui + "User");
		displays = ontManager.getIndividualOfClass(adaptui + "Display");
		audios 	 = ontManager.getIndividualOfClass(adaptui + "Audio");

		ontManager.addObjectPropertyValue(users.get(0), adaptui + "userIsDefinedBy", 	displays.get(0));
		ontManager.addObjectPropertyValue(users.get(0), adaptui + "userIsDefinedBy", 	audios.get(0));

		for (String user : users) {
			System.out.println("users: " + user);
		}

		for (String display : displays) {
			System.out.println("displays: " + display);
		}

		for (String audio : audios) {
			System.out.println("audios: " + audio);
		}

		System.out.println("userIsDefinedBy: " 	+ ontManager.getPropertyValue(users.get(0), adaptui + "userIsDefinedBy"));

		//TODO: Aún no lo sabemos... Si el usuario responde YES (o mantiene pulsado) puede que sea ciego 
//		ontManager.addDataTypePropertyValue(displays.get(0), 	ADAPT_UI + "userDisplayApplicableIsStatic", false);
//		ontManager.addDataTypePropertyValue(displays.get(0), 	ADAPT_UI + "userDisplayHasApplicable", 		true);
//		ontManager.addDataTypePropertyValue(displays.get(0), 	ADAPT_UI + "userDisplayHasBrightness", 		50);
	}

	@Override
	public void addListeners() {
		//OnLongClick -> audio-based interaction
		GridLayout grid = (GridLayout) findViewById(R.id.grid_layout);
		grid.setOnLongClickListener(this);

		findViewById(R.id.input_button).setOnLongClickListener(this);
		findViewById(R.id.input_button).setOnClickListener(this);
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
			Log.d(MainActivity.class.getSimpleName(), "Voice recognizer not present");
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
				ontManager.addDataTypePropertyValue(displays.get(0), 	adaptui + "userDisplayHasApplicabe", true);
				
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

		ontManager.addDataTypePropertyValue(audios.get(0), 	adaptui + "userAudioHasApplicabe", true);
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
//				ontManager.addDataTypePropertyValue(audios.get(0), 	ADAPT_UI + "userAudioHasApplicabe", true);
				longPush = false;

//				Intent intent = new Intent(this, VolumeConfigActivity.class);
//				intent.putExtra("caller", 0); //0 - MainActivity; 1 - BrightnessAtivity
//				startActivity(intent);
			}
		}
	}

}
