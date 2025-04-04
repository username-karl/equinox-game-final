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

            // Load Backgrounds 
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
            Image img = new ImageIcon(getClass().getResource(path)).getImage();
            if (img != null) {
                imageMap.put(key, img);
                System.out.println("Loaded: " + key + " from " + path);
                return img;
            } else {
                 System.err.println("Failed to load image: " + key + " from " + path + " (Resource not found)");
                 return null;
            }
        } catch (Exception e) {
            System.err.println("Failed to load image: " + key + " from " + path);
            e.printStackTrace();
            return null;
        }
    }

    private void loadBackgroundImage(int stageNumber, String key, String path) {
        Image bg = loadImage(key, path);
        if (bg != null) {
            backgroundImages.put(stageNumber, bg);
        }
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
        return backgroundImages.getOrDefault(stageNumber, backgroundImages.get(0)); 
    }

    // Maybe add methods for loading sounds in the future
    // public SoundClip getSound(String key) { ... }
} 