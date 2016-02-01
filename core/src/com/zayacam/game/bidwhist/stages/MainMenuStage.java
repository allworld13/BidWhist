package com.zayacam.game.bidwhist.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;
import com.zayacam.game.bidwhist.actors.MainMenuActor;

public class MainMenuStage extends _BidWhistStage {

    final Table table = new Table();
    TextButton btnGameButton;

    public MainMenuStage(BidWhistGame bidWhistGame, ScreenViewport sViewport) {
        super(bidWhistGame, sViewport);

        currentScreen = new MainMenuActor(bidWhistGame, this);
        Assets.LoadMainMenuScreen();

        bidWhistGame.gamePlay.Init();

        this.setKeyboardFocus(currentScreen);
        this.addActor(currentScreen);

        //region load main menu buttons
        table.align(Align.center | Align.topLeft);
        table.setPosition(this.getWidth() * .68f,
                (this.getHeight() + 6f) * .80f);

        btnGameButton = new TextButton("New Game  ", Assets.Skins);
        btnGameButton.setName("btnNewGame");
        btnGameButton.setSize(this.getWidth() * Assets.MainMenuButton_Width, this.getWidth() * Assets.MainMenuButton_Height);
        btnGameButton.addListener(new MainMenuButtonClickListener());
        table.add(btnGameButton);
        table.row();
        btnGameButton = new TextButton("Options", Assets.Skins);
        btnGameButton.setName("btnOptions");
        btnGameButton.setSize(this.getWidth() * Assets.MainMenuButton_Width, this.getWidth() * Assets.MainMenuButton_Height);
        btnGameButton.addListener(new MainMenuButtonClickListener());
        table.add(btnGameButton).align(Align.left);

        this.addActor(table);
        //endregion
    }

    class MainMenuButtonClickListener extends ClickListener {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            try {
                switch (event.getListenerActor().getName()) {
                    case "btnNewGame":
                        bidWhistGame.ChangeScreenTo("BiddingStage");
                        break;
                    case "btnOptions":
                        Gdx.app.log("MainMenu Button", "Options fired");
                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
