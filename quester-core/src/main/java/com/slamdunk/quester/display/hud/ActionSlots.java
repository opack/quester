package com.slamdunk.quester.display.hud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.slamdunk.quester.logic.controlers.ActionSlotControler;
import com.slamdunk.quester.logic.controlers.GameControler;
import com.slamdunk.quester.logic.controlers.WorldElementControler;
import com.slamdunk.quester.model.data.ActionSlotData;
import com.slamdunk.quester.utils.Assets;
import com.slamdunk.quester.utils.Config;

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
	private static final SlotData EMPTY_SLOT = new SlotData(
		QuesterActions.NONE, 
		0,
		Assets.action_none);
	private static final Map<QuesterActions, SlotData> SLOT_DATAS;
	
	static {
		SLOT_DATAS = new HashMap<QuesterActions, SlotData>();
		SLOT_DATAS.put(
			QuesterActions.ATTACK,
			new SlotData(
				QuesterActions.ATTACK, 
				Config.asFloat("action.appearRate.attack", 0.5f),
				Assets.action_attack));
		SLOT_DATAS.put(
			QuesterActions.PROTECT,
			new SlotData(
				QuesterActions.PROTECT, 
				Config.asFloat("action.appearRate.shield", 0.25f),
				Assets.action_shield));
		SLOT_DATAS.put(
			QuesterActions.CHEST,
			new SlotData(
				QuesterActions.CHEST, 
				Config.asFloat("action.appearRate.chest", 0.05f),
				Assets.action_chest));
		SLOT_DATAS.put(
			QuesterActions.TECHSPE,
			new SlotData(
				QuesterActions.TECHSPE, 
				Config.asFloat("action.appearRate.techspe", 0.1f),
				Assets.action_techspe));
		SLOT_DATAS.put(
			QuesterActions.HEAL,
			new SlotData(
				QuesterActions.HEAL, 
				Config.asFloat("action.appearRate.heal", 0.05f),
				Assets.action_heal));
		SLOT_DATAS.put(
			QuesterActions.END_TURN,
			new SlotData(
				QuesterActions.END_TURN, 
				Config.asFloat("action.appearRate.endturn", 0.05f),
				Assets.action_endturn));
	}
	
	private final List<ActionSlotActor> arrivalSlots;
	private final float cellHeight;
	private final float cellWidth;
	private final DragAndDrop dragAndDrop;
	private final List<ActionSlotActor> stockSlots;
	private final List<ActionSlotActor> upcomingSlots;
	private final ActionSlotActor dragActor;
	
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
		dragActor = createEmptySlot();
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
				copySlot(source, dragActor);
				dragActor.setSize(cellWidth, cellHeight);
				payload.setDragActor(dragActor);
				// On modifie l'image source pour afficher un slot vide
				setSlotData(EMPTY_SLOT, source);

				return payload;
			}
			
			@Override
			public void dragStop(InputEvent event, float x, float y, int pointer, Target target) {
				super.dragStop(event, x, y, pointer, target);
				
				// Fin du drag and drop. Si pas lâché sur une cible valide
				// on replace l'action d'origine dans le slot
				if (target == null) {
					copySlot((ActionSlotActor)dragAndDrop.getDragActor(), source);
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
					copySlot((ActionSlotActor)dragAndDrop.getDragActor(), (ActionSlotActor)source.getActor());
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

	/**
	 * Choisit une action au hasard pour remplit ce slot
	 */
	private void fillActionSlot(ActionSlotActor slot) {
		do {
			// On prend la première action dont le nombre aléatoire correspond
			for (SlotData data : SLOT_DATAS.values()) {
				if (Math.random() < data.rate) {
					slot.getControler().getData().action = data.action;
					slot.getImage().setDrawable(data.drawable);
					return;
				}
			}
		}
		// Si aucune action n'a été choisie, alors on réessaie !
		while (true);		
	}

	public void fillActionSlots() {
		// Remplit chaque upcomingSlot
		for (ActionSlotActor slot : upcomingSlots) {
			if (slot.getControler().getData().action == QuesterActions.NONE) {
				fillActionSlot(slot);
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
				copySlot(upcomingSlot, arrivalSlot);
				// Descend tous les upcomings d'un cran
				ActionSlotActor previousUpcoming;
				for (int curUpcoming = upcomingSlots.size() - 1; curUpcoming > 0; curUpcoming--) {
					upcomingSlot = upcomingSlots.get(curUpcoming);
					previousUpcoming = upcomingSlots.get(curUpcoming - 1);
					copySlot(previousUpcoming, upcomingSlot);
				}
				// Remplit de nouveau le slot vide, qui est le premier
				fillActionSlot(upcomingSlots.get(0));
			}
		}
	}

	public static void copySlot(ActionSlotActor from, ActionSlotActor to) {
		SlotData data = SLOT_DATAS.get(from.getControler().getData().action);
		setSlotData(data, to);
	}
	
	public static ActionSlotActor createEmptySlot() {
		ActionSlotData data = new ActionSlotData(QuesterActions.NONE);
		ActionSlotControler slotControler = new ActionSlotControler(data);
		ActionSlotActor slotActor = new ActionSlotActor(Assets.action_none);
		
		slotActor.setControler(slotControler);		
		slotControler.setActor(slotActor);
		
		return slotActor;
	}
	
	private static void setSlotData(SlotData from, ActionSlotActor to) {
		to.getControler().getData().action = from.action;
		to.getImage().setDrawable(from.drawable);
	}

	public boolean isDragging() {
		return dragAndDrop.isDragging();
	}
}
