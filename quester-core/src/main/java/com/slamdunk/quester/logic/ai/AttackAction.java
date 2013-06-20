package com.slamdunk.quester.logic.ai;

import static com.slamdunk.quester.logic.ai.QuesterActions.ATTACK;

import com.slamdunk.quester.logic.controlers.CharacterControler;
import com.slamdunk.quester.logic.controlers.Damageable;
import com.slamdunk.quester.logic.controlers.WorldElementControler;
import com.slamdunk.quester.utils.Assets;

/**
 * Fait attaquer la cible target par l'attacker, fournis lors de la création
 * de l'action. 
 */
public class AttackAction implements AIAction {
	private CharacterControler attacker;
	private Damageable target;
	
	public AttackAction(CharacterControler attacker, Damageable target) {
		this.attacker = attacker;
		this.target = target;
	}
	
	public void act() {
		// Lance l'animation de l'attaque
		attacker.getActor().setCurrentAction(ATTACK, ((WorldElementControler)target).getActor().getWorldX());
		
		// Fait un bruit d'épée
		Assets.playSound(attacker.getAttackSound());
		
		// Retire des PV à la cible
		target.receiveDamage(attacker.getData().attack);
		
		// L'action est consommée : réalisation de la prochaine action
		attacker.getAI().nextAction();
		attacker.getAI().setNextActions(new WaitCompletionAction(attacker));
	}

	@Override
	public QuesterActions getAction() {
		return ATTACK;
	}
}
