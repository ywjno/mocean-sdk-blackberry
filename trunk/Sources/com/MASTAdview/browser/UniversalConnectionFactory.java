/**
 * Copyright &copy; 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All
 * Rights Reserved.
 */
package com.MASTAdview.browser;

import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.io.transport.TransportInfo;

/**
 * This class enables you to create HTTP, HTTPS, socket, TLS, and SSL connections over supported transports. This class is not thread-safe
 * and the getConnection() method blocks. So you should not call these methods from the main event thread of an application.
 */
public class UniversalConnectionFactory extends ConnectionFactory {
	public UniversalConnectionFactory() {
		//Sets the preferred transport types and priority/order used 
		//  by the factory when attempting to get a connection:
		setPreferredTransportTypes(new int[] { TransportInfo.TRANSPORT_TCP_WIFI, TransportInfo.TRANSPORT_MDS,
				TransportInfo.TRANSPORT_TCP_CELLULAR, TransportInfo.TRANSPORT_WAP2 });

		//Sets the disallowed transport types used by the factory 
		//   attempting to get a connection:
		setDisallowedTransportTypes(new int[] { TransportInfo.TRANSPORT_WAP, TransportInfo.TRANSPORT_BIS_B });

		//Sets the maximum number of attempts the factory will make 
		//   to create a connection.  The default value is 1. 
		//   Valid values range from 1 to 500:
		setAttemptsLimit(10);

		//Sets connectionTimeout to the desired value (ms):
		setConnectionTimeout(10000);

		//Sets the maximum time (ms) the factory will try to 
		//   create a connection:
		setTimeLimit(10000);
	}
}