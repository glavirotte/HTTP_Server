package httpserver.itf.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmlet;
import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;
import httpserver.itf.HttpSession;

public class HttpRicmletRequestImpl extends HttpRicmletRequest{
	
	protected HashMap<String, String> args = new HashMap<String, String>();
	protected HashMap<String, String> cookies = new HashMap<String, String>();
	protected HttpSession session;

	public HttpRicmletRequestImpl(HttpServer hs, String method, String ressname, BufferedReader br) throws IOException {
		super(hs, method, ressname, br);
		// TODO Auto-generated constructor stub
		if (ressname.contains("?")) {
			String[] url = ressname.split("\\?");
			m_ressname = url[0];
			String[] arguments = url[1].split("&");
			for (String a : arguments) {
				String[] vars = a.split("=");
				args.put(vars[0], vars[1]);
			}
		}
		parseCookies(br);
	}

	@Override
	public HttpSession getSession() {
		// TODO Auto-generated method stub
		if (session==null) {
			session = new Session(m_hs.getSessionNumber());
			m_hs.setSession(session.getId(), session);
		}
		((Session) session).resetTimer();
		return session;
	}

	@Override
	public String getArg(String name) {
		// TODO Auto-generated method stub
		return args.get(name);
	}

	@Override
	public String getCookie(String name) {
		// TODO Auto-generated method stub
		return cookies.get(name);
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
				HttpRicmlet ressource = m_hs.getInstance(clsname);
				resp.setReplyOk();
				resp.setContentType(HttpRequest.getContentType(m_ressname));
				ressource.doGet(this, (HttpRicmletResponse)resp);
			} catch (ClassNotFoundException e) {
				resp.setReplyError(404, "Class not found");
				e.printStackTrace();
			}
		}
	}
	
	public void parseCookies(BufferedReader br) throws IOException {
		String currentLine;
		while (!(currentLine = br.readLine()).isEmpty()) {
			if (currentLine.startsWith("Cookie: ")) {
				String cookieLine = currentLine.substring(8);
				if (cookieLine.contains(";")) {
					String[] cookieName = cookieLine.split(";");
					for (String c : cookieName) {
						String cookie[] = c.split("=");
						if (cookie[0].equals("session-id")) {
							if (m_hs.getSession(cookie[1]) == null) {
								session = new Session(cookie[1]);
								m_hs.setSession(cookie[1], session);
							}
							else {
								session = m_hs.getSession(cookie[1]);
							}
						}
						else {
							if(cookie[0].startsWith(" ")) {
								cookie[0] = cookie[0].substring(1);
							}
							cookies.put(cookie[0], cookie[1]);
							System.out.println("Cookie: " + cookie[0] + " = " + cookie[1]);
						}
					}
				}
				else {
					String cookie[] = cookieLine.split("=");
					if (cookie[0].equals("session-id")) {
						if (m_hs.getSession(cookie[1]) == null) {
							session = new Session(cookie[1]);
							m_hs.setSession(cookie[1], session);
						}
						else {
							session = m_hs.getSession(cookie[1]);
						}
					}
					else {
						if(cookie[0].startsWith(" ")) {
							cookie[0] = cookie[0].substring(1);
						}
						cookies.put(cookie[0], cookie[1]);
						System.out.println("Cookie: " + cookie[0] + " = " + cookie[1]);
					}
				}
			}
		}
	}

}
