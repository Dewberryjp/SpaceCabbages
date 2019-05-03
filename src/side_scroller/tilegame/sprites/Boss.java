package side_scroller.tilegame.sprites;

import java.util.ArrayList;
import java.util.Random;

import side_scroller.graphics.Animation;
import side_scroller.graphics.Sprite;
import side_scroller.tilegame.ResourceManager;
import side_scroller.tilegame.TileMap;

public class Boss extends Creature{
private static final float JUMP_SPEED = -.95f;
protected String animName;

	public Boss(String name, Animation anim) {
		super(name, anim);

	}

	public float getMaxSpeed() {
		return 0.09f;
	}
	
	public void processBoss(Creature player, TileMap map, ResourceManager manager, ArrayList<Sprinkle> newSprinkles) {
		// TODO Auto-generated method stub
		
		//follows player
		this.followPlayer(player);
				
		//shoot sprinkles when in range
		this.shootSprinkles(player, map, manager,newSprinkles);

		
	}

	
	/**
	 * Method that changes velocity based on
	 * if player is left or right of boss
	 * @param player
	 */
	private void followPlayer(Creature player) {
		if(isLeft(player)) {
			this.setVelocityX(this.getVelocityX()*-1);
		}else if (isRight(player)) { 
			this.setVelocityX(this.getVelocityX()*-1);
		}
	}
	/**
	 * Boolean method to see if player is left of the Boss
	 * @param player -the player
	 * @return - true if player is left
	 */
	private boolean isLeft(Creature player) {
		if(this.getX() < player.getX() && this.getVelocityX() < 0) {
			return true;
		}
		return false;
	}
	/**
	 * Boolean method to see if player is right of the Boss
	 * @param player - the player
	 * @return - true if player is right
	 */
	private boolean isRight(Creature player) {
		if(this.getX() > player.getX() && this.getVelocityX() > 0) {
			return true;
		}
		return false;
	}
	/**
	 * Method that shoots sprinkles whenever the player is within range
	 * @param player
	 */
	private void shootSprinkles(Creature player, TileMap map, ResourceManager manager, ArrayList<Sprinkle> newSprinkles) {
		// TODO Auto-generated method stub
		float range = 400;
		float distance = player.getX()-this.getX();	
		Random rand = new Random();
		//System.out.println("\nDistance from Player and Boss: "+distance); -- test to see current distance between player and boss	
//		Random r = new Random();
//		return r.nextInt((max - min) + 1) + min;
		//Animation for attack when in range
		//if within range
		int prob = rand.nextInt(100);
		if(prob <= 15) {
			
			Sprinkle s = manager.getRandomSprinkle();
			newSprinkles.add(s);
		
			if(distance >= -range && distance < 0) { 
				animName = "bossAttack_Left"; 
				float startSp = this.getX() -this.getWidth()/2;
				float sDist = startSp-newSprinkles.get(0).getX();
				//sets sprinkle location
				s.setX(this.getX()+this.getWidth()/2-20);		
				s.setY(this.getY()+this.getHeight()/2+rand.nextInt((50-35)+1)+35);
				s.setVelocityX(-s.getMaxSpeed());

				
			}else if(distance  <= range && distance > 0) {
				animName = "bossAttack_Right";		
				//sets sprinkle location
				s.setX(this.getX()+this.getWidth()/2+20);		
				s.setY(this.getY()+this.getHeight()/2+rand.nextInt((50-35)+1)+35);
				s.setVelocityX(s.getMaxSpeed());
				
	
			}
		}
		//if out of Range
		if(distance <= -range && distance < 0) { 
			animName = "left";
		}else if(distance  >= range && distance > 0) {
			animName ="right";
		}
	}
	
	public void update(long elapsedTime) {
        // select the correct Animation
 
	        if (state == STATE_DYING && animName.equals("left")) {
	            this.animName = "deadLeft";
	        }
	        else if (state == STATE_DYING && animName.equals("right")) {
	        	this.animName="deadRight";
	        }
         
        // update the Animation
        if (!this.animName.equals(getAnimName())) { 
        	switchAnimation(this.animName);
            anim.start();
        }
        else {
            anim.update(elapsedTime);
        }
        
        // update to "dead" state
        stateTime += elapsedTime;
        if (state == STATE_DYING && stateTime >= DIE_TIME) {
            setState(STATE_DEAD);
        }
    }
}
