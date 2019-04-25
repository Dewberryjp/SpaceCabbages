package side_scroller.tilegame.sprites;

import java.lang.reflect.Constructor;

import side_scroller.graphics.Animation;

/**
    The Player.
*/
public class Player extends Creature {

    private static final float JUMP_SPEED = -.95f;

    private boolean onGround;
    

	private Animation jumpLeft;
    private Animation jumpRight;
    protected Animation deadLeft;
    protected Animation deadRight; 
    
    public Player(Animation left, Animation right,
        Animation deadLeft, Animation deadRight,
        Animation jumpLeft, Animation jumpRight)
    {
        super(left, right, deadLeft, deadRight);
        this.jumpLeft=jumpLeft;
        this.jumpRight=jumpRight;
        this.deadLeft = deadLeft;
        this.deadRight = deadRight;
        state = STATE_NORMAL;
    }
    
    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(new Object[] {
                (Animation)left.clone(),
                (Animation)right.clone(),
                (Animation)deadLeft.clone(),
                (Animation)deadRight.clone(),
                (Animation)jumpLeft.clone(),
                (Animation)jumpRight.clone()
            });
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }

    public boolean isOnGround() {
		return onGround;
	}
    
    public void collideHorizontal() {
        setVelocityX(0);
    }


    public void collideVertical() {
        // check if collided with ground
        if (getVelocityY() > 0) {
            onGround = true;
        }
        setVelocityY(0);
    }


    public void setY(float y) {
        // check if falling
        if (Math.round(y) > Math.round(getY())) {
            onGround = false;
        }
        super.setY(y);
    }


    public void wakeUp() {
        // do nothing
    }
    public int getState() {
        return state;
    }
    
    public void setState(int state) {
        if (this.state != state) {
            this.state = state;
            stateTime = 0;
            if (state == STATE_DYING) {
                setVelocityX(0);
                setVelocityY(0);
            }
        }
    }

    	
    public boolean isAlive() {
        return (state == STATE_NORMAL);
    }

    /**
        Makes the player jump if the player is on the ground or
        if forceJump is true.
    */
    public void jump(boolean forceJump) {
        if (onGround || forceJump) {
            onGround = false;
            setVelocityY(JUMP_SPEED);
            
        }
    }


    public float getMaxSpeed() {
        return 0.5f;
    }
    
    /**
    Updates the animaton for this creature.
     */
    public void update(long elapsedTime) {
    	// select the correct Animation
    	Animation newAnim = anim;
    	if (getVelocityX() < 0) {
    		if (onGround) {
    			newAnim = left;
    		} else {
    			
    			newAnim = jumpLeft;
    		}
    	}
    	else if (getVelocityX() > 0) {
    		if (onGround) {

    			newAnim = right;
    		} else {
    			newAnim = jumpRight;
    		}
    	} else {
    		if (!onGround && newAnim==right) {
    			newAnim= jumpRight;
    		} else if (!onGround && newAnim==left) {
    			newAnim=jumpLeft;
    		} else if (onGround && newAnim==jumpRight) {
    			newAnim = right;
    		} else if (onGround && newAnim==jumpLeft) {
    			newAnim = left;
    		}
    	} 
    	if (state == STATE_DYING &&  newAnim == left) {
    		newAnim = deadLeft;
    		
    	}
    	else if (state == STATE_DYING &&  newAnim == right) {
    		newAnim = deadRight;
    		
    	}

    	// update the Animation
    	if (anim != newAnim) {
    		anim = newAnim;
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
