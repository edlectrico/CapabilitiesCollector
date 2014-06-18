package es.deusto.deustotech.capabilities.views;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.semanticweb.owlapi.model.OWLLiteral;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.GridLayout;
import es.deusto.deustotech.R;

@SuppressLint("NewApi")
public class EditTextConfigActivity extends AbstractActivity {

	private static final String TAG = EditTextConfigActivity.class.getSimpleName();
	
//	private OntologyManager ontManager;
	private static List<String> edits;

	private Button testTextEdit;
	private GridLayout grid;
	private OnTouchListener onTouchListener;
	
	private int backgroundColor = 0;
	private int textColor = 0;

	private boolean editTextTextColorChanged = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_text_config);

//		ontManager = super.getOntologyManager();
		
		Bundle bundle = getIntent().getExtras();
		userPrefs = bundle.getParcelable("viewParams");
		
		testTextEdit = (Button) findViewById(R.id.test_text_edit);
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

	@SuppressLint("NewApi")
	@Override
	public void addListeners() {
		grid.getChildAt(1).setOnTouchListener(onTouchListener);
		grid.getChildAt(2).setOnTouchListener(onTouchListener);
		grid.getChildAt(3).setOnTouchListener(onTouchListener);

		findViewById(R.id.next_button).setOnClickListener(this);
		findViewById(R.id.background_color_button).setOnClickListener(this);
		findViewById(R.id.text_color_button).setOnClickListener(this);
		testTextEdit.setOnClickListener(this);
	}

	@Override
	public void redrawViews() {
		findViewById(R.id.next_button).setMinimumWidth((int)userPrefs.getButtonWidth());
		findViewById(R.id.next_button).setMinimumHeight((int) userPrefs.getButtonHeight());
		((Button)findViewById(R.id.next_button)).setTextColor(userPrefs.getButtonTextColor());

		findViewById(R.id.background_color_button).setMinimumWidth((int)userPrefs.getButtonWidth());
		findViewById(R.id.background_color_button).setMinimumHeight((int) userPrefs.getButtonHeight());
		((Button)findViewById(R.id.background_color_button)).setTextColor(userPrefs.getButtonTextColor());

		findViewById(R.id.text_color_button).setMinimumWidth((int)userPrefs.getButtonWidth());
		findViewById(R.id.text_color_button).setMinimumHeight((int) userPrefs.getButtonHeight());
		((Button)findViewById(R.id.text_color_button)).setTextColor(userPrefs.getButtonTextColor());

		grid.setBackgroundColor(userPrefs.getBackgroundColor());

		if (userPrefs.getButtonBackgroundColor() != 0){
			((Button)findViewById(R.id.next_button)).setBackgroundColor(userPrefs.getButtonBackgroundColor());
			((Button)findViewById(R.id.background_color_button)).setBackgroundColor(userPrefs.getButtonBackgroundColor());
			((Button)findViewById(R.id.text_color_button)).setBackgroundColor(userPrefs.getButtonBackgroundColor());
		}
	}

	private OnTouchListener createOnTouchListener(){
		return new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x = event.getRawX();

				testTextEdit.setTextSize((float) (x / 10.0));
				testTextEdit.invalidate();

				return true;
			}
		};
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.next_button){
			Intent intent = new Intent(this, BrightnessConfigActivity.class);

//			if (editTextTextColorChanged){
//				editTextTextColorChanged = false;
//			} else {
//				userPrefs.setTextEditTextColor(0);
//			}
			//				userPrefs.setTextEditTextColor(testTextEdit.getTextColors().getDefaultColor());
			userPrefs.setTextEditSize(testTextEdit.getTextSize());

			intent.putExtra("viewParams", userPrefs);
			
			edits = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "EditText");
			super.getOntologyManager().addDataTypePropertyValue(edits.get(0), super.getOntologyNamespace() + "viewHasHeight", 	testTextEdit.getHeight());
			super.getOntologyManager().addDataTypePropertyValue(edits.get(0), super.getOntologyNamespace() + "viewHasTextSize", testTextEdit.getTextSize());
			super.getOntologyManager().addDataTypePropertyValue(edits.get(0), super.getOntologyNamespace() + "viewHasColor", 	backgroundColor);
			super.getOntologyManager().addDataTypePropertyValue(edits.get(0), super.getOntologyNamespace() + "viewHasTextColor", (int) textColor);
			
			if (userPrefs.getSightProblem() == 1){
				speakOut("Well done!");
			}
			
			checkOntology();
			startActivity(intent);
		} else if (view.getId() == R.id.background_color_button){
			setEditTextTextColorChanged(true);
			Random randomBackColor = new Random(); 
			backgroundColor = Color.argb(255, randomBackColor.nextInt(256), randomBackColor.nextInt(256), randomBackColor.nextInt(256));
			//				testTextEdit.setBackgroundColor(backgroundColor);
			((Button)findViewById(R.id.test_text_edit)).setBackgroundColor(backgroundColor);
			userPrefs.setTextEditBackgroundColor(backgroundColor);

		} else if (view.getId() == R.id.text_color_button){
			setEditTextTextColorChanged(true);
			Random randomTextColor = new Random(); 
			textColor = Color.argb(255, randomTextColor.nextInt(256), randomTextColor.nextInt(256), randomTextColor.nextInt(256));
			//				testTextEdit.setTextColor(textColor);
			((Button)findViewById(R.id.test_text_edit)).setTextColor(textColor);
			userPrefs.setTextEditTextColor(textColor);
		}
	}
	
	private void checkOntology() {
		final List<String> buttons = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "EditText");
		
//		final Collection<OWLLiteral> width 		= super.getOntologyManager().getDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasWidth");
		final Collection<OWLLiteral> heigth 	= super.getOntologyManager().getDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasHeigth");
		final Collection<OWLLiteral> backColor 	= super.getOntologyManager().getDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasColor");
		final Collection<OWLLiteral> textColor 	= super.getOntologyManager().getDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasTextColor");
		final Collection<OWLLiteral> textSize 	= super.getOntologyManager().getDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasTextSize");
		
		System.out.println("checkOntology(): " 	+ TAG);
//		System.out.println("width: " 		+ width);
		System.out.println("heigth: " 		+ heigth);
		System.out.println("backColor: " 	+ backColor);
		System.out.println("textColor: " 	+ textColor);
		System.out.println("textSize: " 	+ textSize);
	}

	public boolean isEditTextTextColorChanged() {
		return editTextTextColorChanged;
	}

	public void setEditTextTextColorChanged(boolean editTextTextColorChanged) {
		this.editTextTextColorChanged = editTextTextColorChanged;
	}

}
