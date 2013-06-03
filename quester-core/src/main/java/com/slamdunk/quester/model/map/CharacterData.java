package com.slamdunk.quester.model.map;

public class CharacterData extends ElementData {
	public int hp;
	public int att;
	
	public CharacterData(MapElements element, int hp, int att) {
		super(element);
		this.hp = hp;
		this.att = att;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CharacterData)) {
			return false;
		}
		CharacterData characterData = (CharacterData)obj;
		return super.equals(characterData)
			&& characterData.hp == hp
			&& characterData.att == att;
	}
	
	@Override
	public int hashCode() {
		return element.ordinal() ^ hp ^ att;
	}
}
