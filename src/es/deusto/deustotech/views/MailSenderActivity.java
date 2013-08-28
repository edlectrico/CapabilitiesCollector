package es.deusto.deustotech.views;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import es.deusto.deustotech.R;
import es.deusto.deustotech.utils.UserMinimumPreferences;

/**
 * @author edlectrico
 *
 * This Activity gathers the UI configuration from the previous bundles
 * and shows a builds a user interaction profile through a email
 * application capturing the total time to do this task, clicks,
 * device orientation variations, etc.
 *
 */

public class MailSenderActivity extends Activity implements OnClickListener{

	private Button buttonSend;
	private EditText textTo;
	private EditText textSubject;
	private EditText textMessage;

	private UserMinimumPreferences userPrefs;

//	private int topLayoutClicks 	= 0;
	private int bottomLayoutClicks 	= 0;
	private int editTextClicks 		= 0;
	
	private long startedAt;
	private long elapsedTime;
	private long finishedAt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.email_activity);

		startedAt = System.currentTimeMillis();
		
		buttonSend = (Button) findViewById(R.id.buttonSend);
		textTo = (EditText) findViewById(R.id.editTextTo);
		textSubject = (EditText) findViewById(R.id.editTextSubject);
		textMessage = (EditText) findViewById(R.id.editTextMessage);

		Bundle bundle = getIntent().getExtras();
		userPrefs = bundle.getParcelable("viewParams");

		customizeActivity();
		addListeners();
	}

	private void customizeActivity() {
		buttonSend.setWidth((int) userPrefs.getButtonWidth());
		buttonSend.setHeight((int) userPrefs.getButtonHeight());
		buttonSend.setBackgroundColor(userPrefs.getButtonBackgroundColor());
		buttonSend.setTextColor(userPrefs.getButtonTextColor());

		textTo.setTextSize(userPrefs.getTextEditSize());
		textTo.setTextColor(userPrefs.getTextEditTextColor());
		textTo.setBackgroundColor(userPrefs.getTextEditBackgroundColor());

		textSubject.setTextSize(userPrefs.getTextEditSize());
		textSubject.setTextColor(userPrefs.getTextEditTextColor());
		textSubject.setBackgroundColor(userPrefs.getTextEditBackgroundColor());

		textMessage.setTextSize(userPrefs.getTextEditSize());
		textMessage.setTextColor(userPrefs.getTextEditTextColor());
		textMessage.setBackgroundColor(userPrefs.getTextEditBackgroundColor());

		WindowManager.LayoutParams layoutParams = getWindow()
				.getAttributes();
		layoutParams.screenBrightness = userPrefs.getBrightness();
	}

	private void addListeners() {
		buttonSend.setOnClickListener(this);
		textMessage.setOnClickListener(this);
		textSubject.setOnClickListener(this);
		textTo.setOnClickListener(this);
//		findViewById(R.id.linearLayout0).setOnClickListener(this);
//		findViewById(R.id.linearLayout1).setOnClickListener(this);
		//is he/she capable of pushing the button with one single click?
		findViewById(R.id.linearLayout2).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.buttonSend:
				String to = textTo.getText().toString();
				String subject = textSubject.getText().toString();
				String message = textMessage.getText().toString();
	
				Intent email = new Intent(Intent.ACTION_SEND);
				email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
				email.putExtra(Intent.EXTRA_CC, new String[]{ to});
				email.putExtra(Intent.EXTRA_BCC, new String[]{to});
				email.putExtra(Intent.EXTRA_SUBJECT, subject);
				email.putExtra(Intent.EXTRA_TEXT, message);
	
				//need this to prompts email client only
				email.setType("message/rfc822");
				
				calculateElapsedtime();
				buildInteractionModel();
	
				startActivity(Intent.createChooser(email, "Choose an Email client :"));
				
				break;
	
//			case R.id.linearLayout1: 
//				topLayoutClicks++;
//				
			case R.id.linearLayout2:
				bottomLayoutClicks++;
				
			case R.id.editTextTo:
				editTextClicks++;
				
			case R.id.editTextSubject:
				editTextClicks++;
				
			case R.id.editTextMessage:
				editTextClicks++;
				
			default:
				break;
		}
	}

	private void calculateElapsedtime() {
		finishedAt = System.currentTimeMillis();
		elapsedTime = finishedAt - startedAt;
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if (startedAt == 0){
			startedAt = System.currentTimeMillis();
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
		Map<String, Integer> model = new HashMap<String, Integer>();
		
//		System.out.println("topLayoutClicks: " + topLayoutClicks);
		System.out.println("bottomLayoutClicks: " + bottomLayoutClicks);
		System.out.println("editTextClicks: " + editTextClicks);
		
//		model.put("topLayoutClicks", topLayoutClicks);
		model.put("bottomLayoutClicks", bottomLayoutClicks);
		model.put("editTextClicks", editTextClicks);
		model.put("elapsedTime", (int)elapsedTime);
	}

}
