package com.leancoder.photogallery.models.domains.paginator;

public class PageItemBean {

	private int numero;
	private boolean actual;

	public PageItemBean(int numero, boolean actual) {
		this.numero = numero;
		this.actual = actual;
	}

	public int getNumero() {
		return numero;
	}

	public boolean isActual() {
		return actual;
	}

}
