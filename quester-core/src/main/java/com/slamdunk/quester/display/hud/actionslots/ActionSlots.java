package com.slamdunk.quester.display.hud.actionslots;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.slamdunk.quester.display.actors.ActionSlotActor;
import com.slamdunk.quester.display.actors.WorldElementActor;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.WorldElementControler;

public class ActionSlots {
	private final List<ActionSlotActor> arrivalSlots;
	private final float cellHeight;
	private final float cellWidth;
	private final ActionSlotActor dragActor;
	
	private final DragAndDrop dragAndDrop;

	private final List<ActionSlotActor> stockSlots;

	private final List<ActionSlotActor> upcomingSlots;

	public ActionSlots() {
		upcomingSlots = new ArrayList<ActionSlotActor>();
		arrivalSlots = new ArrayList<ActionSlotActor>();
		stockSlots = new ArrayList<ActionSlotActor>();
		
		cellWidth = GameControler.instance.getScreen().getMap().getCellWidth();
		cellHeight = GameControler.instance.getScreen().getMap().getCellHeight();
		dragAndDrop = new DragAndDrop();
		dragAndDrop.setDragActorPosition(- cellWidth / 2, cellHeight / 2);
		
		// On crée l'acteur qui nous servira pendant les drags. Inutile d'en créer un différent
		// à chaque fois, on réutilisera le même.
		dragActor = ActionSlotsHelper.createEmptySlot();
	}
	
	public void act(float delta) {
		for (ActionSlotActor slot : upcomingSlots) {
			slot.act(delta);
		}
	}
	
	public void addArrivalSlots(ActionSlotActor... slots) {
		for (ActionSlotActor slot : slots) {
			arrivalSlots.add(slot);
			addSource(slot);
		}
	}

	public void addSource(final ActionSlotActor source) {
		dragAndDrop.addSource(new Source(source) {
			public Payload dragStart (InputEvent event, float x, float y, int pointer) {
				Payload payload = new Payload();
				payload.setObject(source.getControler());

				// On crée un dragActor correspondant à ce que contient la source
				ActionSlotsHelper.copySlot(source, dragActor);
				dragActor.setSize(cellWidth, cellHeight);
				payload.setDragActor(dragActor);
				// On modifie l'image source pour afficher un slot vide
				ActionSlotsHelper.setSlotData(ActionSlotsHelper.EMPTY_SLOT, source);

				return payload;
			}
			
			@Override
			public void dragStop(InputEvent event, float x, float y, int pointer, Target target) {
				super.dragStop(event, x, y, pointer, target);
				
				// Fin du drag and drop. Si pas lâché sur une cible valide
				// on replace l'action d'origine dans le slot
				if (target == null) {
					ActionSlotsHelper.copySlot((ActionSlotActor)dragAndDrop.getDragActor(), source);
				}
			}
		});
	}

	public void addStockSlots(ActionSlotActor... slots) {
		for (ActionSlotActor slot : slots) {
			stockSlots.add(slot);
			addSource(slot);
			addTarget(slot);
		}
	}

	public void addTarget(final WorldElementActor target) {
		dragAndDrop.addTarget(new Target(target) {
			public boolean drag (Source source, Payload payload, float x, float y, int pointer) {
				Image targetImage = target.getImage();
				if (targetImage != null) {
					if (target.getControler().canAcceptDrop(payload)) {
						targetImage.setColor(Color.GREEN);
					} else {
						targetImage.setColor(Color.RED);
					}
				}
				return true;
			}

			public void drop (Source source, Payload payload, float x, float y, int pointer) {
				WorldElementControler controler = target.getControler();
				if (controler.canAcceptDrop(payload)) {
					// On laisse le contrôleur gérer l'action
					target.getControler().receiveDrop(dragActor.getControler());
					// Et on met à jour les slots
					fillActionSlots();
				} else {
					// Si le chargement a été refusé, on replace l'action d'origine dans le slot
					ActionSlotsHelper.copySlot((ActionSlotActor)dragAndDrop.getDragActor(), (ActionSlotActor)source.getActor());
				}
			}

			public void reset (Source source, Payload payload) {
				Image targetImage = target.getImage();
				if (targetImage != null) {
					targetImage.setColor(Color.WHITE);
				}
			}
		});
	}
	
	public void addUpcomingSlots(ActionSlotActor... slots) {
		for (ActionSlotActor slot : slots) {
			upcomingSlots.add(slot);
		}
	}
	
	public void fillActionSlots() {
		// Remplit chaque upcomingSlot
		for (ActionSlotActor slot : upcomingSlots) {
			if (slot.getControler().getData().action == QuesterActions.NONE) {
				ActionSlotsHelper.randomlyFillActionSlot(slot);
			}
		}
		
		// Fait descendre les actions pour remplir les arrivalSlots, en commençant par le bas :
		// gravité oblige, c'est ce slot qui sera remplit d'abord.
		for (int curArrivalSlot = arrivalSlots.size() - 1; curArrivalSlot >= 0; curArrivalSlot --) {
			ActionSlotActor arrivalSlot = arrivalSlots.get(curArrivalSlot);
			if (arrivalSlot.getControler().getData().action == QuesterActions.NONE) {
				// Récupère le dernier upcomingSlot
				ActionSlotActor upcomingSlot = upcomingSlots.get(upcomingSlots.size() - 1);
				// Affecte ses données au slot d'arrivée vide
				upcomingSlot.fallTo(arrivalSlot);
				// Descend tous les upcomings d'un cran
				ActionSlotActor previousUpcoming;
				for (int curUpcoming = upcomingSlots.size() - 1; curUpcoming > 0; curUpcoming--) {
					upcomingSlot = upcomingSlots.get(curUpcoming);
					previousUpcoming = upcomingSlots.get(curUpcoming - 1);
					previousUpcoming.fallTo(upcomingSlot);
				}
				// Remplit de nouveau le slot vide, qui est le premier
				ActionSlotsHelper.randomlyFillActionSlot(upcomingSlots.get(0));
			}
		}
	}

	public boolean isDragging() {
		return dragAndDrop.isDragging();
	}
}
