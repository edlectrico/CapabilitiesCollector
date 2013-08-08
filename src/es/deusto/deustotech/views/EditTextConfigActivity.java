package es.deusto.deustotech.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.GridLayout;
import es.deusto.deustotech.R;
import es.deusto.deustotech.utils.ViewParams;

public class EditTextConfigActivity extends Activity implements View.OnClickListener, TextToSpeech.OnInitListener {

	private Button testTextEdit;
	private GridLayout grid;
	private TextToSpeech tts;
	private int viewColor;
	
	private ViewParams viewParams;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_text_config);
		
		Bundle bundle = getIntent().getExtras();
		viewParams = bundle.getParcelable("viewParams");
		
		testTextEdit = (Button) findViewById(R.id.test_text_edit);
		testTextEdit.setOnClickListener(this);
		
		findViewById(R.id.next_button).setOnClickListener(this);
		findViewById(R.id.next_button).setMinimumWidth((int)viewParams.getButtonWidth());
		findViewById(R.id.next_button).setMinimumHeight((int) viewParams.getButtonHeight());
		
		tts = new TextToSpeech(this, this);
		
		grid = (GridLayout) findViewById(R.id.default_layout);
		
		grid.getChildAt(1).setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				float x = event.getRawX();

				testTextEdit.setTextSize((float) (x / 10.0));
				testTextEdit.invalidate();

				return true;
			}
		});
		
	}

	@Override
	public void onInit(int status) {
		
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.next_button:
			Intent intent = new Intent(this, BrightnessConfigActivity.class);
			
			viewParams.setTextColor(testTextEdit.getTextColors().getDefaultColor());
			viewParams.setTextEditSize(testTextEdit.getTextSize());
			
			intent.putExtra("viewParams", viewParams);
			
			startActivity(intent);
			break;

		default:
			break;
		}
	}


	
}
