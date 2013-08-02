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

public class TextEditConfigActivity extends Activity implements android.view.View.OnClickListener,
		TextToSpeech.OnInitListener {

	private static final String TAG = ButtonConfigActivity.class.getSimpleName();
	private Button testTextEdit;
	private GridLayout grid;
	private AudioManager audioManager = null;
	private TextToSpeech tts;
	private int viewColor;
	
	private OnTouchListener onTouchListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text_edit_config);
		
		this.testTextEdit = (Button) findViewById(R.id.test_text_edit);
		this.testTextEdit.setOnClickListener(this);
		
		this.tts = new TextToSpeech(this, this);
		
		this.audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		this.grid = (GridLayout) findViewById(R.id.default_layout);
		
		this.onTouchListener = createOnTouchListener();
		
		this.grid.getChildAt(0).setOnTouchListener(this.onTouchListener);
		this.grid.getChildAt(1).setOnTouchListener(this.onTouchListener);
		this.testTextEdit.setOnTouchListener(this.onTouchListener);
	}

	public OnTouchListener createOnTouchListener(){
		return new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				final int width  = testTextEdit.getWidth();
				final int height = testTextEdit.getHeight();
				
				testTextEdit.setWidth(width + 10);
				testTextEdit.setHeight(height + 10);
				
				return true;
			}
		};
	}
	
	@Override
	public void onClick(View view) {
		
	}

	@Override
	public void onInit(int status) {
		
	}


	
}
