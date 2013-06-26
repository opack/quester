/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.slamdunk.quester.old;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;

public class DragAndDropTest extends InputAdapter implements ApplicationListener {
	Stage stage;

	public void create () {
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		final Skin skin = new Skin();
		skin.add("default", new LabelStyle(new BitmapFont(), Color.WHITE));
		skin.add("hero", new Texture("textures/hero.png"));
		skin.add("empty", new Texture("textures/actions/emptySlot.png"));
		skin.add("castle", new Texture("textures/castle.png"));
		skin.add("village", new Texture("textures/village.png"));

		final Image sourceImage = new Image(skin, "hero");
		sourceImage.setBounds(50, 125, 100, 100);
		stage.addActor(sourceImage);

		Image validTargetImage = new Image(skin, "castle");
		validTargetImage.setBounds(200, 50, 100, 100);
		stage.addActor(validTargetImage);

		Image invalidTargetImage = new Image(skin, "village");
		invalidTargetImage.setBounds(200, 200, 100, 100);
		stage.addActor(invalidTargetImage);

		final DragAndDrop dragAndDrop = new DragAndDrop();
		dragAndDrop.addSource(new Source(sourceImage) {
			public Payload dragStart (InputEvent event, float x, float y, int pointer) {
				Payload payload = new Payload();
				payload.setObject("ATTAQUE !");

				// On crée un dragActor correspondant à ce que contient la source
				Image dragActor = new Image(sourceImage.getDrawable());
				dragActor.setSize(sourceImage.getWidth(), sourceImage.getHeight());
				payload.setDragActor(dragActor);
				// On modifie l'image source pour afficher un slot vide
				sourceImage.setDrawable(skin.getDrawable("empty"));

//				Label validLabel = new Label("A l'attaque !", skin);
//				validLabel.setColor(0, 1, 0, 1);
//				payload.setValidDragActor(validLabel);

//				Label invalidLabel = new Label("Pas de chevalier ici !", skin);
//				invalidLabel.setColor(1, 0, 0, 1);
//				payload.setInvalidDragActor(invalidLabel);

				return payload;
			}
			
			@Override
			public void dragStop(InputEvent event, float x, float y, int pointer, Target target) {
				super.dragStop(event, x, y, pointer, target);
				Image dragActor = (Image)dragAndDrop.getDragActor();
				
				// Si pas lâché sur une cible valide on le replace aux coordonnées initiales
				if (target == null) {
					sourceImage.setDrawable(dragActor.getDrawable());
				}
			}
		});
		dragAndDrop.addTarget(new Target(validTargetImage) {
			public boolean drag (Source source, Payload payload, float x, float y, int pointer) {
				getActor().setColor(Color.GREEN);
				return true;
			}

			public void reset (Source source, Payload payload) {
				getActor().setColor(Color.WHITE);
			}

			public void drop (Source source, Payload payload, float x, float y, int pointer) {
				System.out.println("Accepted: " + payload.getObject() + " " + x + ", " + y);
				Image image = (Image)getActor();
				Image dragActor = (Image)payload.getDragActor();
				image.setDrawable(dragActor.getDrawable());
			}
		});
		dragAndDrop.addTarget(new Target(invalidTargetImage) {
			public boolean drag (Source source, Payload payload, float x, float y, int pointer) {
				getActor().setColor(Color.RED);
				return false;
			}

			public void reset (Source source, Payload payload) {
				getActor().setColor(Color.WHITE);
			}

			public void drop (Source source, Payload payload, float x, float y, int pointer) {
			}
		});
	}

	public void render () {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		Table.drawDebug(stage);
	}

	public void resize (int width, int height) {
		stage.setViewport(width, height, true);
	}

	public boolean needsGL20 () {
		return false;
	}

	public void dispose () {
		stage.dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}
}