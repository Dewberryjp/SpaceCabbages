package side_scroller.tilegame.sprites;
import side_scroller.tilegame.GameManager;
import side_scroller.tilegame.TileMap;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import side_scroller.graphics.Animation;
import side_scroller.graphics.Sprite;
import side_scroller.tilegame.TileMap;

public class Sprinkle extends Sprite{

	public Sprinkle(String name, Animation anim) {
		super(name, anim);

	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	public float getMaxSpeed() {
		return 0.485f;
	}

	public boolean shouldDie(Player player, TileMap map, GameManager gameMan) {
		// TODO Auto-generated method stub
		// get the pixel location of the Sprites
		int s1x = Math.round(player.getX());
		int s1y = Math.round(player.getY());

		int s2x = Math.round(this.getX());
		int s2y = Math.round(this.getY());

		int s3x = Math.round(map.getWidth());
		int s3y = Math.round(map.getHeight());
		// check if the two sprites' boundaries intersect
		if (s1x < s2x + this.getWidth() && s2x < s1x + player.getWidth() && s1y < s2y + this.getHeight() && s2y < s1y + player.getHeight()) {
			player.setHealth(player.getHealth()-1);
			if(player.getHealth()<1)
				player.setState(player.STATE_DYING);
			return true;
		}
		Point tile =gameMan.getTileCollision(this, this.getX(), this.getY());
		if(tile!=null) {
			return true;
		}
		return false;
	}
	
}
