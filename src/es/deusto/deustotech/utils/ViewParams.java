package es.deusto.deustotech.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class ViewParams implements Parcelable {

	private int backgroundColor;
	private int textColor;
	private float buttonWidth;
	private float buttonHeight;
	private int buttonBackgroundColor;
	private float textEditSize;
	private int textEditBackgroundColor;
	
	public ViewParams() {
		super();
	}

	public ViewParams(int backgroundColor,
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
	}
	
	public ViewParams(Parcel in) { 
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

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
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
		
	}
	
	private void readFromParcel(Parcel in) {   
		backgroundColor = in.readInt();
		textColor = in.readInt();
		buttonWidth = in.readFloat();
		buttonHeight = in.readFloat();
		buttonBackgroundColor = in.readInt();
		textEditSize = in.readFloat();
		textEditBackgroundColor = in.readInt();
	}
	
	public static final Parcelable.Creator<ViewParams> CREATOR = new Parcelable.Creator<ViewParams>() { 
		public ViewParams createFromParcel(Parcel in) { 
			return new ViewParams(in); 
		}   
		
		public ViewParams[] newArray(int size) { 
			return new ViewParams[size]; 
		} 
	}; 
}
