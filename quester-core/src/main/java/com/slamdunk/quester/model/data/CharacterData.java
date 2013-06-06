package com.slamdunk.quester.model.data;

import com.slamdunk.quester.model.map.MapElements;

public class CharacterData extends ObstacleData {
	/**
	 * Nom
	 */
	public String name;
	/**
	 * Points de vie
	 */
	public int health;
	/**
	 * Points d'attaque
	 */
	public int attack;
	/**
	 * Distance � laquelle l'arme peut attaquer
	 */
	public int weaponRange;
	/**
	 * Vitesse (en nombre de cases par seconde) � laquelle se d�place le personnage
	 */
	public float speed;
	/**
	 * Ordre de jeu
	 */
	public int playRank;
	
	public CharacterData(MapElements element, int hp, int attack) {
		super(element);
		// Nom par d�faut
		name = element.name();
		// HP et attaque
		this.health = hp;
		this.attack = attack;
		// Vitesse par d�faut : 1s/case
		speed = 1;
		// Port�e par d�faut : 1 case
		weaponRange = 1;
		// Ordre de jeu par d�faut : 1er
		playRank = 0;
		// Si aucune AI, on en cr�e une par d�faut
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CharacterData)) {
			return false;
		}
		CharacterData characterData = (CharacterData)obj;
		return super.equals(characterData)
			&& characterData.health == health
			&& characterData.attack == attack;
	}
	
	@Override
	public int hashCode() {
		return element.ordinal() ^ health ^ attack;
	}
}