package com.slamdunk.quester.display.hud;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.logic.controlers.WorldElementControler;
import com.slamdunk.quester.utils.Assets;

public class ActionSlots {
	public interface DropReceiver {
		/**
		 * Indique si ce receiver serait prêt à accepter ce payload
		 */
		boolean canAcceptDrop(Payload payload);
		
		/**
		 * Méthode appelée lorsqu'un chargement est lâché sur ce receiver
		 */
		void receiveDrop(WorldElementControler dropped);
	}
	
	private final DragAndDrop dragAndDrop = new DragAndDrop();
	private final List<WorldElementActor> upcomingSlots;
	private final List<WorldElementActor> arrivalSlots;
	private final List<WorldElementActor> stockSlots;
	
	public ActionSlots() {
		upcomingSlots = new ArrayList<WorldElementActor>();
		arrivalSlots = new ArrayList<WorldElementActor>();
		stockSlots = new ArrayList<WorldElementActor>();
	}

	public void addUpcomingSlots(WorldElementActor... slots) {
		for (WorldElementActor slot : slots) {
			upcomingSlots.add(slot);
		}
	}

	public void addArrivalSlots(WorldElementActor... slots) {
		for (WorldElementActor slot : slots) {
			arrivalSlots.add(slot);
			addSource(slot);
		}
	}

	public void addStockSlots(WorldElementActor... slots) {
		for (WorldElementActor slot : slots) {
			stockSlots.add(slot);
			addSource(slot);
			addTarget(slot);
		}
	}
	
	public void addSource(final WorldElementActor source) {
		dragAndDrop.addSource(new Source(source) {
			public Payload dragStart (InputEvent event, float x, float y, int pointer) {
				Payload payload = new Payload();
				payload.setObject(source.getControler());

				// On crée un dragActor correspondant à ce que contient la source
				Image dragActor = new Image(source.getImage().getDrawable());
				dragActor.setSize(source.getWidth(), source.getHeight());
				payload.setDragActor(dragActor);
				// On modifie l'image source pour afficher un slot vide
				source.getImage().setDrawable(new TextureRegionDrawable(Assets.emptySlot));

				return payload;
			}
			
			@Override
			public void dragStop(InputEvent event, float x, float y, int pointer, Target target) {
				super.dragStop(event, x, y, pointer, target);
				Image dragActor = (Image)dragAndDrop.getDragActor();
				
				// Si pas lâché sur une cible valide on le replace aux coordonnées initiales
				if (target == null) {
					source.getImage().setDrawable(dragActor.getDrawable());
				}
			}
		});
	}
	
	public void addTarget(final WorldElementActor target) {
		dragAndDrop.addTarget(new Target(target) {
			public boolean drag (Source source, Payload payload, float x, float y, int pointer) {
				if (target.getControler().canAcceptDrop(payload)) {
					getActor().setColor(Color.GREEN);
				} else {
					getActor().setColor(Color.RED);
				}
				return true;
			}

			public void reset (Source source, Payload payload) {
				getActor().setColor(Color.WHITE);
			}

			public void drop (Source source, Payload payload, float x, float y, int pointer) {
				target.getControler().receiveDrop((WorldElementControler)payload.getObject());
			}
		});
	}

}
