package es.deusto.deustotech.capabilities.views;

import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import es.deusto.deustotech.R;
import es.deusto.deustotech.pellet4android.OntologyManager;

/**
 * This activity shows a Button and a TextEdit as configured in previous activities
 * and changes dynamically the screen brightness for the user to easily check if 
 * he/she is able to interact with the presented views
 * 
 * @author edlectrico
 *
 */
public class BrightnessConfigActivity extends AbstractActivity {

	private static final String TAG = BrightnessConfigActivity.class.getSimpleName();
	
	private OntologyManager ontManager;
	private static List<String> displays;
	
	private GridLayout grid;
	private OnTouchListener onTouchListener;
	
	private float brightnessValue = 0.5f; // dummy default value
	private boolean brightnessChanged = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brightness_config);
		
		ontManager = super.getOntologyManager();
		
		Bundle bundle = getIntent().getExtras();
		userPrefs = bundle.getParcelable("viewParams");
		
		grid = (GridLayout) findViewById(R.id.default_layout);
		onTouchListener = createOnTouchListener();
		
		redrawViews();
		initializeServices(TAG);
		addListeners();
	}
	
	@Override
	public void initializeServices(String TAG) {
		if (userPrefs.getSightProblem() == 1){
			super.initializeServices(TAG);
			
			speakOut(getResources().getString(R.string.edit_text_info_message));
		}
	}
	
	@Override
	public void addListeners() {
		grid.getChildAt(0).setOnTouchListener(onTouchListener);
		grid.getChildAt(1).setOnTouchListener(onTouchListener);
		grid.getChildAt(2).setOnTouchListener(onTouchListener);
		grid.getChildAt(3).setOnTouchListener(onTouchListener);
		
		findViewById(R.id.next_button).setOnClickListener(this);
		findViewById(R.id.test_text_edit).setOnTouchListener(onTouchListener);
	}
	
	@Override
	public void redrawViews() {
		//EditText config
		((EditText)findViewById(R.id.test_text_edit)).setTextSize(userPrefs.getTextEditSize());
		
		if (userPrefs.getTextEditTextColor() != 0){
			((EditText) findViewById(R.id.test_text_edit)).setTextColor(userPrefs.getTextEditTextColor());
			((EditText) findViewById(R.id.test_text_edit)).setBackgroundColor(userPrefs.getTextEditBackgroundColor());
		}
		
		((TextView)findViewById(R.id.brightness_message)).setTextSize(userPrefs.getTextEditSize());
		((TextView)findViewById(R.id.brightness_message)).setTextColor(userPrefs.getTextEditTextColor());
		
		findViewById(R.id.next_button).setMinimumWidth((int)userPrefs.getButtonWidth());
		findViewById(R.id.next_button).setMinimumHeight((int) userPrefs.getButtonHeight());
		((Button)findViewById(R.id.next_button)).setTextColor(userPrefs.getButtonTextColor());
		
		findViewById(R.id.test_button).setMinimumWidth((int)userPrefs.getButtonWidth());
		findViewById(R.id.test_button).setMinimumHeight((int) userPrefs.getButtonHeight());
		((Button)findViewById(R.id.test_button)).setTextColor(userPrefs.getButtonTextColor());
		
		grid.setBackgroundColor(userPrefs.getBackgroundColor());

		if (userPrefs.getBackgroundColor() != 0){
			((Button)findViewById(R.id.next_button)).setBackgroundColor(userPrefs.getButtonBackgroundColor());
			((Button)findViewById(R.id.test_button)).setBackgroundColor(userPrefs.getButtonBackgroundColor());
		}
	}
	
	private OnTouchListener createOnTouchListener() {
		return new OnTouchListener() {
			//Each time the user presses the screen a new brightness value
			//is generated
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN){
					brightnessChanged = true;
					
					Random randomGenerator = new Random();
				    int randomInt = randomGenerator.nextInt(100);
					
					brightnessValue = (float) randomInt / 100;

					WindowManager.LayoutParams layoutParams = getWindow()
							.getAttributes();
					layoutParams.screenBrightness = brightnessValue;
					getWindow().setAttributes(layoutParams);
				}
				
				return false;
			}
		};
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.next_button){
			Intent intent = new Intent(this, VolumeConfigActivity.class);
			
			if (brightnessChanged){
				userPrefs.setBrightness(brightnessValue);
				brightnessChanged = false;
			} else {
				userPrefs.setBrightness(0);
			}
			
			intent.putExtra("viewParams", userPrefs);
			intent.putExtra("caller", 1); //0 - MainActivity; 1 - BrightnessAtivity
			
			displays = ontManager.getIndividualOfClass(super.getOntologyNamespace() + "Display");
			ontManager.addDataTypePropertyValue(displays.get(0), super.getOntologyNamespace() + "userDisplayHasBrightness", (int) brightnessValue);
			
			if (userPrefs.getSightProblem() == 1){
				speakOut("Well done!");
			}
			
			startActivity(intent);
			
		}
	}

}
