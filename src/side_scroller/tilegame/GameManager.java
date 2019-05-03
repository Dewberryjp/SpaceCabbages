package side_scroller.tilegame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Iterator;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import side_scroller.graphics.Sprite;
import side_scroller.input.*;
import side_scroller.sound.*;
import side_scroller.test.GameCore;
import side_scroller.tilegame.sprites.*;

/**
    GameManager manages all parts of the game.
*/
public class GameManager extends GameCore {

    public static void main(String[] args) {
        new GameManager().run();
    }

    // uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
    private static final AudioFormat PLAYBACK_FORMAT =
        new AudioFormat(44100, 16, 1, true, false);

    private static final int DRUM_TRACK = 1;

    public static final float GRAVITY = 0.002f;

    private Point pointCache = new Point();
    private TileMap map;
    private MidiPlayer midiPlayer;
    private SoundManager soundManager;
    private ResourceManager resourceManager;
    private Sound prizeSound;
    private Sound boopSound;
    private Sound alienSound;
    private Sound layerCakeSound;
    private Sound deadBossSound;
    private Sound mobDyingSound;
    private Sound newLevelSound;
    private Sound playerDamageSound;
    private Sound playerDyingSound;
    private Sound playerJumpingSound;
    private Sound rollSound;
    private Sound tittyJuiceSound;
    private Sound ultimateSound;
    
    private InputManager inputManager;
    private TileMapRenderer renderer;
    
    private GameAction smash;
    private GameAction moveLeft;
    private GameAction moveRight;
    private GameAction jump;
    private GameAction pauseKeyPress;
    
    boolean paused;
    boolean soundSelection = true;
    boolean musicSelection = true;

	private GameAction roll;


    public void init() {
        super.init();

        // set up input manager
        initInput();

        // start resource manager
        resourceManager = new ResourceManager(
        screen.getFullScreenWindow().getGraphicsConfiguration());

        // load resources
        renderer = new TileMapRenderer();
        renderer.setBackground(
            resourceManager.loadImage("background.png"));
        

        // load first map
        map = resourceManager.loadNextMap();

        // load sounds
        soundManager = new SoundManager(PLAYBACK_FORMAT);
        prizeSound = soundManager.getSound("sounds/prize.wav");
        boopSound = soundManager.getSound("sounds/boop2.wav");
        alienSound = soundManager.getSound("sounds/alien.wav");
        layerCakeSound = soundManager.getSound("sounds/3layercake.wav");
        deadBossSound = soundManager.getSound("sounds/dead boss.wav");
        mobDyingSound = soundManager.getSound("sounds/mobDying.wav");
        newLevelSound = soundManager.getSound("sounds/NEW LVL.wav");
        playerDamageSound = soundManager.getSound("sounds/Player Damage.wav");
        playerDyingSound = soundManager.getSound("sounds/Player Dying.wav");
        playerJumpingSound = soundManager.getSound("sounds/playerJump.wav");
        rollSound = soundManager.getSound("sounds/roll.wav");
        tittyJuiceSound = soundManager.getSound("sounds/TittyJuice.wav");
        ultimateSound = soundManager.getSound("sounds/ultMove.wav"); 

        // start music
        midiPlayer = new MidiPlayer();
        Sequence sequence =
            midiPlayer.getSequence("sounds/music.midi");
        midiPlayer.play(sequence, true);
        toggleDrumPlayback();
    }


    /**
        Closes any resurces used by the GameManager.
    */
    public void stop() {
        super.stop();
        midiPlayer.close();
        soundManager.close();
    }


    private void initInput() {
        moveLeft = new GameAction("moveLeft");
        moveRight = new GameAction("moveRight");
        smash = new GameAction("smash",
        	GameAction.DETECT_INITAL_PRESS_ONLY);
        jump = new GameAction("jump",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        pauseKeyPress = new GameAction("pauseKeyPress",GameAction.DETECT_INITAL_PRESS_ONLY);
       
        roll = new GameAction("roll",
        	GameAction.DETECT_INITAL_PRESS_ONLY);
        		
        inputManager = new InputManager(
            screen.getFullScreenWindow());
        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);
        inputManager.mapToKey(smash, KeyEvent.VK_DOWN);
        inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(jump, KeyEvent.VK_SPACE);
        inputManager.mapToKey(pauseKeyPress, KeyEvent.VK_ESCAPE);
        inputManager.mapToKey(roll, KeyEvent.VK_SHIFT);
    }


