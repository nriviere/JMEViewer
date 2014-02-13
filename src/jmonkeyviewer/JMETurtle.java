package jmonkeyviewer;

import com.jme3.math.Vector3f;

public class JMETurtle {
	protected Vector3f position;
	protected Vector3f orientation;
	
	public JMETurtle(Vector3f position){
		this.position = position;
	}
	
	public Vector3f getPosition()
	{
		return position;
	}
	
	public Vector3f getOrientation()
	{
		return orientation;
	}
}
