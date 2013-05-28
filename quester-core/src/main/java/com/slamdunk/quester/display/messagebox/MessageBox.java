package com.slamdunk.quester.display.messagebox;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class MessageBox extends Window {
	private Label message;
	private TextButton button;

	public MessageBox(String title, String messageText, String buttonText, MessageBoxStyle style) {
		super(title, style.windowStyle);
		setBackground(new NinePatchDrawable(MessageBoxNinePatch.getInstance()));
		padTop(30);
		
		message = new Label(messageText, style.messageStyle);
		
		button = new TextButton(buttonText, style.buttonStyle);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setVisible(false);
			}
		});
		
		add(message).expandX().row();
		add(button);
		
		pack();
		
		setVisible(false);
	}

	public void setStyle(MessageBoxStyle style) {
		if (style == null)
			throw new IllegalArgumentException("style cannot be null.");
		
		message.setStyle(style.messageStyle);
		button.setStyle(style.buttonStyle);
		
		invalidateHierarchy();
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		if (isVisible()) {
			super.draw(batch, parentAlpha);
		}
	}

	public void show() {
		setVisible(true);
	}

	public void hide() {
		setVisible(false);
	}

	public void addButtonListener(EventListener listener) {
		button.addListener(listener);
	}

	static public class MessageBoxStyle {
		public WindowStyle windowStyle;
		public LabelStyle messageStyle;
		public TextButtonStyle buttonStyle;

		public MessageBoxStyle() {
		}

		public MessageBoxStyle(BitmapFont font, Color fontColor) {
			windowStyle = new WindowStyle();
			windowStyle.titleFont = font;
			windowStyle.titleFontColor = fontColor;
			
			messageStyle = new LabelStyle();
			messageStyle.font = font;
			messageStyle.fontColor = fontColor;
			
			buttonStyle = new TextButtonStyle();
			buttonStyle.font = font;
			buttonStyle.fontColor = fontColor;
		}
	}
}