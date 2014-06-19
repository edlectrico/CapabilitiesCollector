package es.deusto.deustotech.capabilities.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.semanticweb.owlapi.model.OWLLiteral;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import es.deusto.deustotech.R;
import es.deusto.deustotech.capabilities.UserInteraction;
import es.deusto.deustotech.pellet4android.exceptions.OntologySavingException;

/**
 * @author edlectrico
 * 
 *         This Activity gathers the UI configuration from the previous bundles
 *         and shows a builds a user interaction profile through a email
 *         application capturing the total time to do this task, clicks, device
 *         orientation variations, etc.
 * 
 */

public class MailSenderActivity extends AbstractActivity implements
		OnFocusChangeListener {

	private final String TAG = MailSenderActivity.class.getSimpleName();

	private Button buttonSend;
	private Button buttonContextChange;
	private EditText textTo;
	private EditText textSubject;
	private EditText textMessage;
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
	
	private WindowManager.LayoutParams layoutParams;
	
	private long start;
	private List<Double> times;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.email_activity);

		buttonSend = (Button) findViewById(R.id.buttonSend);
		buttonContextChange = (Button) findViewById(R.id.buttonTriggerContextChange);
		textTo = (EditText) findViewById(R.id.editTextTo);
		textSubject = (EditText) findViewById(R.id.editTextSubject);
		textMessage = (EditText) findViewById(R.id.editTextMessage);
		layout = (LinearLayout) findViewById(R.id.linearLayout0);

		Bundle bundle = getIntent().getExtras();
		
		userPrefs = bundle.getParcelable("viewParams");
		redrawViews();
		initializeServices(TAG);
		addListeners();
	}

	@Override
	public void initializeServices(String TAG) {
		launchedAt = System.currentTimeMillis();

		if (userPrefs.getBrightness() != 0) {
			layoutParams = getWindow().getAttributes();
			layoutParams.screenBrightness = userPrefs.getBrightness();
			
			System.out.println("layoutParams.screenBrightness: " + layoutParams.screenBrightness);
		}
	}

	@Override
	public void redrawViews() {
		layout.setBackgroundColor(userPrefs.getBackgroundColor());
		((TextView) findViewById(R.id.textViewPhoneNo)).setTextSize(userPrefs
				.getTextEditSize());
		((TextView) findViewById(R.id.textViewSubject)).setTextSize(userPrefs
				.getTextEditSize());
		((TextView) findViewById(R.id.textViewMessage)).setTextSize(userPrefs
				.getTextEditSize());

		buttonSend.setWidth((int) userPrefs.getButtonWidth());
		buttonSend.setHeight((int) userPrefs.getButtonHeight());

		if (userPrefs.getBackgroundColor() != 0) {
			buttonSend.setBackgroundColor(userPrefs.getButtonBackgroundColor());
		}
		buttonSend.setTextColor(userPrefs.getButtonTextColor());

		textTo.setTextSize(userPrefs.getTextEditSize());
		textSubject.setTextSize(userPrefs.getTextEditSize());
		textMessage.setTextSize(userPrefs.getTextEditSize());

		if (userPrefs.getTextEditTextColor() != 0) {
			textTo.setTextColor(userPrefs.getTextEditTextColor());
			textTo.setBackgroundColor(userPrefs.getTextEditBackgroundColor());
			textSubject.setTextColor(userPrefs.getTextEditTextColor());
			textSubject.setBackgroundColor(userPrefs
					.getTextEditBackgroundColor());
			textMessage.setTextColor(userPrefs.getTextEditTextColor());
			textMessage.setBackgroundColor(userPrefs
					.getTextEditBackgroundColor());

			((TextView) findViewById(R.id.textViewPhoneNo))
					.setTextColor(userPrefs.getTextEditTextColor());
			((TextView) findViewById(R.id.textViewSubject))
					.setTextColor(userPrefs.getTextEditTextColor());
			((TextView) findViewById(R.id.textViewMessage))
					.setTextColor(userPrefs.getTextEditTextColor());
		}
	}

	@Override
	public void addListeners() {
		buttonSend.setOnClickListener(this);
		buttonContextChange.setOnClickListener(this);
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
		
		if (view.getId() == R.id.buttonTriggerContextChange) {
			times = new ArrayList<Double>();
			start = System.nanoTime();
			
			//http://u2m.org/2003/02/UserModelOntology.rdf#Light
			List<String> lights = super.getOntologyManager().getIndividualOfClass("http://u2m.org/2003/02/UserModelOntology.rdf#" + "Light");
			super.getOntologyManager().addDataTypePropertyValue(lights.get(0), super.getOntologyNamespace() + "contextHasLight", 50000); 
			
			final Collection<OWLLiteral> l = super.getOntologyManager().getDataTypePropertyValue(lights.get(0), super.getOntologyNamespace() + "contextHasLight");
			System.out.println(l);
			
			try {
				super.getOntologyManager().saveOntologyAs(Environment.getExternalStorageDirectory() + "/data/" + super.getOntologyFilename());
			} catch (OntologySavingException e) {
				e.printStackTrace();
			}
			
			List<String> contexts = super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "ContextAux");
			final Collection<OWLLiteral> c = super.getOntologyManager().getDataTypePropertyValue(contexts.get(0), super.getOntologyNamespace() + "contextAuxHasLightLevel");
			
			System.out.println(c);
			
			List<String> displays 	= super.getOntologyManager().getIndividualOfClass(super.getOntologyNamespace() + "Display");
			final Collection<OWLLiteral> brightness = super.getOntologyManager().getDataTypePropertyValue(displays.get(0), super.getOntologyNamespace() + "displayHasBrightness");
			System.out.println(brightness);
			
			final String bri = ((OWLLiteral) brightness.toArray()[1]).getLiteral();
			
			System.out.println(bri);

			layoutParams.screenBrightness = Float.parseFloat(bri);
			
			System.out.println("layoutParams.screenBrightness: " + layoutParams.screenBrightness);
			
			double end = System.nanoTime();
			double elapsed = end - start;
			double seconds = (elapsed / Math.pow(10, 9));
			
			times.add(seconds);
			
			System.out.println("Elapsed: " + seconds);
		}
		
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

		System.out.println();
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

}
