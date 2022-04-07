package httpserver.itf.impl;

import java.io.PrintStream;
import httpserver.itf.HttpRequest;
import httpserver.itf.HttpRicmletResponse;

public class HttpRicmletResponseImpl extends HttpResponseImpl implements HttpRicmletResponse {
	
	protected HttpRicmletResponseImpl(HttpServer hs, HttpRequest req, PrintStream ps) {
		super(hs, req, ps);
	}

	public void setCookie(String name, String value) {
		m_ps.println("Set-Cookie: " + name + "=" + value);			// Ajoute le cookie et sa valeur dans la requête HTTP
		System.out.println("Set-Cookie: " + name + "=" + value);
	}

}
