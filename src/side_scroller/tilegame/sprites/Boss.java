package side_scroller.tilegame.sprites;

import side_scroller.graphics.Animation;

public class Boss extends Creature{
	float playerCurrX, playerCurrY;
	public Boss(String name, Animation anim) {
		super(name, anim);
		//this.followPlayer();
		// TODO Auto-generated constructor stub
	}
//	public void getPlayerLoc(Player p) {
//		
//	}
//	public void followPlayer() {
//		//if: player x is less than boss x velocity is negative
//		playerCurrX = 
//		//else: velocity positive
//	}
	
	public float getMaxSpeed() {
		return 0.09f;
	}
	
}
