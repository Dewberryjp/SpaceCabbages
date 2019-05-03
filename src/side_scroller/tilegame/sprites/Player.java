package side_scroller.tilegame.sprites;

import java.lang.reflect.Constructor;

import side_scroller.graphics.Animation;

/**
    The Player.
*/
public class Player extends Creature implements Cloneable {

    private static final float JUMP_SPEED = -.95f;
    private boolean onGround;
    private boolean isRolling;
    private boolean isSmashing;

    public Player(String name, Animation anim) {
    	super(name,anim);
    	onGround=true;
    	isRolling=false;
    	isSmashing=false;
    	state=STATE_NORMAL;    }
    
    public Object clone() throws CloneNotSupportedException {
    	Player p=(Player)super.clone();
    	p.onGround=this.onGround;
    	return p;
    }

    /*
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
    */


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
    	String nextAnim=getNextAnim();
    	
    	// update the Animation
    	if (!getAnimName().equals(nextAnim)) {
    		switchAnimation(nextAnim);
    		anim.start();
    	}
    	else {
    		boolean switchBack=anim.update(elapsedTime);
    		if (switchBack && isRolling) {
    			isRolling=false;
    			String newNextAnim=getNextAnim();
    			switchAnimation(newNextAnim);
    			anim.start();
    		}
    	}

    	// update to "dead" state
    	stateTime += elapsedTime;
    	if (state == STATE_DYING && stateTime >= DIE_TIME) {
    		setState(STATE_DEAD);
    	}
    }
    
    public String getNextAnim() {
    	// select the correct Animation
    	String animName=getAnimName();
    	if(!onGround) {
    		isRolling=false;
    	}
    	if (getVelocityX() < 0) {
    		if (onGround) {
    			animName = "left";
    		} if(isRolling&&getVelocityX() < 0) {
    			animName="rollLeft";
    		} else {
    			animName = "jumpLeft";
    		}
    	}
    	else if (getVelocityX() > 0) {
    		if (onGround) {
    			animName = "right";
    		} if(isRolling&&getVelocityX() > 0) {
    			animName="rollRight";
    		} else {
    			animName = "jumpRight";
    		}
    	} else {
    		if (!onGround && animName.equals("right")) {
    			animName= "jumpRight";
    		} else if (!onGround && animName.equals("left")) {
    			animName="jumpLeft";
    		} else if (onGround && animName.equals("jumpRight")) {
    			animName="right";
    		} else if (onGround && animName.equals("jumpLeft")) {
    			animName="left";
    		}
    		
    	} 
    	if (state == STATE_DYING &&  animName.equals("left")) {
    		animName="deadLeft";
    	}
    	else if (state == STATE_DYING &&  animName.equals("right")) {
    		animName="deadRight";
    		
    	}
    	return animName;
    }

	public boolean isOnGround() {
		// TODO Auto-generated method stub
		return onGround;
	}
	
	public String toString() {
		String s=this.getClass().getName();
		s+=(this.anim==null?"animNull":"good");
		s+=super.toString();
		return s;
	}

	public void setIsSmashing(boolean smashing) {
		// TODO Auto-generated method stub
		this.isSmashing=smashing;
	}
	public void setIsRolling(boolean isRolling) {
		this.isRolling=isRolling;
	}
	
	public boolean getIsSmashing() {
		return this.isSmashing;
	}
	

}
