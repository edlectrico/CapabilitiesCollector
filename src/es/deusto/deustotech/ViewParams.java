package es.deusto.deustotech;

public class ViewParams {

	private int width;
	private int height;
	private int backgroundColor;
	private int textColor;
	
	public ViewParams() {
		super();
	}
	public ViewParams(int width, int height, int backgroundColor, int textColor) {
		super();
		this.width = width;
		this.height = height;
		this.backgroundColor = backgroundColor;
		this.textColor = textColor;
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
	
}
