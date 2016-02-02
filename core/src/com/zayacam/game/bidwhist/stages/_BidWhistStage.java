package com.zayacam.game.bidwhist.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zayacam.Utils;
import com.zayacam.game.BidWhistGame;

public abstract class _BidWhistStage extends Stage implements InputProcessor {

    private String stageName;

    protected BidWhistGame bidWhistGame;
    protected Actor currentScreen;

    //region ctor
    _BidWhistStage(ScreenViewport vPort) {
        super(vPort);
        stageName = Utils.GetStageName(this.toString());
        setKeyboardFocus(currentScreen);
    }

    public _BidWhistStage(BidWhistGame bidWhistGame, ScreenViewport vPort) {
        this(vPort);
        this.bidWhistGame = bidWhistGame ;
    }
    //endregion

    @Override
    public boolean keyDown(int keyCode) {
        String fromStage, toStage = "";
        fromStage = this.getStageName();
        switch (keyCode) {
            //region move forward
            case Input.Keys.SPACE:
                Gdx.app.log(fromStage, "Leaving");
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                switch (fromStage) {
                    case "MainMenuStage":
                        toStage = "BiddingStage";
                        break;
                    case "BiddingStage":
                        toStage = "GamePlayStage";
                        break;
                    case "TrumpSelectStage":
                        toStage = "GamePlayStage";
                        break;
                    case "GamePlayStage":
                        toStage = "BiddingStage";
                        break;
                }
                break;
            //endregion

            //region move backward
            case Input.Keys.B:
                switch (fromStage) {
                    case "MainMenuStage":
                        toStage = "";
                        break;
                    case "BiddingStage":
                        toStage = "MainMenuStage";
                        break;
                    case "TrumpSelectStage":
                        toStage = "BiddingStage";
                        break;
                    case "GamePlayStage":
                        toStage = "TrumpSelectStage";
                        break;
                }
                break;
            //endregion
        }
        if (toStage != "") {
            try {
                bidWhistGame.ChangeScreenTo(toStage);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getStageName() {
        return stageName;
    }
}