    private void checkInput(long elapsedTime) {

    	//Pauses the game if the player presses the Esc key
        if (pauseKeyPress.isPressed()) {
            paused = !paused;
            jump.reset(); //Ensures game doesn't have the player jump if he/she presses SPACE while paused
        }
        if (!paused) {
        Player player = (Player)map.getPlayer();
        if (player.isAlive()) {
            float velocityX = 0;
            boolean moveLeft=this.moveLeft.isPressed();
            boolean moveRight=this.moveRight.isPressed();
            boolean roll=this.roll.isPressed();
            
            
            if (moveLeft) {
                velocityX-=player.getMaxSpeed();
                
            }
            if (moveRight) {
                velocityX+=player.getMaxSpeed();
                
            }
            if(roll &&  moveLeft) {
            	 velocityX-=player.getMaxSpeed();
            	player.setIsRolling(true);
            }
            if(roll && moveRight) {
            	player.setIsRolling(true);
            	velocityX+=player.getMaxSpeed();
            	System.out.println("roll is true");
            }
            if (smash.isPressed() && !player.isOnGround()) {
            	velocityX=0;
            	player.setVelocityY(1);
            	player.setIsSmashing(true);
            } else if (player.isOnGround() && player.getIsSmashing()){
            	player.setIsSmashing(false);
            }
            if (jump.isPressed()) {
            	if (player.isOnGround()) {
                	soundManager.play(boopSound);
                	player.setIsRolling(false);
                }
            	player.jump(false);
            }
            player.setVelocityX(velocityX);
        }
        }

    }


    public void draw(Graphics2D g) {
    		renderer.draw(g, map, screen.getWidth(), screen.getHeight(), resourceManager);
    }


    /**
        Gets the current map.
    */
    public TileMap getMap() {
        return map;
    }


    /**
        Turns on/off drum playback in the midi music (track 1).
    */
    public void toggleDrumPlayback() {
        Sequencer sequencer = midiPlayer.getSequencer();
        if (sequencer != null) {
            sequencer.setTrackMute(DRUM_TRACK,
                !sequencer.getTrackMute(DRUM_TRACK));
        }
    }


    /**
        Gets the tile that a Sprites collides with. Only the
        Sprite's X or Y should be changed, not both. Returns null
        if no collision is detected.
    */
    public Point getTileCollision(Sprite sprite,
        float newX, float newY)
    {
        float fromX = Math.min(sprite.getX(), newX);
        float fromY = Math.min(sprite.getY(), newY);
        float toX = Math.max(sprite.getX(), newX);
        float toY = Math.max(sprite.getY(), newY);

        // get the tile locations
        int fromTileX = TileMapRenderer.pixelsToTiles(fromX);
        int fromTileY = TileMapRenderer.pixelsToTiles(fromY);
        int toTileX = TileMapRenderer.pixelsToTiles(
            toX + sprite.getWidth() - 1);
        int toTileY = TileMapRenderer.pixelsToTiles(
            toY + sprite.getHeight() - 1);

        // check each tile for a collision
        for (int x=fromTileX; x<=toTileX; x++) {
            for (int y=fromTileY; y<=toTileY; y++) {
                if (x < 0 || x >= map.getWidth() ||
                    map.getTile(x, y) != null)
                {
                    // collision found, return the tile
                    pointCache.setLocation(x, y);
                    return pointCache;
                }
            }
        }

        // no collision found
        return null;
    }


    /**
        Checks if two Sprites collide with one another. Returns
        false if the two Sprites are the same. Returns false if
        one of the Sprites is a Creature that is not alive.
    */
    public boolean isCollision(Sprite s1, Sprite s2) {
        // if the Sprites are the same, return false
        if (s1 == s2) {
            return false;
        }

        // if one of the Sprites is a dead Creature, return false
        if (s1 instanceof Creature && !((Creature)s1).isAlive()) {
            return false;
        }
        if (s2 instanceof Creature && !((Creature)s2).isAlive()) {
            return false;
        }

        // get the pixel location of the Sprites
        int s1x = Math.round(s1.getX());
        int s1y = Math.round(s1.getY());
        int s2x = Math.round(s2.getX());
        int s2y = Math.round(s2.getY());

        // check if the two sprites' boundaries intersect
        return (s1x < s2x + s2.getWidth() &&
            s2x < s1x + s1.getWidth() &&
            s1y < s2y + s2.getHeight() &&
            s2y < s1y + s1.getHeight());
    }


    /**
        Gets the Sprite that collides with the specified Sprite,
        or null if no Sprite collides with the specified Sprite.
    */
    public Sprite getSpriteCollision(Sprite sprite) {

        // run through the list of Sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite otherSprite = (Sprite)i.next();
            if (isCollision(sprite, otherSprite)) {
                // collision found, return the Sprite
                return otherSprite;
            }
        }

