/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cape.audio.core;

import java.io.IOException;
import javax.media.NoPlayerException;
import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author ludetc
 */
public class CapeAudioCorePlayerTest extends TestCase {
    
    CapeAudioCorePlayer player = new CapeAudioCorePlayer();
    
    public void testPlay() throws IOException, NoPlayerException {
        
        System.out.println("testing");
        
        player.play("roberto.mp3");
    }
    
}
