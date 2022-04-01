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
	
	private Hashtable<String, String> arguments = new Hashtable<String, String>();
	private Hashtable<String, String> cookies = new Hashtable<String, String>();
	private BufferedReader reader;

	public HttpRicmletRequestImpl(HttpServer hs, String method, String ressname, BufferedReader br) throws IOException {
		super(hs, method, ressname, br);
		this.reader = br;
		parseCookies();
	}

	@SuppressWarnings("deprecation")
	@Override
	public HttpSession getSession() {
		Session session;
		String sessionId = cookies.get("sessionId");
		if(sessionId == null) {
			session = new Session((new Integer(m_hs.sessionNumber++)).toString());
			this.m_hs.sessions.put(session.getId(), session);
		}else {
			session = this.m_hs.sessions.get(sessionId);
			if(session == null) {
				session = new Session(sessionId);
				this.m_hs.sessions.put(session.getId(), session);
				System.out.println("Session:" + session.getId());
			}
			session.resetTimer();
		}
		return session;
	}

	@Override
	public String getArg(String name) {
		return arguments.get(name);
	}

	@Override
	public String getCookie(String name) {
		return cookies.get(name);
	}

	@Override
	public void process(HttpResponse resp) throws Exception {
		
		if (m_method.equals("GET")) {
			try {
				HttpRicmlet ressource;
				String clsname = null;
				String appName = null;
				
				if(m_ressname.indexOf("?") < 0) {
					String str = m_ressname.substring(10);
					appName = (str.substring(0, str.indexOf("/")));
					clsname = (str.substring(appName.length()+1)).replace("/", ".");
				}else {
					String str = m_ressname.substring(10);
					appName = (str.substring(0, str.indexOf("/")));
					clsname = (str.substring(appName.length()+1, str.indexOf("?"))).replace("/", ".");
					String args[] = m_ressname.substring(m_ressname.indexOf("?")+1).split("&");
					for(int i  = 0; i < args.length; i++) {
						String[] arg = args[i].split("=");
						arguments.put(arg[0], arg[1]);
					}
					
				}
				Application app = new Application();
				ressource = app.getInstance(clsname, appName);
				resp.setReplyOk();
				resp.setContentType(HttpRequest.getContentType(m_ressname));
				ressource.doGet(this, (HttpRicmletResponse)resp);
			} catch (ClassNotFoundException e) {
				resp.setReplyError(404, " Class not found");
				e.printStackTrace();
			}
		}
	}
	
	public void parseCookies() throws IOException {
			String currentLine;
			while (!(currentLine = reader.readLine()).isEmpty()) {
				if (currentLine.startsWith("Cookie: ")) {
					String cookieLine = currentLine.substring(8);
					String cookie[] = cookieLine.split("=");
					cookies.put(cookie[0], cookie[1]);
					System.out.println("Cookie: " + cookie[0] + " = " + cookie[1]);
				}
			}
	}
	
}
