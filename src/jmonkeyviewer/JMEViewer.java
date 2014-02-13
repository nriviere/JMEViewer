package jmonkeyviewer;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

import turtlekit.agr.TKOrganization;
import turtlekit.kernel.Patch;
import turtlekit.kernel.Turtle;
import turtlekit.kernel.TurtleKit;
import turtlekit.viewer.TKDefaultViewer;

public class JMEViewer extends TKDefaultViewer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4020322960978452872L;
	protected final Object verrou = new Object();
	protected JMERenderer renderer;
	protected TurtleKit turtleKit;
	protected ArrayList<Geometry> geometries;
	protected JMETurtleKitModel model;

	public JMEViewer() {
		model = new JMETurtleKitModel();
	}

	public JMETurtleKitModel getModel() {
		return model;
	}

	protected void render(Graphics g) {
		updateModel();
	}

	protected void updateModel() {
		synchronized (model.getLock()) {
			Patch[] patches = getPatchGrid();
			ArrayList<JMETurtle> turtles = new ArrayList<JMETurtle>();
			for (Patch p : patches) {
				List<Turtle> turtleList = p.getTurtles();
				for (Turtle t : turtleList) {
					turtles.add(new JMETurtle(new Vector3f(1.f*p.x / 100,
							1.f*p.y / 100, 0)));
				}
				model.setTurtles(turtles);
			}
			model.setChanged(true);
			//renderer.UpdateGeometries();
		}
	}
	
	public void setRenderer(JMERenderer renderer)
	{
		this.renderer = renderer;
	}

	public void activate() {
		super.activate();
		requestRole("testing", TKOrganization.ENGINE_GROUP,
				TKOrganization.VIEWER_ROLE);
		renderer = new JMERenderer(this);
		renderer.setShowSettings(false);
		renderer.setDisplayStatView(false);
		renderer.setDisplayFps(false);
		renderer.setPauseOnLostFocus(false);
		renderer.start();
	}

	public static void main(String[] args) {
		TurtleKit t = new TurtleKit("--turtles",
				"turtlekit.termites.Termite,300", "--viewers",
				"jmonkeyviewer.JMEViewer", "--startSimu",
				"true");
	}
}
