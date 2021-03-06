/*
 * Copyright (c) 2015 by Axway Software All brand or product names are
 * trademarks or registered trademarks of their respective holders. This
 * document and the software described in this document are the property of
 * Axway Software and are protected as Axway Software trade secrets. No part of
 * this work may be reproduced or disseminated in any form or by any means,
 * without the prior written permission of Axway Software.
 */
package tools.Client.impl;

import java.util.UUID;

import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.log4j.Logger;

public class ProducerWithouListener extends JMSClient implements
		ExceptionListener, Runnable {

	private final static Logger LOG = Logger
			.getLogger(ProducerWithouListener.class);
	private final String IN_QUEUE = "authors";
	private final String OUT_QUEUE = "response";
	private Session session = null;

	public ProducerWithouListener(final String url) {
		super(url);
	}

	public ProducerWithouListener() {
	}

	public void setUp() throws JMSException {
		getConnection().start();
		session = getConnection()
				.createSession(true, Session.AUTO_ACKNOWLEDGE);
	}

	private MessageConsumer getConsumer(final Destination destination,
			final String messageSelector) throws JMSException {
		if (messageSelector == null) {
			return session.createConsumer(destination);
		} else {
			return session.createConsumer(destination, messageSelector);
		}
	}

	@Override
	public void receiveMessage(final String correlationID) throws JMSException {
		final Destination destination = session.createQueue(OUT_QUEUE);
		final MessageConsumer receiver = getConsumer(destination,
				correlationID == null ? null : "JMSCorrelationID='"
						+ correlationID + "'");
		final Message message = receiver.receive(3000);
		if (message instanceof ObjectMessage) {
			final ObjectMessage object = (ObjectMessage) message;
			try {
				LOG.info("Response received:" + object.getObject()
						+ ", JMSCorrelationID:" + message.getJMSCorrelationID());
			} catch (final JMSException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		session.commit();
	}

	@Override
	public String sendMessage(final String object) throws JMSException {
		getConnection().setExceptionListener(this);
		final Destination destination = session.createQueue(IN_QUEUE);
		final Message message = session.createObjectMessage(object);
		message.setJMSCorrelationID(UUID.randomUUID().toString());

		final MessageProducer producer = session.createProducer(destination);
		producer.send(message);
		session.commit();
		LOG.info("Message sent: " + object + ", correlationID:"
				+ message.getJMSCorrelationID());
		return message.getJMSCorrelationID();
	}

	public void onException(final JMSException exception) {
		LOG.error(exception.getMessage(), exception);
	}

	@Override
	public void shutDown() throws JMSException {
		if (session != null) {
			session.close();
		}
		if (getConnection() != null) {
			getConnection().close();
		}
	}

	public void run() {
		try {
			String correlationID = sendMessage("author_Thread1");
			receiveMessage(correlationID);
		} catch (final JMSException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public static void main(String[] args) throws JMSException {
		final ProducerWithouListener producer = new ProducerWithouListener();
		producer.setUp();
		final String objectToSend="author_Of message";
		String correlationID = producer.sendMessage(objectToSend);
		producer.receiveMessage(correlationID);
		producer.shutDown();
	}

}
