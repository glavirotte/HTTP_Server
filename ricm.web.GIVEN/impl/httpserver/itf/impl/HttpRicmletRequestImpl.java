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
		parseCookies();		// On extrait les cookies de la requête
	}

	@SuppressWarnings("deprecation")
	@Override
	public HttpSession getSession() {
		Session session;								
		String sessionId = cookies.get("sessionId");	// On récupère l'id de la session associée à l'utilisateur via les cookies
		if(sessionId == null) {
			session = new Session((new Integer(m_hs.sessionNumber++)).toString()); // Si elle n'existe pas encore, on créer une nouvelle sessions
			this.m_hs.sessions.put(session.getId(), session);			// On l'ajoute à la hashmap du serveur pour la conserver
		}else {
			session = this.m_hs.sessions.get(sessionId);	// On récupère l'objet session correspondant à la session de l'utilisateur
			if(session == null) {						
				session = new Session(sessionId);		// Si la session n'existe plus car son timer est dépassé, on la recréer
				this.m_hs.sessions.put(session.getId(), session); // On l'ajoute à la hashmap
				System.out.println("Session:" + session.getId());
			}
			session.resetTimer();	// On remet le timer de session à 0
		}
		return session;
	}

	@Override
	public String getArg(String name) {		// Retourne l'argument "name" présent dans la requête
		return arguments.get(name);
	}

	@Override
	public String getCookie(String name) {	// Retourne le cookie "name" présent dans la requête
		return cookies.get(name);
	}

	@Override
	public void process(HttpResponse resp) throws Exception { // Traite la requête 
		if (m_method.equals("GET")) {
			try {
				HttpRicmlet ressource;
				String clsname = null;
				String appName = null;
				
				if(m_ressname.indexOf("?") < 0) {			// Si il n'y a pas d'arguments dans la requête, on récupère le nom de la classe et de l'application
					String str = m_ressname.substring(10);
					appName = (str.substring(0, str.indexOf("/")));
					clsname = (str.substring(appName.length()+1)).replace("/", ".");
				}else {			// Si il y a des arguments on récupère le nom de la classe, de l'application et les arguments
					String str = m_ressname.substring(10);
					appName = (str.substring(0, str.indexOf("/")));
					clsname = (str.substring(appName.length()+1, str.indexOf("?"))).replace("/", ".");
					String args[] = m_ressname.substring(m_ressname.indexOf("?")+1).split("&");
					for(int i  = 0; i < args.length; i++) {
						String[] arg = args[i].split("=");
						arguments.put(arg[0], arg[1]);
					}
					
				}
				Application app = new Application();				// Instanciation de l'application
				ressource = app.getInstance(clsname, appName);		// Récupère l'application via le classLoader
				resp.setReplyOk();		// Réponse valide
				resp.setContentType(HttpRequest.getContentType(m_ressname));	// Type de contenu de la réponse
				ressource.doGet(this, (HttpRicmletResponse)resp);				// On appelle la méthode do GET
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
					String[] cookieName = cookieLine.split(";");
					for (String c : cookieName) {
						String cookie[] = c.split("=");
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
