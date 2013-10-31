package es.deusto.deustotech.capabilities.views;

import java.util.ArrayList;
import java.util.List;

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
public class MainInputActivity extends AbstractActivity {

	private static final String TAG = MainInputActivity.class.getSimpleName();
	
	private boolean longPush 			= false;
	private boolean voiceRecognition 	= false;
	private Intent interactionIntent;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_activity);
		
		initializeServices(TAG);
		addListeners();
		
		interactionIntent = new Intent(this, ButtonConfigActivity.class);
		voiceRecognition = checkVoiceRecognition();
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
			Log.d(MainInputActivity.class.getSimpleName(), "Voice recognizer not present");
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
