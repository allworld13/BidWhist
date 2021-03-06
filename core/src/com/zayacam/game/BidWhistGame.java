package com.zayacam.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zayacam.game.bidwhist.cards.Card;
import com.zayacam.game.bidwhist.game.BidPlayer;
import com.zayacam.game.bidwhist.game.GamePlay;
import com.zayacam.game.bidwhist.stages.*;

public class BidWhistGame extends Game implements InputProcessor {

	public GamePlay gamePlay;
	public _BidWhistStage stage;
	public ScreenViewport sViewport;
	public OrthographicCamera camera;
	public boolean promptShown = false;

	public void setLastRoundWinner(BidPlayer bidPlayer) {
		if (gamePlay != null)
			gamePlay.lastRoundWinner = bidPlayer;
	}

	public BidPlayer GetLastRoundWinner() {
		if (gamePlay != null) {
			return gamePlay.lastRoundWinner;
		}
		return null;
	}

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
		Assets.ClearScreen();
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	public void ChangeScreenTo(String gamePlayScreen) throws InterruptedException {
		if (stage != null)
			stage.dispose();

		stage = null;
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		switch (gamePlayScreen) {
			case "MainMenuStage":
				LoadMainMenu();
				break;
			case "BiddingStage":
				LoadBiddingMenu();
				break;
			case "TrumpSelectStage":
				LoadDetermineTrumpStage();
				break;
			case "GamePlayStage":
				LoadGamePlay();
				break;
		}
		stage.im = new InputMultiplexer(stage, this);
		Gdx.input.setInputProcessor(stage.im);
	}

	//region game screen
	private void LoadMainMenu() {
		stage = new MainMenuStage(this, sViewport);
	}

	private void LoadBiddingMenu() {
		stage = new BiddingStage(this, sViewport);
	}

	private void LoadDetermineTrumpStage() {
		stage = new TrumpSelectStage(this, sViewport);
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

	/*
		Declares the bid winner after all bids are accepted, and then declares the winner
    */
	public BidPlayer DetermineBidWinner() {
		BidPlayer bidWinner;
		if (!promptShown)
			Gdx.app.log("Bidding", "Determining Bid Winner");

		promptShown = true;
		bidWinner = gamePlay.DetermineBidWinner();
		return bidWinner;
	}

	public boolean PlayerHasBidded(BidPlayer biddingPlayer) throws InterruptedException {
		boolean validBid = false;
		Gdx.app.log("Bidding", "\t----> "+ biddingPlayer.toString() + " has bidded");
		gamePlay.PlayerHasBidded(biddingPlayer);
		try {
			validBid = gamePlay.ValidatePlayersBid(biddingPlayer);
			if (!validBid) {
				biddingPlayer.setPlayerHasBidded(false);
			} else {
				promptShown = false;
				GamePlay.GAME_BOOKS = biddingPlayer.getBidHand_Books();
				GamePlay.GAME_DIRECTION = biddingPlayer.getBidDirection();
				GamePlay.GAME_SUIT = biddingPlayer.getBidSuit();
			}
		} catch (Exception ex) {
			Gdx.app.log(stage.getStageName(), ex.getMessage());
		}

		return validBid;
	}

	public void PlayerPassed(BidPlayer biddingPlayer) {
		Gdx.app.log("Bidding", "\t----> "+ biddingPlayer.toString() + " passed");
		gamePlay.PlayerHasPassed(biddingPlayer);
		promptShown = false;
	}

	public void YouMustBid(BidPlayer biddingPlayer) {
		Gdx.app.log("Bidding", "\t **** " + biddingPlayer.toString() + " must bid!");
	}

	public void ResetAllCards() {
		try {
			for (Card c : gamePlay.deck.getCards()) {
				GamePlay.RestCard(c);
			}
		} catch (Exception ex) {
		}
		try {

			for (Card c : gamePlay.KittyHand.getCards()) {
				GamePlay.RestCard(c);
			}
		} catch (Exception ex) {
		}
		try {

			for (BidPlayer bp : gamePlay.gamePlayers) {
				for (Card c : bp.getHand().getCards()) {
					GamePlay.RestCard(c);
				}
			}
		} catch (Exception ex) {
		}

	}


//endregion
}
