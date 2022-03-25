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
	
	public void process(HttpResponse resp) throws Exception {
		
		if(m_method.compareTo("GET") == 0) {
			File ressource = new File(m_hs.getFolder()+m_ressname);
			
			if(!ressource.exists()) {
				resp.setReplyError(404, "File not found");
				throw new FileNotFoundException("File "+m_ressname+"not found");
			}else {
				FileInputStream fis = new FileInputStream(ressource);
				resp.setReplyOk();
				resp.setContentLength((int) ressource.length());
				resp.setContentType("HTML");

				PrintStream ps = resp.beginBody();
				ps.write(fis.readAllBytes());
				fis.close();
			}
		}
	}

}
