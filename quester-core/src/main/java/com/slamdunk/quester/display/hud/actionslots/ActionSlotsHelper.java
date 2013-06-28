package com.slamdunk.quester.display.hud.actionslots;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.slamdunk.quester.display.actors.ActionSlotActor;
import com.slamdunk.quester.logic.ai.QuesterActions;
import com.slamdunk.quester.logic.controlers.ActionSlotControler;
import com.slamdunk.quester.model.data.ActionSlotData;
import com.slamdunk.quester.utils.Assets;
import com.slamdunk.quester.utils.Config;

public class ActionSlotsHelper {
	public static final SlotData EMPTY_SLOT = new SlotData(
		QuesterActions.NONE, 
		0,
		Assets.action_none);
	public static final Map<QuesterActions, SlotData> SLOT_DATAS;
	private static final float APPEAR_RATE_TOTAL = Config.asFloat("action.appearRate.total", 6f);
	private static final List<QuesterActions> NEXT_ACTIONS;
	
	static {
		// Création des données des slots pour chaque action possible
		SLOT_DATAS = new HashMap<QuesterActions, SlotData>();
		SLOT_DATAS.put(
			QuesterActions.ATTACK,
			new SlotData(
				QuesterActions.ATTACK, 
				Config.asFloat("action.appearRate.attack", 1),
				Assets.action_attack));
		SLOT_DATAS.put(
			QuesterActions.PROTECT,
			new SlotData(
				QuesterActions.PROTECT, 
				Config.asFloat("action.appearRate.shield", 1),
				Assets.action_shield));
		SLOT_DATAS.put(
			QuesterActions.CHEST,
			new SlotData(
				QuesterActions.CHEST, 
				Config.asFloat("action.appearRate.chest", 1),
				Assets.action_chest));
		SLOT_DATAS.put(
			QuesterActions.TECHSPE,
			new SlotData(
				QuesterActions.TECHSPE, 
				Config.asFloat("action.appearRate.techspe", 1),
				Assets.action_techspe));
		SLOT_DATAS.put(
			QuesterActions.HEAL,
			new SlotData(
				QuesterActions.HEAL, 
				Config.asFloat("action.appearRate.heal", 1),
				Assets.action_heal));
		SLOT_DATAS.put(
			QuesterActions.END_TURN,
			new SlotData(
				QuesterActions.END_TURN, 
				Config.asFloat("action.appearRate.endturn", 1),
				Assets.action_endturn));
		
		// Création de la liste des prochaines actions
		NEXT_ACTIONS = new ArrayList<QuesterActions>();
		chooseNextActions();
	}
	
	/**
	 * Remplit la liste des prochaines actions avec un paquet d'actions
	 * choisies en fonction des taux d'apparition de chaque action,
	 * et mélangé aléatoirement.
	 */
	private static void chooseNextActions() {
		final int nbActionsToCreate = Config.asInt("action.appearRate.foreseeSize", (int)(APPEAR_RATE_TOTAL + 1));
		NEXT_ACTIONS.clear();
		for (SlotData data : SLOT_DATAS.values()) {
			final int nbOccurrences = (int)(data.rate * nbActionsToCreate / APPEAR_RATE_TOTAL);
			for (int count = 0; count < nbOccurrences; count++) {
				NEXT_ACTIONS.add(data.action);
			}
		}
		Collections.shuffle(NEXT_ACTIONS);
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
	
	/**
	 * Remplit ce slot avec la prochaine action prévue
	 */
	public static void fillActionSlot(ActionSlotActor slot) {
		if (NEXT_ACTIONS.isEmpty()) {
			chooseNextActions();
		}
		final QuesterActions action = NEXT_ACTIONS.remove(0);
		final SlotData data = SLOT_DATAS.get(action);
		slot.getControler().getData().action = data.action;
		slot.getImage().setDrawable(data.drawable);
		slot.appear();
	}
	
	public static void setSlotData(SlotData from, ActionSlotActor to) {
		to.getControler().getData().action = from.action;
		to.getImage().setDrawable(from.drawable);
	}
}
