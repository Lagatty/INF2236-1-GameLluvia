package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color; 
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer; 
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType; 
import com.badlogic.gdx.math.Rectangle; 
import com.badlogic.gdx.math.Vector3; 
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
	private BitmapFont fontTitulo;
	
	private ShapeRenderer shapeRenderer; 
	
	private Rectangle btnJugar;
	private Rectangle btnCreditos;
	private Rectangle btnSalir;
	
	// --- CONTROL DE PANTALLAS EXPRESS ---
	// 0: Menú de Inicio, 1: Jugando, 2: Créditos
	private int estadoPantalla = 0; 

	@Override
	public void create () {
		font = new BitmapFont(); 
		font.getData().setScale(1.2f); 
		font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		
		fontTitulo = new BitmapFont();
		fontTitulo.getData().setScale(2.5f); 
		fontTitulo.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		
		shapeRenderer = new ShapeRenderer();
		
		// --- ZONAS DE LOS BOTONES --- (X, Y, Ancho, Alto)
		btnJugar = new Rectangle(800/2 - 100, 250, 200, 40);
		btnCreditos = new Rectangle(800/2 - 100, 180, 200, 40);
		btnSalir = new Rectangle(800/2 - 100, 110, 200, 40);
		
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
		
		// --- ESTADO 0: MENÚ DE INICIO CON BOTONES DIBUJADOS ---
		if (estadoPantalla == 0) {
			if (!stableMusic.isPlaying()) stableMusic.play();
			
			// Primero dibujamos los botones de fondo (ShapeRenderer)
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(Color.DARK_GRAY);
			shapeRenderer.rect(btnJugar.x, btnJugar.y, btnJugar.width, btnJugar.height);
			shapeRenderer.rect(btnCreditos.x, btnCreditos.y, btnCreditos.width, btnCreditos.height);
			shapeRenderer.rect(btnSalir.x, btnSalir.y, btnSalir.width, btnSalir.height);
			shapeRenderer.end();
			
			// Después dibujamos las letras encima (SpriteBatch)
			batch.begin();
			fontTitulo.draw(batch, "¡SALVA  AL GATO SALUDABLE!", 180, 400); 
			font.draw(batch, "Comenzar Juego", btnJugar.x + 25, btnJugar.y + 28);
			font.draw(batch, "Ver Creditos", btnCreditos.x + 40, btnCreditos.y + 28);
			font.draw(batch, "Salir del Juego", btnSalir.x + 35, btnSalir.y + 28);
			batch.end();
			
			// Lógica del clic del Mouse
			if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
				Vector3 touchPos = new Vector3();
				touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				camera.unproject(touchPos); 
				
				if (btnJugar.contains(touchPos.x, touchPos.y)) {
					gato.reiniciar();
					comida.reiniciar();
					estadoPantalla = 1;
				} else if (btnCreditos.contains(touchPos.x, touchPos.y)) {
					estadoPantalla = 2;
				} else if (btnSalir.contains(touchPos.x, touchPos.y)) {
					Gdx.app.exit();
				}
			}
			return;
		}
		
		// --- ESTADO 2: PANTALLA DE CRÉDITOS ---
		if (estadoPantalla == 2) {
			batch.begin();
			font.draw(batch, "--- CRÉDITOS DEL PROYECTO ---", 260, 320);
			font.draw(batch, "Desarrollado por: Pablo Alvarez / Francisco Gatica", 190, 260);
			font.draw(batch, "Asignatura: Programación Avanzada - PUCV", 220, 220);
			font.draw(batch, "Presiona ESC para volver al menú", 250, 120);
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
			font.draw(batch, "Presiona ENTER para volver al Menu", 280, 80);

			if (gato.getCausaMuerte().equals("Infarto")) {
				fontTitulo.draw(batch, "¡INFARTO POR SOBREPESO!", 200, 430);
				batch.draw(texGatoInfarto, 800/2 - 128/2, 480/2 - 128/2, 128, 128);
			} else if (gato.getCausaMuerte().equals("Desnutricion")) {
				fontTitulo.draw(batch, "¡MURIO DE DESNUTRICION!", 200, 430);
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
		
		// DIBUJAR JUEGO ACTIVO (Imágenes)
		batch.begin();
		font.draw(batch, "Puntaje total: " + gato.getPuntos(), 5, 475);
		font.draw(batch, String.format("Peso Gato : %.2f kg", gato.getPeso()), 350, 475);
		
		gato.dibujar(batch);
		comida.actualizarDibujoLluvia(batch);
		batch.end();

		// DIBUJAR LA BARRA DE SALUD AL FINAL (Figuras)
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.DARK_GRAY);
		shapeRenderer.rect(550, 460, 200, 15);
		
		float pesoActual = gato.getPeso();
		if (pesoActual <= 3.5f || pesoActual >= 6.5f) {
			shapeRenderer.setColor(Color.RED); 
		} else if (pesoActual <= 4.5f && pesoActual >= 3.5f) {
			shapeRenderer.setColor(Color.YELLOW); 
		} else {
			shapeRenderer.setColor(Color.GREEN); 
		}
		
		float anchoBarra = ((pesoActual - 2.5f) / 5.5f) * 200;
		if (anchoBarra < 0) anchoBarra = 0;
		if (anchoBarra > 200) anchoBarra = 200;
		
		shapeRenderer.rect(550, 460, anchoBarra, 15);
		shapeRenderer.end();
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
		fontTitulo.dispose();
		shapeRenderer.dispose();
	}
}