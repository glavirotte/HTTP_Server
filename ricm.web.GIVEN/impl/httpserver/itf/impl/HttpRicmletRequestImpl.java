package httpserver.itf.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmletRequest;
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
				String clsname = m_ressname;
				Class<?> c = Class.forName(clsname);
				c.getDeclaredConstructor().newInstance();
				resp.setReplyOk();
			} catch (ClassNotFoundException e) {
				resp.setReplyError(404, "Class not found");
			}
			
//			if (ressource.exists()) {
//				FileInputStream fis = new FileInputStream(ressource);
//				resp.setReplyOk();
//				resp.setContentLength((int) ressource.length());
//				resp.setContentType(HttpRequest.getContentType(m_ressname));
//				PrintStream ps = resp.beginBody();
//				ps.write(fis.readAllBytes());
//				fis.close();
//			}
//			else {
//				resp.setReplyError(404, "File not found");
//			}
		}
	}

}
