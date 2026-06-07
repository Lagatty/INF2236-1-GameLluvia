package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Gato {
	
	private Rectangle hitBox;
	private Sound sonidoHerido;
	private int puntos = 0;
	private int velx = 400;
	private boolean herido = false; 
	private int tiempoHeridoMax = 50;
	private int tiempoHerido;

	// --- ATRIBUTOS DE PESO ---
	private float peso = 4.0f;          // Parte en 4.0 kg (Estado Normal)
	private final float PESO_MAX = 8.0f; 
	private final float PESO_MIN = 2.5f; 
	
	// --- TEXTURAS Y ANIMACIONES ---
	private Texture texDelgado, texNormal, texGordo;
	private Animation<TextureRegion> animDelgado;
	private Animation<TextureRegion> animNormal;
	private Animation<TextureRegion> animGordo;
	private float stateTime; 
	
	// El constructor ahora recibe las 3 texturas correspondientes a los estados
	public Gato(Texture tDelgado, Texture tNormal, Texture tGordo, Sound ss) {
		this.texDelgado = tDelgado;
		this.texNormal = tNormal;
		this.texGordo = tGordo;
		this.sonidoHerido = ss;
		this.stateTime = 0f;
		
		// Suponiendo que todos tus spritesheets tienen la misma estructura (ej. 4 columnas, 1 fila)
		int cols = 4;
		int rows = 1;
		
		// Inicializamos las 3 animaciones usando el método auxiliar de abajo
		this.animDelgado = crearAnimacion(texDelgado, cols, rows);
		this.animNormal  = crearAnimacion(texNormal, cols, rows);
		this.animGordo   = crearAnimacion(texGordo, cols, rows);
	}
	
	// Método auxiliar para evitar duplicar el código de recorte de texturas
	private Animation<TextureRegion> crearAnimacion(Texture sheet, int cols, int rows) {
		TextureRegion[][] tmp = TextureRegion.split(sheet, 
				sheet.getWidth() / cols, 
				sheet.getHeight() / rows);
		
		TextureRegion[] frames = new TextureRegion[cols * rows];
		int index = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				frames[index++] = tmp[i][j];
			}
		}
		return new Animation<TextureRegion>(0.1f, frames);
	}
	
	public int getPuntos() { return puntos; }
	public void sumarPuntos(int pp) { puntos += pp; }
	public Rectangle getArea() { return hitBox; }
	public float getPeso() { return this.peso; }
	public boolean estaHerido() { return herido; }
	
	public void crear() {
		hitBox = new Rectangle();
		
		// Si el frame original es 126x214, la mitad es 63x107
	    hitBox.width = 63; 
	    hitBox.height = 107;
	    
	    // Centramos en X tomando en cuenta el nuevo ancho
	    hitBox.x = 800 / 2 - hitBox.width / 2;
	    hitBox.y = 20;
	}
	
	public void danar() {
	    peso += 0.6f; // Subirá de peso al comer comida chatarra/dañarse
	    herido = true; 
	    tiempoHerido = tiempoHeridoMax;
	    sonidoHerido.play(); 
	}
	
	public void reiniciar() {
	    this.peso = 4.0f;     // Vuelve al peso normal
	    this.puntos = 0;      // Reinicia el contador
	    this.herido = false;  // Quita el estado de daño
	    crear();              // Reposiciona al gato en el centro
	}
	
	
	public void comerSaludable() {
	    if (peso <= 4.0f) {
	        peso -= 0.1f; // Puede empezar a volverse flaco si abusa de lo saludable
	    } else {
	    	peso -= 0.3f; 
	    }
	    sumarPuntos(10); 
	}
	
	public void dibujar(SpriteBatch batch) {
	    stateTime += Gdx.graphics.getDeltaTime();
	    
	    Animation<TextureRegion> animacionActiva;
	    if (peso < 3.5f) {
	        animacionActiva = animDelgado;
	    } else if (peso <= 5.5f) {
	        animacionActiva = animNormal;
	    } else {
	        animacionActiva = animGordo;
	    }
	    
	    TextureRegion frameActual = animacionActiva.getKeyFrame(stateTime, true);

	    // NUEVO: Agregamos hitBox.width y hitBox.height como parámetros
	    if (!herido) {
	        batch.draw(frameActual, hitBox.x, hitBox.y, hitBox.width, hitBox.height);
	    } else {
	        batch.draw(frameActual, hitBox.x, hitBox.y + MathUtils.random(-5, 5), hitBox.width, hitBox.height);
	        tiempoHerido--;
	    
	        if (tiempoHerido <= 0) herido = false;
	    }
	}
	   
	public boolean estaMuerto() {
	    return (peso >= PESO_MAX || peso <= PESO_MIN);
	}
	
	public void actualizarMovimiento() { 
	   if(Gdx.input.isKeyPressed(Input.Keys.LEFT)|| Gdx.input.isKeyPressed(Input.Keys.A)) hitBox.x -= velx * Gdx.graphics.getDeltaTime();
	   if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)|| Gdx.input.isKeyPressed(Input.Keys.D)) hitBox.x += velx * Gdx.graphics.getDeltaTime();
	   
	   if(hitBox.x < 0) hitBox.x = 0;
	   if(hitBox.x > 800 - 64) hitBox.x = 800 - 64;
   }
   
	public void destruir() {
		// IMPORTANTE: Liberar las 3 texturas de la memoria
		texDelgado.dispose();
		texNormal.dispose();
		texGordo.dispose();
   }
}