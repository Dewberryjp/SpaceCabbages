package side_scroller.tilegame;

import java.awt.*;

import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;

import side_scroller.graphics.*;
import side_scroller.tilegame.sprites.*;


/**
    The ResourceManager class loads and manages tile Images and
    "host" Sprites used in the game. Game Sprites are cloned from
    "host" Sprites.
*/
public class ResourceManager extends TileMapRenderer{

    private ArrayList tiles;
    private int currentMap;
    private GraphicsConfiguration gc;

    // host sprites used for cloning
    private Sprite playerSprite;
    private Sprite musicSprite;
    private Sprite coinSprite;
    private Sprite goalSprite;
    private Sprite grubSprite;
    private Sprite flySprite;
    private Sprite otherSprite;
    private Sprite waterSprite;
   
    private Boss bossSprite;
    private Sprinkle blueSprinkle, 
				    greenSprinkle, 
				    yellowSprinkle, 
				    pinkSprinkle, 
				    orangeSprinkle;
    private ArrayList<Sprinkle> sprinkles = new ArrayList <Sprinkle>();

    private Animation life;
    private Animation key;
    
    /**
        Creates a new ResourceManager with the specified
        GraphicsConfiguration.
    */
    public ResourceManager(GraphicsConfiguration gc) {
        this.gc = gc;
        loadTileImages();
        loadCreatureSprites();
        loadPowerUpSprites();
        loadLifeKeyImages();
    }


   

	/**
        Gets an image from the images/ directory.
    */
    public Image loadImage(String name) {
        String filename = "images/" + name;
        return new ImageIcon(filename).getImage();
    }


    public Image getMirrorImage(Image image) {
        return getScaledImage(image, -1, 1);
    }


    public Image getFlippedImage(Image image) {
        return getScaledImage(image, 1, -1);
    }


    private Image getScaledImage(Image image, float x, float y) {

        // set up the transform
        AffineTransform transform = new AffineTransform();
        transform.scale(x, y);
        transform.translate(
            (x-1) * image.getWidth(null) / 2,
            (y-1) * image.getHeight(null) / 2);

        // create a transparent (not translucent) image
        Image newImage = gc.createCompatibleImage(
            image.getWidth(null),
            image.getHeight(null),
            Transparency.BITMASK);

        // draw the transformed image
        Graphics2D g = (Graphics2D)newImage.getGraphics();
        g.drawImage(image, transform, null);
        g.dispose();

        return newImage;
    }


    public TileMap loadNextMap(){
    	
        TileMap map = null;
        while (map == null) {
            currentMap++;
            try {
                map = loadMap(
                    "maps/map" + currentMap + ".txt");
            }
            catch (IOException ex) {
                if (currentMap == 1) {
                    // no maps to load!
                	return null; 
                }
                currentMap = 0;
                map = null;
            }
        }
        
        return map;
    }


