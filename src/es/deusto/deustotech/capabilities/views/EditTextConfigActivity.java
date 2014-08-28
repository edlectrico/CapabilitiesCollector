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
	
	private Button btnTextEdit, btnNext, btnBackColor, btnTextColor,
	btnBlackOverWhite, btnWhiteOverBlack;
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

		btnNext = (Button) findViewById(R.id.button_next_et);
		btnBackColor = (Button) findViewById(R.id.button_background_color);
		btnTextColor = (Button) findViewById(R.id.button_text_color);
		btnBlackOverWhite = (Button) findViewById(R.id.button_black_over_white);
		btnWhiteOverBlack = (Button) findViewById(R.id.button_white_over_black);
		
		redrawViews();
		initializeServices(TAG);
		addListeners();
		
		userPrefs.setEditTextBackgroundColor(DEFAULT_BACK_COLOR);
		userPrefs.setTextViewBackgroundColor(DEFAULT_BACK_COLOR);
	}

	@Override
	public void initializeServices(String TAG) {
		if (userPrefs.getDisplayHasApplicable() == 0){
			super.initializeServices(TAG);

			speakOut(getResources().getString(R.string.edit_text_info_message_es));
		}
	}

	@Override
	public void addListeners() {
		grid.setOnTouchListener(onTouchListener);
		
		btnNext.setOnClickListener(this);
		btnBackColor.setOnClickListener(this);
		btnTextColor.setOnClickListener(this);
		btnTextEdit.setOnClickListener(this);
		btnBlackOverWhite.setOnClickListener(this);
		btnWhiteOverBlack.setOnClickListener(this);
	}

	@Override
	public void redrawViews() {
		if (ButtonConfigActivity.getButtonBackgroundColorChanged()){
			btnNext.setBackgroundColor(userPrefs.getButtonBackgroundColor());
			btnBackColor.setBackgroundColor(userPrefs.getButtonBackgroundColor());
			btnTextColor.setBackgroundColor(userPrefs.getButtonBackgroundColor());
			btnBlackOverWhite.setBackgroundColor(userPrefs.getButtonBackgroundColor());
			btnWhiteOverBlack.setBackgroundColor(userPrefs.getButtonBackgroundColor());
		}

		if (ButtonConfigActivity.getLayoutBackgroundColorChanged()){
			grid.setBackgroundColor(userPrefs.getLayoutBackgroundColor());
		}

		btnNext.setMinimumWidth((int)userPrefs.getButtonWidth());
		btnNext.setMinimumHeight((int) userPrefs.getButtonHeight());
		btnNext.setTextColor(userPrefs.getButtonTextColor());
		btnNext.setTextSize(userPrefs.getButtonTextSize() / 2);

		btnBackColor.setMinimumWidth((int)userPrefs.getButtonWidth());
		btnBackColor.setMinimumHeight((int) userPrefs.getButtonHeight());
		btnBackColor.setTextColor(userPrefs.getButtonTextColor());
		btnBackColor.setTextSize(userPrefs.getButtonTextSize() / 2);
		
		btnTextColor.setMinimumWidth((int)userPrefs.getButtonWidth());
		btnTextColor.setMinimumHeight((int) userPrefs.getButtonHeight());
		btnTextColor.setTextColor(userPrefs.getButtonTextColor());
		btnTextColor.setTextSize(userPrefs.getButtonTextSize() / 2);
		
		btnBlackOverWhite.setMinimumWidth((int)userPrefs.getButtonWidth());
		btnBlackOverWhite.setMinimumHeight((int) userPrefs.getButtonHeight());
		btnBlackOverWhite.setTextColor(userPrefs.getButtonTextColor());
		btnBlackOverWhite.setTextSize(userPrefs.getButtonTextSize() / 2);
		
		btnWhiteOverBlack.setMinimumWidth((int)userPrefs.getButtonWidth());
		btnWhiteOverBlack.setMinimumHeight((int) userPrefs.getButtonHeight());
		btnWhiteOverBlack.setTextColor(userPrefs.getButtonTextColor());
		btnWhiteOverBlack.setTextSize(userPrefs.getButtonTextSize() / 2);
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
		if (view.getId() == R.id.button_next_et){
			Intent intent = new Intent(this, BrightnessConfigActivity.class);
			
			userPrefs.setEditTextTextSize(btnTextEdit.getTextSize());
			userPrefs.setTextViewTextSize(btnTextEdit.getTextSize());

			intent.putExtra(getResources().getString(R.string.view_params), userPrefs);
			intent.putExtra(getResources().getString(R.string.activity_caller), 1);
			
			if (edit_backgroundcolor_changed){
				userPrefs.setTextViewBackgroundColor(backgroundColor);
				userPrefs.setEditTextBackgroundColor(backgroundColor);
			}
			
			userPrefs.setEditTextWidth(btnTextEdit.getWidth());
			userPrefs.setEditTextHeight(btnTextEdit.getHeight());
			userPrefs.setEditTextTextSize(btnTextEdit.getTextSize());
			userPrefs.setEditTextTextColor((int) textColor);
			
			userPrefs.setTextViewWidth(userPrefs.getEditTextWidth());
			userPrefs.setTextViewHeight(userPrefs.getEditTextHeight());
			userPrefs.setTextViewTextSize(userPrefs.getEditTextTextSize());
			userPrefs.setTextViewTextColor(userPrefs.getEditTextTextColor());
			
			if (CapabilitiesActivity.getDisplayIsApplicable() == 0){
				speakOut("Well done!");
			}
			startActivity(intent);
		}
		else if (view.getId() == R.id.button_background_color){
			edit_backgroundcolor_changed = true;
			
			Random randomBackColor = new Random(); 
			backgroundColor = Color.argb(255, randomBackColor.nextInt(256), randomBackColor.nextInt(256), randomBackColor.nextInt(256));
			btnTextEdit.setBackgroundColor(backgroundColor);
			userPrefs.setEditTextBackgroundColor(backgroundColor);
			userPrefs.setTextViewBackgroundColor(backgroundColor);
		}
		else if (view.getId() == R.id.button_text_color){
			Random randomTextColor = new Random(); 
			textColor = Color.argb(255, randomTextColor.nextInt(256), randomTextColor.nextInt(256), randomTextColor.nextInt(256));
			btnTextEdit.setTextColor(textColor);
			userPrefs.setEditTextTextColor(textColor);
			userPrefs.setTextViewTextColor(textColor);
		}
		else if (view.getId() == R.id.button_black_over_white){
			btnTextEdit.setTextColor(Color.BLACK);
			btnTextEdit.setBackgroundColor(Color.WHITE);
			userPrefs.setEditTextTextColor(textColor);
			userPrefs.setTextViewTextColor(textColor);
			userPrefs.setEditTextBackgroundColor(backgroundColor);
			userPrefs.setTextViewBackgroundColor(backgroundColor);
		}
		else if (view.getId() == R.id.button_white_over_black){
			btnTextEdit.setTextColor(Color.WHITE);
			btnTextEdit.setBackgroundColor(Color.BLACK);
			userPrefs.setEditTextTextColor(textColor);
			userPrefs.setTextViewTextColor(textColor);
			userPrefs.setEditTextBackgroundColor(backgroundColor);
			userPrefs.setTextViewBackgroundColor(backgroundColor);
		}
	}
	
	public boolean isEditTextTextColorChanged() {
		return editTextTextColorChanged;
	}

}
