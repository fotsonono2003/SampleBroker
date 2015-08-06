/*
 * Copyright (c) 2015 by Axway Software All brand or product names are
 * trademarks or registered trademarks of their respective holders. This
 * document and the software described in this document are the property of
 * Axway Software and are protected as Axway Software trade secrets. No part of
 * this work may be reproduced or disseminated in any form or by any means,
 * without the prior written permission of Axway Software.
 */
package tools.server.Client.impl;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;

import tools.server.Client.Client;

public abstract class JMSClient<T extends Serializable> implements Client {

	private Connection connection;
	protected String url = "tcp://127.0.0.1:61616";
	protected final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
			url);

	public JMSClient(final String url) {
		this.url = url;
	}

	public JMSClient() {
	}

	abstract public String sendMessage(final T object) throws JMSException;

	abstract public void receiveMessage(final String correlationID) throws JMSException;

	public String getURL() {
		return url;
	}

	protected Connection getConnection() throws JMSException {
		if (connection == null) {
			connection = connectionFactory.createQueueConnection();
		}
		return connection;
	}

	public void shutDown() throws JMSException {
		if (connection != null) {
			connection.close();
		}
	}
}