        // no collision found
        return null;
    }


    /**
        Updates Animation, position, and velocity of all Sprites
        in the current map.
    */
    public boolean update(long elapsedTime) {
    	if (!paused) { //Checks if game is not paused
	        Creature player = (Creature)map.getPlayer();
	
	
	        // player is dead! start map over
	        if (player.getState() == Creature.STATE_DEAD) {
	            map = resourceManager.reloadMap();
	            return false;
	        }
	
	        // get keyboard/mouse input
	        checkInput(elapsedTime);
	
	        // update player
	        updateCreature(player, elapsedTime);
	        player.update(elapsedTime);
	
	        // update other sprites
	        Iterator i = map.getSprites(); 	
	        while (i.hasNext()) {
	            Sprite sprite = (Sprite)i.next();
	            if (sprite instanceof Creature) {
	                Creature creature = (Creature)sprite;
	                if (creature.getState() == Creature.STATE_DEAD) {
	                    i.remove();
	                }
	                else {
	                    updateCreature(creature, elapsedTime);
	                }
	            }
	            // normal update
	            sprite.update(elapsedTime);
	        }
    	}
    	
    	else { //If game is paused
    		Object[] options = new Object[4]; //Array of pause menu buttons
    		options[0]=new JCheckBox("Sound");
    		if (soundSelection == true) { //If player wants sound
    			((JCheckBox)options[0]).setSelected(true);
    		}
    		
    		options[1]=new JCheckBox("Music");
    		if (musicSelection == true) { //If player wants music
    			((JCheckBox)options[1]).setSelected(true);
    		}
    		options[2]="Resume";
    		options[3]="Exit";

    		
    		checkInput(elapsedTime);

    		//Shows instructions and controls to the player on the pause menu
    		int pauseMenuSelection =JOptionPane.showOptionDialog(screen.getFullScreenWindow(), 
    				"\tInstructions: "
    				+ "\nCollect all the keys on each level in order to open the door to the next level!"
    				+ "\n"
    				+ "\nControls: "
    				+ "\nA - Move left"
    				+ "\nD - Move right"
    				+ "\nSPACE - Jump"
    				+ "\nS - Smash" +
    				"\nA or D, then SHIFT - Roll",
    				"Pause Menu",
    				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    		
    		//Turns sound off if sound is not selected
    		JCheckBox soundSelector = (JCheckBox)options[0];
    		soundSelection = soundSelector.isSelected();
    		if ((soundSelection == false)) {
    			soundManager.setPaused(true);
    		}
    		//Turns sound on if sound is selected
    		else {
    			soundManager.setPaused(false);
    		}
  
    		//Turns music off if music is not selected
    		JCheckBox musicSelector = (JCheckBox)options[1];
    		musicSelection = musicSelector.isSelected();
    		Sequencer sequencer = midiPlayer.getSequencer();
    		if (musicSelection == false) {
    	        if (sequencer != null) {
    	            sequencer.stop();
    	        }
    		}
    	    
    		//Turns music on if music is selected
    	    else {
    	    	if (sequencer != null) {
    	    		sequencer.start();
    	    	}
    	    }
    		
    		//If player presses "Resume", unpause the game
    		if (pauseMenuSelection == 2) paused = false;

    		//If player presses "Exit", exit the game
    		else if (pauseMenuSelection == 3) {
    			stop();
    		}
    		
    		//If player presses "X" (close pause menu)
    		else {
    			paused = false;
    		}
    		

    		

    	}
    	return paused;
    }


    /**
        Updates the creature, applying gravity for creatures that
        aren't flying, and checks collisions.
    */
    private void updateCreature(Creature creature,
        long elapsedTime)
    {

    	if (!paused) {
	        if (creature instanceof Player && (creature.getY() < 0 || creature.getY() > 1000)) {
	        	creature.setState(Creature.STATE_DYING);
	        	soundManager.play(playerDyingSound);
	        }
	        
	        else {
	        // apply gravity
		        if (!creature.isFlying()) {
		            creature.setVelocityY(creature.getVelocityY() +
		                GRAVITY * elapsedTime);
		        }
		
		        // change x
		        float dx = creature.getVelocityX();
		        float oldX = creature.getX();
		        float newX = oldX + dx * elapsedTime;
		        Point tile =
		            getTileCollision(creature, newX, creature.getY());
		        if (tile == null) {
		            creature.setX(newX);
		        }
		        else {
		            // line up with the tile boundary
		            if (dx > 0) {
		                creature.setX(
		                    TileMapRenderer.tilesToPixels(tile.x) -
		                    creature.getWidth());
		            }
		            else if (dx < 0) {
		                creature.setX(
		                    TileMapRenderer.tilesToPixels(tile.x + 1));
		            }
		            creature.collideHorizontal();
		        }
		        if (creature instanceof Player) {
		            checkPlayerCollision((Player)creature, false);
		        }
		
		        // change y
		        float dy = creature.getVelocityY();
		        float oldY = creature.getY();
		        float newY = oldY + dy * elapsedTime;
		        tile = getTileCollision(creature, creature.getX(), newY);
		        if (tile == null) {
		            creature.setY(newY);
		        }
		        else {
		            // line up with the tile boundary
		            if (dy > 0) {
		                creature.setY(
		                    TileMapRenderer.tilesToPixels(tile.y) -
		                    creature.getHeight());
		            }
		            else if (dy < 0) {
		                creature.setY(
		                    TileMapRenderer.tilesToPixels(tile.y + 1));
		            }
		            creature.collideVertical();
		        }
		        if (creature instanceof Player) {
		        	boolean canKill = (oldY < creature.getY()) && ((Player)creature).getIsSmashing();
		            checkPlayerCollision((Player)creature, canKill);
		        }
		        
	    	}
    	}

<<<<<<< HEAD
=======
        // change x
        float dx = creature.getVelocityX();
        float oldX = creature.getX();
        float newX = oldX + dx * elapsedTime;
        Point tile =
            getTileCollision(creature, newX, creature.getY());
        if (tile == null) {
            creature.setX(newX);
        }
        else {
            // line up with the tile boundary
            if (dx > 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x) -
                    creature.getWidth());
            }
            else if (dx < 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x + 1));
            }
            creature.collideHorizontal();
        }
        if (creature instanceof Player) {
        	boolean canKill=((Player)creature).getIsRolling();
            checkPlayerCollision((Player)creature, canKill);
        }

        // change y
        float dy = creature.getVelocityY();
        float oldY = creature.getY();
        float newY = oldY + dy * elapsedTime;
        tile = getTileCollision(creature, creature.getX(), newY);
        if (tile == null) {
            creature.setY(newY);
        }
        else {
            // line up with the tile boundary
            if (dy > 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y) -
                    creature.getHeight());
            }
            else if (dy < 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y + 1));
            }
            creature.collideVertical();
        }
        if (creature instanceof Player) {
        	boolean canKill = (oldY < creature.getY()) && ((Player)creature).getIsSmashing();
        	if(((Player)creature).getIsRolling()) {
        		canKill=true;
        		
        	}
        	
            checkPlayerCollision((Player)creature, canKill);
        }
