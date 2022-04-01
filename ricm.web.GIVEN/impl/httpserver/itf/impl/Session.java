package httpserver.itf.impl;

import java.util.HashMap;

import httpserver.itf.HttpSession;

public class Session implements HttpSession {

	protected String id;
	protected HashMap<String, Object> content = new HashMap<String, Object>();
	protected long currentTime;
	protected long duration = 30000;
	
	protected Session(String id) {
		this.id = id;
		this.currentTime = System.currentTimeMillis();
	}
	
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public Object getValue(String key) {
		// TODO Auto-generated method stub
		return content.get(key);
	}

	@Override
	public void setValue(String key, Object value) {
		// TODO Auto-generated method stub
		content.put(key, value);
	}
	
	@Override
	public long getDeathTime() {
		return currentTime+duration;
	}
	
	public void resetTimer() {
		this.currentTime = System.currentTimeMillis();
	}

}
