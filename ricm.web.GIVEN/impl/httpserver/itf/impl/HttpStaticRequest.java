package httpserver.itf.impl;

import java.io.File;
import java.io.FileInputStream;
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
		if (m_ressname.equals("/FILES/")) {
			m_ressname = "/FILES/"+DEFAULT_FILE;
		}
		
		if (m_method.equals("GET")) {
			File ressource = new File(m_hs.getFolder()+m_ressname);
			
			if (ressource.exists()) {
				FileInputStream fis = new FileInputStream(ressource);
				resp.setReplyOk();
				resp.setContentLength((int) ressource.length());
				resp.setContentType(HttpRequest.getContentType(m_ressname));
				PrintStream ps = resp.beginBody();
				ps.write(fis.readAllBytes());
				fis.close();
			}
			else {
				resp.setReplyError(404, "File not found");
			}
		}
	}

}
