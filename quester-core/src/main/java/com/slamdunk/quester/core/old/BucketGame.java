package com.slamdunk.quester.core.old;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class BucketGame implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;
	
	private Rectangle bucket;
	private Vector3 touchPos;
	
	private Array<Rectangle> raindrops;
	private long lastDropTime;
	
	private boolean dndActivated;

	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();
		touchPos = new Vector3();
		
		// load the images for the droplet and the bucket, 48x48 pixels each
		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		  
		// load the drop sound effect and the rain background "music"
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		  
		// start the playback of the background music immediately
		rainMusic.setLooping(true);
		rainMusic.play();
		
		// Définition de la taille et de la position initiale du sceau
		bucket = new Rectangle();
		bucket.x = 800 / 2 - 48 / 2;
		bucket.y = 20;
		bucket.width = 48;
		bucket.height = 48;
		
		// Ajout d'une première goutte
		raindrops = new Array<Rectangle>();
		spawnRaindrop();
	}

	@Override
	public void resize (int width, int height) {
	}

	@Override
	public void render () {
		// Effacement de l'écran
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Recalcul des matrices de la caméra
		camera.update();
		
		// Dessin du sceau et des gouttes
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for(Rectangle raindrop: raindrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		batch.draw(bucketImage, bucket.x, bucket.y);
		batch.end();
		
		// Prise en compte des entrées du joueur
		if(Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			
			// Si le drag'n'drop n'est pas encore activé, on regarde si on l'active
			if (!dndActivated) {
				dndActivated = bucket.contains(touchPos.x, touchPos.y);
			}
		} else {
			// On n'appuie plus sur l'écran, donc le drag'n'drop est désactivé
			dndActivated = false;
		}
		
		// Si le dnd est activé et que le pointeur n'est pas encore 
		// sur le sceau, alors on déplace le sceau vers le pointeur.
		if (dndActivated
		&& touchPos.x != bucket.x + bucket.width / 2) {
			// Distance à parcourir
			float distance = (touchPos.x - 48 /2) - bucket.x;
			// On ne déplace le sceau que si le pointeur est suffisament loin
			if (Math.abs(distance) > 10) {
				// Pas de déplacement
				float step = Math.max(0, 400 * Gdx.graphics.getDeltaTime());
				// Déplacement du sceau
				if (distance > 0) {
					bucket.x += step;
				} else {
					bucket.x -= step;
				}
			}
		}
		
		// Ajout d'une nouvelle goutte
		if(TimeUtils.nanoTime() - lastDropTime > 1000000000) {
			spawnRaindrop();
		}
		
		// Déplacement des gouttes vers le bas de l'écran
		Iterator<Rectangle> iter = raindrops.iterator();
		while(iter.hasNext()) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if(raindrop.y + 48 < 0) {
				iter.remove();
			} else if(raindrop.overlaps(bucket)) {
				dropSound.play();
				iter.remove();
			}
		}
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();
	}
	
	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800-48);
		raindrop.y = 480;
		raindrop.width = 48;
		raindrop.height = 48;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}
}
