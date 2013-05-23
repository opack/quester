package com.slamdunk.quester.messagebox;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.slamdunk.quester.actors.MenuNinePatch;
import com.slamdunk.quester.core.Assets;
import com.slamdunk.quester.messagebox.MessageBox.MessageBoxStyle;

public class MessageBoxFactory {
	public static MessageBox createSimpleMessage(String message, Stage stage){ //float centerX, float centerY) {
		BitmapFont font = new BitmapFont();
		LabelStyle messageStyle = new LabelStyle();
		messageStyle.font = font;
		messageStyle.fontColor = Color.WHITE;
		
		TextButtonStyle buttonStyle = new TextButtonStyle();
		buttonStyle.font = font;
		buttonStyle.fontColor = Color.WHITE;
		buttonStyle.up = new NinePatchDrawable(MenuNinePatch.getInstance());
		
		WindowStyle windowStyle = new WindowStyle();
		windowStyle.titleFont = Assets.characterFont;
		windowStyle.titleFontColor = Color.WHITE;
		
		MessageBoxStyle msgBoxStyle = new MessageBoxStyle();
		msgBoxStyle.windowStyle = windowStyle;
		msgBoxStyle.messageStyle = messageStyle;
		msgBoxStyle.buttonStyle = buttonStyle;

		MessageBox msg = new MessageBox("Quester", message, "OK", msgBoxStyle);
		msg.setPosition((stage.getCamera().viewportWidth - msg.getWidth()) / 2, (stage.getCamera().viewportHeight - msg.getHeight()) / 2);
		
		stage.addActor(msg);
		return msg;
	}
}
