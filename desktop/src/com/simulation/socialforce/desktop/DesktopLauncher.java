package com.simulation.socialforce.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.simulation.socialforce.SocialForceModel;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Social Force Model Simulation";
		config.width = 800;
		config.height = 480;
		new LwjglApplication(new SocialForceModel(), config);
	}
}