package side_scroller.graphics;

import java.awt.Image;
import java.util.HashMap;

public class Sprite implements Cloneable {

    protected Animation anim;
    protected String animName;
    // position (pixels)
    private float x;
    private float y;
    // velocity (pixels per millisecond)
    private float dx;
    private float dy;
    
    private HashMap<String,Animation> animations;

    /**
        Creates a new Sprite object with the specified Animation.
    */
    public Sprite(String name, Animation anim) {
    	animName=name;
        this.anim = anim;
        animations=new HashMap<String,Animation>();
        animations.put(name, anim);
    }
    
    public void addAnimation(String name, Animation anim) {
    	animations.put(name,anim);
    }
    
    public void switchAnimation(String name) {
    	anim=animations.get(name);
    	animName=name;
    }
    
    public String getAnimName() {
    	return animName;
    }

    /**
        Updates this Sprite's Animation and its position based
        on the velocity.
    */
    public void update(long elapsedTime) {
        x += dx * elapsedTime;
        y += dy * elapsedTime;
        anim.update(elapsedTime);
    }

    /**
        Gets this Sprite's current x position.
    */
    public float getX() {
        return x;
    }

    /**
        Gets this Sprite's current y position.
    */
    public float getY() {
        return y;
    }

    /**
        Sets this Sprite's current x position.
    */
    public void setX(float x) {
        this.x = x;
    }

    /**
        Sets this Sprite's current y position.
    */
    public void setY(float y) {
        this.y = y;
    }

    /**
        Gets this Sprite's width, based on the size of the
        current image.
    */
    public int getWidth() {
        return anim.getImage().getWidth(null);
    }

    /**
        Gets this Sprite's height, based on the size of the
        current image.
    */
    public int getHeight() {
        return anim.getImage().getHeight(null);
    }

    /**
        Gets the horizontal velocity of this Sprite in pixels
        per millisecond.
    */
    public float getVelocityX() {
        return dx;
    }

    /**
        Gets the vertical velocity of this Sprite in pixels
        per millisecond.
    */
    public float getVelocityY() {
        return dy;
    }

    /**
        Sets the horizontal velocity of this Sprite in pixels
        per millisecond.
    */
    public void setVelocityX(float dx) {
        this.dx = dx;
    }

    /**
        Sets the vertical velocity of this Sprite in pixels
        per millisecond.
    */
    public void setVelocityY(float dy) {
        this.dy = dy;
    }

    /**
        Gets this Sprite's current image.
    */
    public Image getImage() {
        return anim.getImage();
    }

    /**
        Clones this Sprite. Does not clone position or velocity
        info.
    */
    public Object clone() throws CloneNotSupportedException {
    	Sprite s=(Sprite) super.clone();
    	s.animName=animName;
    	s.anim=(Animation)anim.clone();
    	s.animations=(HashMap<String, Animation>) animations.clone();
        return s;
    }
    
    public String toString() {
    	return "sprite:"+animations.keySet().toString();
    }
}
