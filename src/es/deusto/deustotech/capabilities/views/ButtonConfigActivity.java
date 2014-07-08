package es.deusto.deustotech.capabilities.views;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.semanticweb.owlapi.model.OWLLiteral;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.GridLayout;
import es.deusto.deustotech.R;
import es.deusto.deustotech.capabilities.UserMinimumPreferences;

/**
 * This activity configures the minimum visual interaction values
 * 
 * @author edlectrico
 * 
 */
public class ButtonConfigActivity extends AbstractActivity {

	private static final int VIEW_MAX_SIZE = 500;

	private static final String TAG = ButtonConfigActivity.class.getSimpleName();

	private static List<String> buttons;
	private static List<String> backgrounds;
	
	private Button btnResize;
	private Button btnBackgroundColor;
	private Button btnColorButton;
	private Button btnTextColor;
	private GridLayout grid;
	
	private int maxWidth = 0;
	private int maxHeight = 0;
	private int buttonBackgroundColor = 0;
	private int textColor = 0;
	private int layoutBackgroundColor = 0;
	private int defaultButtonColor;
	private static final int DEFAULT_BACK_COLOR = Color.WHITE;

	private OnTouchListener onTouchListener;

	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Rect mBounds;
	
	int callerActivity = -1;

	public static boolean layout_backgroundcolor_changed = false;
	public static boolean button_backgroundcolor_changed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.button_config_activity);
		
		drawButtons();

		Bundle bundle = getIntent().getExtras();
		userPrefs = new UserMinimumPreferences();
		callerActivity = bundle.getInt(getResources().getString(R.string.activity_caller));

		if (callerActivity != 2){ //2: VolumeActivity
			userPrefs.setSightProblem(bundle.getInt(getResources().getString(R.string.visual_impairment)));
			userPrefs.setHearingProblem(bundle.getInt(getResources().getString(R.string.hearing_impairment)));
			
			userPrefs.setButtonBackgroundColor(getBackgroundColor(btnResize));
			userPrefs.setButtonTextColor(btnResize.getTextColors().getDefaultColor());
		} else {
			speakOut(getResources().getString(R.string.button_info_message_es));
		}

		grid = (GridLayout) findViewById(R.id.default_layout);
		onTouchListener = createOnTouchListener();
		
		initializeServices(TAG);
		addListeners();
		
		//Assigning default back colors to avoid null/black configuration if no color is selected
		buttons = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Button");
		backgrounds = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Background");

		if (super.getOntologyManager().getDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasColor").size() > 0){
			super.getOntologyManager().deleteAllValuesOfProperty(buttons.get(0), super.getOntologyNamespace() + "viewHasColor");
		}
		if (super.getOntologyManager().getDataTypePropertyValue(backgrounds.get(0), super.getOntologyNamespace() + "viewHasColor").size() > 0){
			super.getOntologyManager().deleteAllValuesOfProperty(backgrounds.get(0), super.getOntologyNamespace() + "viewHasColor");
		}
		
		super.getOntologyManager().addDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasColor", defaultButtonColor);
		super.getOntologyManager().addDataTypePropertyValue(backgrounds.get(0), super.getOntologyNamespace() + "viewHasColor", DEFAULT_BACK_COLOR);
	}

	private void drawButtons() {
		btnResize = (Button) findViewById(R.id.button_resize);
		textColor = btnResize.getTextColors().getDefaultColor();
		btnBackgroundColor = (Button) findViewById(R.id.button_background_color);
		btnTextColor = (Button) findViewById(R.id.button_text_color);
		btnColorButton = (Button) findViewById(R.id.button_color);
		
		btnBackgroundColor.setVisibility(View.INVISIBLE);
		btnTextColor.setVisibility(View.INVISIBLE);
		btnColorButton.setVisibility(View.INVISIBLE);
		
		defaultButtonColor = getBackgroundColor(btnResize);
	}

	@Override
	public void initializeServices(String TAG) {
		//If blind user, voice control
		if (userPrefs.getSightProblem() == 1){
			super.initializeServices(TAG);
			speakOut(getResources().getString(R.string.button_info_message_es));
		}
	}

	@Override
	public void addListeners() {
		grid.setOnTouchListener(onTouchListener);
		grid.getChildAt(0).setOnTouchListener(onTouchListener);
		grid.getChildAt(1).setOnTouchListener(onTouchListener);
		grid.getChildAt(2).setOnTouchListener(onTouchListener);
		grid.getChildAt(3).setOnTouchListener(onTouchListener);

		findViewById(R.id.button_next).setOnClickListener(this);
		findViewById(R.id.button_background_color).setOnClickListener(this);
		findViewById(R.id.button_text_color).setOnClickListener(this);
		findViewById(R.id.button_color).setOnClickListener(this);
		btnResize.setOnClickListener(this);
	}

	@Override
	public void redrawViews() {
		findViewById(R.id.button_next).setMinimumWidth(btnResize.getWidth());
		findViewById(R.id.button_next).setMinimumHeight(btnResize.getHeight());
		findViewById(R.id.button_background_color).setMinimumWidth(btnResize.getWidth());
		findViewById(R.id.button_background_color).setMinimumHeight(btnResize.getHeight());
		findViewById(R.id.button_text_color).setMinimumWidth(btnResize.getWidth());
		findViewById(R.id.button_text_color).setMinimumHeight(btnResize.getHeight());
		findViewById(R.id.button_color).setMinimumWidth(btnResize.getWidth());
		findViewById(R.id.button_color).setMinimumHeight(btnResize.getHeight());
	}


	private OnTouchListener createOnTouchListener(){
		return new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				final int width  = btnResize.getWidth();
				final int height = btnResize.getHeight();
				//TODO: And the Text size?
				if ((view.getWidth() > maxWidth) && (view.getHeight() > maxHeight)){
					if (btnResize.getWidth() < VIEW_MAX_SIZE) {
						btnResize.setWidth(width + 10);
						btnResize.setHeight(height + 10);
					}
				}
				redrawViews();
				return true;
			}
		};
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.button_next) {
			//TODO: next activity for configuring TextEdit size and color
			if (userPrefs.getSightProblem() == 1){
				speakOut("Well done!");
			}
			Intent intent = new Intent(this, EditTextConfigActivity.class);

			userPrefs.setButtonWidth(btnResize.getWidth());
			userPrefs.setButtonHeight(btnResize.getHeight());
			userPrefs.setButtonTextColor(textColor);
			intent.putExtra(getResources().getString(R.string.activity_caller), 1);

			intent.putExtra(getResources().getString(R.string.view_params), userPrefs);
			
			removePreviousValuesFromOntology();
			
			//Store in the ontology
			if (button_backgroundcolor_changed){
				if (super.getOntologyManager().getDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasColor").size() > 0){
					super.getOntologyManager().deleteAllValuesOfProperty(buttons.get(0), super.getOntologyNamespace() + "viewHasColor");
				}
				super.getOntologyManager().addDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasColor", buttonBackgroundColor);
				userPrefs.setButtonBackgroundColor(getBackgroundColor(btnResize));
			}
			
			if (layout_backgroundcolor_changed ){
				if (super.getOntologyManager().getDataTypePropertyValue(backgrounds.get(0), super.getOntologyNamespace() + "viewHasColor").size() > 0){
					super.getOntologyManager().deleteAllValuesOfProperty(backgrounds.get(0), super.getOntologyNamespace() + "viewHasColor");
				}
				super.getOntologyManager().addDataTypePropertyValue(backgrounds.get(0), super.getOntologyNamespace() + "viewHasColor", layoutBackgroundColor);
				userPrefs.setLayoutBackgroundColor(layoutBackgroundColor);
			}
			
			super.getOntologyManager().addDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasWidth", btnResize.getWidth());
			super.getOntologyManager().addDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasHeight", btnResize.getHeight());
			super.getOntologyManager().addDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasTextColor", textColor);
			super.getOntologyManager().addDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasTextSize", btnResize.getTextSize());

			checkOntology();
			startActivity(intent);
		}				
		else if (view.getId() == R.id.button_color){
			button_backgroundcolor_changed = true;
			
			Random randomBackColor = new Random(); 
			buttonBackgroundColor = Color.argb(255, randomBackColor.nextInt(256), randomBackColor.nextInt(256), randomBackColor.nextInt(256));
			btnResize.setBackgroundColor(buttonBackgroundColor);
			findViewById(R.id.button_next).setBackgroundColor(getBackgroundColor(this.btnResize));
			findViewById(R.id.button_background_color).setBackgroundColor(buttonBackgroundColor);
			findViewById(R.id.button_text_color).setBackgroundColor(buttonBackgroundColor);
			findViewById(R.id.button_color).setBackgroundColor(buttonBackgroundColor);
		} else if (view.getId() == R.id.button_text_color){
			Random randomTextColor = new Random(); 
			int textColor = Color.argb(255, randomTextColor.nextInt(256), randomTextColor.nextInt(256), randomTextColor.nextInt(256));   
			btnResize.setTextColor(textColor);
			this.textColor = textColor;
			((Button)findViewById(R.id.button_next)).setTextColor(textColor);
			((Button)findViewById(R.id.button_background_color)).setTextColor(textColor);
			((Button)findViewById(R.id.button_text_color)).setTextColor(textColor);
			((Button)findViewById(R.id.button_color)).setTextColor(textColor);
		} else if (view.getId() == R.id.button_background_color){
			layout_backgroundcolor_changed = true;
			
			Random randomColor = new Random(); 
			layoutBackgroundColor = Color.argb(255, randomColor.nextInt(256), randomColor.nextInt(256), randomColor.nextInt(256));   
			grid.setBackgroundColor(layoutBackgroundColor);
			Log.d(ButtonConfigActivity.class.getSimpleName(), "BackgroundColor: " + layoutBackgroundColor);
		} else if (view.getId() == R.id.button_resize){
			btnTextColor.setVisibility(View.VISIBLE);
			btnBackgroundColor.setVisibility(View.VISIBLE);
			btnColorButton.setVisibility(View.VISIBLE);
		}
	}

	private void removePreviousValuesFromOntology() {
		super.getOntologyManager().deleteAllValuesOfProperty(buttons.get(0), super.getOntologyNamespace() + "viewHasHeight");
		super.getOntologyManager().deleteAllValuesOfProperty(buttons.get(0), super.getOntologyNamespace() + "viewHasWidth");
		super.getOntologyManager().deleteAllValuesOfProperty(buttons.get(0), super.getOntologyNamespace() + "viewHasTextColor");
		super.getOntologyManager().deleteAllValuesOfProperty(buttons.get(0), super.getOntologyNamespace() + "viewHasTextSize");
//		super.getOntologyManager().deleteAllValuesOfProperty(buttons.get(0), super.getOntologyNamespace() + "viewHasColor");
		
//		super.getOntologyManager().deleteAllValuesOfProperty(backgrounds.get(0), super.getOntologyNamespace() + "viewHasColor");
	}

	private void checkOntology() {
		buttons = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Button");
		backgrounds = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Background");
		
		final Collection<OWLLiteral> width 		= super.getOntologyManager().getDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasWidth");
		final Collection<OWLLiteral> height 	= super.getOntologyManager().getDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasHeight");
		final Collection<OWLLiteral> btnBackColor = super.getOntologyManager().getDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasColor");
		final Collection<OWLLiteral> textColor 	= super.getOntologyManager().getDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasTextColor");
		final Collection<OWLLiteral> textSize 	= super.getOntologyManager().getDataTypePropertyValue(buttons.get(0), super.getOntologyNamespace() + "viewHasTextSize");
		final Collection<OWLLiteral> backColor 	= super.getOntologyManager().getDataTypePropertyValue(backgrounds.get(0), super.getOntologyNamespace() + "viewHasColor");
		
		System.out.println("checkOntology(): " 	+ TAG);
		System.out.println("width: " 			+ width);
		System.out.println("heigth: " 			+ height);
		System.out.println("buttonBackColor: " 	+ btnBackColor);
		System.out.println("textColor: " 		+ textColor);
		System.out.println("textSize: " 		+ textSize);
		System.out.println("backColor: " 		+ backColor);
	}

	private int getBackgroundColor(View view) {
		// The actual color, not the id.
		int color = Color.BLACK;

		if(view.getBackground() instanceof ColorDrawable) {
			if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				initIfNeeded();
				// If the ColorDrawable makes use of its bounds in the draw method,
				// we may not be able to get the color we want. This is not the usual
				// case before Ice Cream Sandwich (4.0.1 r1).
				// Yet, we change the bounds temporarily, just to be sure that we are
				// successful.
				ColorDrawable colorDrawable = (ColorDrawable)view.getBackground();

				mBounds.set(colorDrawable.getBounds()); // Save the original bounds.
				colorDrawable.setBounds(0, 0, 1, 1); // Change the bounds.

				colorDrawable.draw(mCanvas);
				color = mBitmap.getPixel(0, 0);

				colorDrawable.setBounds(mBounds); // Restore the original bounds.
			}
			else {
				color = ((ColorDrawable)view.getBackground()).getColor();
			}
		}
		return color;
	}

	private void initIfNeeded() {
		if(mBitmap == null) {
			mBitmap = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);
			mBounds = new Rect();
		}
	}
	
}
