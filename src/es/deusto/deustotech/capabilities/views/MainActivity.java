package es.deusto.deustotech.capabilities.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import es.deusto.deustotech.R;
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
	
	private final static String ONTOLOGY 		= "adaptui_rdf_rules.owl";
	private static final String ADAPT_UI 		= "http://www.morelab.deusto.es/ontologies/adaptui#";
	private static OntologyManager ontManager 	= new OntologyManager();
	
	private static List<String> displays;
	private static List<String> users;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_activity);
		
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
						ontManager.loadOntologyFromFile(getAssets().open(ONTOLOGY));
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (OntologyLoadException e) {
					e.printStackTrace();
				}
				
			return null;
	    }

	    protected void onPostExecute(String string) {
	    	insertDefaultUser();
	    }
	}
	
	private void insertDefaultUser(){
		System.out.println("\n Input users");
		System.out.println("-----------");
		
		ontManager.addIndividualMembership(ADAPT_UI + "defaultUser", 	ADAPT_UI + "User");
		ontManager.addIndividualMembership(ADAPT_UI + "display1", 	ADAPT_UI + "Display");

		users 		= ontManager.getIndividualOfClass(ADAPT_UI + "User");
		displays 	= ontManager.getIndividualOfClass(ADAPT_UI + "Display");
		
		ontManager.addObjectPropertyValue(users.get(0), ADAPT_UI + "userIsDefinedBy", 	displays.get(0));

		for (String user : users) {
			System.out.println("users: " + user);
		}
		
		for (String display : displays) {
			System.out.println("displays: " + display);
		}
		
		System.out.println("userIsDefinedBy: " 	+ ontManager.getPropertyValue(users.get(0), ADAPT_UI + "userIsDefinedBy"));
		
		ontManager.addDataTypePropertyValue(displays.get(0), 	ADAPT_UI + "userDisplayApplicableIsStatic", false);
		ontManager.addDataTypePropertyValue(displays.get(0), 	ADAPT_UI + "userDisplayHasApplicable", 		true);
		ontManager.addDataTypePropertyValue(displays.get(0), 	ADAPT_UI + "userDisplayHasBrightness", 		50);
		
		System.out.println("userDisplayApplicableIsStatic: " 	+ ontManager.getPropertyValue(displays.get(0), ADAPT_UI + "userDisplayApplicableIsStatic"));
		System.out.println("userDisplayHasApplicable: " 		+ ontManager.getPropertyValue(displays.get(0), ADAPT_UI + "userDisplayHasApplicable"));
		System.out.println("userDisplayHasBrightness: " 		+ ontManager.getPropertyValue(displays.get(0), ADAPT_UI + "userDisplayHasBrightness"));
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
            	interactionIntent.putExtra(getResources().getString(R.string.visual_impairment), 0);
            	interactionIntent.putExtra(getResources().getString(R.string.hearing_impairment), 0);
            }
            //TODO: If no answer, hearing_impairments = true
            startActivity(interactionIntent);
        }
	}

	@Override
	public boolean onLongClick(View view) {
		if (view.getId() == R.id.grid_layout) {
			onLongClickView();
		} else if (view.getId() == R.id.input_button){
			onLongClickView();
		}
		
		return super.onLongClick(view);
	}
	
	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.input_button){
				if (!longPush){
					vibrator.vibrate(500);
					speakOut("Visual based interaction selected");
					startActivity(getDefaultIntent());
				}
		}
	}
	
}
