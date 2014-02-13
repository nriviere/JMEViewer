package turtlekit.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

import madkit.simulation.probe.SingleAgentProbe;
import turtlekit.agr.TKOrganization;
import turtlekit.kernel.TKEnvironment;
import turtlekit.kernel.Patch;
import turtlekit.pheromone.Pheromone;
import turtlekit.pheromone.PheromoneView;

@GenericViewer
public class PheromoneViewer extends TKDefaultViewer {

	private SingleAgentProbe<TKEnvironment, Map<String, Pheromone>> pheroProbe;
	private Pheromone selectedPheromone;
	private double max;
	private JToolBar tb;
	private PheromoneView defaultView;
	private int redCanal;
	private int blueCanal;
	private int greenCanal;
	@Override
	protected void activate() {
		super.activate();
		setSynchronousPainting(false);
	}
	
	@Override
	protected void initProbes() {
		super.initProbes();
		pheroProbe = new SingleAgentProbe<>(
				getCommunity(), TKOrganization.MODEL_GROUP,
				TKOrganization.ENVIRONMENT_ROLE, "pheromones");
		addProbe(pheroProbe);
	}
	
	public Pheromone setSelectedPheromone(String name) {
		getFrame().setTitle("phero view on "+name);
		selectedPheromone = pheroProbe.getPropertyValue().get(name);
		if(selectedPheromone == null){
			for(Pheromone p : pheroProbe.getPropertyValue().values()){
				selectedPheromone = p;
				break;//TODO ugly
			}
		}
		if (selectedPheromone != null) {
			if (tb != null) {
				getFrame().remove(tb);
			}
			tb = new JToolBar();
			defaultView = new PheromoneView(selectedPheromone);
			tb.add(defaultView);
			getFrame().add(tb, BorderLayout.PAGE_END);
			getFrame().validate();
			getFrame().pack();
		}
		return selectedPheromone;
	}
	

	@Override
	public void setupFrame(JFrame frame) {
		super.setupFrame(frame);
		JMenuBar menuB = frame.getJMenuBar();
		JMenu menu = new JMenu("pheros");
		for (final Pheromone p : pheroProbe.getPropertyValue().values()) {
			setSelectedPheromone(p.getName());
		menu.add(new JMenuItem(new AbstractAction(p.getName()) {
				@Override
				public void actionPerformed(ActionEvent e) {
					setSelectedPheromone(p.getName());
				}
			}));
		}
		menuB.add(menu);
	}
	
	@Override
	protected void render(Graphics g) {
		if (selectedPheromone != null) {
			max = Math.log10(selectedPheromone.getMaximum() + 1) / 256;
			if(max == 0)
				max = 1;
			redCanal = defaultView.getRed().getValue();
			greenCanal = defaultView.getGreen().getValue();
			blueCanal = defaultView.getBlue().getValue();
//			timer.setText(selectedPheromone.toString());
		}
		super.render(g);
	}
	
	@Override
	public void paintPatch(final Graphics g, final Patch p, final int x, final int y, final int index) {
		if (selectedPheromone != null) {
			final double value = selectedPheromone.get(index);
			if (value > 10) {
				int r = (int) (Math.log10(value + 1) / max);
				r += redCanal;
				if (r > 255)
					r = 255;
				g.setColor(new Color(r, greenCanal, blueCanal));
				g.fillRect(x, y, cellSize, cellSize);
			}
		}
		else {
			super.paintPatch(g, p, x, y, index);
		}
	}
	
	public Pheromone getSelectedPheromone() {
		return selectedPheromone;
	}
	public void setSelectedPheromone(Pheromone selectedPheromone) {
		this.selectedPheromone = selectedPheromone;
	}
//	
//	public Pheromone selectPheromone(String name) {
////		getFrame().setTitle("phero view "+name);
//		selectedPheromone = pheroProbe.getPropertyValue().get(name);
//		if(tb != null){
//			getFrame().remove(tb);
//		}
//		tb = new JToolBar();
//		tb.add(selectedPheromone.getDefaultView());
//		getFrame().add(tb,BorderLayout.PAGE_END);		
//		getFrame().validate();
//		return selectedPheromone;
//	}
//	

}
