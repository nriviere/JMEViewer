package jmonkeyviewer;

import java.util.ArrayList;

public class JMETurtleKitModel {
	protected float width, height;
	protected ArrayList<JMETurtle> jmeTurtles;
	protected ArrayList<JMEPatch> jmePatches;
	protected final Object lock = new Object();
	protected boolean hasChanged;

	public JMETurtleKitModel() {
		jmeTurtles = new ArrayList<JMETurtle>();
		jmePatches = new ArrayList<JMEPatch>();
		hasChanged = false;
	}

	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;
	}

	public void setTurtles(ArrayList<JMETurtle> jmeTurtles) {
		synchronized (lock) {
			this.jmeTurtles = jmeTurtles;
		}
	}

	public void setPatches(ArrayList<JMEPatch> jmePatches) {
		synchronized (lock) {
			this.jmePatches = jmePatches;
		}
	}

	public ArrayList<JMETurtle> getTurtles() {
		synchronized (lock) {
			return jmeTurtles;
		}
	}

	public ArrayList<JMEPatch> getPatches() {
		synchronized (lock) {
			return jmePatches;
		}
	}
	
	public boolean getChanged()
	{
		return hasChanged;
	}
	
	public void setChanged(boolean b)
	{
		hasChanged = b;
	}
	
	public Object getLock()
	{
		return lock;
	}

}
