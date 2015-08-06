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
import com.tnt.axway.server.JMSClient;

public class Consumer extends JMSClient<Author> implements Runnable,
		MessageListener, ExceptionListener {

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
		session = getConnection().createSession(false,
				Session.CLIENT_ACKNOWLEDGE);
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
		final Destination destination = session.createQueue(subject);
		if (correlationID == null) {
			final MessageConsumer receiver = getConsumer(destination,
					correlationID == null ? null : "JMSCorrelationID='"
							+ correlationID + "'");

			receiver.setMessageListener(this);
		}
		getConnection().setExceptionListener(this);
		getConnection().start();
		// final Message message = receiver.receive();
	}

	public void receiveMessage() throws JMSException {
		receiveMessage(null);
	}

	@Override
	public String sendMessage(final Author object) throws JMSException {
		final ObjectMessage message = session.createObjectMessage();
		message.setObject("OK");
		final Destination replyQueue = session.createQueue(OUT_QUEUE);
		final MessageProducer replyProducer = session
				.createProducer(replyQueue);
		replyProducer.send(message);
		return null;
	}

	public void onMessage(final Message message) {
		if (message instanceof ObjectMessage) {
			final ObjectMessage object = (ObjectMessage) message;
			try {
				System.out.println("Message received:" + object.getObject()
						+ " with correlationID: "
						+ message.getJMSCorrelationID());
				LOG.info("Message received:" + message.toString());
				final Message response = session.createObjectMessage("OK");
				response.setJMSCorrelationID(message.getJMSCorrelationID());
				reply(response);
				message.acknowledge();
				LOG.info("Response sent");
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
	}

	public void run() {
		// try {
		// receiveMessage();
		// while (true) {
		// }
		// } catch (final JMSException e) {
		// LOG.error(e.getMessage(), e);
		// }
	}

	public void onException(final JMSException exception) {
		LOG.error(exception.getMessage(), exception);
	}

	public static void main(String[] args) throws JMSException {
		final Consumer consumer = new Consumer();
		consumer.setUp();
		consumer.receiveMessage();
		System.out.println("Consumer is working");
	}

}
