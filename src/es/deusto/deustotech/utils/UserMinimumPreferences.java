package es.deusto.deustotech.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class UserMinimumPreferences implements Parcelable {

	private int backgroundColor;
	private int textColor;
	private float buttonWidth;
	private float buttonHeight;
	private int buttonBackgroundColor;
	private float textEditSize;
	private int textEditBackgroundColor;
	private float brightness;
	private float volume;
	
	private boolean[] capabilities;
	
	public UserMinimumPreferences() {
		super();
	}

	public UserMinimumPreferences(int backgroundColor,
			int textColor, float buttonWidth, float buttonHeight, int buttonBackgroundColor,
			float textEditSize, int textEditBackgroundColor) {
		super();
		this.backgroundColor = backgroundColor;
		this.textColor = textColor;
		this.buttonWidth = buttonWidth;
		this.buttonHeight = buttonHeight;
		this.buttonBackgroundColor = buttonBackgroundColor;
		this.textEditSize = textEditSize;
		this.textEditBackgroundColor = textEditBackgroundColor;
		
		capabilities[0] = false; //sight problem
		capabilities[1] = false; //hearing problem
	}
	
	public UserMinimumPreferences(Parcel in) { 
		readFromParcel(in); 
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

	public int getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public float getButtonSize() {
		return buttonWidth;
	}

	public void setButtonSize(float buttonSize) {
		this.buttonWidth = buttonSize;
	}

	public int getButtonBackgroundColor() {
		return buttonBackgroundColor;
	}

	public void setButtonBackgroundColor(int buttonBackgroundColor) {
		this.buttonBackgroundColor = buttonBackgroundColor;
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
	
	public boolean[] getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(boolean[] capabilities) {
		this.capabilities = capabilities;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(backgroundColor);
		dest.writeInt(textColor);
		dest.writeFloat(buttonWidth);
		dest.writeFloat(buttonHeight);
		dest.writeInt(buttonBackgroundColor);
		dest.writeFloat(textEditSize);
		dest.writeInt(textEditBackgroundColor);
		dest.writeFloat(brightness);
		dest.writeFloat(volume);
		dest.writeBooleanArray(capabilities);
	}
	
	private void readFromParcel(Parcel in) {   
		backgroundColor = in.readInt();
		textColor = in.readInt();
		buttonWidth = in.readFloat();
		buttonHeight = in.readFloat();
		buttonBackgroundColor = in.readInt();
		textEditSize = in.readFloat();
		textEditBackgroundColor = in.readInt();
		brightness = in.readFloat();
		volume = in.readFloat();
		
		capabilities[0] = (in.readInt() == 1) ? true : false;
		capabilities[1] = (in.readInt() == 1) ? true : false;
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
