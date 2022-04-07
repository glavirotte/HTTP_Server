package httpserver.itf.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;

/*
 * This class allows to build an object representing an HTTP static request
 */
public class HttpStaticRequest extends HttpRequest {
	static final String DEFAULT_FILE = "index.html";
	
	public HttpStaticRequest(HttpServer hs, String method, String ressname) throws IOException {
		super(hs, method, ressname);
	}
	
	// Fonction permettant la modification d'une réponse HTTP en fonction de l'existence ou non d'un fichier
	public void process(HttpResponse resp) throws Exception {
		
		if(m_method.compareTo("GET") == 0) {  // on verifie que le protocole HTTP est bien GET (comme demandé pour les requêtes statiques) 
			File ressource = new File(m_hs.getFolder()+m_ressname); // On récupère le fichier avec le dossier du serveur et le nom du fichier fourni dans la requête
			
			if(!ressource.exists()) { // Si le fichier n'existe pas/que la ressource n'a pas été trouvée, on ajoute dans la réponse HTTP un message d'erreur
				resp.setReplyError(404, "File not found");
				throw new FileNotFoundException("File "+m_ressname+"not found");
			}else { // Sinon, le fichier existe et a été trouvé
				FileInputStream fis = new FileInputStream(ressource);
				resp.setReplyOk(); // On ajoute dans la réponse HTTP les lignes indiquant le succès de la requête
				resp.setContentLength((int) ressource.length());
				resp.setContentType("HTML");

				PrintStream ps = resp.beginBody(); // On commence à écrire dans le corps de la page HTTP
				ps.write(fis.readAllBytes()); // On ajoute tout le contenu du fichier voulu à la page HTTP
				fis.close();
			}
		}
	}

}
