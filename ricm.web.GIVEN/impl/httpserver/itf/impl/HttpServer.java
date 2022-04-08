package httpserver.itf.impl;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.StringTokenizer;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmlet;


/**
 * Basic HTTP Server Implementation 
 * 
 * Only manages static requests
 * The url for a static ressource is of the form: "http//host:port/<path>/<ressource name>"
 * For example, try accessing the following urls from your brower:
 *    http://localhost:<port>/
 *    http://localhost:<port>/voile.jpg
 *    ...
 */
public class HttpServer {

	private int m_port;
	private File m_folder;  // default folder for accessing static resources (files)
	private ServerSocket m_ssoc;
	private Hashtable<String, HttpRicmlet> instances; // HashMap qui contient les différentes instances exécutées
	public Hashtable<String, Session> sessions; // HashMap contenant les différentes sessions courantes
	public int sessionNumber = 1;

	protected HttpServer(int port, String folderName) {
		m_port = port;
		instances = new Hashtable<String, HttpRicmlet>();
		sessions = new Hashtable<String, Session>();
		
		if (!folderName.endsWith(File.separator)) 
			folderName = folderName + File.separator;
		m_folder = new File(folderName);
		try {
			m_ssoc=new ServerSocket(m_port);
			System.out.println("HttpServer started on port " + m_port);
		} catch (IOException e) {
			System.out.println("HttpServer Exception:" + e );
			System.exit(1);
		}
	}
	
	public File getFolder() {
		return m_folder;
	}
	
	// A chaque interaction avec une session, on vérifie que les autres existent encore, ou si elles ont dépassé leur temps de vie et doivent être supprimées
	// Cette méthode doit être déclaré comme synchronized car la hashmap est partagée entre les threads et il peut y avoir des accès concurents
	public synchronized void manageSession() {
		for (String key : sessions.keySet()) { 
			
			long currentTime = System.currentTimeMillis();
			Session currentSession = sessions.get(key);
			
			if(currentSession.getDeathTime() <= currentTime) { // Pour chaque session, on vérifie que la date de suppression soit déjà passée
				sessions.remove(key); // Si c'est le cas, la session n'existe plus, on la supprime de la liste des sessions actives
			}
		}
	}

	// On récupère le Ricmlet correspondant à la classe fournie
	public HttpRicmlet getInstance(String clsname)
			throws Exception {
		if(!instances.containsKey(clsname)) { // Si la classe demandée existe, on récupère l'objet Class correspondant
			Class<?> c = Class.forName(clsname);
			HttpRicmlet instance = (HttpRicmlet) c.getDeclaredConstructor().newInstance(); // On crée une nouvelle instance de cette classe
			instances.put(clsname, instance); // On ajoute cette instance dans la liste des instances actives
		}
		
		return instances.get(clsname); // On retourne l'instance trouvée précédemment (si on n'a pas trouvé, cela retournera null qui sera traité dans la couche supérieure
		
	}




	/*
	 * Reads a request on the given input stream and returns the corresponding HttpRequest object
	 */
	public HttpRequest getRequest(BufferedReader br) throws IOException {
		HttpRequest request = null;
		
		String startline = br.readLine();
		StringTokenizer parseline = new StringTokenizer(startline);
		String method = parseline.nextToken().toUpperCase(); 
		String ressname = parseline.nextToken();
		if (method.equals("GET")) {
			if (ressname.startsWith("/ricmlets")) { // Si la ressource commence par ricmlet, on appelle la requête de ricmlet
				request = new HttpRicmletRequestImpl(this, method, ressname, br);
			}
			else { 
				request = new HttpStaticRequest(this, method, ressname);
			}
		} else 
			request = new UnknownRequest(this, method, ressname);
		return request;
	}


	/*
	 * Returns an HttpResponse object associated to the given HttpRequest object
	 */
	public HttpResponse getResponse(HttpRequest req, PrintStream ps) {
		if (req.getRessname().startsWith("/ricmlets")) { // Si la ressource commence par ricmlet, on appelle la requête de ricmlet
			return new HttpRicmletResponseImpl(this, req, ps);
		}
		else {
			return new HttpResponseImpl(this, req, ps);
		}
	}


	/*
	 * Server main loop
	 */
	protected void loop() {
		try {
			while (true) {
				Socket soc = m_ssoc.accept();
				(new HttpWorker(this, soc)).start();
			}
		} catch (IOException e) {
			System.out.println("HttpServer Exception, skipping request");
			e.printStackTrace();
		}
	}

	
	
	public static void main(String[] args) {
		int port = 0;
		if (args.length != 2) {
			System.out.println("Usage: java Server <port-number> <file folder>");
		} else {
			port = Integer.parseInt(args[0]);
			String foldername = args[1];
			HttpServer hs = new HttpServer(port, foldername);
			hs.loop();
		}
	}

}

