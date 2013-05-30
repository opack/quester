package com.slamdunk.quester.map.logical;

public class CharacterData extends ElementData {
	public int hp;
	public int att;
	
	public CharacterData(MapElements element, int hp, int att) {
		super(element);
		this.hp = hp;
		this.att = att;
	}
}
