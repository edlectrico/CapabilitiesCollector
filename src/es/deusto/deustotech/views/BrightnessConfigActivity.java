package es.deusto.deustotech.views;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import es.deusto.deustotech.R;
import es.deusto.deustotech.utils.UserMinimumPreferences;

/**
 * This activity shows a Button and a TextEdit as configured in previous activities
 * and changes dynamically the screen brightness for the user to easily check if 
 * he/she is able to interact with the presented views
 * 
 * @author edlectrico
 *
 */
public class BrightnessConfigActivity extends Activity implements View.OnClickListener, TextToSpeech.OnInitListener{

	private UserMinimumPreferences userPrefs;
	private GridLayout grid;
	private OnTouchListener onTouchListener;
	private TextToSpeech tts;
	float brightnessValue = 0.5f; // dummy default value
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brightness_config);
		
		Bundle bundle = getIntent().getExtras();
		userPrefs = bundle.getParcelable("viewParams");
		
		if (userPrefs.getSightProblem() == 1){
			tts = new TextToSpeech(this, this);
			
			speakOut(getResources().getString(R.string.brightness_info_message));
		}
		
		//EditText config
		((EditText)findViewById(R.id.test_text_edit)).setTextSize(userPrefs.getTextEditSize());
		((EditText) findViewById(R.id.test_text_edit)).setTextColor(userPrefs.getTextEditTextColor());
		((EditText) findViewById(R.id.test_text_edit)).setBackgroundColor(userPrefs.getTextEditBackgroundColor());
		
		grid = (GridLayout) findViewById(R.id.default_layout);
		
		findViewById(R.id.next_button).setOnClickListener(this);
		
		redrawButtons();
		
		onTouchListener = new OnTouchListener() {
			//Each time the user presses the screen a new brightness value
			//is generated
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN){
					
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
		
		this.grid.getChildAt(0).setOnTouchListener(onTouchListener);
		this.grid.getChildAt(1).setOnTouchListener(onTouchListener);
		this.grid.getChildAt(2).setOnTouchListener(onTouchListener);
		this.grid.getChildAt(3).setOnTouchListener(onTouchListener);
		
		findViewById(R.id.test_text_edit).setOnTouchListener(onTouchListener);
	}
	
	private void redrawButtons() {
		findViewById(R.id.next_button).setMinimumWidth((int)userPrefs.getButtonWidth());
		findViewById(R.id.next_button).setMinimumHeight((int) userPrefs.getButtonHeight());
		((Button)findViewById(R.id.next_button)).setBackgroundColor(userPrefs.getButtonBackgroundColor());
		((Button)findViewById(R.id.next_button)).setTextColor(userPrefs.getButtonTextColor());
		
		findViewById(R.id.test_button).setMinimumWidth((int)userPrefs.getButtonWidth());
		findViewById(R.id.test_button).setMinimumHeight((int) userPrefs.getButtonHeight());
		((Button)findViewById(R.id.test_button)).setBackgroundColor(userPrefs.getButtonBackgroundColor());
		((Button)findViewById(R.id.test_button)).setTextColor(userPrefs.getButtonTextColor());
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.next_button:
			Intent intent = new Intent(this, VolumeConfigActivity.class);
			
			userPrefs.setBrightness(brightnessValue);
			
			intent.putExtra("viewParams", userPrefs);
			
			if (userPrefs.getSightProblem() == 1){
				speakOut("Well done!");
			}
			
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onInit(int status) { }
	
	private void speakOut(final String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

}
