package es.deusto.deustotech.capabilities;

/**
 * 
 * @author edlectrico
 *
 * This class models the user interactions
 * with the activities to extract a compatibility
 * percentage with the generated UI from 
 * experience.
 */

public class UserInteraction {

	private int lostClicks; //amount of clicks that are not valuable (interaction level lost)
	private int editTextClicks; //Amount of clicks in EditText components
	private int buttonClicks; //Amount of clicks to push a Button (by capturing its layout click events)
	private long timeToStartTask; //Passed time since the UI is presented and the user starts interacting with it
	private long timeToFinishTask; //Total elapsed time between the start and the end of the interaction
	private float timeToNextView; //Mean time between the interaction with one component to another one
	private float timeToFillEditText; //Mean time needed to fill an EditText view
	
	public UserInteraction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserInteraction(int lostClicks, int editTextClicks,
			int buttonClicks, long timeToStartTask, long timeToFinishTask,
			long timeToNextView, long timeToFillEditText) {
		super();
		this.lostClicks = lostClicks;
		this.editTextClicks = editTextClicks;
		this.buttonClicks = buttonClicks;
		this.timeToStartTask = timeToStartTask;
		this.timeToFinishTask = timeToFinishTask;
		this.timeToNextView = timeToNextView;
		this.timeToFillEditText = timeToFillEditText;
	}

	public int getLostClicks() {
		return lostClicks;
	}

	public void setLostClicks(int lostClicks) {
		this.lostClicks = lostClicks;
	}

	public int getEditTextClicks() {
		return editTextClicks;
	}

	public void setEditTextClicks(int editTextClicks) {
		this.editTextClicks = editTextClicks;
	}

	public int getButtonClicks() {
		return buttonClicks;
	}

	public void setButtonClicks(int buttonClicks) {
		this.buttonClicks = buttonClicks;
	}

	public long getTimeToStartTask() {
		return timeToStartTask;
	}

	public void setTimeToStartTask(long timeToStartTask) {
		this.timeToStartTask = timeToStartTask;
	}

	public long getTimeToFinishTask() {
		return timeToFinishTask;
	}

	public void setTimeToFinishTask(long timeToFinishTask) {
		this.timeToFinishTask = timeToFinishTask;
	}

	public float getTimeToNextView() {
		return timeToNextView;
	}

	public void setTimeToNextView(float timeToNextView) {
		this.timeToNextView = timeToNextView;
	}

	public float getTimeToFillEditText() {
		return timeToFillEditText;
	}

	public void setTimeToFillEditText(float timeToFillEditText) {
		this.timeToFillEditText = timeToFillEditText;
	}
	
}
