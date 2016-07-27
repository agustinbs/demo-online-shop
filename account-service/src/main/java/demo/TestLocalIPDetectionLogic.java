package demo;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * 
 * @author aburguet
 * 
 * Esta lógica es una copia de lo que hay en la clase 
 * 
 *    - org.springframework.cloud.commons.util.InetUtils
 * 
 * para probar fácilmente los valores a emplear para 'ignoredInterfaces' en los ficheros .yml
 * 
 * para un equipo PC dado
 *
 */
public class TestLocalIPDetectionLogic {
	static String[] IGNORED_INTERFACES ={".*Gigabit.*",".*VirtualBox.*",".*VMware.*"};

	public static void main(String[] args) {
		InetAddress ia=findFirstNonLoopbackAddress();
		System.out.println();	
		System.out.println("hostName:"+ ia.getHostName());		
		System.out.println("hostAddress:"+ ia.getHostAddress());


	}
	
	static boolean  ignoreInterface(String interfaceName) {
		for (String regex : IGNORED_INTERFACES) {
			if (interfaceName.matches(regex)) {
				System.out.print("--> ");				
				System.out.println("Ignoring interface: " + interfaceName);				
				return true;
			}
		}
		return false;
	}	
	public static InetAddress findFirstNonLoopbackAddress() {
		InetAddress result = null;
		try {
			int lowest = Integer.MAX_VALUE;
			for (Enumeration<NetworkInterface> nics = NetworkInterface
					.getNetworkInterfaces(); nics.hasMoreElements();) {
				NetworkInterface ifc = nics.nextElement();
				if (ifc.isUp()) {
					System.out.println("Testing interface: [" + ifc.getDisplayName() + "] with index=" + ifc.getIndex());
					if (ifc.getIndex() < lowest || result == null) {
						lowest = ifc.getIndex();
					}
					else if (result != null) {
						continue;
					}

					// @formatter:off
					if (!ignoreInterface(ifc.getDisplayName())) {
						for (Enumeration<InetAddress> addrs = ifc
								.getInetAddresses(); addrs.hasMoreElements();) {
							InetAddress address = addrs.nextElement();
							if (address instanceof Inet4Address
									&& !address.isLoopbackAddress()) {
								System.out.println("--");
								System.out.println("Found non-loopback interface: ["
										+ ifc.getDisplayName() + "] with address: " + address.getHostAddress());
								System.out.println("--");
								result = address;
							}
						}
					}
					// @formatter:on
				} else {
					System.out.println("   ** not UP ** " + ifc.getDisplayName() );
				}
			}
			} 
		catch (IOException ex) {
			System.out.println("Cannot get first non-loopback address:" + ex);
		}

		if (result != null) {
			return result;
		}

		try {
			return InetAddress.getLocalHost();
		}
		catch (UnknownHostException e) {
			System.out.println("Unable to retrieve localhost");
		}

		return null;
	}
}
