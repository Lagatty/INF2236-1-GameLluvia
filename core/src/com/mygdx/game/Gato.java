package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;


public class Gato {
	
	private Rectangle hitBox;
	private Texture cat;
	private Sound sonidoHerido;
	private int puntos = 0;
	private int velx = 400;
	private boolean herido = false; 
	private int tiempoHeridoMax = 50;
	private int tiempoHerido;

	// --- NUEVOS ATRIBUTOS PARA GATO ---
	private float peso = 4.0f;          // El gato parte con un peso ideal de 4.0 kg
	private final float PESO_MAX = 8.0f; // Si llega a 8 kg le da un infarto
	private final float PESO_MIN = 2.5f; // si baja de 2.5 kg muere por desnutrición
	
	public Gato(Texture tex, Sound ss) {
		cat = tex;
		sonidoHerido = ss;
	}
	
	public int getPuntos() {
		return puntos;
	}
	public void sumarPuntos(int pp) {
		puntos+=pp;
	}
	
	public Rectangle getArea() {
		return hitBox;
	}
	
	public void crear() {

		hitBox = new Rectangle();
		hitBox.x = 800 / 2 - 64 / 2;
		hitBox.y = 20;
		hitBox.width = 64;
		hitBox.height = 64;
	}
	
	public void danar() {
		// Cuando el gato come comida chatarra, el peso sube drásticamente
	    peso += 0.6f; 
	    herido = true; // Hace que el sprite vibre o parpadee en pantalla
	    tiempoHerido = tiempoHeridoMax;
	    sonidoHerido.play(); // Sonido de queja
	}
	
	public void comerSaludable() {
	    // La comida saludable estabiliza el peso quemando un poquito o manteniéndolo
	    if (peso <= 4.0f) {
	        peso -= 0.1f; // Si está sano, baja gradualmente
	    } else {
	    	peso -= 0.3; // Si está más gordo baja más rapido
	    }
	    sumarPuntos(10); // Te da el puntaje normal del juego
	}
	
	public void dibujar(SpriteBatch batch) {
		if (!herido)  
			batch.draw(cat, hitBox.x, hitBox.y);
		else {
			batch.draw(cat, hitBox.x, hitBox.y+ MathUtils.random(-5,5));
			tiempoHerido--;
		
			if (tiempoHerido<=0) herido = false;
		}
	} 
	   
	public float getPeso() {
	    return this.peso;
	}
	
	public boolean estaMuerto() {
	    return (peso >= PESO_MAX || peso <= PESO_MIN);
	}
	
	public void actualizarMovimiento() { 
		   // movimiento desde mouse/touch
		   /*if(Gdx.input.isTouched()) {
			      Vector3 touchPos = new Vector3();
			      touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			      camera.unproject(touchPos);
			      cat.x = touchPos.x - 64 / 2;
			}*/
		   //movimiento desde teclado
		   if(Gdx.input.isKeyPressed(Input.Keys.LEFT)|| Gdx.input.isKeyPressed(Input.Keys.A)) hitBox.x -= velx * Gdx.graphics.getDeltaTime();
		   if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)|| Gdx.input.isKeyPressed(Input.Keys.D)) hitBox.x += velx * Gdx.graphics.getDeltaTime();
		   // que no se salga de los bordes izq y der
		   if(hitBox.x < 0) hitBox.x = 0;
		   if(hitBox.x > 800 - 64) hitBox.x = 800 - 64;
	   }
	    

	public void destruir() {
		    cat.dispose();
	   }
	
   public boolean estaHerido() {
	   return herido;
   }
	   
}
