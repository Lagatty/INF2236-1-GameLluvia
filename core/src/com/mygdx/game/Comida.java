package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Comida {
	private Array<Rectangle> rainDropsPos;
	private Array<Integer> rainDropsType;
    private long lastDropTime;
    private Texture gotaBuena;
    private Texture gotaMala;
    private Sound dropSound;
    private Music stableMusic;
	   
	public Comida(Texture gotaBuena, Texture gotaMala, Sound ss, Music mm) {
		stableMusic = mm;
		dropSound = ss;
		this.gotaBuena = gotaBuena;
		this.gotaMala = gotaMala;
	}
	
	public void crear() {
		rainDropsPos = new Array<Rectangle>();
		rainDropsType = new Array<Integer>();
		crearComida();
	      // start the playback of the background music immediately
	      stableMusic.setLooping(true);
	      stableMusic.play();
	}
	
	public void reiniciar() {
	    //Limpiar arreglo de comida
	    rainDropsPos.clear(); 
	    // Vuelve a iniciar la música si se detuvo
	    stableMusic.play(); 
	}
	
	private void crearComida() {
	      Rectangle raindrop = new Rectangle();
	      raindrop.x = MathUtils.random(0, 800-64);
	      raindrop.y = 480;
	      raindrop.width = 64;
	      raindrop.height = 64;
	      rainDropsPos.add(raindrop);
	      // ver el tipo de gota
	      if (MathUtils.random(1,10)<3)	    	  
	         rainDropsType.add(1);
	      else 
	    	 rainDropsType.add(2);
	      lastDropTime = TimeUtils.nanoTime();
	   }
	
   public void actualizarMovimiento(Gato gato) { 
	   // generar gotas de lluvia 
	   if(TimeUtils.nanoTime() - lastDropTime > 100000000) crearComida();
	  
	   
	   // revisar si la comida cayó al suelo o chocaron con el gato
	   for (int i=0; i < rainDropsPos.size; i++ ) {
		  Rectangle raindrop = rainDropsPos.get(i);
	      raindrop.y -= 300 * Gdx.graphics.getDeltaTime();
	      //cae al suelo y se elimina
	      if(raindrop.y + 64 < 0) {
	    	  rainDropsPos.removeIndex(i); 
	    	  rainDropsType.removeIndex(i);
	      }
	      if(raindrop.overlaps(gato.getArea())) { //la comida choca con el gato
	    	if(rainDropsType.get(i)==1) { // comida danina
	    	  gato.danar();
	    	  
	    	  rainDropsPos.removeIndex(i);
	          rainDropsType.removeIndex(i);
	      	}else { // comida saludable
	    	  gato.comerSaludable();
	          dropSound.play();
	          rainDropsPos.removeIndex(i);
	          rainDropsType.removeIndex(i);
	      	}
	      }
	   }   
   }
   
   public void actualizarDibujoLluvia(SpriteBatch batch) { 
	   
	  for (int i=0; i < rainDropsPos.size; i++ ) {
		  Rectangle raindrop = rainDropsPos.get(i);
		  if(rainDropsType.get(i)==1) // gota danina
	         batch.draw(gotaMala, raindrop.x, raindrop.y); 
		  else
			 batch.draw(gotaBuena, raindrop.x, raindrop.y); 
	   }
   }
   public void destruir() {
	      dropSound.dispose();
	      stableMusic.dispose();
   }
   
}
