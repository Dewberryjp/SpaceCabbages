package side_scroller.tilegame.sprites;

import java.lang.reflect.Constructor;

import side_scroller.graphics.Animation;

/**
    The Player.
*/
public class Player extends Creature implements Cloneable {

    private static final float JUMP_SPEED = -.95f;
    public static final int STATE_STOMPING=3;
    private boolean onGround;
    private float x ,y;
    public Player(String name, Animation anim) {
    	super(name,anim);
    	onGround=true;
    	state=STATE_NORMAL;
    	this.x = this.getX();
    	this.y = this.getY();
    }
    
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

    public float getPlayerX() {
    	return x;
    }
    public float getPlayerY() {
    	return y;
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
    	String animName=getAnimName();
    	if (getVelocityX() < 0) {
    		if (onGround) {
    			animName = "left";
    		} else {
    			
    			animName = "jumpLeft";
    		}
    	}
    	else if (getVelocityX() > 0) {
    		if (onGround) {

    			animName = "right";
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

    	// update the Animation
    	if (!getAnimName().equals(animName)) {
    		switchAnimation(animName);
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

}
