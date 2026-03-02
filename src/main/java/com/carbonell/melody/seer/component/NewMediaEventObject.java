/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.carbonell.melody.seer.component;

import com.carbonell.melody.seer.component.api.Media;
import java.util.EventObject;
import java.util.List;

/**
 * Custom <code>EventObject</code>
 * @author marta
 */
public class NewMediaEventObject extends EventObject {
    private final List<Media> media;    
    private final String discoveredTime;

    public NewMediaEventObject(Object source, List<Media> media, String discoveredTime) {
        super(source);
        
        this.media = media;
        this.discoveredTime = discoveredTime;
    }
   

    public List<Media> getMedia() {
        return media;
    }

    public String getDiscoveredTime() {
        return discoveredTime;
    }
}
