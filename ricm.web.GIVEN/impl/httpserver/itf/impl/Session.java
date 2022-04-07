package httpserver.itf.impl;

import java.util.Hashtable;

import httpserver.itf.HttpSession;

public class Session implements HttpSession{
	
	public String id;
	public Hashtable<String, Object> objects; // HashMap contenant la valeur des différents objets propres à une session (par exemple un counter) avec l'identifiant correspondant
	private long duration = 60000; // En millisecondes, la durée de vie d'une session inutilisée est de 60s
	private long creationTimestamp; // Contient l'heure de la dernière interaction avec la session
	
	public Session(String id) {
		this.id = id;
		this.objects = new Hashtable<String, Object>();
		this.creationTimestamp = System.currentTimeMillis();
	}

	@Override
	public String getId() { // Retourne l'ID de la session
		return this.id;
	}
	

	@Override
	public Object getValue(String key) { // Retourne la valeur d'un objet dans la HashMap
		return this.objects.get(key);
	}

	@Override
	public void setValue(String key, Object value) { // Modifie la valeur d'un objet dans la HashMap
		this.objects.put(key, value);
		
	}
	
	public long getDeathTime() { // Calcule l'heure à partir de laquelle la session deviendra inactive et supprimable. Cette heure sera mise à jour à chaque interaction avec la session
		long n = duration+ creationTimestamp;
		return n;
	}
	
	public void resetTimer() { // Remet à l'heure courante le timer de la session, réinitialise le compteur d'inactivité du timer
		this.creationTimestamp = System.currentTimeMillis();
	}
	
}
