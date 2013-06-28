package com.slamdunk.quester.display.hud.actionslots;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.MathUtils;
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
	private static final float APPEAR_RATE_TOTAL = Config.asFloat("action.appearRate.total", 1.0f);
	
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
	 * Choisit une action au hasard pour remplit ce slot
	 */
	public static void randomlyFillActionSlot(ActionSlotActor slot) {
		do {
			// On prend la première action dont le nombre aléatoire correspond
			for (SlotData data : ActionSlotsHelper.SLOT_DATAS.values()) {
				if (MathUtils.random(APPEAR_RATE_TOTAL) < data.rate) {
					slot.getControler().getData().action = data.action;
					slot.getImage().setDrawable(data.drawable);
					slot.appear();
					return;
				}
			}
		}
		// Si aucune action n'a été choisie, alors on réessaie !
		while (true);		
	}
	
	public static void setSlotData(SlotData from, ActionSlotActor to) {
		to.getControler().getData().action = from.action;
		to.getImage().setDrawable(from.drawable);
	}
}
