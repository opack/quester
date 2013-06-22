package com.slamdunk.quester.java;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.slamdunk.quester.Quester;

public class QuesterDesktop {
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Quester";
		config.useGL20 = true;
		config.width = 800;
		config.height = 480;
		new LwjglApplication(new Quester(), config);
	}
}
