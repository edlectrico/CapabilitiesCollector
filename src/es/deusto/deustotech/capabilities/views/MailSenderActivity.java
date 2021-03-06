package es.deusto.deustotech.capabilities.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLLiteral;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import es.deusto.deustotech.R;
import es.deusto.deustotech.capabilities.UserInteraction;
import es.deusto.deustotech.capabilities.UserMinimumPreferences;

/**
 * @author edlectrico
 * 
 *         This Activity gathers the UI configuration from the previous bundles
 *         and shows a builds a user interaction profile through a email
 *         application capturing the total time to do this task, clicks, device
 *         orientation variations, etc.
 */

public class MailSenderActivity extends AbstractActivity implements OnFocusChangeListener {

	private static final String TAG = MailSenderActivity.class.getSimpleName();
	private static final int ALPHA = 255;
	private static final int DEFAULT_BUTTON_COLOR = -16777216;

	private Button buttonSend;
	private EditText textTo, textSubject, textMessage;
	private TextView textViewPhoneNo, textViewSubject, textViewMessage, textViewInstructions;
	
	private LinearLayout layout;

	private long elapsedTime;

	private boolean firstClick = false;
	private boolean isEditText = false;

	private long timeToFillEditText = 0;
	private long launchedAt = 0;
	private long timeToStartTask = 0;
	private long timeToFinishTask = 0;
	private int lostClicks = 0; // Amount of clicks that do not matter for the
	// interaction
	private int editTextClicks = 0;
	private int buttonClicks = 0; // Amount of clicks trying to push a button

	private long focusStarted = 0;
	private long focusElapsedTime = 0;
	private int focusCounter = 0;
	private int editTextCounter = 0;

