package com.equinox.game.data;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import com.equinox.game.utils.AssetLoader;

// Placeholder - Structure needs definition based on actual usage
public class CutsceneData {

    private List<CutsceneFrame> frames;
    private int currentFrameIndex;
    private boolean isActive;

    public CutsceneData() {
        this.frames = new ArrayList<>();
        this.currentFrameIndex = 0;
        this.isActive = false;
        // TODO: Populate frames, perhaps load from a file or define here
        // Example frame structure (needs CutsceneFrame class definition):
        // frames.add(new CutsceneFrame("Captain's Log, Stardate 47634.4...", assetLoader.getImage("some_bg"), assetLoader.getImage("captain_portrait")));
        // frames.add(new CutsceneFrame("We've arrived at the designated coordinates.", assetLoader.getImage("bridge_bg"), null));
    }

    // Example: Load specific cutscene data based on ID
    public CutsceneData(int cutsceneId, AssetLoader assetLoader) {
         this(); // Call default constructor
         // TODO: Load specific frames based on cutsceneId
         // e.g., loadCutsceneFromFile(cutsceneId, assetLoader);
    }

    public void start() {
        this.currentFrameIndex = 0;
        if (!frames.isEmpty()) {
             this.isActive = true;
        }
    }

    public CutsceneFrame getCurrentFrame() {
        if (isActive && currentFrameIndex < frames.size()) {
            return frames.get(currentFrameIndex);
        }
        return null;
    }

    public void nextFrame() {
        if (isActive && currentFrameIndex < frames.size() - 1) {
            currentFrameIndex++;
        } else {
            end();
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void end() {
        isActive = false;
        currentFrameIndex = 0; // Reset for next time
    }

    public void addFrame(CutsceneFrame frame) {
        if (frame != null) {
            this.frames.add(frame);
        }
    }

    // Inner class or separate file for frame data
    public static class CutsceneFrame {
        public String text;
        public Image background;
        public Image characterPortrait;

        public CutsceneFrame(String text, Image background, Image characterPortrait) {
            this.text = text;
            this.background = background;
            this.characterPortrait = characterPortrait;
        }
    }

    // TODO: Add method to load cutscene data (e.g., from JSON/XML)
    // private void loadCutsceneFromFile(int cutsceneId, AssetLoader assetLoader) { ... }
} 