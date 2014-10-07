package es.deusto.deustotech.capabilities.views;

import yuku.ambilwarna.AmbilWarnaDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;
import es.deusto.deustotech.R;

public class EditTextConfigActivity extends AbstractActivity {

	private static final String TAG = EditTextConfigActivity.class.getSimpleName();
	
	private Button btnTextEdit, btnNext, btnBackColor, btnTextColor;
	private GridLayout grid;
	private OnTouchListener onTouchListener;
	
	private static final int DEFAULT_BACK_COLOR = Color.WHITE;

	private boolean editTextTextColorChanged = false;
	public static boolean edit_backgroundcolor_changed = false;
	private int lastTextColor, lastBackColor = -1;
	int color = Color.WHITE;
	
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
		
		btnBackColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openDialog(false, v);
			}
		});
		
		btnTextColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openDialog(false, v);
			}
		});
		btnTextEdit.setOnClickListener(this);
	}

	@Override
	public void redrawViews() {
//		if (ButtonConfigActivity.getLayoutBackgroundColorChanged()){
		grid.setBackgroundColor(userPrefs.getLayoutBackgroundColor());
//		}
		
//		if (ButtonConfigActivity.getButtonBackgroundColorChanged()){
//			System.out.println("ButtonColorEditTextConfig: " + userPrefs.getButtonBackgroundColor());
			
		btnNext.setBackgroundColor(userPrefs.getButtonBackgroundColor());
		btnBackColor.setBackgroundColor(userPrefs.getButtonBackgroundColor());
		btnTextColor.setBackgroundColor(userPrefs.getButtonBackgroundColor());
//		}

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
				userPrefs.setTextViewBackgroundColor(lastBackColor);
				userPrefs.setEditTextBackgroundColor(lastBackColor);
			}
			
//			userPrefs.setEditTextWidth(btnTextEdit.getWidth());
//			userPrefs.setEditTextHeight(btnTextEdit.getHeight());
//			userPrefs.setEditTextTextSize(btnTextEdit.getTextSize());
//			userPrefs.setEditTextTextColor((int) lastTextColor);
//			
//			userPrefs.setTextViewWidth(userPrefs.getEditTextWidth());
//			userPrefs.setTextViewHeight(userPrefs.getEditTextHeight());
//			userPrefs.setTextViewTextSize(userPrefs.getEditTextTextSize());
//			userPrefs.setTextViewTextColor(userPrefs.getEditTextTextColor());
			
			if (CapabilitiesActivity.getDisplayIsApplicable() == 0){
				speakOut("Well done!");
			}
			startActivity(intent);
		}
		
//		userPrefs.setEditTextTextColor(lastTextColor);
//		userPrefs.setTextViewTextColor(lastTextColor);
//		userPrefs.setEditTextBackgroundColor(lastBackColor);
//		userPrefs.setTextViewBackgroundColor(lastBackColor);
	}
	
	public boolean isEditTextTextColorChanged() {
		return editTextTextColorChanged;
	}
	
	void openDialog(boolean supportsAlpha, final View v) {
		AmbilWarnaDialog dialog = new AmbilWarnaDialog(EditTextConfigActivity.this, color, supportsAlpha, new AmbilWarnaDialog.OnAmbilWarnaListener() {
			@Override
			public void onOk(AmbilWarnaDialog dialog, int color) {
				EditTextConfigActivity.this.color = color;
				System.out.println(color);
				
				if (v.getId() == R.id.button_background_color){
					edit_backgroundcolor_changed = true;
					lastBackColor = color;
					
					btnTextEdit.setBackgroundColor(lastBackColor);
					userPrefs.setTextViewBackgroundColor(lastBackColor);
					userPrefs.setEditTextBackgroundColor(lastBackColor);
					
				} else if (v.getId() == R.id.button_text_color){
					lastTextColor = color;
					
					btnTextEdit.setTextColor(lastTextColor);
					userPrefs.setTextViewTextColor(lastTextColor);
					userPrefs.setEditTextTextColor(lastTextColor);
				}
			}

			@Override
			public void onCancel(AmbilWarnaDialog dialog) {
//				Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_SHORT).show();
			}
		});
		dialog.show();
	}
	
	void displayColor() {
		Toast.makeText(getApplicationContext(), String.format("Current color: 0x%08x", color), Toast.LENGTH_LONG).show();
	}

}
