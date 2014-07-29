package es.deusto.deustotech.capabilities;

import android.os.Parcel;
import android.os.Parcelable;

public class UserMinimumPreferences implements Parcelable {

	private int layoutBackgroundColor;
	private float buttonWidth;
	private float buttonHeight;
	private int buttonBackgroundColor;
	private int buttonTextColor;
	private float editTextTextSize;
	private int editTextBackgroundColor;
	private int editTextTextColor;
	private float brightness;
	private float volume;
	private int textViewBackgroundColor;
	private int textViewTextColor;
	private float textViewTextSize;
	private int editTextWidth;
	private int textViewWidth;
	private int editTextHeight;
	private int textViewHeight;
	
	private int displayHasApplicable = 0; //false
	private int audioHasApplicable = 0;
	
	public UserMinimumPreferences() {
		super();
	}

	public UserMinimumPreferences(int layoutBackgroundColor,
			float buttonWidth, float buttonHeight, int buttonBackgroundColor, int buttonTextColor, 
			float editTextTextSize, int editTextBackgroundColor, int editTextTextColor, int textViewBackgroundColor,
			int textViewTextColor, float textViewTextSize, int editTextWidth, int textViewWidth, int editTextHeight,
			int textViewHeight, int displayHasApplicable, int audioHasApplicable) {
		super();
		this.layoutBackgroundColor = layoutBackgroundColor;
		this.buttonWidth = buttonWidth;
		this.buttonHeight = buttonHeight;
		this.buttonBackgroundColor = buttonBackgroundColor;
		this.buttonTextColor = buttonTextColor;
		this.editTextTextSize = editTextTextSize;
		this.editTextBackgroundColor = editTextBackgroundColor;
		this.editTextTextColor = editTextTextColor;
		this.textViewBackgroundColor = textViewBackgroundColor;
		this.textViewTextColor = textViewTextColor;
		this.textViewTextSize = textViewTextSize;
		this.editTextWidth = editTextWidth;
		this.textViewWidth = textViewWidth;
		this.editTextHeight = editTextHeight;
		this.textViewHeight = textViewHeight;
		
		this.displayHasApplicable = displayHasApplicable;
		this.audioHasApplicable = audioHasApplicable;
	}
	
	public UserMinimumPreferences(Parcel in) { 
		readFromParcel(in); 
	}

	public int getLayoutBackgroundColor() {
		return layoutBackgroundColor;
	}

	public void setLayoutBackgroundColor(int backgroundColor) {
		this.layoutBackgroundColor = backgroundColor;
	}

	public float getButtonWidth() {
		return buttonWidth;
	}

	public void setButtonWidth(float buttonWidth) {
		this.buttonWidth = buttonWidth;
	}

	public float getButtonHeight() {
		return buttonHeight;
	}

	public void setButtonHeight(float buttonHeight) {
		this.buttonHeight = buttonHeight;
	}

	public int getButtonBackgroundColor() {
		return buttonBackgroundColor;
	}

	public void setButtonBackgroundColor(int buttonBackgroundColor) {
		this.buttonBackgroundColor = buttonBackgroundColor;
	}

	public int getButtonTextColor() {
		return buttonTextColor;
	}

	public void setButtonTextColor(int buttonTextColor) {
		this.buttonTextColor = buttonTextColor;
	}

	public float getEditTextTextSize() {
		return editTextTextSize;
	}

	public void setEditTextTextSize(float editTextTextSize) {
		this.editTextTextSize = editTextTextSize;
	}

	public int getEditTextBackgroundColor() {
		return editTextBackgroundColor;
	}

	public void setEditTextBackgroundColor(int editTextBackgroundColor) {
		this.editTextBackgroundColor = editTextBackgroundColor;
	}

	public int getEditTextTextColor() {
		return editTextTextColor;
	}

	public void setEditTextTextColor(int editTextTextColor) {
		this.editTextTextColor = editTextTextColor;
	}

	public float getBrightness() {
		return brightness;
	}

	public void setBrightness(float brightness) {
		this.brightness = brightness;
	}

