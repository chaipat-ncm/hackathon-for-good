package org.c4i.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Check whether a given address is equivalent to localhost.
 * @author Arvid
 * @version 5-6-2015 - 21:07
 */
public class LocalHost {

    public static boolean isLocalHost(String ip) {
        try {
            return isLocalHost(InetAddress.getByName(ip));
        } catch (UnknownHostException e) {
            return false;
        }
    }

    public static boolean isLocalHost(InetAddress addr) {
        // Check if the address is a valid special local or loop back
        if (addr.isAnyLocalAddress() || addr.isLoopbackAddress())
            return true;

        // Check if the address is defined on any interface
        try {
            return NetworkInterface.getByInetAddress(addr) != null;
        } catch (SocketException e) {
            return false;
        }
    }
}
