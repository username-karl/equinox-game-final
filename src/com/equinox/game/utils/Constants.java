package com.equinox.game.utils;

import java.awt.Color;

public class Constants {

    // Board Dimensions
    public static final int TILE_SIZE = 32;
    public static final int ROWS = 24;
    public static final int COLUMNS = 24;
    public static final int BOARD_WIDTH = TILE_SIZE * COLUMNS; // 768
    public static final int BOARD_HEIGHT = TILE_SIZE * ROWS; // 768

    // Game Loop
    public static final int TIMER_DELAY_MS = 1000 / 60; // Approx 60 FPS

    // Ship Properties
    public static final int SHIP_WIDTH = TILE_SIZE * 2;
    public static final int SHIP_HEIGHT = TILE_SIZE;
    public static final int SHIP_START_X = BOARD_WIDTH / 2 - SHIP_WIDTH / 2;
    public static final int SHIP_START_Y = BOARD_HEIGHT - TILE_SIZE * 3; // Adjusted start Y
    public static final int SHIP_ACCELERATION = 2;
    public static final int SHIP_MAX_SPEED = 8;
    public static final int SHIP_DECELERATION = 1;
    public static final int SHIP_INITIAL_HEALTH = 5;

    // Enemy Properties
    public static final int ENEMY_WIDTH = TILE_SIZE * 2;
    public static final int ENEMY_HEIGHT = TILE_SIZE;
    public static final int ENEMY_START_X = 0;
    public static final int ENEMY_START_Y = 0;
    public static final int ENEMY_BASE_VELOCITY_X = 1;
    public static final int ENEMY_INITIAL_ROWS = 5;
    public static final int ENEMY_INITIAL_COLUMNS = 5;
    public static final int ENEMY_MAX_ROWS = 15;
    public static final int ENEMY_MAX_COLS = 11;

    // Bullet Properties
    public static final int BULLET_WIDTH = TILE_SIZE / 8;
    public static final int BULLET_HEIGHT = TILE_SIZE / 2;
    public static final int BULLET_VELOCITY_Y = -20;

    // Enemy Bullet Properties
    public static final int ENEMY_BULLET_WIDTH = TILE_SIZE / 8;
    public static final int ENEMY_BULLET_HEIGHT = TILE_SIZE / 2;
    // Enemy bullet velocity might vary per enemy type

    // Abilities
    public static final int WAVE_BLAST_WIDTH = TILE_SIZE / 4;
    public static final int WAVE_BLAST_HEIGHT = TILE_SIZE * 10;
    public static final int WAVE_BLAST_VELOCITY_Y = -100;
    public static final long WAVE_BLAST_COOLDOWN_MS = 2500;

    public static final int LASER_BEAM_WIDTH = TILE_SIZE;
    public static final int LASER_BEAM_HEIGHT = TILE_SIZE / 8;
    public static final int LASER_BEAM_VELOCITY_Y = -15;
    public static final long LASER_BEAM_COOLDOWN_MS = 1000;

    public static final long PHASE_SHIFT_DURATION_MS = 1500;
    public static final long PHASE_SHIFT_COOLDOWN_MS = 10000;

    // Scoring & Economy
    public static final int SCORE_PER_ENEMY = 100;
    public static final int SCORE_PER_MINIBOSS = 10000;
    public static final int SCORE_PER_MAINBOSS = 20000;
    public static final int STARTING_MONEY = 150;
    public static final int MONEY_PER_MINIBOSS = 2500;
    public static final int MONEY_PER_MAINBOSS = 5000;
    public static final int CHEAT_MONEY_BONUS = 100000;

    // HUD & UI
    public static final int HUD_PADDING = 10;
    public static final int HUD_TOP_Y = 25;
    public static final Color SCORE_COLOR = Color.LIGHT_GRAY;
    public static final Color MONEY_COLOR = Color.ORANGE;
    public static final Color KILLED_COLOR = Color.RED;
    public static final Color STAGE_INFO_COLOR = Color.WHITE;
    public static final Color HEALTH_TEXT_COLOR = Color.WHITE;
    public static final Color HEALTH_BAR_BG_COLOR = Color.DARK_GRAY;
    public static final Color HEALTH_BAR_FILL_COLOR = Color.GREEN;
    public static final Color HEALTH_BAR_BORDER_COLOR = Color.WHITE;
    public static final Color COOLDOWN_Q_COLOR = Color.YELLOW;
    public static final Color COOLDOWN_E_COLOR = Color.ORANGE;
    public static final Color COOLDOWN_R_COLOR = Color.CYAN;
    public static final Color COOLDOWN_R_ACTIVE_COLOR = Color.MAGENTA;
    public static final Color COOLDOWN_READY_COLOR = Color.GREEN;
    public static final Color CHEAT_INDICATOR_COLOR = Color.MAGENTA;
    public static final Color GAME_OVER_MSG_COLOR = Color.RED;
    public static final Color MENU_TITLE_COLOR = Color.CYAN;
    public static final Color MENU_ITEM_DEFAULT_COLOR = Color.WHITE;
    public static final Color MENU_ITEM_SELECTED_COLOR = Color.YELLOW;
    public static final Color MENU_ITEM_CHEAT_ACTIVE_COLOR = Color.MAGENTA;

