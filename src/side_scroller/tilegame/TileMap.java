package side_scroller.tilegame;

import java.awt.Image;

import java.util.LinkedList;

import side_scroller.graphics.Animation;
import side_scroller.graphics.Sprite;
import side_scroller.tilegame.sprites.PowerUp;

import java.util.Iterator;

/**
    The TileMap class contains the data for a tile-based
    map, including Sprites. Each tile is a reference to an
    Image. Of course, Images are used multiple times in the tile
    map.
*/
public class TileMap {

    private Image[][] tiles;
    private LinkedList sprites;
    private Sprite player;
    private Sprite boss;
    private PowerUp powerup;




    private int x,y; 
    // life
    // background
    private Animation life;
    private Animation key; 
    private Image background;
   

    /**
        Creates a new TileMap with the specified width and
        height (in number of tiles) of the map.
    */
    public TileMap(int width, int height) {
        tiles = new Image[width][height];
        sprites = new LinkedList();
    }
    public Animation getKey() {
    	return key;
    }
    public void setKey(Animation key) {
    	this.key = key; 
    }
    public Animation getLife() {
		return life;
    }
    public void setLife(Animation life) {
    	this.life = life; 
    }
    public Image getBackground() {
    	return background;
    }
    public void setBackground(Image background) {
    	this.background = background; 
    }

    /**
        Gets the width of this TileMap (number of tiles across).
    */
    public int getWidth() {
        return tiles.length;
    }


    /**
        Gets the height of this TileMap (number of tiles down).
    */
    public int getHeight() {
        return tiles[0].length;
    }


    /**
        Gets the tile at the specified location. Returns null if
        no tile is at the location or if the location is out of
        bounds.
    */
    public Image getTile(int x, int y) {
        if (x < 0 || x >= getWidth() ||
            y < 0 || y >= getHeight())
        {
            return null;
        }
        else {
            return tiles[x][y];
        }
    }


    /**
        Sets the tile at the specified location.
    */
    public void setTile(int x, int y, Image tile) {
        tiles[x][y] = tile;
    }


    /**
        Gets the player Sprite.
    */
    public Sprite getPlayer() {
        return player;
    }

    /**
        Sets the player Sprite.
    */
    public void setPlayer(Sprite player) {
        this.player = player;
    }
    /**
     * gets the boss sprite 
     * @return
     */
    public Sprite getBoss() {
    	return boss; 
    }
    /**
     * sets the boss sprite
     * @param boss
     */
    public void setBoss(Sprite boss) {
    	this.boss = boss;
    }

    /**
        Adds a Sprite object to this map.
    */
    public void addSprite(Sprite sprite) {
        sprites.add(sprite);
    }
    




    /**
        Removes a Sprite object from this map.
    */
    public void removeSprite(Sprite sprite) {
        sprites.remove(sprite);
    }


    /**
        Gets an Iterator of all the Sprites in this map,
        excluding the player Sprite.
    */
    public Iterator getSprites() {
        return sprites.iterator();
    }


}