	private Bundle bundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mail_sender_activity);
		
		if (super.tts != null){
			super.tts.stop();
		}
		
		buttonSend = (Button) findViewById(R.id.buttonSend);
		textViewInstructions = (TextView) findViewById(R.id.textViewEmailInstructions);
		textTo = (EditText) findViewById(R.id.editTextTo);
		textSubject = (EditText) findViewById(R.id.editTextSubject);
		textMessage = (EditText) findViewById(R.id.editTextMessage);
		textViewMessage = (TextView) findViewById(R.id.textViewMessage);
		textViewSubject = (TextView) findViewById(R.id.textViewSubject);
		textViewPhoneNo = (TextView) findViewById(R.id.textViewPhoneNo);
		layout = (LinearLayout) findViewById(R.id.linearLayout0);
		

		bundle = getIntent().getExtras();
		userPrefs = bundle.getParcelable(getResources().getString(R.string.view_params));
		
		redrawViews();
		initializeServices(TAG);
		addListeners();
		initOntology();
		
		final List<String> items = new ArrayList<String>();
		
		final Collection<OWLLiteral> contextAuxHasLightLevel = getOntologyManager().getDataTypePropertyValue(getContexts().get(0), getOntologyNamespace() + "contextAuxHasLightLevel");
		for (OWLLiteral brightness : contextAuxHasLightLevel){
			items.add(String.valueOf(((OWLLiteral) brightness).getLiteral()));
		}
		
		Spinner spinner = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { 
		    	String[] spin = items.toArray(new String[items.size()]);
		    	System.out.println("Selected: " + spin[i]);
		    	
		    	Gson gson = new Gson();
		    	
		    	String selectedJSON = searchAdaptation(spin[i]);
		    	System.out.println("SelectedJSON: " + selectedJSON);
		    	
		    	if (selectedJSON.length() > 0){
		    		JSONObject json;
					try {
						json = new JSONObject(selectedJSON);
						userPrefs = gson.fromJson(json.toString(), UserMinimumPreferences.class);
						System.out.println(userPrefs.toString());
						redrawViews();
					} catch (JSONException e) {
						e.printStackTrace();
					}
		    	}
		    } 
		    
			public void onNothingSelected(AdapterView<?> adapterView) {
		        return;
		    } 
		}); 
	}
	
	private String searchAdaptation(final String selectedContextLight) {
		final Collection<OWLLiteral> contextJSONS = getOntologyManager().getDataTypePropertyValue(getContextJSON().get(0), getOntologyNamespace() + "contextJSONHasValue");
		
		for (OWLLiteral json : contextJSONS){
			if ((String.valueOf(((OWLLiteral) json).getLiteral()).contains(selectedContextLight))){
				return String.valueOf(((OWLLiteral) json).getLiteral());
			}
		}
		
		return "";
	}

	@Override
	public void initializeServices(String TAG) {
		System.out.println("initializeServices()");
		launchedAt = System.currentTimeMillis();
		
		if (userPrefs.getDisplayHasApplicable() == 0){
			super.initializeServices(TAG);

			speakOut(getResources().getString(R.string.edit_text_info_message_es));
		}
		
		if (userPrefs.getBrightness() != 0){
			System.out.println("Brightness: " + userPrefs.getBrightness());
			WindowManager.LayoutParams layoutParams = getWindow()
					.getAttributes();
			layoutParams.screenBrightness = userPrefs.getBrightness();
			getWindow().setAttributes(layoutParams);
		}
	}
	
	@Override
	public void redrawViews() {
		initializeServices(TAG);
		
		if ((ButtonConfigActivity.getLayoutBackgroundColorChanged()) || (userPrefs.getLayoutBackgroundColor() != 0)){
			final int layoutBackgroundColor = userPrefs.getLayoutBackgroundColor();
			final int redBackgroundColor = Color.red(layoutBackgroundColor);
			final int greenBackgroundColor = Color.green(layoutBackgroundColor);
			final int blueBackgroundColor = Color.blue(layoutBackgroundColor);
			layout.setBackgroundColor(Color.argb(ALPHA, redBackgroundColor, greenBackgroundColor, blueBackgroundColor));
		}
		/*ButtonSend*/
		buttonSend.setWidth((int) userPrefs.getButtonWidth());
		buttonSend.setHeight((int) userPrefs.getButtonHeight());
		buttonSend.setTextSize(userPrefs.getButtonTextSize() / 2);

		if ((ButtonConfigActivity.getButtonBackgroundColorChanged()) || (userPrefs.getButtonBackgroundColor() != DEFAULT_BUTTON_COLOR)) { 
			final int buttonBackgroundColor = userPrefs.getButtonBackgroundColor();
			final int redButtonBackgroundColor = Color.red(buttonBackgroundColor);
			final int greenButtonBackgroundColor = Color.green(buttonBackgroundColor);
			final int blueButtonBackgroundColor = Color.blue(buttonBackgroundColor);
			buttonSend.setBackgroundColor(Color.argb(ALPHA, redButtonBackgroundColor, greenButtonBackgroundColor, blueButtonBackgroundColor));
		}

		if (userPrefs.getButtonTextColor() != DEFAULT_BUTTON_COLOR){
			final int buttonTextColor = userPrefs.getButtonTextColor();
			final int redTextColor = Color.red(buttonTextColor);
			final int greenTextColor = Color.green(buttonTextColor);
			final int blueTextColor = Color.blue(buttonTextColor);
			buttonSend.setTextColor(Color.argb(ALPHA, redTextColor, greenTextColor, blueTextColor));
		}
		////////////////////////////////////////////////////////////////////////////////////////
		
		/*TextViews*/
		if ((EditTextConfigActivity.edit_backgroundcolor_changed) || (userPrefs.getTextViewBackgroundColor() != 0)){
			final int textViewBackgroundColor = userPrefs.getTextViewBackgroundColor();
			final int redTextViewBackgroundColor = Color.red(textViewBackgroundColor);
			final int greenTextViewBackgroundColor = Color.green(textViewBackgroundColor);
			final int blueTextViewBackgroundColor = Color.blue(textViewBackgroundColor);
			textViewMessage.setBackgroundColor(Color.argb(ALPHA, redTextViewBackgroundColor, greenTextViewBackgroundColor, blueTextViewBackgroundColor));
			textViewSubject.setBackgroundColor(Color.argb(ALPHA, redTextViewBackgroundColor, greenTextViewBackgroundColor, blueTextViewBackgroundColor));
			textViewPhoneNo.setBackgroundColor(Color.argb(ALPHA, redTextViewBackgroundColor, greenTextViewBackgroundColor, blueTextViewBackgroundColor));
			textViewInstructions.setBackgroundColor(Color.argb(ALPHA, redTextViewBackgroundColor, greenTextViewBackgroundColor, blueTextViewBackgroundColor));

			final int textViewTextColor = userPrefs.getTextViewTextColor();
			final int redTextViewTextColor = Color.red(textViewTextColor);
			final int greenTextViewTextColor = Color.green(textViewTextColor);
			final int blueTextViewTextColor = Color.blue(textViewTextColor);
			textViewMessage.setTextColor(Color.argb(ALPHA, redTextViewTextColor, greenTextViewTextColor, blueTextViewTextColor));
			textViewSubject.setTextColor(Color.argb(ALPHA, redTextViewTextColor, greenTextViewTextColor, blueTextViewTextColor));
			textViewPhoneNo.setTextColor(Color.argb(ALPHA, redTextViewTextColor, greenTextViewTextColor, blueTextViewTextColor));
			textViewInstructions.setTextColor(Color.argb(ALPHA, redTextViewTextColor, greenTextViewTextColor, blueTextViewTextColor));
		}
		
		textViewMessage.setTextSize(userPrefs.getEditTextTextSize() / 2);
		textViewSubject.setTextSize(userPrefs.getEditTextTextSize() / 2);
		textViewPhoneNo.setTextSize(userPrefs.getEditTextTextSize() / 2);
		////////////////////////////////////////////////////////////////////////////////////////
		
		/*EditText*/
		textTo.setTextSize(userPrefs.getEditTextTextSize() / 2);
		textSubject.setTextSize(userPrefs.getEditTextTextSize() / 2);
		textMessage.setTextSize(userPrefs.getEditTextTextSize() / 2);

		if (userPrefs.getEditTextTextColor() != 0){
			final int textEditTextColor = userPrefs.getEditTextTextColor();
			final int redTextEditTextColor = Color.red(textEditTextColor);
			final int greenTextEditTextColor = Color.green(textEditTextColor);
			final int blueTextEditTextColor = Color.blue(textEditTextColor);
			textTo.setTextColor(Color.argb(ALPHA, redTextEditTextColor, greenTextEditTextColor, blueTextEditTextColor));
			textSubject.setTextColor(Color.argb(ALPHA, redTextEditTextColor, greenTextEditTextColor, blueTextEditTextColor));
			textMessage.setTextColor(Color.argb(ALPHA, redTextEditTextColor, greenTextEditTextColor, blueTextEditTextColor));
		}

		if ((EditTextConfigActivity.edit_backgroundcolor_changed) || (userPrefs.getEditTextBackgroundColor() != 0)){
			final int textEditBackgroundColor = userPrefs.getEditTextBackgroundColor();
			final int redTextEditBackgroundColor = Color.red(textEditBackgroundColor);
			final int greenTextEditBackgroundColor = Color.green(textEditBackgroundColor);
			final int blueTextEditBackgroundColor = Color.blue(textEditBackgroundColor);
			textTo.setBackgroundColor(Color.argb(ALPHA, redTextEditBackgroundColor, greenTextEditBackgroundColor, blueTextEditBackgroundColor));
			textSubject.setBackgroundColor(Color.argb(ALPHA, redTextEditBackgroundColor, greenTextEditBackgroundColor, blueTextEditBackgroundColor));
			textMessage.setBackgroundColor(Color.argb(ALPHA, redTextEditBackgroundColor, greenTextEditBackgroundColor, blueTextEditBackgroundColor));
		}
	}

	@Override
	public void addListeners() {
		buttonSend.setOnClickListener(this);
		textMessage.setOnClickListener(this);
		textSubject.setOnClickListener(this);
		textTo.setOnClickListener(this);

		textMessage.setOnFocusChangeListener(this);
		textSubject.setOnFocusChangeListener(this);
		textTo.setOnFocusChangeListener(this);

		findViewById(R.id.linearLayout0).setOnClickListener(this);
		findViewById(R.id.linearLayout1).setOnClickListener(this);
		// is he/she capable of pushing the button with one single click?
		findViewById(R.id.linearLayout2).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		if (!firstClick) {
			firstClick = true;

			timeToStartTask = System.currentTimeMillis() - launchedAt;
		}
		
		if (view.getId() == R.id.buttonSend) {
			String to = textTo.getText().toString();
			String subject = textSubject.getText().toString();
			String message = textMessage.getText().toString();

			Intent email = new Intent(Intent.ACTION_SEND);
			email.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
			email.putExtra(Intent.EXTRA_CC, new String[] { to });
			email.putExtra(Intent.EXTRA_BCC, new String[] { to });
			email.putExtra(Intent.EXTRA_SUBJECT, subject);
			email.putExtra(Intent.EXTRA_TEXT, message);

			// need this to prompts email client only
			email.setType("message/rfc822");

			calculateElapsedtime();
			buildInteractionModel();

			startActivity(Intent.createChooser(email,
					"Choose an Email client :"));
		} else if (view.getId() == R.id.linearLayout0) {
			lostClicks++;
		} else if (view.getId() == R.id.linearLayout1) {
			lostClicks++;
		} else if (view.getId() == R.id.linearLayout2) {
			lostClicks++;
			buttonClicks++;
		} else if (view.getId() == R.id.editTextTo) {
			Log.d(TAG, "editTextTo clicked!");
			editTextClicks++;
		} else if (view.getId() == R.id.editTextSubject) {
			Log.d(TAG, "editTextSubject clicked!");
			editTextClicks++;
		} else if (view.getId() == R.id.editTextMessage) {
			Log.d(TAG, "editTextMessage clicked!");
			editTextClicks++;
		}
		
		if (view.getId() == R.id.editTextTo){
			if (bundle.getInt(getResources().getString(R.string.activity_caller)) == 1){
				speakOut(getResources().getString(R.string.to_es));
			}
		}
		if (view.getId() == R.id.editTextSubject){
			if (bundle.getInt(getResources().getString(R.string.activity_caller)) == 1){
				speakOut(getResources().getString(R.string.subject_es));
			}
		}
		if (view.getId() == R.id.editTextMessage){
			if (bundle.getInt(getResources().getString(R.string.activity_caller)) == 1){
				speakOut(getResources().getString(R.string.message_es));
			}
		}

//
//			double end = System.nanoTime();
//			double elapsed = end - start;
//			double seconds = (elapsed / Math.pow(10, 9));
//
//			times.add(seconds);
//
//			System.out.println("Elapsed: " + seconds);

	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		if (hasFocus) {
			Log.d(TAG, view + " focused!");
			focusStarted = System.currentTimeMillis();

			if (view instanceof EditText) {
				isEditText = true;
				editTextCounter++;
			}
		} else {
			focusElapsedTime = focusElapsedTime
					+ (System.currentTimeMillis() - focusStarted); // accumulate
			focusCounter++;

			// DateFormat df = new
			// SimpleDateFormat("HH 'hours', mm 'mins,' ss 'seconds'");
			// df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
			//
			// Log.d(TAG, "elapsedFocus for " + view.getId() + ": " +
			// df.format(new Date(focusElapsedTime)));

			if (isEditText) {
				timeToFillEditText = timeToFillEditText + focusElapsedTime;
				isEditText = false;
			}
		}
	}

	private long calculateElapsedtime() {
		timeToFinishTask = System.currentTimeMillis();
		elapsedTime = timeToFinishTask - launchedAt;

		return elapsedTime;

		// DateFormat df = new
		// SimpleDateFormat("HH 'hours', mm 'mins,' ss 'seconds'");
		// df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
		//
		// Log.d(TAG, "Elapsed total time: " + df.format(new
		// Date(elapsedTime)));
		//
		// return df.format(new Date(elapsedTime));
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (launchedAt == 0) {
			launchedAt = System.currentTimeMillis();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		calculateElapsedtime();
	}

	@Override
	protected void onStop() {
		super.onStop();
		calculateElapsedtime();
	}

	private void buildInteractionModel() {

		UserInteraction userInteraction = new UserInteraction();
		userInteraction.setButtonClicks(buttonClicks);
		userInteraction.setEditTextClicks(editTextClicks);
		userInteraction.setLostClicks(lostClicks);
		userInteraction.setTimeToFillEditText(timeToFillEditText
				/ editTextCounter);
		userInteraction.setTimeToFinishTask(timeToFinishTask);
		userInteraction.setTimeToNextView(focusElapsedTime / focusCounter);
		userInteraction.setTimeToStartTask(timeToStartTask);
		/*
		 * Map<String, Object> model = new HashMap<String, Object>();
		 * 
		 * //Number of EditText clicks model.put("editTextClicks",
		 * editTextClicks); //Number of "wrong" clicks model.put("lostClicks",
		 * lostClicks); //Time to fill each EditText
		 * model.put("timeToFillEditText", timeToFillEditText); //Time for
		 * starting the task model.put("timeToStartTask", timeToStartTask);
		 * //Time for finishing the task model.put("TotalElapsedTime",
		 * calculateElapsedtime()); //Time for next view (mean) =
		 * focusElapsedTime (accumulate) / number of focus changes
		 * model.put("focusedElapsedTime", focusElapsedTime/focusCounter);
		 */
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		startActivity(new Intent(this, CapabilitiesActivity.class));
	}

}
