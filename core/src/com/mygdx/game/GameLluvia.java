package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameLluvia extends ApplicationAdapter {
	private OrthographicCamera camera;
	private SpriteBatch batch;    
	private BitmapFont font;
	   
	private Gato gato;
	private Comida comida;
	private Music stableMusic;
	private Music warningMusic;
	private Sound deathSound;
	private boolean sonidoMuerteReproducido = false;
	
	private Texture texGatoInfarto;
	private Texture texGatoEsqueleto;
	
	// --- CONTROL DE PANTALLAS EXPRESS ---
	// 0: Menú de Inicio, 1: Jugando, 2: Créditos
	private int estadoPantalla = 0; 

	@Override
	public void create () {
		font = new BitmapFont(); 
		
		Sound hurtSound = Gdx.audio.newSound(Gdx.files.internal("hurt.ogg"));
		Texture gatoFlaco = new Texture(Gdx.files.internal("FlacoCatSmoking.png"));
		Texture gatoNormal = new Texture(Gdx.files.internal("NormalCatDance.png"));
		Texture gatoGordo = new Texture(Gdx.files.internal("GordoCatEating.png"));
          
		gato = new Gato(gatoFlaco, gatoNormal, gatoGordo, hurtSound);
		
		
		texGatoInfarto = new Texture(Gdx.files.internal("death_heart_attack.png"));
		texGatoEsqueleto = new Texture(Gdx.files.internal("death_malnutrition.png")); 
		
		Texture comidaSaludable = new Texture(Gdx.files.internal("Food_Good.png"));
		Texture comidaMala = new Texture(Gdx.files.internal("Food_Bad.png"));
          
		Sound dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		deathSound = Gdx.audio.newSound(Gdx.files.internal("game_over_sound.wav"));
		
		stableMusic = Gdx.audio.newMusic(Gdx.files.internal("Music_PesoEstable.mp3"));
		warningMusic = Gdx.audio.newMusic(Gdx.files.internal("Music_Peligro.mp3"));
		
		comida = new Comida(comidaSaludable, comidaMala, dropSound, stableMusic);
      
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();
		
		gato.crear();
		comida.crear();
		
		stableMusic.setLooping(true);
		stableMusic.play();
	}
	
	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0.2f, 1); 
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		// --- ESTADO 0: MENÚ DE INICIO ---
		if (estadoPantalla == 0) {
			if (!stableMusic.isPlaying()) stableMusic.play();
			
			batch.begin();
			font.draw(batch, "¡SALVA AL GATO SALUDABLE!", 320, 350);
			font.draw(batch, "1. Comenzar Juego", 340, 260);
			font.draw(batch, "2. Ver Créditos", 340, 220);
			font.draw(batch, "3. Salir del Juego", 340, 180);
			batch.end();
			
			if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
				gato.reiniciar();
				comida.reiniciar();
				estadoPantalla = 1;
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
				estadoPantalla = 2;
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
				Gdx.app.exit(); 
			}
			return;
		}
		
		// --- ESTADO 2: PANTALLA DE CRÉDITOS ---
		if (estadoPantalla == 2) {
			batch.begin();
			font.draw(batch, "--- CRÉDITOS DEL PROYECTO ---", 290, 320);
			font.draw(batch, "Desarrollado por: Pablo Alvarez / Francisco Gatica", 280, 260);
			font.draw(batch, "Asignatura: Programación Avanzada - PUCV", 270, 220);
			font.draw(batch, "Presiona ESC para volver al menú", 300, 120);
			batch.end();
			
			if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
				estadoPantalla = 0;
			}
			return;
		}

		// --- ESTADO 1: JUEGO ACTIVO / GAME OVER ---
		if (gato.estaMuerto()) {
			if (stableMusic.isPlaying()) stableMusic.stop();
			if (warningMusic.isPlaying()) warningMusic.stop();
			
			if (!sonidoMuerteReproducido) {
				deathSound.play();
				sonidoMuerteReproducido = true;
			}
			
			batch.begin();
			font.draw(batch, "Presiona ENTER para volver al Menú Inicial", 280, 80);
			
			if (gato.getCausaMuerte().equals("Infarto")) {
				font.draw(batch, "¡GAME OVER! Tu gato sufrió un infarto por sobrepeso.", 240, 420);
				batch.draw(texGatoInfarto, 800/2 - 128/2, 480/2 - 128/2, 128, 128);
			} else if (gato.getCausaMuerte().equals("Desnutricion")) {
				font.draw(batch, "¡GAME OVER! Tu gato murió de desnutrición.", 250, 420);
				batch.draw(texGatoEsqueleto, 800/2 - 128/2, 480/2 - 128/2, 128, 128);
			}
			batch.end();
			
			if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
				gato.reiniciar();
				comida.reiniciar();
				sonidoMuerteReproducido = false;
				estadoPantalla = 0; 
			}
			return; 
		}
		
		// LÓGICA ACTIVA DE JUEGO RUNTIME
		gato.actualizarMovimiento();        
		comida.actualizarMovimiento(gato);     
			
		if (gato.getPeso() <= 3.5f || gato.getPeso() >= 6.5f) {
			if (stableMusic.isPlaying()) {
				stableMusic.pause();
				warningMusic.setLooping(true);
				warningMusic.play();
			}
		} else if (warningMusic.isPlaying()) {
			warningMusic.stop();
			stableMusic.play();
		}
		
		// DIBUJAR JUEGO ACTIVO
		batch.begin();
		font.draw(batch, "Puntaje total: " + gato.getPuntos(), 5, 475);
		font.draw(batch, String.format("Peso Gato : %.2f kg", gato.getPeso()), 650, 475);
		
		gato.dibujar(batch);
		comida.actualizarDibujoLluvia(batch);
		batch.end();
	}
	
	@Override
	public void dispose () {
		gato.destruir();
		comida.destruir();
		batch.dispose();
		font.dispose();
		texGatoInfarto.dispose();
		texGatoEsqueleto.dispose();
		stableMusic.dispose();
		warningMusic.dispose();
		deathSound.dispose();
	}
}