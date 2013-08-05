package es.deusto.deustotech.views;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.GridLayout;
import es.deusto.deustotech.R;

public class TextEditConfigActivity extends Activity implements TextToSpeech.OnInitListener {

	private static final String TAG = ButtonConfigActivity.class.getSimpleName();
	private Button testTextEdit;
	private GridLayout grid;
	private AudioManager audioManager = null;
	private TextToSpeech tts;
	private int viewColor;
	
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text_edit_config);
		
		this.testTextEdit = (Button) findViewById(R.id.test_text_edit);
		
		this.tts = new TextToSpeech(this, this);
		
		this.audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		this.grid = (GridLayout) findViewById(R.id.default_layout);
		
		this.grid.getChildAt(1).setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				float x = event.getRawX();

				testTextEdit.setTextSize((float) (x / 10.0));
				testTextEdit.invalidate();

				return true;
			}
		});
		
		this.context = this.getApplicationContext();
	}

	

	@Override
	public void onInit(int status) {
		
	}


	
}
