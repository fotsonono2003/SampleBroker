/*
 * Copyright (c) 2015 by Axway Software All brand or product names are
 * trademarks or registered trademarks of their respective holders. This
 * document and the software described in this document are the property of
 * Axway Software and are protected as Axway Software trade secrets. No part of
 * this work may be reproduced or disseminated in any form or by any means,
 * without the prior written permission of Axway Software.
 */
package IT.server;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tnt.axway.server.Server;
import com.tnt.axway.server.impl.EmbeddedBrokerService;

public class ServerTest {
	private static final Server server = new EmbeddedBrokerService();

	@BeforeClass
	public static void startServer() {
		server.start();
	}

	@AfterClass
	public static void stopServer() {
		server.stop();
	}

	@Test
	public void test() {
		System.out.println("we are in test");
	}

}
