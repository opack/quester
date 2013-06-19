package com.slamdunk.quester.model.data;


public class ContextMenuData extends WorldElementData {

	/**
	 * Rayon (EN PIXELS !!!) entre le centre du menu vers le
	 * centre des items
	 */
	public float radius;
	/**
	 * Position depuis laquelle a été invoqué le menu contextuel
	 * (exprimé en cases)
	 */
	public int sourceX;
	
	public int sourceY;
}