>>>>>>> branch 'master' of https://github.com/Dewberryjp/SpaceCabbages.git

    }


    /**
        Checks for Player collision with other Sprites. If
        canKill is true, collisions with Creatures will kill
        them.
    */
    public void checkPlayerCollision(Player player,
        boolean canKill)
    {
        if (!player.isAlive()) {
            return;
        }

        // check for player collision with other sprites
        Sprite collisionSprite = getSpriteCollision(player);
        if (collisionSprite instanceof PowerUp) {
            acquirePowerUp(player,(PowerUp)collisionSprite);
        }
        else if (collisionSprite instanceof Creature) {
            Creature badguy = (Creature)collisionSprite;
            if (canKill) { 
                // kill the badguy and make player bounce
                soundManager.play(mobDyingSound);
                badguy.setState(Creature.STATE_DYING);
                player.setY(badguy.getY() - player.getHeight());
                player.jump(true);          
            }
            else {
                // player dies!
                //player.setState(Creature.STATE_DYING);
                soundManager.play(playerDyingSound);
            	 float dx = badguy.getVelocityX();
                //player dies!
                //player.setState(Creature.STATE_DYING);
            	int damageCount = 1;
            	if(player.getHealth() > 1) {
            		player.setHealth(player.getHealth()- damageCount);
            		  
                      if (dx < 0) {
                          badguy.setVelocityX(Math.abs(dx));
                          badguy.setX(player.getX()+player.getWidth());
                      } else {
                    	  badguy.setVelocityX(-dx);
                    	  badguy.setX(player.getX()-badguy.getWidth());
                      }
                      
            	}
            	else {
            		player.setState(Creature.STATE_DYING);
            		soundManager.play(playerDyingSound);
            	}
            	
                
            	
            }
           
        }
        
    }


    /**
        Gives the player the speicifed power up and removes it
        from the map.
    */
    public void acquirePowerUp(Player player,PowerUp powerUp) {
        // remove it from the map
        map.removeSprite(powerUp);
      
        if (powerUp instanceof PowerUp.Star) {
            // do something here, like give the player points
            soundManager.play(prizeSound);
        }
        else if (powerUp instanceof PowerUp.Music) {
            // change the music
            soundManager.play(prizeSound);
            toggleDrumPlayback();
        }
        else if (powerUp instanceof PowerUp.Goal) {
            // advance to next map
            soundManager.play(newLevelSound);
            map = resourceManager.loadNextMap();
        } else if (powerUp instanceof PowerUp.Other) {
        	soundManager.play(alienSound);
        	player.jump(true);
        }
        else if(powerUp instanceof PowerUp.Water ) {
        	//add more health
        	
        	//adding player will increase speed 2x
        	
        	
        }
    }

    
}