package side_scroller.tilegame.sprites;

import side_scroller.graphics.Animation;
/**
    A Fly is a Creature that fly slowly in the air.
*/
public class Fly extends Creature {

	public Fly(String name, Animation anim) {
		super(name,anim);
	}

    public float getMaxSpeed() {
        return 0.8f;
    }


    public boolean isFlying() {
        return isAlive();
    }

}
