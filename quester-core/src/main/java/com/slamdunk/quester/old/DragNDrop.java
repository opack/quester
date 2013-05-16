package com.slamdunk.quester.old;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.XmlReader;

public class DragNDrop implements ApplicationListener {
	private final int SCREEN_WIDTH = 480;
	private final int SCREEN_HEIGHT = 800;
	private final int SYMBOL_SIZE = 48;
	
	private final String SVG_PREFIX_SYMBOL = "symbol";
	private final int SVG_PREFIX_SYMBOL_LENGTH = SVG_PREFIX_SYMBOL.length();
	private final String SVG_PREFIX_ARRIVAL = "arrival";
	private final int SVG_PREFIX_ARRIVAL_LENGTH = SVG_PREFIX_ARRIVAL.length();
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	
	private Texture bucketImage;
	private Texture backgroundImage;
		
	private Rectangle bucket;
	private Rectangle background;
	private Vector3 touchPos;
	
	private boolean dndActivated;
	private long lastDndTime;
	
	private Rectangle[][] grid;
	private Rectangle[] arrivals;

	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
		batch = new SpriteBatch();
		touchPos = new Vector3();
		shapeRenderer = new ShapeRenderer();
		
		// Charge les images
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		backgroundImage = new Texture(Gdx.files.internal("background.png"));
		  
		// Définition de la taille et de la position initiale du sceau
		bucket = new Rectangle();
		bucket.x = SCREEN_WIDTH / 2 - SYMBOL_SIZE / 2;
		bucket.y = 20;
		bucket.width = SYMBOL_SIZE;
		bucket.height = SYMBOL_SIZE;
		
		background = new Rectangle();
		background.x = 0;
		background.y = 0;
		background.width = backgroundImage.getWidth();
		background.height = backgroundImage.getHeight();
		
		// Création de la grille
		try {
			loadSymbolsGrid();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadSymbolsGrid() throws ParserConfigurationException, SAXException, IOException {
		grid = new Rectangle[5][5];
		arrivals = new Rectangle[3];
		
		// Ouverture du SVG
		XmlReader svg = new XmlReader();
		XmlReader.Element root = svg.parse(Gdx.files.internal("background.svg"));
		
		// Recherche des rectangles (balise rect) dont l'id est symbolXX
		Array<XmlReader.Element> rects = root.getChildrenByNameRecursively("rect");
		for (XmlReader.Element xmlRect : rects) {
			// Récupération du x, y et de la taille
			Rectangle rect = new Rectangle();
			rect.width = Float.parseFloat(xmlRect.getAttribute("width"));
			rect.height = Float.parseFloat(xmlRect.getAttribute("height"));
			rect.x = Float.parseFloat(xmlRect.getAttribute("x"));
			// On est obligés de soustraire la hauteur du rectangle car dans libgdx on dessine depuis
			// le coin bas-gauche et non pas haut-gauche. Ainsi la hauteur du rectangle doit être 
			// soustraite au y indiqué dans le svg (pour le coin haut-gauche). De plus, comme notre
			// repère a son origine en bas à droite, on décale par rapport au SCREEN_HEIGHT.
			rect.y = SCREEN_HEIGHT - Float.parseFloat(xmlRect.getAttribute("y")) - rect.width;
			
			
			// Découpage de l'identifiant
			String id = xmlRect.getAttribute("id");
			if (id.startsWith(SVG_PREFIX_SYMBOL)) {
				int row = Integer.parseInt(id.substring(SVG_PREFIX_SYMBOL_LENGTH, SVG_PREFIX_SYMBOL_LENGTH + 1));
				int col = Integer.parseInt(id.substring(SVG_PREFIX_SYMBOL_LENGTH + 1, SVG_PREFIX_SYMBOL_LENGTH + 2));
				grid[row][col] = rect;
			} else if (id.startsWith(SVG_PREFIX_ARRIVAL)) {
				int pos = Integer.parseInt(id.substring(SVG_PREFIX_ARRIVAL_LENGTH));
				arrivals[pos] = rect;
			}
		}
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
		
		// Dessin de la grille
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);
		for (Rectangle[] symbolRows : grid){
			for (Rectangle symbolBox : symbolRows) {
				shapeRenderer.rect(symbolBox.x, symbolBox.y, symbolBox.width, symbolBox.height, Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN);
			}
		}
		shapeRenderer.circle(0, 0, 10);
		shapeRenderer.end();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(backgroundImage, background.x, background.y, background.width, background.height);
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
			releaseDragNDrop();
		}
		
		// Si le dnd est activé et que le pointeur n'est pas encore 
		// sur le sceau, alors on déplace le sceau vers le pointeur.
		if (dndActivated) {
			if(TimeUtils.nanoTime() - lastDndTime > 100000000) {
				lastDndTime = TimeUtils.nanoTime();
			}
			
			if (touchPos.x != bucket.x + bucket.width / 2) {
				// Distance à parcourir
				float distance = (touchPos.x - SYMBOL_SIZE /2) - bucket.x;
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
			if (touchPos.y != bucket.y + bucket.height / 2) {
				float distance = (touchPos.y - SYMBOL_SIZE /2) - bucket.y;
				if (Math.abs(distance) > 10) {
					float step = Math.max(0, 400 * Gdx.graphics.getDeltaTime());
					if (distance > 0) {
						bucket.y += step;
					} else {
						bucket.y -= step;
					}
				}
			}
		}
	}

	private void releaseDragNDrop() {
		if (dndActivated) {
			// On a relâché alors qu'on était en plein drag'n'drop. On regarde si on
			// a lâché au-dessus d'une case.
			ROWSLOOP : for (Rectangle[] symbolRows : grid){
				for (Rectangle symbolBox : symbolRows) {
					if (symbolBox.contains(touchPos.x, touchPos.y)) {
						bucket.x = (symbolBox.x + symbolBox.width / 2) - (SYMBOL_SIZE / 2);
						bucket.y = (symbolBox.y + symbolBox.height / 2) - (SYMBOL_SIZE / 2);
						break ROWSLOOP;
					}
				}
			}						
		}
		// On n'appuie plus sur l'écran, donc le drag'n'drop est désactivé
		dndActivated = false;
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
		backgroundImage.dispose();
		bucketImage.dispose();
		
		batch.dispose();
		shapeRenderer.dispose();
	}
}
