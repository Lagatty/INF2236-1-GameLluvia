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
	
	@Override
	public void create () {
		font = new BitmapFont(); 
		
		Sound hurtSound = Gdx.audio.newSound(Gdx.files.internal("hurt.ogg"));
		
		Texture gatoFlaco = new Texture(Gdx.files.internal("FlacoCatSmoking.png"));
		Texture gatoNormal = new Texture(Gdx.files.internal("NormalCatDance.png"));
		Texture gatoGordo = new Texture(Gdx.files.internal("GordoCatEating.png"));
          
		gato = new Gato(gatoFlaco, gatoNormal, gatoGordo, hurtSound);
		
		Texture comidaSaludable = new Texture(Gdx.files.internal("Food_Good.png"));
		Texture comidaMala = new Texture(Gdx.files.internal("Food_Bad.png"));
          
		Sound dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
         
		Music rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		comida = new Comida(comidaSaludable, comidaMala, dropSound, rainMusic);
	      
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();
		
		gato.crear();
		comida.crear();
	}
	
	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0.2f, 1);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		font.draw(batch, "Comidas atrapadas: " + gato.getPuntos(), 5, 475);
		font.draw(batch, String.format("Peso Gato : %.2f%n", gato.getPeso()), 650, 475);
		
		gato.dibujar(batch);
		comida.actualizarDibujoLluvia(batch);

		if(gato.estaMuerto()) {
			if(gato.getPeso() >= 8.0f) {
				font.draw(batch, "¡GAME OVER! Tu gato sufrió un infarto", 280, 250);
			} else if(gato.getPeso() <= 2.5f) {
				font.draw(batch, "¡GAME OVER! Tu gato sufrió de desnutrición", 250, 250);
			}
			
			// --- NUEVA LÓGICA DE REINICIO ---
			font.draw(batch, "Presiona ENTER para reiniciar", 300, 200);
			
			if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
				gato.reiniciar();
				comida.reiniciar();
			}
		} else {
			gato.actualizarMovimiento();        
			comida.actualizarMovimiento(gato);     
		}
		
		batch.end();	
	}
	
	@Override
	public void dispose () {
	      gato.destruir();
          comida.destruir();
	      batch.dispose();
	      font.dispose();
	}
}