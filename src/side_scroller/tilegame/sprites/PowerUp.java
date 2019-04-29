package side_scroller.tilegame.sprites;

import side_scroller.graphics.*;

import side_scroller.graphics.Sprite;


/**
    A PowerUp class is a Sprite that the player can pick up.
*/
public abstract class PowerUp extends Sprite implements Cloneable {
	
   
	public PowerUp(String name, Animation anim) {
        super(name,anim);
    }


    public Object clone() throws CloneNotSupportedException {
    	return super.clone();
    	/*
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(
                new Object[] {(Animation)anim.clone()});
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
        */
    }


    /**
        A Star PowerUp. Gives the player points.
    */
    public static class Star extends PowerUp {
        public Star(String name, Animation anim) {
            super(name, anim);
        }
    }
    
    /**
     *  A water PowerUp. Gives the player health
     */
    public static class Water extends PowerUp {
    	public Water(String name, Animation anim) {
    		super(name,anim);
    	}
    }
    
    /**
     * A heart PowerUp. Shows the players current health
     */
    
    /**
     * public static class Health extends PowerUp {
    	public Health(String name, Animation anim) {
    		super(name, anim);
    	}
    }
     */
    
    /**
        A Music PowerUp. Changes the game music.
    */
    public static class Music extends PowerUp {
        public Music(String name, Animation anim) {
            super(name, anim);
        }
    }


    /**
        A Goal PowerUp. Advances to the next map.
    */
    public static class Goal extends PowerUp {
        public Goal(String name, Animation anim) {
            super(name, anim);
        }
    }
    
    /**
    	A Glowing Orb (air-jump).
     */
    public static class Other extends PowerUp {
    	public Other(String name, Animation anim) {
    		super(name, anim);
    	}
    }
  

}
