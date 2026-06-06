package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

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
		font = new BitmapFont(); // use libGDX's default Arial font
		 
		// load the images for the droplet and the bucket, 64x64 pixels each 	     
		Sound hurtSound = Gdx.audio.newSound(Gdx.files.internal("hurt.ogg"));
          
		gato = new Gato(new Texture(Gdx.files.internal("bucket.png")),hurtSound);
		
		// load the drop sound effect and the rain background "music" 
		Texture comidaSaludable = new Texture(Gdx.files.internal("drop.png"));
		Texture comidaMala = new Texture(Gdx.files.internal("dropBad.png"));
          
		Sound dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
         
		Music rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		comida = new Comida(comidaSaludable, comidaMala, dropSound, rainMusic);
	      
		// camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();
		// creacion del gato
		gato.crear();
	      
		// creacion de la comdia
		comida.crear();
	}
	


	@Override
	
	public void render () {
	
		//limpia la pantalla con color azul obscuro.
		ScreenUtils.clear(0, 0, 0.2f, 1);
		
		//actualizar matrices de la camara
		camera.update();
		
		//actualizar 
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		//dibujar textos
		font.draw(batch, "Comidas atrapadas: " + gato.getPuntos(), 5, 475);
		font.draw(batch, String.format("Peso Gato : %.2f%n", gato.getPeso()), 650, 475);
		
		
		gato.dibujar(batch);
		comida.actualizarDibujoLluvia(batch);

		if(gato.estaMuerto()) {
			if(gato.getPeso() > 8.0f) {
				font.draw(batch, "¡GAME OVER! Tu gato sufrió un infarto", 280, 250);
			} else if(gato.getPeso() < 2.5f) {
				font.draw(batch, "¡GAME OVER! Tu gato sufrió de desnutrición", 250, 250);
			}
		} else {
			// movimiento del gato desde teclado
	        gato.actualizarMovimiento();        
			// caida de la comida 
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

