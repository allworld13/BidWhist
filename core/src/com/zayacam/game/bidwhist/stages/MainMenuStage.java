package com.zayacam.game.bidwhist.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zayacam.game.Assets;
import com.zayacam.game.BidWhistGame;

public class MainMenuStage extends _BidWhistStage {

    public static boolean MainMenuReset = false;
    final Table tblMenuOptions = new Table();
    TextButton btnGameButton;

    public MainMenuStage(BidWhistGame bidWhistGame, ScreenViewport sViewport) {
        super(bidWhistGame, sViewport);

        bidWhistGame.gamePlay.Init();

        //region load main menu buttons
        tblMenuOptions.align(Align.center | Align.topLeft);
        tblMenuOptions.setPosition(getWidth() * .68f, (getHeight() + 6f) * .80f);

        btnGameButton = new TextButton("New Game  ", Assets.Skins);
        btnGameButton.setName("btnNewGame");
        btnGameButton.setSize(getWidth() * Assets.MainMenuButton_Width, getWidth() * Assets.MainMenuButton_Height);
        btnGameButton.addListener(new MainMenuButtonClickListener());
        tblMenuOptions.add(btnGameButton);
        tblMenuOptions.row();
        btnGameButton = new TextButton("Options", Assets.Skins);
        btnGameButton.setName("btnOptions");
        btnGameButton.setSize(getWidth() * Assets.MainMenuButton_Width, getWidth() * Assets.MainMenuButton_Height);
        btnGameButton.addListener(new MainMenuButtonClickListener());
        tblMenuOptions.add(btnGameButton).align(Align.left);

        this.addActor(tblMenuOptions);
        this.setKeyboardFocus(tblMenuOptions);
        //endregion
    }

    @Override
    public void act() {
        if (!MainMenuStage.MainMenuReset)
            bidWhistGame.gamePlay.Init();
        MainMenuStage.MainMenuReset = true;
    }

    @Override
    public void draw() {
        super.draw();

        batch.begin();
        Assets.sprite_background.draw(batch);
        tblMenuOptions.draw(batch, 1f);
        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
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
