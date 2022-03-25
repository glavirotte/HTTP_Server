package httpserver.itf.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;

import examples.HelloRicmlet;
import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmlet;
import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;
import httpserver.itf.HttpSession;

public class HttpRicmletRequestImpl extends HttpRicmletRequest{

	public HttpRicmletRequestImpl(HttpServer hs, String method, String ressname, BufferedReader br) throws IOException {
		super(hs, method, ressname, br);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HttpSession getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getArg(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCookie(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void process(HttpResponse resp) throws Exception {
		// TODO Auto-generated method stub
		
		if (m_method.equals("GET")) {
			try {
				String[] splitr = m_ressname.split("/");
				String clsname = splitr[2];
				for (int i = 3; i < splitr.length; i++) {
					clsname += "."+splitr[i];
				}
				Class<?> c = Class.forName(clsname);
				HttpRicmlet ressource = (HttpRicmlet) c.getDeclaredConstructor().newInstance();
				resp.setReplyOk();
				resp.setContentType(HttpRequest.getContentType(m_ressname));
				ressource.doGet(this, (HttpRicmletResponse)resp);
			} catch (ClassNotFoundException e) {
				resp.setReplyError(404, "Class not found");
				e.printStackTrace();
			}
		}
	}

}
