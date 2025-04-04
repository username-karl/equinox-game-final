package com.equinox.game.utils;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList; // Added for enemy image list

public class AssetLoader {

    private Map<String, Image> imageMap;
    private ArrayList<Image> enemyImageVariations; // Store general enemy variations
    private Map<Integer, Image> backgroundImages; // Store background images by stage number

    public AssetLoader() {
        imageMap = new HashMap<>();
        enemyImageVariations = new ArrayList<>();
        backgroundImages = new HashMap<>();
        loadImages();
    }

    private void loadImages() {
        try {
            // Load Player Ship
            loadImage(Constants.PLAYER_SHIP_IMG_KEY, "/assets/player_captainnova_portrait.png");
            
            // Load Captain Portrait (using captainnova.png for cutscenes)
            loadImage(Constants.CAPTAIN_PORTRAIT_KEY, "/assets/captainnova.png");

            // Load Enemy Variations
            Image enemy1 = loadImage(Constants.ENEMY_DRONE_IMG_KEY, "/assets/enemy_drone_animated.gif");
            // Also load enemy_alien_type1.png if needed, potentially add ENEMY_ALIEN1_IMG_KEY
            Image enemy2 = loadImage(Constants.ENEMY_ALIEN2_IMG_KEY, "/assets/enemy_alien_type2.png");
            Image enemy3 = loadImage(Constants.ENEMY_ALIEN3_IMG_KEY, "/assets/enemy_alien_type3.png");
            if (enemy1 != null) enemyImageVariations.add(enemy1);
            if (enemy2 != null) enemyImageVariations.add(enemy2);
            if (enemy3 != null) enemyImageVariations.add(enemy3);

            // Load Bosses
            loadImage(Constants.MINIBOSS_IMG_KEY, "/assets/boss_mini_alien.png");
            loadImage(Constants.MAINBOSS_IMG_KEY, "/assets/boss_main_alien.png");

            // Load Weapons
            loadImage(Constants.PLAYER_LASER_IMG_KEY, "/assets/weapon_laser_blue.png");
            loadImage(Constants.ENEMY_LASER_IMG_KEY, "/assets/weapon_laser_red.png");

            // Load Backgrounds (Store them in the map)
            loadAndStoreBackground(Constants.BG_STAGE_1_KEY, "/assets/bg_space_nebula.png");
            loadAndStoreBackground(Constants.BG_STAGE_2_KEY, "/assets/bg_ancient_ruins.png"); // World 2 Background
            loadAndStoreBackground(Constants.BG_STAGE_3_KEY, "/assets/bg_quantum_singularity.png"); // World 3 Background
            loadAndStoreBackground(Constants.BG_STAGE_4_KEY, "/assets/bg_sacred_temple.png"); // World 4 Background
            loadAndStoreBackground(Constants.BG_DEFAULT_KEY, "/assets/bg_space_nebula.png"); // Default fallback

             // Load Cutscene Backgrounds (Assuming these paths are correct or placeholders)
            loadAndStoreBackground(Constants.BG_CUTSCENE_1_KEY, "/assets/bg_cutscene_bridge.png"); 
            loadAndStoreBackground(Constants.BG_CUTSCENE_2_KEY, "/assets/bg_cutscene_planet.png"); 

            // Load World 2 Enemy Assets (Update paths)
            loadImage(Constants.ENEMY_W2_TYPE1_IMG_KEY, "/assets/enemy_w2_type1.png"); // Placeholder enemy name
            loadImage(Constants.ENEMY_W2_TYPE2_IMG_KEY, "/assets/enemy_w2_type2.png"); // Placeholder enemy name
            loadImage(Constants.ENEMY_W2_TYPE3_IMG_KEY, "/assets/enemy_w2_type3.png"); // Placeholder enemy name
            loadImage(Constants.ENEMY_W2_TYPE4_IMG_KEY, "/assets/enemy_w2_type4.png"); // Placeholder enemy name
            loadImage(Constants.ENEMY_W2_TYPE5_IMG_KEY, "/assets/enemy_w2_type5.png"); // Placeholder enemy name
            loadImage(Constants.GUARDIAN_SPAWN_IMG_KEY, "/assets/guardian_spawn.png"); // Miniboss W2
            loadImage(Constants.GUARDIAN_CONSTRUCT_IMG_KEY, "/assets/guardian_construct.png"); // Boss W2

            // Load World 3 Enemy Assets (Update paths)
            loadImage(Constants.ENEMY_W3_TYPE1_IMG_KEY, "/assets/enemy_w3_type1.png"); // Placeholder enemy name
            loadImage(Constants.ENEMY_W3_TYPE2_IMG_KEY, "/assets/enemy_w3_type2.png"); // Placeholder enemy name
            loadImage(Constants.ENEMY_W3_TYPE3_IMG_KEY, "/assets/enemy_w3_type3.png"); // Placeholder enemy name
            loadImage(Constants.ENEMY_W3_TYPE4_IMG_KEY, "/assets/enemy_w3_type4.png"); // Placeholder enemy name
            loadImage(Constants.ENEMY_W3_TYPE5_IMG_KEY, "/assets/enemy_w3_type5.png"); // Placeholder enemy name
            loadImage(Constants.PARADOX_ENTITY_IMG_KEY, "/assets/paradox_entity.png"); // Boss W3

            // Load World 4 Enemy Assets (Update paths)
            loadImage(Constants.ENEMY_W4_TYPE1_IMG_KEY, "/assets/enemy_w4_type1.png"); // Placeholder enemy name
            loadImage(Constants.ENEMY_W4_TYPE2_IMG_KEY, "/assets/enemy_w4_type2.png"); // Placeholder enemy name
            loadImage(Constants.ENEMY_W4_TYPE3_IMG_KEY, "/assets/enemy_w4_type3.png"); // Placeholder enemy name
            loadImage(Constants.ENEMY_W4_TYPE4_IMG_KEY, "/assets/enemy_w4_type4.png"); // Placeholder enemy name
            loadImage(Constants.ENEMY_W4_TYPE5_IMG_KEY, "/assets/enemy_w4_type5.png"); // Placeholder enemy name
            loadImage(Constants.TEMPLE_GUARDIAN_IMG_KEY, "/assets/temple_guardian.png"); // Final Boss W4

            // Load UI Placeholders
            loadImage(Constants.PLACEHOLDER_ICON_KEY, "/assets/temp.png");
            loadImage(Constants.BG_SHOP_KEY, "/assets/bg_shop_deep_space.png"); // Load shop background
            // Load Backgrounds 
            // Title Screen Background
            loadImage(Constants.BG_MAIN_MENU_KEY, "/assets/bg_main_menu.png");
            loadBackgroundImage(0, Constants.BG_DEFAULT_KEY, "/assets/bg_nebula.png"); // Default gameplay BG
            // Load specific Cutscene Backgrounds
            loadImage(Constants.BG_CUTSCENE_1_KEY, "/assets/cg_stage1_scene1.png"); // Load Cutscene 1 BG
            loadImage(Constants.BG_CUTSCENE_2_KEY, "/assets/cg_stage2_scene1.png"); // Load Cutscene 2 BG
            // Map gameplay backgrounds for stages 1, 2, 3 to the default nebula
            backgroundImages.put(1, getImage(Constants.BG_DEFAULT_KEY));
            backgroundImages.put(2, getImage(Constants.BG_DEFAULT_KEY)); 
            backgroundImages.put(3, getImage(Constants.BG_DEFAULT_KEY));

            System.out.println("Assets loaded successfully.");

        } catch (Exception e) {
            System.err.println("Error loading assets: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Image loadImage(String key, String path) {
        try {
            // Use getResourceAsStream for better path handling, especially in JARs
            java.io.InputStream imgStream = getClass().getResourceAsStream(path);
            if (imgStream != null) {
                Image img = new ImageIcon(javax.imageio.ImageIO.read(imgStream)).getImage();
                if (img != null) {
                    imageMap.put(key, img);
                    System.out.println("SUCCESS: Loaded asset '" + key + "' from path: " + path);
                    return img;
                } else {
                    System.err.println("ERROR: Failed to decode image for key '" + key + "' from path: " + path);
                    return null;
                }
            } else {
                 System.err.println("ERROR: Asset resource not found for key '" + key + "' at path: " + path);
                 return null;
            }
        } catch (Exception e) {
            System.err.println("EXCEPTION loading asset '" + key + "' from path: " + path);
            e.printStackTrace();
            return null;
        }
    }

    private Image loadAndStoreBackground(String key, String path) {
        Image img = loadImage(key, path);
        if (img != null) {
            // Determine stage number from key (simple example)
             if (key.contains("_STAGE_1")) backgroundImages.put(1, img);
            else if (key.contains("_STAGE_2")) backgroundImages.put(2, img);
            else if (key.contains("_STAGE_3")) backgroundImages.put(3, img);
            else if (key.contains("_STAGE_4")) backgroundImages.put(4, img);
             // Add logic for cutscene keys if storing differently
            else if (key.equals(Constants.BG_CUTSCENE_1_KEY)) imageMap.put(key, img); // Store cutscenes normally for now
            else if (key.equals(Constants.BG_CUTSCENE_2_KEY)) imageMap.put(key, img);
             // Handle default separately or based on map contents later
        }
        return img;
    }

    public Image getImage(String key) {
        Image img = imageMap.get(key);
        if (img == null) {
             System.err.println("Warning: Image not found for key: " + key);
             // Return a default placeholder image? Or null?
        }
        return img;
    }

    // Get a random enemy image variation
    public Image getRandomEnemyImage() {
        if (enemyImageVariations.isEmpty()) {
            return null; // Or a default enemy image
        }
        int randomIndex = (int) (Math.random() * enemyImageVariations.size());
        return enemyImageVariations.get(randomIndex);
    }

    // Get a specific enemy image by index (less ideal, use getRandomEnemyImage or specific keys)
    public Image getEnemyImage(int index) {
         if (index >= 0 && index < enemyImageVariations.size()) {
            return enemyImageVariations.get(index);
         } 
         System.err.println("Warning: Invalid enemy image index: " + index);
         return getRandomEnemyImage(); // Fallback to random
    }

    public Image getBackgroundImage(int stageNumber) {
        // Return specific stage background if available, otherwise default
        return backgroundImages.getOrDefault(stageNumber, getImage(Constants.BG_DEFAULT_KEY)); 
    }

    // Maybe add methods for loading sounds in the future
    // public SoundClip getSound(String key) { ... }
} 