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
import com.slamdunk.quester.display.actors.ActionSlotActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.logic.controlers.ActionSlotControler;
import com.slamdunk.quester.logic.controlers.GameControler;
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
		void receiveDrop(ActionSlotControler dropped);
	}
	
	private final DragAndDrop dragAndDrop;
	private final List<ActionSlotActor> upcomingSlots;
	private final List<ActionSlotActor> arrivalSlots;
	private final List<ActionSlotActor> stockSlots;
	private final float cellWidth;
	private final float cellHeight;
	
	public ActionSlots() {
		upcomingSlots = new ArrayList<ActionSlotActor>();
		arrivalSlots = new ArrayList<ActionSlotActor>();
		stockSlots = new ArrayList<ActionSlotActor>();
		
		cellWidth = GameControler.instance.getScreen().getMap().getCellWidth();
		cellHeight = GameControler.instance.getScreen().getMap().getCellHeight();
		dragAndDrop = new DragAndDrop();
		dragAndDrop.setDragActorPosition(- cellWidth / 2, cellHeight / 2);
	}

	public void addUpcomingSlots(ActionSlotActor... slots) {
		for (ActionSlotActor slot : slots) {
			upcomingSlots.add(slot);
		}
	}

	public void addArrivalSlots(ActionSlotActor... slots) {
		for (ActionSlotActor slot : slots) {
			arrivalSlots.add(slot);
			addSource(slot);
		}
	}

	public void addStockSlots(ActionSlotActor... slots) {
		for (ActionSlotActor slot : slots) {
			stockSlots.add(slot);
			addSource(slot);
			addTarget(slot);
		}
	}
	
	public void addSource(final ActionSlotActor source) {
		dragAndDrop.addSource(new Source(source) {
			public Payload dragStart (InputEvent event, float x, float y, int pointer) {
				Payload payload = new Payload();
				payload.setObject(source.getControler());

				// On crée un dragActor correspondant à ce que contient la source
				Image dragActor = new Image(source.getImage().getDrawable());
				dragActor.setSize(cellWidth, cellHeight);
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
					payload.getDragActor().setColor(Color.GREEN);
				} else {
					payload.getDragActor().setColor(Color.RED);
				}
				return true;
			}

			public void reset (Source source, Payload payload) {
				payload.getDragActor().setColor(Color.WHITE);
			}

			public void drop (Source source, Payload payload, float x, float y, int pointer) {
				target.getControler().receiveDrop((ActionSlotControler)payload.getObject());
			}
		});
	}

	public boolean isDragging() {
		return dragAndDrop.isDragging();
	}

}
