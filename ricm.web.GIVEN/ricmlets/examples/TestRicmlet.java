package examples;


import java.io.IOException;
import java.io.PrintStream;

import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;
import httpserver.itf.HttpSession;

public class TestRicmlet implements httpserver.itf.HttpRicmlet{

	@Override
	public void doGet(HttpRicmletRequest req,  HttpRicmletResponse resp) throws IOException {
		// Partie Session
		HttpSession s = req.getSession();
		resp.setCookie("sessionId", s.getId());
		Integer c = (Integer) s.getValue("counter");
		if (c == null) {
			Integer zero = 0;
			s.setValue("counter", zero);
		}else {
			Integer i = c.intValue() + 1;
			s.setValue("counter", i);
		}

		// Partie Cookie
		String myFirstCookie = req.getCookie("MyFirstCookie");
		if (myFirstCookie == null) 
			resp.setCookie("MyFirstCookie", "1");
		else {
			int n =  Integer.valueOf(myFirstCookie);
				// modify the cookie's value each time the ricmlet is invoked
			Integer i = n+2;
			resp.setCookie("MyFirstCookie", i.toString());
		}
	
		resp.setReplyOk();
		resp.setContentType("text/html");
		PrintStream ps = resp.beginBody();
		ps.println("<HTML><HEAD><TITLE> Ricmlet processing </TITLE></HEAD>");
		ps.print("<BODY><H4> Hello " + req.getArg("name") + " " + req.getArg("surname") + " !!!<br></H4>");
		ps.print("<H4> MyFirstCookie " + req.getCookie("MyFirstCookie") + "<br></H4>");
		ps.print("<H4> Hello for the " + s.getValue("counter") + " times for sessions: "+s.getId()+"!!!<br></H4>");
		ps.println("</BODY></HTML>");
		ps.println();
	}
}