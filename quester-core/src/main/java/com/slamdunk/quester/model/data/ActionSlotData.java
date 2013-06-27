package com.slamdunk.quester.model.data;

import com.slamdunk.quester.logic.ai.QuesterActions;

public class ActionSlotData extends WorldElementData {
	public QuesterActions action;
	
	public ActionSlotData(QuesterActions action) {
		this.action = action;
	}
}
