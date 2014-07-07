package es.deusto.deustotech.capabilities.views;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.semanticweb.owlapi.model.OWLLiteral;

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
	
	private static List<String> edits, textViews;

	private Button btnTextEdit;
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
		userPrefs = bundle.getParcelable("viewParams");
		
		btnTextEdit = (Button) findViewById(R.id.button_text_edit);
		grid = (GridLayout) findViewById(R.id.default_layout);
		onTouchListener = createOnTouchListener();

		redrawViews();
		initializeServices(TAG);
		addListeners();
		
		edits = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "EditText");
		textViews = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "TextView");
		
		super.getOntologyManager().addDataTypePropertyValue(edits.get(0), super.getOntologyNamespace() + "viewHasColor", DEFAULT_BACK_COLOR);
		super.getOntologyManager().addDataTypePropertyValue(textViews.get(0), super.getOntologyNamespace() + "viewHasColor", DEFAULT_BACK_COLOR);
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

		findViewById(R.id.button_next).setOnClickListener(this);
		findViewById(R.id.button_background_color).setOnClickListener(this);
		findViewById(R.id.button_text_color).setOnClickListener(this);
		btnTextEdit.setOnClickListener(this);
	}

	@Override
	public void redrawViews() {
		if (ButtonConfigActivity.button_backgroundcolor_changed){
			((Button)findViewById(R.id.button_next)).setBackgroundColor(userPrefs.getButtonBackgroundColor());
			((Button)findViewById(R.id.button_background_color)).setBackgroundColor(userPrefs.getButtonBackgroundColor());
			((Button)findViewById(R.id.button_text_color)).setBackgroundColor(userPrefs.getButtonBackgroundColor());
		}

		if (ButtonConfigActivity.layout_backgroundcolor_changed){
			grid.setBackgroundColor(userPrefs.getLayoutBackgroundColor());
		}
		
		findViewById(R.id.button_next).setMinimumWidth((int)userPrefs.getButtonWidth());
		findViewById(R.id.button_next).setMinimumHeight((int) userPrefs.getButtonHeight());
		((Button)findViewById(R.id.button_next)).setTextColor(userPrefs.getButtonTextColor());

		findViewById(R.id.button_background_color).setMinimumWidth((int)userPrefs.getButtonWidth());
		findViewById(R.id.button_background_color).setMinimumHeight((int) userPrefs.getButtonHeight());
		((Button)findViewById(R.id.button_background_color)).setTextColor(userPrefs.getButtonTextColor());

		findViewById(R.id.button_text_color).setMinimumWidth((int)userPrefs.getButtonWidth());
		findViewById(R.id.button_text_color).setMinimumHeight((int) userPrefs.getButtonHeight());
		((Button)findViewById(R.id.button_text_color)).setTextColor(userPrefs.getButtonTextColor());
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
			
			userPrefs.setTextEditSize(btnTextEdit.getTextSize());
			userPrefs.setTextViewTextSize(btnTextEdit.getTextSize());

			intent.putExtra("viewParams", userPrefs);
			intent.putExtra("caller", 1);
			
			removePreviousValuesFromOntology();
			
			if (edit_backgroundcolor_changed){
				super.getOntologyManager().addDataTypePropertyValue(edits.get(0), super.getOntologyNamespace() + "viewHasColor", 	backgroundColor);
				super.getOntologyManager().addDataTypePropertyValue(textViews.get(0), super.getOntologyNamespace() + "viewHasColor", 	backgroundColor);
				userPrefs.setTextViewBackgroundColor(backgroundColor);
			}
			
			super.getOntologyManager().addDataTypePropertyValue(edits.get(0), super.getOntologyNamespace() + "viewHasHeight", 	btnTextEdit.getHeight());
			super.getOntologyManager().addDataTypePropertyValue(edits.get(0), super.getOntologyNamespace() + "viewHasTextSize", btnTextEdit.getTextSize());
			super.getOntologyManager().addDataTypePropertyValue(edits.get(0), super.getOntologyNamespace() + "viewHasTextColor", (int) textColor);
			
			super.getOntologyManager().addDataTypePropertyValue(textViews.get(0), super.getOntologyNamespace() + "viewHasTextColor", (int) textColor);
			super.getOntologyManager().addDataTypePropertyValue(textViews.get(0), super.getOntologyNamespace() + "viewHasTextSize", btnTextEdit.getTextSize());
			
			System.out.println("TextEditTextSize: " + btnTextEdit.getTextSize());
			
			if (userPrefs.getSightProblem() == 1){
				speakOut("Well done!");
			}
			
			checkOntology();
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
			userPrefs.setTextEditTextColor(textColor);
			userPrefs.setTextViewTextColor(textColor);
		}
	}
	
	private void removePreviousValuesFromOntology() {
		super.getOntologyManager().deleteAllValuesOfProperty(edits.get(0), super.getOntologyNamespace() + "viewHasHeight");
		super.getOntologyManager().deleteAllValuesOfProperty(edits.get(0), super.getOntologyNamespace() + "viewHasWidth");
		super.getOntologyManager().deleteAllValuesOfProperty(edits.get(0), super.getOntologyNamespace() + "viewHasColor");
		super.getOntologyManager().deleteAllValuesOfProperty(edits.get(0), super.getOntologyNamespace() + "viewHasTextColor");
		super.getOntologyManager().deleteAllValuesOfProperty(edits.get(0), super.getOntologyNamespace() + "viewHasTextSize");
		
		super.getOntologyManager().deleteAllValuesOfProperty(textViews.get(0), super.getOntologyNamespace() + "viewHasHeight");
		super.getOntologyManager().deleteAllValuesOfProperty(textViews.get(0), super.getOntologyNamespace() + "viewHasWidth");
		super.getOntologyManager().deleteAllValuesOfProperty(textViews.get(0), super.getOntologyNamespace() + "viewHasColor");
		super.getOntologyManager().deleteAllValuesOfProperty(textViews.get(0), super.getOntologyNamespace() + "viewHasTextColor");
		super.getOntologyManager().deleteAllValuesOfProperty(textViews.get(0), super.getOntologyNamespace() + "viewHasTextSize");
		
	}

	private void checkOntology() {
		final List<String> editTexts = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "EditText");
		final List<String> textViews = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "TextView");
		
		final Collection<OWLLiteral> editHeight 	= super.getOntologyManager().getDataTypePropertyValue(editTexts.get(0), super.getOntologyNamespace() + "viewHasHeight");
		final Collection<OWLLiteral> editBackColor 	= super.getOntologyManager().getDataTypePropertyValue(editTexts.get(0), super.getOntologyNamespace() + "viewHasColor");
		final Collection<OWLLiteral> editTextColor 	= super.getOntologyManager().getDataTypePropertyValue(editTexts.get(0), super.getOntologyNamespace() + "viewHasTextColor");
		final Collection<OWLLiteral> editTextSize 	= super.getOntologyManager().getDataTypePropertyValue(editTexts.get(0), super.getOntologyNamespace() + "viewHasTextSize");
		
		System.out.println("checkOntology(): " 	+ TAG);
		System.out.println("editHeight: " 		+ editHeight);
		System.out.println("editBackColor: " 	+ editBackColor);
		System.out.println("editTextColor: " 	+ editTextColor);
		System.out.println("editTextSize: " 	+ editTextSize);
		
		final Collection<OWLLiteral> textViewBackColor 	= super.getOntologyManager().getDataTypePropertyValue(textViews.get(0), super.getOntologyNamespace() + "viewHasColor");
		final Collection<OWLLiteral> textViewTextColor 	= super.getOntologyManager().getDataTypePropertyValue(textViews.get(0), super.getOntologyNamespace() + "viewHasTextColor");
		final Collection<OWLLiteral> textViewTextSize 	= super.getOntologyManager().getDataTypePropertyValue(textViews.get(0), super.getOntologyNamespace() + "viewHasTextSize");
		
		System.out.println("textViewBackColor: " 	+ textViewBackColor);
		System.out.println("textViewTextColor: " 	+ textViewTextColor);
		System.out.println("textViewTextSize: " 	+ textViewTextSize);
	}

	public boolean isEditTextTextColorChanged() {
		return editTextTextColorChanged;
	}

}