    public TileMap reloadMap() {
        try {
            return loadMap(
                "maps/map" + currentMap + ".txt");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private TileMap loadMap(String filename)
        throws IOException
    {
    	
        ArrayList lines = new ArrayList();
        int width = 0;
        int height = 0;

        // read every line in the text file into the list
        BufferedReader reader = new BufferedReader(
            new FileReader(filename));
        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            // add every line except for comments
            if (!line.startsWith("#")) {
                lines.add(line);
                width = Math.max(width, line.length());
            }
        }

        // parse the lines to create a TileEngine
        height = lines.size();
        TileMap newMap = new TileMap(width, height);
        
        
        newMap.setLife(life);
        newMap.setKey(key);
        
        String getNum = filename.substring(8,9);
        newMap.setBackground(loadImage("back"+ getNum +".png"));
     
        for (int y=0; y<height; y++) {
            String line = (String)lines.get(y);
            for (int x=0; x<line.length(); x++) {
                char ch = line.charAt(x);

                // check if the char represents tile A, B, C etc.
                int tile = ch - 'A';
                if (tile >= 0 && tile < tiles.size()) {
                    newMap.setTile(x, y, (Image)tiles.get(tile));
                }

                // check if the char represents a sprite
                else if (ch == 'o') {
                    addSprite(newMap, coinSprite, x, y);
                }
                else if (ch == '!') {
                    addSprite(newMap, musicSprite, x, y);
                }
                else if (ch == '*') {
                    addSprite(newMap, goalSprite, x, y);
                }
                else if (ch == '1') {
                    addSprite(newMap, grubSprite, x, y);
                }
                else if (ch == '2') {
                    addSprite(newMap, flySprite, x, y);
                }
                else if (ch == '3') {
                	addSprite(newMap, otherSprite, x, y);
                }
                else if (ch == '4') {
                	addSprite(newMap, bossSprite, x, y); 
                }
                else if (ch == 'w') {
                	addSprite(newMap, waterSprite, x, y);
                }
            }
        }
        
        // add the player to the map
        Sprite player = null;
		try {
			player = (Sprite)playerSprite.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        player.setX(TileMapRenderer.tilesToPixels(3));
        player.setY(0);
        newMap.setPlayer(player);
               
        return newMap;
    }


    private void addSprite(TileMap map,
        Sprite hostSprite, int tileX, int tileY)
    {
        if (hostSprite != null) {
            // clone the sprite from the "host"
            Sprite sprite=null;
			try {
				sprite = (Sprite)hostSprite.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            // center the sprite
            sprite.setX(
                TileMapRenderer.tilesToPixels(tileX) +
                (TileMapRenderer.tilesToPixels(1) -
                sprite.getWidth()) / 2);

            // bottom-justify the sprite
            sprite.setY(
                TileMapRenderer.tilesToPixels(tileY + 1) -
                sprite.getHeight());

            // add it to the map
            map.addSprite(sprite);
            
        }
    }


    // -----------------------------------------------------------
    // code for loading sprites and images
    // -----------------------------------------------------------


    public void loadTileImages() {
        // keep looking for tile A,B,C, etc. this makes it
        // easy to drop new tiles in the images/ directory
        tiles = new ArrayList();
        char ch = 'A';
        while (true) {
     //   if(playerSprite.getCurrentKeys()==10 && ch=='S') {
        	
       // 	}
            String name = "tile_" + ch + ".png";
            File file = new File("images/" + name);
            if (!file.exists()) {
                break;
            }
            tiles.add(loadImage(name));
            ch++;
            
        }
    }


    public void loadCreatureSprites() {

        Image[][] images = new Image[4][];

        // load left-facing images
        images[0] = new Image[] {
            loadImage("player1.png"),
            loadImage("player2.png"),
            loadImage("player3.png"),
            loadImage("fly1.png"),
            loadImage("fly2.png"),
            loadImage("fly3.png"),
            loadImage("grub1.png"),
            loadImage("grub2.png"),
            loadImage("cupCake_boss1.png"),
            loadImage("cupCake_boss2.png"),
            loadImage("cupCake_boss_attack1.png"),
            loadImage("cupCake_boss_attack2.png")  
         };


        images[1] = new Image[images[0].length];
        images[2] = new Image[images[0].length];
        images[3] = new Image[images[0].length];
  
        for (int i=0; i<images[0].length; i++) {
            // right-facing images
            images[1][i] = getMirrorImage(images[0][i]);
            // left-facing "dead" images
            images[2][i] = getFlippedImage(images[0][i]);
            // right-facing "dead" images
            images[3][i] = getFlippedImage(images[1][i]);
        }

        // create creature animations
        Animation[] playerAnim = new Animation[4];
        Animation[] flyAnim = new Animation[4];
        Animation[] grubAnim = new Animation[4];
        Animation[] bossAnim = new Animation[4];
        Animation[] bossAttackAnim = new Animation[4];

      
        for (int i=0; i<4; i++) {
            playerAnim[i] = createPlayerAnim(
                images[i][0], images[i][1], images[i][2]);
            flyAnim[i] = createFlyAnim(
                images[i][3], images[i][4], images[i][5]);
            grubAnim[i] = createGrubAnim(
                images[i][6], images[i][7]);

            bossAnim[i] = createBossAnim(
            		images[i][8], images[i][9]);
            bossAttackAnim[i] = createBossAttackAnim(images[i][10],images[i][11]);

        }
      
        //make the two jumping animations
        Image[][] jumpImages=new Image[2][];
        jumpImages[0]= new Image[] {
        	loadImage("playerJump1.png"),
        	loadImage("playerJump2.png"),
        	loadImage("playerJump3.png")
        };
        jumpImages[1]=new Image[] {
        		getMirrorImage(jumpImages[0][0]),
        		getMirrorImage(jumpImages[0][1]),
        		getMirrorImage(jumpImages[0][2])
        };
        Image[][] rollImages=new Image[2][];
        rollImages[0]=new Image[] {
        	loadImage("playerRoll1.png"),
        	loadImage("playerRoll2.png"),
        	loadImage("playerRoll3.png"),
        	loadImage("playerRoll4.png"),
        	loadImage("playerRoll5.png"),
        	loadImage("playerRoll6.png"),
        	loadImage("playerRoll7.png")
        };
        rollImages[1]=new Image[] {
        	getMirrorImage(rollImages[0][0]),
        	getMirrorImage(rollImages[0][1]),
        	getMirrorImage(rollImages[0][2]),
        	getMirrorImage(rollImages[0][3]),
        	getMirrorImage(rollImages[0][4]),
        	getMirrorImage(rollImages[0][5]),
        	getMirrorImage(rollImages[0][6])
        };
        Animation jumpLeft = new Animation();
        jumpLeft.addFrame(jumpImages[0][0], 250);
        jumpLeft.addFrame(jumpImages[0][1], 150);
        jumpLeft.addFrame(jumpImages[0][2], 150);
        Animation jumpRight = new Animation();
        jumpRight.addFrame(jumpImages[1][0], 250);
        jumpRight.addFrame(jumpImages[1][1], 150);
        jumpRight.addFrame(jumpImages[1][2], 150);
        Animation rollLeft = new Animation();
        rollLeft.addFrame(rollImages[0][0], 40);
        rollLeft.addFrame(rollImages[0][1], 40);
        rollLeft.addFrame(rollImages[0][2], 50);
        rollLeft.addFrame(rollImages[0][3], 100);
        rollLeft.addFrame(rollImages[0][4], 100);
        rollLeft.addFrame(rollImages[0][5], 50);
        rollLeft.addFrame(rollImages[0][6], 50);
        Animation rollRight = new Animation();
        rollRight.addFrame(rollImages[1][0], 40);
        rollRight.addFrame(rollImages[1][1], 40);
        rollRight.addFrame(rollImages[1][2], 50);
        rollRight.addFrame(rollImages[1][3], 100);
        rollRight.addFrame(rollImages[1][4], 100);
        rollRight.addFrame(rollImages[1][5], 50);
        rollRight.addFrame(rollImages[1][6], 50);
        // create creature sprites
        
        playerSprite =new Player("left",playerAnim[0]);
        playerSprite.addAnimation("right", playerAnim[1]);
        playerSprite.addAnimation("deadLeft", playerAnim[2]);
        playerSprite.addAnimation("deadRight", playerAnim[3]);
        playerSprite.addAnimation("jumpLeft", jumpLeft);
        playerSprite.addAnimation("jumpRight", jumpRight);
        playerSprite.addAnimation("rollLeft", rollLeft);
        playerSprite.addAnimation("rollRight", rollRight);
        flySprite = new Fly("left",flyAnim[0]);
        flySprite.addAnimation("right", flyAnim[1]);
        flySprite.addAnimation("deadLeft", flyAnim[2]);
        flySprite.addAnimation("deadRight", flyAnim[3]);        
        grubSprite = new Grub("left",grubAnim[0]);
        grubSprite.addAnimation("right", grubAnim[1]);
        grubSprite.addAnimation("deadLeft", grubAnim[2]);
        grubSprite.addAnimation("deadRight", grubAnim[3]);

        //Adding Sprinkles "Animations" (Just the same picture twice)
        blueSprinkle = new Sprinkle("bossAttack_Left",createSprinkle(loadImage("b_sprinkle.png")));
        	blueSprinkle.addAnimation("bossAttack_Right", createSprinkle(loadImage("b_sprinkle.png")));
        	
        greenSprinkle = new Sprinkle("bossAttack_Left",createSprinkle(loadImage("g_sprinkle.png")));
        	greenSprinkle.addAnimation("bossAttack_Right", createSprinkle(loadImage("g_sprinkle.png")));
        	
        orangeSprinkle = new Sprinkle("bossAttack_Left",createSprinkle(loadImage("o_sprinkle.png")));
        	orangeSprinkle.addAnimation("bossAttack_Right", createSprinkle(loadImage("o_sprinkle.png")));
        	
        pinkSprinkle = new Sprinkle("bossAttack_Left",createSprinkle(loadImage("p_sprinkle.png")));
        	pinkSprinkle.addAnimation("bossAttack_Right", createSprinkle(loadImage("p_sprinkle.png")));
        	
        yellowSprinkle = new Sprinkle("bossAttack_Left",createSprinkle(loadImage("y_sprinkle.png")));
        	yellowSprinkle.addAnimation("bossAttack_Right", createSprinkle(loadImage("y_sprinkle.png")));
        	
        //Adding Sprinkles to boss ArrayList
        this.sprinkles.add(blueSprinkle);
        this.sprinkles.add(greenSprinkle);
        this.sprinkles.add(orangeSprinkle);
        this.sprinkles.add(pinkSprinkle);
        this.sprinkles.add(yellowSprinkle);
        //Boss regular walking animation
        bossSprite = new Boss("left",bossAnim[0]);
        bossSprite.addAnimation("right", bossAnim[1]);
        bossSprite.addAnimation("deadLeft", bossAnim[2]);
        bossSprite.addAnimation("deadRight", bossAnim[3]);
        //Boss attacking animations
        bossSprite.addAnimation("bossAttack_Left", bossAttackAnim[0]);//Left facing boss attack
        bossSprite.addAnimation("bossAttack_Right", bossAttackAnim[1]);//right facing boss attack

        
      
        
    }


    private Animation createPlayerAnim(Image player1,
        Image player2, Image player3)
    {
        Animation anim = new Animation();
        anim.addFrame(player1, 250);
        anim.addFrame(player2, 150);
        anim.addFrame(player1, 150);
        anim.addFrame(player3, 150);
        anim.addFrame(player2, 200);
        anim.addFrame(player1, 150);
        return anim;
    }


    private Animation createFlyAnim(Image img1, Image img2,
        Image img3)
    {
        Animation anim = new Animation();
        anim.addFrame(img1, 50);
        anim.addFrame(img2, 50);
        anim.addFrame(img3, 50);
        anim.addFrame(img2, 50);
        return anim;
    }


    private Animation createGrubAnim(Image img1, Image img2) {
        Animation anim = new Animation();
        anim.addFrame(img1, 250);
        anim.addFrame(img2, 250);
        return anim;
    }

    private Animation createBossAnim(Image img1, Image img2) {
        Animation anim = new Animation();
        anim.addFrame(img1, 1000);
        anim.addFrame(img2, 100);
        return anim;
    }

    private Animation createBossAttackAnim(Image img1, Image img2) {
        Animation anim = new Animation();
        anim.addFrame(img1, 600);
        anim.addFrame(img2, 1000);
        return anim;
    }
    
    private Animation createSprinkle(Image img1) {
    	Animation anim = new Animation();
        anim.addFrame(img1, 600);
        anim.addFrame(img1, 600);
    	return anim;
    }

  
    private void loadPowerUpSprites() {
        // create "goal" sprite
        Animation anim = new Animation();
        anim.addFrame(loadImage("newlvl.png"), 150);
        anim.addFrame(loadImage("newlvl1.png"), 150);
        anim.addFrame(loadImage("newlvl2.png"), 150);
        anim.addFrame(loadImage("newlvl3.png"), 150);
        anim.addFrame(loadImage("newlvl2.png"), 150);
        anim.addFrame(loadImage("newlvl1.png"), 150);
        anim.addFrame(loadImage("newlvl.png"), 150);
        goalSprite = new PowerUp.Goal("right",anim);
       
        // create "star/key" sprite
        anim = new Animation();
        anim.addFrame(loadImage("key.png"), 100);
        anim.addFrame(loadImage("key1.png"), 100);
        anim.addFrame(loadImage("key2.png"), 100);
        anim.addFrame(loadImage("key3.png"), 100);
        anim.addFrame(loadImage("key3.png"), 100);
        anim.addFrame(loadImage("key2.png"), 100);
        anim.addFrame(loadImage("key1.png"), 100);
        anim.addFrame(loadImage("key.png"), 100);
        coinSprite = new PowerUp.Star("right",anim);

        // create "music" sprite
        anim = new Animation();
        anim.addFrame(loadImage("music1.png"), 150);
        anim.addFrame(loadImage("music2.png"), 150);
        anim.addFrame(loadImage("music3.png"), 150);
        anim.addFrame(loadImage("music2.png"), 150);
        musicSprite = new PowerUp.Music("right",anim);
        
        //create "other" sprite
        anim = new Animation();
        anim.addFrame(loadImage("powerup5.png"), 100);
        anim.addFrame(loadImage("powerup6.png"), 100);
        anim.addFrame(loadImage("powerup7.png"), 100);
        anim.addFrame(loadImage("powerup8.png"), 100);
        otherSprite = new PowerUp.Other("right",anim);
        
        //create the "water drop (health powerup)"  sprite
        anim = new Animation();
        anim.addFrame(loadImage("WaterDrop.png"), 100);
        waterSprite = new PowerUp.Water("right", anim);
       
   
    }

    private void loadLifeKeyImages() {
    	life = new Animation();
    	life.addFrame(loadImage("life1.png"), 100);
        life.addFrame(loadImage("life2.png"), 100);
        life.addFrame(loadImage("life3.png"), 100);
        key = new Animation();
    	key.addFrame(loadImage("key1.png"), 150);
    	key.addFrame(loadImage("key2.png"), 150);
    	key.addFrame(loadImage("key3.png"), 150);

    }

    
    public int getCurrentMap() {
    	return currentMap;
    }



	public Sprinkle getRandomSprinkle() {
		// TODO Auto-generated method stub
		Random rand = new Random();
		Sprinkle selectedSprinkle = this.sprinkles.get(rand.nextInt(sprinkles.size()));
		try {
			return (Sprinkle) selectedSprinkle.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
