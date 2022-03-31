package examples;


import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;
import httpserver.itf.HttpSession;

public class CountBySessionRicmlet implements httpserver.itf.HttpRicmlet{
	HashMap<String,Integer> counts = new HashMap<String,Integer>();
	
	/*
	 * Print the number of time this ricmlet has been invoked per user session
	 */
	@Override
	public void doGet(HttpRicmletRequest req,  HttpRicmletResponse resp) throws IOException {
		HttpSession s = req.getSession();
		resp.setCookie("sessionId", s.getId()); 		//TODO demander à la prof
		Integer c = (Integer) s.getValue("counter");
		if (c == null) {
			Integer zero = 0;
			s.setValue("counter", zero);
		}else {
			Integer i = c.intValue() + 1;
			s.setValue("counter", i);
		}
		resp.setReplyOk();
		resp.setContentType("text/html");
		PrintStream ps = resp.beginBody();
		ps.println("<HTML><HEAD><TITLE> Ricmlet processing </TITLE></HEAD>");
		ps.print("<BODY><H4> Hello for the " + s.getValue("counter") + " times for sessions: "+s.getId()+"!!!");
		ps.println("</H4></BODY></HTML>");
		ps.println();
	}

}
