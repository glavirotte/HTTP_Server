package httpserver.itf.impl;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

import httpserver.itf.HttpRicmlet;

public class Application {
	
	protected HashMap<String, HttpRicmlet> classes;

	public Application() {}
	
	@SuppressWarnings({ "rawtypes", "unchecked", "resource" })
	public HttpRicmlet getInstance(String className, String appName) throws Exception {
		ClassLoader parent = ClassLoader.getSystemClassLoader();
		
		File appJar = new File(appName+".jar");
		URL[] classpath = new URL[] {appJar.toURI().toURL()};
		URLClassLoader appCL = new URLClassLoader(classpath, parent);
		
		Class appClass = appCL.loadClass(className);
		
		Class params[] = new Class[] {};
		Constructor ctor = appClass.getConstructor(params);
		
		return (HttpRicmlet) ctor.newInstance();
	}
}
