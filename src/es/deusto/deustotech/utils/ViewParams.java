package es.deusto.deustotech.utils;

public class ViewParams {

	private int width;
	private int height;
	private int backgroundColor;
	private int textColor;
	private float buttonSize;
	private int buttonBackgroundColor;
	private float textEditSize;
	private int textEditBackgroundColor;
	
	public ViewParams() {
		super();
	}

	public ViewParams(int width, int height, int backgroundColor,
			int textColor, float buttonSize, int buttonBackgroundColor,
			float textEditSize, int textEditBackgroundColor) {
		super();
		this.width = width;
		this.height = height;
		this.backgroundColor = backgroundColor;
		this.textColor = textColor;
		this.buttonSize = buttonSize;
		this.buttonBackgroundColor = buttonBackgroundColor;
		this.textEditSize = textEditSize;
		this.textEditBackgroundColor = textEditBackgroundColor;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
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
		return buttonSize;
	}

	public void setButtonSize(float buttonSize) {
		this.buttonSize = buttonSize;
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
}
