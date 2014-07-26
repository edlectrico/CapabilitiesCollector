package es.deusto.deustotech.capabilities.views;

import java.util.Random;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.GridLayout;
import es.deusto.deustotech.R;

public class EditTextConfigActivity extends AbstractActivity {

	private static final String TAG = EditTextConfigActivity.class.getSimpleName();
	
	private Button btnTextEdit, btnNext, btnBackColor, btnTextColor;
	private GridLayout grid;
	private OnTouchListener onTouchListener;
	
	private int backgroundColor = 0;
	private int textColor = 0;
	private static final int DEFAULT_BACK_COLOR = Color.WHITE;

	private boolean editTextTextColorChanged = false;
	
	public static boolean edit_backgroundcolor_changed = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edittext_config_activity);

		Bundle bundle = getIntent().getExtras();
		userPrefs = bundle.getParcelable(getResources().getString(R.string.view_params));
		
		btnTextEdit = (Button) findViewById(R.id.button_text_edit);
		grid = (GridLayout) findViewById(R.id.default_layout);
		onTouchListener = createOnTouchListener();

		btnNext = (Button) findViewById(R.id.button_next);
		btnBackColor = (Button) findViewById(R.id.button_background_color);
		btnTextColor = (Button) findViewById(R.id.button_text_color);
		
		redrawViews();
		initializeServices(TAG);
		addListeners();
		
		super.getOntologyManager().addDataTypePropertyValue(super.getEditTexts().get(0), super.getOntologyNamespace() + "viewHasColor", DEFAULT_BACK_COLOR);
		super.getOntologyManager().addDataTypePropertyValue(super.getTextViews().get(0), super.getOntologyNamespace() + "viewHasColor", DEFAULT_BACK_COLOR);
	}

	@Override
	public void initializeServices(String TAG) {
		if (userPrefs.getSightProblem() == 1){
			super.initializeServices(TAG);

			speakOut(getResources().getString(R.string.edit_text_info_message_es));
		}
	}

	@Override
	public void addListeners() {
		grid.getChildAt(1).setOnTouchListener(onTouchListener);
		grid.getChildAt(2).setOnTouchListener(onTouchListener);
		grid.getChildAt(3).setOnTouchListener(onTouchListener);
		
		btnNext.setOnClickListener(this);
		btnBackColor.setOnClickListener(this);
		btnTextColor.setOnClickListener(this);
		btnTextEdit.setOnClickListener(this);
	}

	@Override
	public void redrawViews() {
		if (ButtonConfigActivity.getButtonBackgroundColorChanged()){
			btnNext.setBackgroundColor(userPrefs.getButtonBackgroundColor());
			btnBackColor.setBackgroundColor(userPrefs.getButtonBackgroundColor());
			btnTextColor.setBackgroundColor(userPrefs.getButtonBackgroundColor());
		}

		if (ButtonConfigActivity.getLayoutBackgroundColorChanged()){
			grid.setBackgroundColor(userPrefs.getLayoutBackgroundColor());
		}

		findViewById(R.id.button_next).setMinimumWidth((int)userPrefs.getButtonWidth());
		findViewById(R.id.button_next).setMinimumHeight((int) userPrefs.getButtonHeight());
		btnNext.setTextColor(userPrefs.getButtonTextColor());

		findViewById(R.id.button_background_color).setMinimumWidth((int)userPrefs.getButtonWidth());
		findViewById(R.id.button_background_color).setMinimumHeight((int) userPrefs.getButtonHeight());
		btnBackColor.setTextColor(userPrefs.getButtonTextColor());

		findViewById(R.id.button_text_color).setMinimumWidth((int)userPrefs.getButtonWidth());
		findViewById(R.id.button_text_color).setMinimumHeight((int) userPrefs.getButtonHeight());
		btnTextColor.setTextColor(userPrefs.getButtonTextColor());
	}


	private OnTouchListener createOnTouchListener(){
		return new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x = event.getRawX();

				btnTextEdit.setTextSize((float) (x / 10.0));
				btnTextEdit.invalidate();

				return true;
			}
		};
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.button_next){
			Intent intent = new Intent(this, BrightnessConfigActivity.class);
			
			userPrefs.setEditTextTextSize(btnTextEdit.getTextSize());
			userPrefs.setTextViewTextSize(btnTextEdit.getTextSize());

			intent.putExtra(getResources().getString(R.string.view_params), userPrefs);
			intent.putExtra(getResources().getString(R.string.activity_caller), 1);
			
			if (edit_backgroundcolor_changed){
				super.getOntologyManager().addDataTypePropertyValue(super.getEditTexts().get(0), super.getOntologyNamespace() + "viewHasColor", 	backgroundColor);
				super.getOntologyManager().addDataTypePropertyValue(super.getTextViews().get(0), super.getOntologyNamespace() + "viewHasColor", 	backgroundColor);
				userPrefs.setTextViewBackgroundColor(backgroundColor);
			}
			
			super.getOntologyManager().addDataTypePropertyValue(super.getEditTexts().get(0), super.getOntologyNamespace() + "viewHasWidth", 	btnTextEdit.getWidth());
			super.getOntologyManager().addDataTypePropertyValue(super.getEditTexts().get(0), super.getOntologyNamespace() + "viewHasHeight", 	btnTextEdit.getHeight());
			super.getOntologyManager().addDataTypePropertyValue(super.getEditTexts().get(0), super.getOntologyNamespace() + "viewHasTextSize", btnTextEdit.getTextSize());
			super.getOntologyManager().addDataTypePropertyValue(super.getEditTexts().get(0), super.getOntologyNamespace() + "viewHasTextColor", (int) textColor);
			
			super.getOntologyManager().addDataTypePropertyValue(super.getTextViews().get(0), super.getOntologyNamespace() + "viewHasWidth", 	btnTextEdit.getWidth());
			super.getOntologyManager().addDataTypePropertyValue(super.getTextViews().get(0), super.getOntologyNamespace() + "viewHasHeight", 	btnTextEdit.getHeight());
			super.getOntologyManager().addDataTypePropertyValue(super.getTextViews().get(0), super.getOntologyNamespace() + "viewHasTextSize", btnTextEdit.getTextSize());
			super.getOntologyManager().addDataTypePropertyValue(super.getTextViews().get(0), super.getOntologyNamespace() + "viewHasTextColor", (int) textColor);
			
			System.out.println("TextEditTextSize: " + btnTextEdit.getTextSize());
			
			if (userPrefs.getSightProblem() == 1){
				speakOut("Well done!");
			}
			
			startActivity(intent);
		} else if (view.getId() == R.id.button_background_color){
			edit_backgroundcolor_changed = true;
			
			Random randomBackColor = new Random(); 
			backgroundColor = Color.argb(255, randomBackColor.nextInt(256), randomBackColor.nextInt(256), randomBackColor.nextInt(256));
			((Button)findViewById(R.id.button_text_edit)).setBackgroundColor(backgroundColor);
			userPrefs.setTextEditBackgroundColor(backgroundColor);
			userPrefs.setTextViewBackgroundColor(backgroundColor);

		} else if (view.getId() == R.id.button_text_color){
			Random randomTextColor = new Random(); 
			textColor = Color.argb(255, randomTextColor.nextInt(256), randomTextColor.nextInt(256), randomTextColor.nextInt(256));
			((Button)findViewById(R.id.button_text_edit)).setTextColor(textColor);
			userPrefs.setEditTextTextColor(textColor);
			userPrefs.setTextViewTextColor(textColor);
		}
	}
	
	public boolean isEditTextTextColorChanged() {
		return editTextTextColorChanged;
	}

}
