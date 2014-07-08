package es.deusto.deustotech.capabilities.views;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.semanticweb.owlapi.model.OWLLiteral;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
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

public class VolumeConfigActivity extends AbstractActivity {

	private static final String TAG = VolumeConfigActivity.class.getSimpleName();
	
	private static List<String> audios;
	
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
		
		if (callerActivity == 1){ //BrightnessActivity
			userPrefs = bundle.getParcelable(getResources().getString(R.string.view_params));
		}
		
		grid = (GridLayout) findViewById(R.id.volume_layout);
		grid.setOnClickListener(this);
		
		initializeServices(TAG);
		
		volumePicker = (NumberPicker) findViewById(R.id.volume_picker);
		volumePicker.setMinValue(0);
		volumePicker.setMaxValue(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		
		redrawViews();
		addListeners();
	}
	
	@Override
	public void initializeServices(String TAG) {
		if (callerActivity == 1){ //BrightnessActivity
			if (userPrefs.getSightProblem() == 1){
				super.initializeServices(TAG);
			}
			if (userPrefs.getBrightness() != 0){
				WindowManager.LayoutParams layoutParams = getWindow()
						.getAttributes();
				layoutParams.screenBrightness = userPrefs.getBrightness();
				getWindow().setAttributes(layoutParams);
			}
		}
		
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
	}
	
	@Override
	public void addListeners() {
		findViewById(R.id.end_button).setOnClickListener(this);
		findViewById(R.id.end_button).setOnClickListener(this);
		
		grid.getChildAt(0).setOnClickListener(this);
		grid.getChildAt(1).setOnClickListener(this);
		
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
	
	@Override
	public void redrawViews() {
		if (callerActivity == 1){ //BrightnessActivity
			grid.setBackgroundColor(userPrefs.getLayoutBackgroundColor());
			
			((TextView)findViewById(R.id.volume_message)).setTextSize(userPrefs.getTextEditSize() / 2);
			
			if (userPrefs.getTextEditTextColor() != 0){
				((TextView)findViewById(R.id.volume_message)).setTextColor(userPrefs.getTextEditTextColor());
			}
			
			if (userPrefs.getTextEditBackgroundColor() != 0){
				((TextView)findViewById(R.id.volume_message)).setBackgroundColor(userPrefs.getTextEditBackgroundColor());
			}
			
			findViewById(R.id.end_button).setMinimumWidth((int)userPrefs.getButtonWidth());
			findViewById(R.id.end_button).setMinimumHeight((int) userPrefs.getButtonHeight());
			
			((Button)findViewById(R.id.end_button)).setTextColor(userPrefs.getButtonTextColor());
			((TextView)findViewById(R.id.volume_message)).setTextSize(userPrefs.getTextEditSize() / 2);
	
			if (userPrefs.getButtonBackgroundColor() != DEFAULT_BUTTON_COLOR){
				((Button)findViewById(R.id.end_button)).setBackgroundColor(userPrefs.getButtonBackgroundColor());
			}
			
			if (userPrefs.getTextEditTextColor() != 0){
				((TextView)findViewById(R.id.volume_message)).setTextColor(userPrefs.getTextEditTextColor());
			}
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
			
			audios = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Audio");
			super.getOntologyManager().addDataTypePropertyValue(audios.get(0), super.getOntologyNamespace() + "audioHasVolume", volumeLevel);
			
			try {
				super.getOntologyManager().saveOntologyAs(Environment.getExternalStorageDirectory() + "/data/" + super.getOntologyFilename());
			} catch (OntologySavingException e) {
				e.printStackTrace();
			}
			
			SharedPreferences  preferences = getPreferences(MODE_PRIVATE);
			Editor prefsEditor = preferences.edit();
			
			Gson gson = new Gson();
			String json = gson.toJson(userPrefs);
			prefsEditor.putString(getResources().getString(R.string.view_params), json);
			prefsEditor.commit();
			
			checkOntology();
			
			if (callerActivity == 1){ //BrightnessActivity
				Intent intent = new Intent(this, MailSenderActivity.class);
				intent.putExtra(getResources().getString(R.string.view_params), userPrefs);
				
				if (userPrefs.getSightProblem() == 1){
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
	
	private void checkOntology() {
		final List<String> audios = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Audio");
		
		final Collection<OWLLiteral> volumes	= super.getOntologyManager().getDataTypePropertyValue(audios.get(0), super.getOntologyNamespace() + "audioHasVolume");
		
		System.out.println("checkOntology(): " 	+ TAG);
		System.out.println("volume: " 		+ volumes);
	}

}
