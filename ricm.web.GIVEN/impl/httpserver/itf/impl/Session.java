package httpserver.itf.impl;

import java.util.Hashtable;

import httpserver.itf.HttpSession;

public class Session implements HttpSession{
	
	public String id;
	public Hashtable<String, Object> objects;
	
	public Session(String id) {
		this.id = id;
		objects = new Hashtable<String, Object>();
	}

	@Override
	public String getId() {
		return this.id;
	}
	

	@Override
	public Object getValue(String key) {
		return this.objects.get(key);
	}

	@Override
	public void setValue(String key, Object value) {
		this.objects.put(key, value);
		
	}
	
}
