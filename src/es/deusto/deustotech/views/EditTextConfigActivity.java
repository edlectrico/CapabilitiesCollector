package es.deusto.deustotech.views;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.GridLayout;
import es.deusto.deustotech.R;
import es.deusto.deustotech.utils.UserMinimumPreferences;

public class EditTextConfigActivity extends Activity implements View.OnClickListener, TextToSpeech.OnInitListener {

	private Button testTextEdit;
	private GridLayout grid;
	private TextToSpeech tts;
//	private int viewColor;
	
	private UserMinimumPreferences userPrefs;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_text_config);
		
		Bundle bundle = getIntent().getExtras();
		userPrefs = bundle.getParcelable("viewParams");
		
		testTextEdit = (Button) findViewById(R.id.test_text_edit);
		testTextEdit.setOnClickListener(this);
		
		findViewById(R.id.next_button).setOnClickListener(this);
		findViewById(R.id.background_color_button).setOnClickListener(this);
		findViewById(R.id.text_color_button).setOnClickListener(this);
		
		redrawButtons();
		
		if (userPrefs.getSightProblem() == 1){
			tts = new TextToSpeech(this, this);
			
			speakOut(getResources().getString(R.string.edit_text_info_message));
		}
		
		grid = (GridLayout) findViewById(R.id.default_layout);
		
		OnTouchListener listener = new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x = event.getRawX();

				testTextEdit.setTextSize((float) (x / 10.0));
				testTextEdit.invalidate();

				return true;
			}
		};
		
		grid.getChildAt(1).setOnTouchListener(listener);
		grid.getChildAt(2).setOnTouchListener(listener);
		grid.getChildAt(3).setOnTouchListener(listener);
	}

	private void redrawButtons() {
		findViewById(R.id.next_button).setMinimumWidth((int)userPrefs.getButtonWidth());
		findViewById(R.id.next_button).setMinimumHeight((int) userPrefs.getButtonHeight());
		((Button)findViewById(R.id.next_button)).setBackgroundColor(userPrefs.getButtonBackgroundColor());
		((Button)findViewById(R.id.next_button)).setTextColor(userPrefs.getButtonTextColor());
		
		findViewById(R.id.background_color_button).setMinimumWidth((int)userPrefs.getButtonWidth());
		findViewById(R.id.background_color_button).setMinimumHeight((int) userPrefs.getButtonHeight());
		((Button)findViewById(R.id.background_color_button)).setBackgroundColor(userPrefs.getButtonBackgroundColor());
		((Button)findViewById(R.id.background_color_button)).setTextColor(userPrefs.getButtonTextColor());
		
		findViewById(R.id.text_color_button).setMinimumWidth((int)userPrefs.getButtonWidth());
		findViewById(R.id.text_color_button).setMinimumHeight((int) userPrefs.getButtonHeight());
		((Button)findViewById(R.id.text_color_button)).setBackgroundColor(userPrefs.getButtonBackgroundColor());
		((Button)findViewById(R.id.text_color_button)).setTextColor(userPrefs.getButtonTextColor());
	}

	@Override
	public void onInit(int status) { }

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.next_button:
				Intent intent = new Intent(this, BrightnessConfigActivity.class);
				
//				userPrefs.setTextEditTextColor(testTextEdit.getTextColors().getDefaultColor());
				userPrefs.setTextEditSize(testTextEdit.getTextSize());
				
				intent.putExtra("viewParams", userPrefs);
				
				if (userPrefs.getSightProblem() == 1){
					speakOut("Well done!");
				}
				
				startActivity(intent);
				break;

			case R.id.background_color_button:
				Random randomBackColor = new Random(); 
				int backgroundColor = Color.argb(255, randomBackColor.nextInt(256), randomBackColor.nextInt(256), randomBackColor.nextInt(256));
//				testTextEdit.setBackgroundColor(backgroundColor);
				((Button)findViewById(R.id.test_text_edit)).setBackgroundColor(backgroundColor);
				userPrefs.setTextEditBackgroundColor(backgroundColor);
				
			case R.id.text_color_button:
				Random randomTextColor = new Random(); 
				int textColor = Color.argb(255, randomTextColor.nextInt(256), randomTextColor.nextInt(256), randomTextColor.nextInt(256));
//				testTextEdit.setTextColor(textColor);
				((Button)findViewById(R.id.test_text_edit)).setTextColor(textColor);
				userPrefs.setTextEditTextColor(textColor);
		default:
			break;
		}
	}

	private void speakOut(final String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
	
}
