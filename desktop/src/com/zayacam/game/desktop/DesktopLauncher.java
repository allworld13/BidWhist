package com.zayacam.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new BidWhistGame(), config);

		config.width = Assets.GameWidth;
		config.height = Assets.GameHeight;
	}
}
