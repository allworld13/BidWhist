package com.zayacam.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zayacam.game.bidwhist.game.BidPlayer;
import com.zayacam.game.bidwhist.game.GamePlay;
import com.zayacam.game.bidwhist.stages.BiddingStage;
import com.zayacam.game.bidwhist.stages.GamePlayStage;
import com.zayacam.game.bidwhist.stages.MainMenuStage;
import com.zayacam.game.bidwhist.stages._BidWhistStage;

public class BidWhistGame extends Game implements InputProcessor {

	public GamePlay gamePlay;
	public _BidWhistStage stage;
	public ScreenViewport sViewport;
	public OrthographicCamera camera;
	InputMultiplexer im;
	public boolean promptShown = false;

	@Override
	public void create() {
		camera = new OrthographicCamera(Assets.GameWidth, Assets.GameHeight);
		sViewport = new ScreenViewport(camera);
		gamePlay = new GamePlay();
		try {
			this.ChangeScreenTo("MainMenuStage");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render() {
		super.render();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	public void ChangeScreenTo(String gamePlayScreen) throws InterruptedException {
		if (stage != null)
			stage.dispose();

		switch (gamePlayScreen) {
			case "MainMenuStage":
				LoadMainMenu();
				break;
			case "BiddingStage":
				LoadBiddingMenu();
				break;
			case "GamePlayStage":
				LoadGamePlay();
				break;
		}
		im = new InputMultiplexer();
		if (stage != null) {
			im.addProcessor(stage);
		}
		im.addProcessor(this);

		Gdx.input.setInputProcessor(im);
	}

	//region game screen
	private void LoadMainMenu() {
		stage = new MainMenuStage(this, sViewport);
	}

	private void LoadBiddingMenu() {
		stage = new BiddingStage(this, sViewport);
	}

	private void LoadGamePlay() throws InterruptedException {
		stage = new GamePlayStage(this, sViewport);

	}
	//endregion

	//region Input Processor Overrides
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	public void PlayersTurnToBid(BidPlayer bp) {
		if (!promptShown)
			Gdx.app.log("Bidding", bp.toString() + "\'s  turn to bid.");
		promptShown = true;
	}

	public void DetermineBidWinner() {
		if (!promptShown)
			Gdx.app.log("Bidding", "Determining Bid Winner");




		promptShown = true;
	}

	public boolean PlayerHasBidded(BidPlayer biddingPlayer) throws InterruptedException {
		boolean validBid = false;
		Gdx.app.log("Bidding", "\t----> "+ biddingPlayer.toString() + " has bidded");
		gamePlay.PlayerHasBidded(biddingPlayer);
		validBid =  gamePlay.ValidatePlayersBid(biddingPlayer);
		if (!validBid)  {
			biddingPlayer.setPlayerHasBidded(false);
		} else {
			promptShown = false;
		}

		return validBid;
	}

	public void PlayerPassed(BidPlayer biddingPlayer) {
		Gdx.app.log("Bidding", "\t----> "+ biddingPlayer.toString() + " passed");
		gamePlay.PlayerHasPassed(biddingPlayer);
		promptShown = false;
	}


	//endregion
}
