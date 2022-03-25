package httpserver.itf.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmlet;
import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;
import httpserver.itf.HttpSession;

public class HttpRicmletRequestImpl extends HttpRicmletRequest{
	
	Hashtable<String, String> arguments = new Hashtable<String, String>();

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
		return arguments.get(name);
	}

	@Override
	public String getCookie(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void process(HttpResponse resp) throws Exception {
		
		if (m_method.equals("GET")) {
			try {
				String clsname = null;
				
				if(m_ressname.indexOf("?") < 0) {
					clsname = (m_ressname.substring(10)).replace("/", ".");
				}else {
					clsname = (m_ressname.substring(10, m_ressname.indexOf("?"))).replace("/", ".");
					String args[] = m_ressname.substring(m_ressname.indexOf("?")+1).split("&");
					for(int i  = 0; i < args.length; i++) {
						String[] arg = args[i].split("=");
						arguments.put(arg[0], arg[1]);
					}
					
				}

				HttpRicmlet ressource = m_hs.getInstance(clsname);
				resp.setReplyOk();
				resp.setContentType(HttpRequest.getContentType(m_ressname));
				ressource.doGet(this, (HttpRicmletResponse)resp);
			} catch (ClassNotFoundException e) {
				resp.setReplyError(404, "HTTP/1.0 404 Class not found");
				e.printStackTrace();
			}
		}
		
	}

}
