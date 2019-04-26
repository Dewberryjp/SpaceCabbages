package side_scroller.tilegame.sprites;

import side_scroller.graphics.Animation;

/**
    A Grub is a Creature that moves slowly on the ground.
*/
public class Grub extends Creature {

	public Grub(String name, Animation anim) {
		super(name,anim);
	}


    public float getMaxSpeed() {
        return 0.5f;
    }

}
