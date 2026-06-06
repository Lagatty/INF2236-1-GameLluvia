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
		Texture gota = new Texture(Gdx.files.internal("drop.png"));
		Texture gotaMala = new Texture(Gdx.files.internal("dropBad.png"));
          
		Sound dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
         
		Music rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		comida = new Comida(gota, gotaMala, dropSound, rainMusic);
	      
		// camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();
		// creacion del tarro
		gato.crear();
	      
		// creacion de la lluvia
		comida.crear();
	}
	


	@Override
	
	public void render () {
	
		//limpia la pantalla con color azul obscuro.
		ScreenUtils.clear(0, 0, 0.2f, 1);
		//actualizar matrices de la cámara
		camera.update();
		//actualizar 
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		//dibujar textos
		font.draw(batch, "Gotas totales: " + gato.getPuntos(), 5, 475);
		font.draw(batch, "Vidas : " + gato.getPeso(), 720, 475);
		
		if (!gato.estaHerido()) {
			// movimiento del tarro desde teclado
	        gato.actualizarMovimiento();        
			// caida de la lluvia 
	        comida.actualizarMovimiento(gato);	   
		}
		
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
	}
}

