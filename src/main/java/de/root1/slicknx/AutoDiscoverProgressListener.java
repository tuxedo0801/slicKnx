/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.root1.slicknx;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.List;

/**
 *
 * @author achristian
 */
public interface AutoDiscoverProgressListener {
    
    public void found(KnxInterfaceDevice device);
    public void noResult();

    public void done(List<KnxInterfaceDevice> foundDevices);
    
}
