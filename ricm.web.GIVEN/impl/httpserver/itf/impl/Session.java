package httpserver.itf.impl;

import java.util.Hashtable;

import httpserver.itf.HttpSession;

public class Session implements HttpSession{
	
	public String id;
	public Hashtable<String, Object> objects;
	private long duration = 60000;
	private long creationTimestamp;
	
	public Session(String id) {
		this.id = id;
		this.objects = new Hashtable<String, Object>();
		this.creationTimestamp = System.currentTimeMillis();
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
	
	public long getDeathTime() {
		long n = duration+ creationTimestamp;
		return n;
	}
	
	public void resetTimer() {
		this.creationTimestamp = System.currentTimeMillis();
	}
	
}
