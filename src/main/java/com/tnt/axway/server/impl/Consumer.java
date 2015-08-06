/*
 * Copyright (c) 2015 by Axway Software All brand or product names are
 * trademarks or registered trademarks of their respective holders. This
 * document and the software described in this document are the property of
 * Axway Software and are protected as Axway Software trade secrets. No part of
 * this work may be reproduced or disseminated in any form or by any means,
 * without the prior written permission of Axway Software.
 */
package com.tnt.axway.server.impl;

import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.log4j.Logger;

import com.tnt.axway.Author;
import com.tnt.axway.server.BaseConsumer;

public class Consumer extends BaseConsumer<Author> implements MessageListener,
		ExceptionListener {

	private final Logger LOG = Logger.getLogger(Consumer.class);
	private String subject = "authors";
	private String OUT_QUEUE = "response";
	private Session session = null;

	public Consumer(String url) {
		super(url);
	}

	public Consumer() {
	}

	public void setUp() throws JMSException {
		session = getConnection()
				.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}

	@Override
	public void receiveMessage(final String correlationID) throws JMSException {
		final Destination destination = session.createQueue(subject);
		final MessageConsumer receiver = session.createConsumer(destination);

		receiver.setMessageListener(this);
		getConnection().setExceptionListener(this);
		getConnection().start();
		LOG.info("Consumer is waiting for message");
	}

	@Override
	public String sendMessage(final Author object) throws JMSException {
		return null;
	}

	public void onMessage(final Message message) {
		if (message instanceof ObjectMessage) {
			final ObjectMessage object = (ObjectMessage) message;
			try {
				LOG.info("Message received:" + message.toString());
				LOG.info("Content received: " + object.getObject().toString());
				final Message response = session.createObjectMessage("OK");
				response.setJMSCorrelationID(message.getJMSCorrelationID());
				reply(response);
			} catch (final JMSException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	private void reply(final Message response) throws JMSException {
		final Destination replyQueue = session.createQueue(OUT_QUEUE);
		final MessageProducer replyProducer = session
				.createProducer(replyQueue);
		replyProducer.send(response);
		LOG.info("Response sent:" + response.getJMSCorrelationID());
	}

	public void onException(final JMSException exception) {
		LOG.error(exception.getMessage(), exception);
	}

	public static void main(String[] args) throws JMSException {
		final Consumer consumer = new Consumer();
		consumer.setUp();
		consumer.receiveMessage(null);
	}

}