	public float getVolume() {
		return volume;
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}
	
	public int getDisplayHasApplicable() {
		return displayHasApplicable;
	}

	public void setDisplayHasApplicable(int isApplicable) {
		this.displayHasApplicable = isApplicable;
	}

	public int getAudioHasApplicable() {
		return audioHasApplicable;
	}

	public void setAudioHasApplicable(int isApplicable) {
		this.audioHasApplicable = isApplicable;
	}
	
	public void setTextViewBackgroundColor(int textViewBackgroundColor) {
		this.textViewBackgroundColor = textViewBackgroundColor;
	}

	public void setTextViewTextColor(int textViewTextColor) {
		this.textViewTextColor = textViewTextColor;
	}

	public void setTextViewTextSize(float textViewTextSize) {
		this.textViewTextSize = textViewTextSize;
	} 
	
	public int getTextViewBackgroundColor() {
		return textViewBackgroundColor;
	}

	public int getTextViewTextColor() {
		return textViewTextColor;
	}

	public float getTextViewTextSize() {
		return textViewTextSize;
	}
	
	public int getEditTextWidth() {
		return editTextWidth;
	}

	public void setEditTextWidth(int editTextWidth) {
		this.editTextWidth = editTextWidth;
	}

	public int getTextViewWidth() {
		return textViewWidth;
	}

	public void setTextViewWidth(int textViewWidth) {
		this.textViewWidth = textViewWidth;
	}

	public int getEditTextHeight() {
		return editTextHeight;
	}

	public void setEditTextHeight(int editTextHeight) {
		this.editTextHeight = editTextHeight;
	}

	public int getTextViewHeight() {
		return textViewHeight;
	}

	public void setTextViewHeight(int textViewHeight) {
		this.textViewHeight = textViewHeight;
	}

	public static Parcelable.Creator<UserMinimumPreferences> getCreator() {
		return CREATOR;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(layoutBackgroundColor);
		dest.writeFloat(buttonWidth);
		dest.writeFloat(buttonHeight);
		dest.writeInt(buttonBackgroundColor);
		dest.writeInt(buttonTextColor);
		dest.writeFloat(editTextTextSize);
		dest.writeInt(editTextBackgroundColor);
		dest.writeInt(editTextTextColor);
		dest.writeFloat(brightness);
		dest.writeFloat(volume);
		dest.writeInt(displayHasApplicable);
		dest.writeInt(audioHasApplicable);
		dest.writeInt(textViewBackgroundColor);
		dest.writeInt(textViewTextColor);
		dest.writeFloat(textViewTextSize);
		dest.writeInt(editTextWidth);
		dest.writeInt(textViewWidth);
		dest.writeInt(editTextHeight);
		dest.writeInt(textViewHeight);
	}
	
	private void readFromParcel(Parcel in) {   
		layoutBackgroundColor = in.readInt();
		buttonWidth = in.readFloat();
		buttonHeight = in.readFloat();
		buttonBackgroundColor = in.readInt();
		buttonTextColor = in.readInt();
		editTextTextSize = in.readFloat();
		editTextBackgroundColor = in.readInt();
		editTextTextColor = in.readInt();
		brightness = in.readFloat();
		volume = in.readFloat();
		displayHasApplicable = in.readInt();
		audioHasApplicable = in.readInt();
		textViewBackgroundColor = in.readInt();
		textViewTextColor = in.readInt();
		textViewTextSize = in.readFloat();
		editTextWidth = in.readInt();
		textViewWidth = in.readInt();
		editTextHeight = in.readInt();
		textViewHeight = in.readInt();
	}
	
	public static final Parcelable.Creator<UserMinimumPreferences> CREATOR = new Parcelable.Creator<UserMinimumPreferences>() { 
		public UserMinimumPreferences createFromParcel(Parcel in) { 
			return new UserMinimumPreferences(in); 
		}   
		
		public UserMinimumPreferences[] newArray(int size) { 
			return new UserMinimumPreferences[size]; 
		} 
	};

}
