# Equinox Game - Asset Requirements

This document outlines the graphical and audio assets needed for the Equinox game, intended for use with standard Java libraries (Swing/AWT, `javax.imageio`, `javax.sound.sampled`).

## Sprite Assets (Format: PNG)

*Note: Standard Java `javax.imageio` provides good support for PNG format. Use the naming convention: `category_specificName_variantOrState.extension`.*

### Player & Related Effects
- **`player_captainnova_portrait.png`** - Portrait for narrative/UI.
- **`player_ship_main_sheet.png`** - Player's spacecraft sprite sheet (idle, turning).
- **`fx_player_shield.png`** - Shield effect overlay (can be animated sheet).
- **`fx_player_engine_sheet.png`** - Engine thruster effect sprite sheet.
- **`fx_player_explosion_sheet.png`** - Ship destruction sequence sprite sheet.

### Weapons & General Effects
- **`weapon_laser_blue.png`** - Basic player weapon projectile.
- **`weapon_missile_sheet.png`** - Secondary player weapon projectile sprite sheet.
- **`weapon_laser_red.png`** - Standard enemy projectile.
- **`fx_explosion_small_sheet.png`** - Small explosion effect sprite sheet.
- **`fx_explosion_medium_sheet.png`** - Medium explosion effect sprite sheet.
- **`fx_explosion_large_sheet.png`** - Large explosion effect sprite sheet.
- **`fx_shield_hit_sheet.png`** - Shield impact effect sprite sheet.

### Enemy Ships
- **`enemy_scout.png`** - Fast, low-health enemy.
- **`enemy_cruiser.png`** - Slow, heavily armored enemy.
- **`enemy_drone.png`** - Small swarm enemy.
- **`enemy_drone_animated.gif`** - Optional animated version for drone.
- **`enemy_turret.png`** - Stationary defense turret.
- **`enemy_alien_type1.png`** - Specific alien enemy variant 1.
- **`enemy_alien_type2.png`** - Specific alien enemy variant 2.
- **`enemy_alien_type3.png`** - Specific alien enemy variant 3.

### Bosses
- **`boss_leviathan.png`** - Void Leviathan (Location 1).
- **`boss_guardian.png`** - Guardian Construct (Location 2).
- **`boss_paradox.png`** - Paradox Entity (Location 3).
- **`boss_temple.png`** - Temple Guardian (Final Boss).
- **`boss_mini_alien.png`** - Example mini-boss.
- **`boss_main_alien.png`** - Example main boss.

### Power-ups
- **`powerup_hull_icon.png`** - Hull repair pickup icon.
- **`powerup_shield_icon.png`** - Shield boost pickup icon.
- **`powerup_weapon_icon.png`** - Temporary weapon boost icon.
- **`powerup_speed_icon.png`** - Temporary speed boost icon.

### UI Elements
- **`ui_button_normal.png`** - Standard button state.
- **`ui_button_hover.png`** - Button hover state.
- **`ui_button_pressed.png`** - Button pressed state.
- **`ui_healthbar_frame.png`** - Hull integrity bar frame/background.
- **`ui_healthbar_fill.png`** - Hull integrity bar fill element.
- **`ui_shieldbar_frame.png`** - Shield strength bar frame/background.
- **`ui_shieldbar_fill.png`** - Shield strength bar fill element.
- **`ui_upgrade_card.png`** - Upgrade selection background/panel.

### Backgrounds & Environment
- **`bg_nebula.png`** - Location 1 background scene.
- **`bg_ruins.png`** - Location 2 background scene.
- **`bg_singularity.png`** - Location 3 background scene.
- **`bg_temple.png`** - Location 4 background scene.
- **`bg_shop.png`** - Background for shop/upgrade screen.
- **`bg_parallax_stars.png`** - Parallax star layer (transparent bg).
- **`bg_parallax_asteroids.png`** - Parallax asteroid layer (transparent bg).

### Cutscene Graphics
- **`cg_stage1_scene1.png`** - Cutscene image for stage 1, scene 1.
- **`cg_stage2_scene1.png`** - Cutscene image for stage 2, scene 1.
- *... (add more as needed)*

## Audio Assets

*Note: Standard Java `javax.sound.sampled` has built-in support primarily for WAV, AIFF, and AU formats. MP3 support might require external libraries or specific Java Runtime Environment configurations (like JavaFX media). It is recommended to use **WAV** format for sound effects for maximum compatibility.* Music might need conversion to WAV or require handling potential MP3 limitations.

### Music (Consider WAV for compatibility, use `category_name.extension`)
- **`music_menu_theme.wav`** - Main menu music
- **`music_location1_theme.wav`** - Cosmic Nebula music
- **`music_location2_theme.wav`** - Ancient Ruins music
- **`music_location3_theme.wav`** - Quantum Singularity music
- **`music_location4_theme.wav`** - Sacred Temple music
- **`music_boss_theme.wav`** - Boss battle music
- **`music_victory_theme.wav`** - Level completion music

### Sound Effects (Format: WAV, use `sfx_category_action.extension`)
- **`sfx_weapon_laser_fire.wav`** - Player laser firing
- **`sfx_weapon_missile_fire.wav`** - Player missile firing
- **`sfx_explosion_small.wav`** - Small explosion sound
- **`sfx_explosion_medium.wav`** - Medium explosion sound
- **`sfx_explosion_large.wav`** - Large explosion sound
- **`sfx_impact_shield.wav`** - Shield impact sound
- **`sfx_impact_hull.wav`** - Hull damage sound
- **`sfx_item_powerup_collect.wav`** - Item collection sound
- **`sfx_ui_button_click.wav`** - UI button press
- **`sfx_player_engine_thrust.wav`** - Ship engine loop
- **`sfx_ui_warning_lowhealth.wav`** - Low health warning
- **`sfx_event_level_complete.wav`** - Level completion jingle
- **`sfx_event_game_over.wav`** - Player death sound

## Asset Placeholder Strategy

For initial development using pure Java graphics, use simple placeholders:

1. **Geometric Shapes (drawn with `Graphics2D`)**:
   - Player Ship: Blue triangle (`fillPolygon`, `drawPolygon`)
   - Enemies: Red shapes (circle for scouts `fillOval`, square for cruisers `fillRect`)
   - Projectiles: Small colored circles (`fillOval`)
   - Powerups: Colored diamonds (`fillPolygon`)

2. **Text & Basic UI (using Swing components or `Graphics2D`)**:
   - Use standard `JButton` or draw text labels (`g.drawString`).
   - Simple colored rectangles (`fillRect`) for health/shield bars.

3. **Background**:
   - Simple starfield gradient (using `GradientPaint`) or randomly drawn stars (`fillRect` or `fillOval`) for all locations initially.
   - Add distinct visual elements as development progresses.

This keeps initial development focused on functionality before dealing with image loading and complex sprite handling. 