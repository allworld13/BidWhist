package com.zayacam.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.ArrayList;

public class Assets {
    //public static final Dialog PopMessage = new Dialog("",Assets.Skins);
    public static final BitmapFont PlayerNameFont = new BitmapFont(Gdx.files.internal("fonts/DancingScript.fnt"));
    public static final BitmapFont ScreenTitleFont = new BitmapFont(Gdx.files.internal("fonts/ScreenTitle.fnt"));
    public static final Skin Skins = new Skin(Gdx.files.internal("uiskin.json"));
    public static Animation loading_animation;
    public static ArrayList<Image> gfxDeck;
    public static ArrayList<TextureRegion> cardSuits = null;
    public static Texture text_background;
    public static Sprite sprite_background;
    private static Sound sound;
    public static GlyphLayout textBounds;
    public static boolean isDeckDrawn;

    public static final int GameWidth = 850; //1920;
    public static final int GameHeight = 600;//1080;
    public static final int CardWidth = 225;
    public static final int CardHeight = 343;
    public static final int CardBack = 54;
    public static final float P1YBaseLine = 5;
    public static final float P1CardYLevitate = 20f;
    public static final float MainMenuButton_Width = .20f;
    public static final float MainMenuButton_Height = .10f;
    public static final float PlayerCardHeightRatio = 0.25F;
    public static final float PlayerCardWidthRatio = 8.8F;
    public static final float PlayerCard_Y_Ratio = 2.6F;
    public static final float PlayerNameEastWest = 0.38f;
    public static final float ScreenTitleYPos = 0.9f;
    public static final float FirstPlayerCardWidth = 0.20f;
    public static final float FirstPlayerCardHeight = .29f;

    static {
        LoadSuits();
    }


    public static void LoadSplashScreen() {
        text_background = new Texture(Gdx.files.internal("screens/splashScreen.png"));
        initSprite();
    }

    public static void LoadGameScreen() {
        LoadGameScreen(false);
    }

    public static void LoadGameScreen(boolean mainMenu) {
        if (mainMenu)
            text_background = new Texture(Gdx.files.internal("screens/mainmenu.png"));
        else
            text_background = new Texture(Gdx.files.internal("screens/blankScreen.png"));
        initSprite();
    }

    private static void initSprite() {
        sprite_background = new Sprite(text_background, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        text_background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public static void DrawDeckOfCards() {
        try {

            if (!isDeckDrawn || gfxDeck == null ) {
                gfxDeck = new ArrayList<>() ;
                Texture deck_sheet = new Texture(Gdx.files.internal("cardSheet.png"));
                TextureRegion[][] temp = TextureRegion.split(deck_sheet, Assets.CardWidth, Assets.CardHeight);
                TextureRegion t;
                Image image;

                for (int i = 0; i <= 4; i++) {
                    for (int j = 0; j < 13; j++) {
                        t = temp[i][j];
                        image = new Image(t);
                        gfxDeck.add(image);
                    }
                }
                isDeckDrawn = true;
            }
        } catch(Exception ex) {
            isDeckDrawn = false;
        }
        //loading_animation = new Animation(1/10F, gfxDeck.toArray(new TextureRegion[gfxDeck.size()]));
    }

    private static void LoadSuits() {
        Texture texture = new Texture(Gdx.files.internal("pickASuit.png"));
        TextureRegion[][] textureRegions = TextureRegion.split(texture, 290, 300);
        cardSuits = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 4; j++) {
                cardSuits.add(textureRegions[i][j]);
            }
        }
    }

    public static void PlayDeckShuffling() {
        sound = Gdx.audio.newSound(Gdx.files.internal("sounds/shuffleDeck.wav"));
        sound.play();
    }

    public static void ClearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }
}