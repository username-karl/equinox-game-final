package com.equinox.game.systems;

import java.awt.Image;
import java.util.List;

// Made class public and moved to its own file
public class CutsceneData {
    private Image characterPortrait;
    private List<String> narrations;
    private Image background;

    public CutsceneData(Image characterPortrait, List<String> narrations, Image background) {
        this.characterPortrait = characterPortrait;
        this.narrations = narrations;
        this.background = background;
    }

    public Image getCharacterPortrait() {
        return characterPortrait;
    }

    public List<String> getNarrations() {
        return narrations;
    }

    public Image getBackground() {
        return background;
    }

    // Note: addNarration was removed as List.of makes the list immutable typically.
    // If mutability is needed, use new ArrayList<>(...) in constructor and keep this method.
    // public void addNarration(String narration) {
    //     narrations.add(narration); 
    // }
} 