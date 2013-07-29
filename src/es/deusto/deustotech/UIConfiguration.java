package es.deusto.deustotech;

public class UIConfiguration {
	private int viewColor;
	private int textColor;
	private int viewWidth;
	private int viewHeight;
	private int volume;
	private float brightness;

	public UIConfiguration(){
		super();
	}

	public UIConfiguration(final ViewParams viewParams, final float brightness, final int volume) {
		super();
		
		this.viewColor = viewParams.getBackgroundColor();
		this.textColor = viewParams.getTextColor();
		this.viewWidth = viewParams.getWidth();
		this.viewHeight = viewParams.getHeight();
	}

	public int getViewColor() {
		return viewColor;
	}

	public void setViewColor(int viewColor) {
		this.viewColor = viewColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public int getViewWidth() {
		return viewWidth;
	}

	public void setViewWidth(int viewWidth) {
		this.viewWidth = viewWidth;
	}

	public int getViewHeight() {
		return viewHeight;
	}

	public void setViewHeight(int viewHeight) {
		this.viewHeight = viewHeight;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public float getBrightness() {
		return brightness;
	}

	public void setBrightness(float brightness) {
		this.brightness = brightness;
	}

}
