/*
 * Copyright (c) 2015 by Axway Software All brand or product names are
 * trademarks or registered trademarks of their respective holders. This
 * document and the software described in this document are the property of
 * Axway Software and are protected as Axway Software trade secrets. No part of
 * this work may be reproduced or disseminated in any form or by any means,
 * without the prior written permission of Axway Software.
 */
package com.tnt.axway;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Author implements Serializable {

	private static final long serialVersionUID = -7472892077127566290L;

	private final String name;
	private final List<String> books = new ArrayList<String>();

	public Author(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<String> getBooks() {
		return books;
	}
	@Override
	public String toString() {
		return "Author name:"+name+", Books:"+books;
	}
}