    // Asset Keys (Use these strings to retrieve assets from AssetLoader)
    public static final String PLAYER_SHIP_IMG_KEY = "player_ship";
    public static final String ENEMY_DRONE_IMG_KEY = "enemy_drone";
    public static final String ENEMY_ALIEN2_IMG_KEY = "enemy_alien2";
    public static final String ENEMY_ALIEN3_IMG_KEY = "enemy_alien3";
    public static final String MINIBOSS_IMG_KEY = "miniboss";
    public static final String MAINBOSS_IMG_KEY = "mainboss";
    public static final String PLAYER_LASER_IMG_KEY = "laser_blue";
    public static final String ENEMY_LASER_IMG_KEY = "laser_red";
    public static final String BG_STAGE_1_KEY = "bg_stage1";
    public static final String BG_STAGE_2_KEY = "bg_stage2";
    public static final String BG_STAGE_3_KEY = "bg_stage3";
    public static final String BG_STAGE_4_KEY = "bg_stage4";
    public static final String BG_DEFAULT_KEY = "bg_default";
    public static final String CAPTAIN_PORTRAIT_KEY = "captain_portrait";
    public static final String BG_CUTSCENE_1_KEY = "bg_cutscene1";
    public static final String BG_CUTSCENE_2_KEY = "bg_cutscene2";
    public static final String BG_MAIN_MENU_KEY = "bg_main_menu";
    // --- World 2 Enemy/Boss Keys (Placeholders) ---
    public static final String ENEMY_W2_TYPE1_IMG_KEY = "enemy_w2_type1";
    public static final String ENEMY_W2_TYPE2_IMG_KEY = "enemy_w2_type2";
    public static final String ENEMY_W2_TYPE3_IMG_KEY = "enemy_w2_type3";
    public static final String ENEMY_W2_TYPE4_IMG_KEY = "enemy_w2_type4";
    public static final String ENEMY_W2_TYPE5_IMG_KEY = "enemy_w2_type5";
    public static final String GUARDIAN_SPAWN_IMG_KEY = "guardian_spawn";
    public static final String GUARDIAN_CONSTRUCT_IMG_KEY = "guardian_construct";

    // --- World 3 Enemy/Boss Keys (Placeholders) ---
    public static final String ENEMY_W3_TYPE1_IMG_KEY = "enemy_w3_type1";
    public static final String ENEMY_W3_TYPE2_IMG_KEY = "enemy_w3_type2";
    public static final String ENEMY_W3_TYPE3_IMG_KEY = "enemy_w3_type3";
    public static final String ENEMY_W3_TYPE4_IMG_KEY = "enemy_w3_type4";
    public static final String ENEMY_W3_TYPE5_IMG_KEY = "enemy_w3_type5";
    public static final String PARADOX_ENTITY_IMG_KEY = "paradox_entity";

    // --- World 4 Enemy/Boss Keys (Placeholders) ---
    public static final String ENEMY_W4_TYPE1_IMG_KEY = "enemy_w4_type1";
    public static final String ENEMY_W4_TYPE2_IMG_KEY = "enemy_w4_type2";
    public static final String ENEMY_W4_TYPE3_IMG_KEY = "enemy_w4_type3";
    public static final String ENEMY_W4_TYPE4_IMG_KEY = "enemy_w4_type4";
    public static final String ENEMY_W4_TYPE5_IMG_KEY = "enemy_w4_type5";
    public static final String TEMPLE_GUARDIAN_IMG_KEY = "temple_guardian";

    // --- UI & Misc Keys ---
    public static final String PLACEHOLDER_ICON_KEY = "placeholder_icon";
    public static final String BG_SHOP_KEY = "bg_shop";

} 