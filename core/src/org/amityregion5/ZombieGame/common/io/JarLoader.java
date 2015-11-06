package org.amityregion5.ZombieGame.common.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.amityregion5.ZombieGame.common.plugin.IPlugin;

public class JarLoader {
	//The parent file to search
	private File jar;

	/**
	 * Creates a new JarLoader
	 * @param parent the parent file to search through
	 * @param searchJars should it search inside jars and zips
	 * @throws FileNotFoundException  thrown when the file sent doesn't exsist or isn't a directory 
	 */
	public JarLoader(File jar) throws FileNotFoundException
	{
		if (!jar.exists()) {
			throw new FileNotFoundException("File does not exist");
		}
		this.jar = jar;
	}

	public List<IPlugin> getIPlugins() throws IOException
	{
		List<IPlugin> plugins = new ArrayList<IPlugin>();
		
		//The jar file
		JarFile jarFile = new JarFile(jar.getPath());
		//Enumeration of the jar file
		Enumeration<JarEntry> e = jarFile.entries();

		//The url of the jar
		URL[] urls = { new URL("jar:file:" + jar.getPath() +"!/") };
		//The class loader
		URLClassLoader cl = URLClassLoader.newInstance(urls, ClassLoader.getSystemClassLoader());

		//Until we run out of elements
		while (e.hasMoreElements()) {
			//Get the next element
			JarEntry je = (JarEntry) e.nextElement();
			//If it is a directory or is not a class file
			if(je.isDirectory() || !je.getName().endsWith(".class")){
				continue;
			}
			//Get class name
			String className = je.getName().substring(0,je.getName().length()-6);
			//Fix class name
			className = className.replace('/', '.');
			//Load it as a class
			try {
				Class<?> clazz = cl.loadClass(className);
				if (IPlugin.class.isAssignableFrom(clazz)) {
					IPlugin plugin = (IPlugin) clazz.newInstance();
					
					plugins.add(plugin);
				}
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e1) {
				e1.printStackTrace();
			}
			
		}
		
		//Close the file
		jarFile.close();
		//Close the classloader
		cl.close();

		return plugins;
	}
}
