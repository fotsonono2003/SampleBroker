/*
 * Copyright (c) 2015 by Axway Software All brand or product names are
 * trademarks or registered trademarks of their respective holders. This
 * document and the software described in this document are the property of
 * Axway Software and are protected as Axway Software trade secrets. No part of
 * this work may be reproduced or disseminated in any form or by any means,
 * without the prior written permission of Axway Software.
 */
package tools.server.impl;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.log4j.Logger;

import tools.server.Server;

public class EmbeddedBrokerService implements Server {
	private final String url = "tcp://127.0.0.1:61616";
	private final BrokerService broker = new BrokerService();
	private final File dataDirectory = new File("target/activeMQ_Directory");

	private final Logger LOG = Logger.getLogger(EmbeddedBrokerService.class);

	public void start() {
		try {
			final TransportConnector connector = getConnector();
			broker.addConnector(connector);
			broker.setDataDirectoryFile(dataDirectory);
			broker.setBrokerName("Track_Trace");
			broker.start();
			LOG.info("Server start");
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	private TransportConnector getConnector() throws URISyntaxException {
		final TransportConnector connector = new TransportConnector();
		connector.setUri(new URI(url));
		connector.setName("Track_Trace_connector");
		return connector;
	}

	public void stop() {
		if (broker != null) {
			try {
				broker.stop();
			} catch (final Exception e) {
				LOG.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
	}

	public static void main(String[] args) {
		Server server = new EmbeddedBrokerService();
		server.start();
	}
}
