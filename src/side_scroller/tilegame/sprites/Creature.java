package side_scroller.tilegame.sprites;

import side_scroller.graphics.*;

import side_scroller.graphics.Sprite;

/**
    A Creature is a Sprite that is affected by gravity and can
    die. It has four Animations: moving left, moving right,
    dying on the left, and dying on the right.
*/
public class Creature extends Sprite implements Cloneable {

    /**
        Amount of time to go from STATE_DYING to STATE_DEAD.
    */
    public static final int DIE_TIME = 1000;

    public static final int STATE_NORMAL = 0;
    public static final int STATE_DYING = 1;
    public static final int STATE_DEAD = 2;

    public int health;
  

    protected int state;
    protected long stateTime;

    public Creature(String name, Animation anim) {
    	super(name,anim);
    	state = STATE_NORMAL;
    	stateTime=0;
    
    }
    
    public Object clone() throws CloneNotSupportedException {
    	Creature c = (Creature)super.clone();
    	c.state = this.state;
    	c.stateTime = this.stateTime;
    	return c;
    }
    
    /**
        Creates a new Creature with the specified Animations.
    
    public Creature(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(right);
        this.left = left;
        this.right = right;
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
                (Animation)deadRight.clone()
            });
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }
    */

    /**
        Gets the maximum speed of this Creature.
    */
    public float getMaxSpeed() {
        return 0;
    }


    /**
        Wakes up the creature when the Creature first appears
        on screen. Normally, the creature starts moving left.
    */
    public void wakeUp() {
        if (getState() == STATE_NORMAL && getVelocityX() == 0) {
            setVelocityX(-getMaxSpeed());
        }
    }


    /**
        Gets the state of this Creature. The state is either
        STATE_NORMAL, STATE_DYING, or STATE_DEAD.
    */
    public int getState() {
        return state;
    }


    /**
        Sets the state of this Creature to STATE_NORMAL,
        STATE_DYING, or STATE_DEAD.
    */
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


    /**
        Checks if this creature is alive.
    */
    public boolean isAlive() {
        return (state == STATE_NORMAL);
    }


    /**
        Checks if this creature is flying.
    */
    public boolean isFlying() {
        return false;
    }


    /**
        Called before update() if the creature collided with a
        tile horizontally.
    */
    public void collideHorizontal() {
        setVelocityX(-getVelocityX());
    }


    /**
        Called before update() if the creature collided with a
        tile vertically.
    */
    public void collideVertical() {
        setVelocityY(0);
    }


    /**
        Updates the animaton for this creature.
    */
    public void update(long elapsedTime) {
        // select the correct Animation
        String animName=getAnimName();
        if (getVelocityX() < 0) {
            animName="left";
        }
        else if (getVelocityX() > 0) {
        	animName="right";
        }
        
        if (state == STATE_DYING && animName.equals("left")) {
            animName = "deadLeft";
        }
        else if (state == STATE_DYING && animName.equals("right")) {
        	animName="deadRight";
        }

        // update the Animation
        if (!animName.equals(getAnimName())) {
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
    
    public String toString() {
    	return "Creature+"+super.toString();
    }

}
