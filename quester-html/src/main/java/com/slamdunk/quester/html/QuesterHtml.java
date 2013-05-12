package com.slamdunk.quester.html;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.slamdunk.quester.core.Quester;

public class QuesterHtml extends GwtApplication {
	@Override
	public ApplicationListener getApplicationListener () {
		return new Quester();
	}
	
	@Override
	public GwtApplicationConfiguration getConfig () {
		return new GwtApplicationConfiguration(800, 480);
	}
}
