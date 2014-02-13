package jmonkeyviewer;

import java.util.ArrayList;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.math.ColorRGBA;
import turtlekit.kernel.*;

/**
 * Sample 1 - how to get started with the most simple JME 3 application. Display
 * a blue 3D cube and view from all sides by moving the mouse and pressing the
 * WASD keys.
 */

public class JMERenderer extends SimpleApplication {

	protected float x, y, z;
	protected boolean running;
	protected TurtleKit turtleKit;
	protected JMEViewer viewer;
	protected ArrayList<Geometry> geometries;

	public JMERenderer(JMEViewer viewer) {
		this.viewer = viewer;
		running = false;
	}

	@Override
	public void simpleInitApp() {
		/*
		 * x = y = z = 0; b = new Box(1, 1, 1); // create cube shape Geometry
		 * geom = new Geometry("Box", b); // create cube geometry from the //
		 * shape Material mat = new Material(assetManager,
		 * "Common/MatDefs/Misc/Unshaded.j3md"); // create a simple // material
		 * mat.setColor("Color", ColorRGBA.Blue); // set color of material to
		 * blue geom.setMaterial(mat); // set the cube's material
		 * rootNode.attachChild(geom); // make the cube appear in the scene
		 */

	}

	public void UpdateGeometries() {
		/*
		 * b.getCenter().set(x, y, z); b.updateGeometry();
		 */
		rootNode.detachAllChildren();
		synchronized (viewer.getModel().getLock()) {
			if (viewer.getModel().getChanged()) {
				for (JMETurtle t : viewer.getModel().getTurtles()) {
					Box b = new Box(0.01f, 0.01f, 0.01f);
					b.getCenter().set(t.getPosition());
					b.updateGeometry();
					Geometry geom = new Geometry("Box", b);
					Material mat = new Material(assetManager,
							"Common/MatDefs/Misc/Unshaded.j3md");
					mat.setColor("Color", ColorRGBA.Blue);
					geom.setMaterial(mat);
					rootNode.attachChild(geom);
				}
				viewer.getModel().setChanged(false);
			}
		}
	}

	@Override
	public void simpleUpdate(float fdt) {
		UpdateGeometries();
	}

	public void simpleRender(RenderManager rm) {

	}

	@Override
	// M�thode pour arr�ter l'application quand on ferme le contexte jmonkey
	public void stop() {
		super.stop();
		running = false;
	}

	@Override
	public void start() {
		super.start();
		running = true;
	}
}