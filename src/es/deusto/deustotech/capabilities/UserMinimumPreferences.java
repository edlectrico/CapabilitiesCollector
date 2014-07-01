package es.deusto.deustotech.capabilities;

import android.os.Parcel;
import android.os.Parcelable;

public class UserMinimumPreferences implements Parcelable {

	private int layoutBackgroundColor;
	private float buttonWidth;
	private float buttonHeight;
	private int buttonBackgroundColor;
	private int buttonTextColor;
	private float textEditSize;
	private int textEditBackgroundColor;
	private int textEditTextColor;
	private float brightness;
	private float volume;
	private int textViewBackgroundColor;
	private int textViewTextColor;
	private float textViewTextSize;
	
	private int sightProblem = 0; //false
	private int hearingProblem = 0;
	
	public UserMinimumPreferences() {
		super();
	}

	public UserMinimumPreferences(int layoutBackgroundColor,
			float buttonWidth, float buttonHeight, int buttonBackgroundColor, int buttonTextColor, 
			float textEditSize, int textEditBackgroundColor, int textEditTextColor, int textViewBackgroundColor,
			int textViewTextColor, float textViewTextSize) {
		super();
		this.layoutBackgroundColor = layoutBackgroundColor;
		this.buttonWidth = buttonWidth;
		this.buttonHeight = buttonHeight;
		this.buttonBackgroundColor = buttonBackgroundColor;
		this.buttonTextColor = buttonTextColor;
		this.textEditSize = textEditSize;
		this.textEditBackgroundColor = textEditBackgroundColor;
		this.textEditTextColor = textEditTextColor;
		this.textViewBackgroundColor = textViewBackgroundColor;
		this.textViewTextColor = textViewTextColor;
		this.textViewTextSize = textViewTextSize;
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

	public float getTextEditSize() {
		return textEditSize;
	}

	public void setTextEditSize(float textEditSize) {
		this.textEditSize = textEditSize;
	}

	public int getTextEditBackgroundColor() {
		return textEditBackgroundColor;
	}

	public void setTextEditBackgroundColor(int textEditBackgroundColor) {
		this.textEditBackgroundColor = textEditBackgroundColor;
	}

	public int getTextEditTextColor() {
		return textEditTextColor;
	}

	public void setTextEditTextColor(int textEditTextColor) {
		this.textEditTextColor = textEditTextColor;
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
	
	public int getSightProblem() {
		return sightProblem;
	}

	public void setSightProblem(int sightProblem) {
		this.sightProblem = sightProblem;
	}

	public int getHearingProblem() {
		return hearingProblem;
	}

	public void setHearingProblem(int hearingProblem) {
		this.hearingProblem = hearingProblem;
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
		dest.writeFloat(textEditSize);
		dest.writeInt(textEditBackgroundColor);
		dest.writeInt(textEditTextColor);
		dest.writeFloat(brightness);
		dest.writeFloat(volume);
		dest.writeInt(sightProblem);
		dest.writeInt(hearingProblem);
		dest.writeInt(textViewBackgroundColor);
		dest.writeInt(textViewTextColor);
		dest.writeFloat(textViewTextSize);
	}
	
	private void readFromParcel(Parcel in) {   
		layoutBackgroundColor = in.readInt();
		buttonWidth = in.readFloat();
		buttonHeight = in.readFloat();
		buttonBackgroundColor = in.readInt();
		buttonTextColor = in.readInt();
		textEditSize = in.readFloat();
		textEditBackgroundColor = in.readInt();
		textEditTextColor = in.readInt();
		brightness = in.readFloat();
		volume = in.readFloat();
		sightProblem = in.readInt();
		hearingProblem = in.readInt();
		textViewBackgroundColor = in.readInt();
		textViewTextColor = in.readInt();
		textViewTextSize = in.readFloat();
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
