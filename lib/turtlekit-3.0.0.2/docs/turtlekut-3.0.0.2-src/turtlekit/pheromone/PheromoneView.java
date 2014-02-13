package turtlekit.pheromone;

import java.awt.Dimension;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import madkit.gui.SwingUtil;

public class PheromoneView extends JPanel implements ChangeListener{

	private DefaultBoundedRangeModel red;
	private DefaultBoundedRangeModel green;
	private DefaultBoundedRangeModel blue;
	private Pheromone myPhero;
	//	private Environment env;
	private JPanel evapPanel;
	private JPanel diffusionPanel;

	public PheromoneView(Pheromone phero) {
		myPhero = phero;
		final DefaultBoundedRangeModel evaporationPercentage = phero.getEvaporationPercentage();
		evaporationPercentage.addChangeListener(this);
		evapPanel = SwingUtil.createSliderPanel(evaporationPercentage,"evaporation");
		add(evapPanel);
		final DefaultBoundedRangeModel diffusionPercentage = phero.getDiffusionPercentage();
		diffusionPercentage.addChangeListener(this);
		diffusionPanel = SwingUtil.createSliderPanel(diffusionPercentage, "diffusion");
		add(diffusionPanel);
		setPreferredSize(new Dimension(600,40));
		
		red = new DefaultBoundedRangeModel(50, 0, 0, 255);
		add(SwingUtil.createSliderPanel(red, "red percentage"));
		green = new DefaultBoundedRangeModel(50, 0, 0, 255);
		add(SwingUtil.createSliderPanel(green, "green percentage"));
		blue = new DefaultBoundedRangeModel(50, 0, 0, 255);
		add(SwingUtil.createSliderPanel(blue, "blue percentage"));
		stateChanged(null);
	}

	/**
	 * @return the red
	 */
	public DefaultBoundedRangeModel getRed() {
		return red;
	}

	/**
	 * @return the green
	 */
	public DefaultBoundedRangeModel getGreen() {
		return green;
	}

	/**
	 * @return the blue
	 */
	public DefaultBoundedRangeModel getBlue() {
		return blue;
	}

	/**
	 * @return the evapPanel
	 */
	public JPanel getEvapPanel() {
		return evapPanel;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		((TitledBorder) diffusionPanel.getBorder())
		.setTitle("diffusion " + myPhero.getDiffusionPercentage().getValue()
				+ "%");
		diffusionPanel.repaint();
		((TitledBorder) evapPanel.getBorder())
		.setTitle("evaporation " + myPhero.getEvaporationPercentage().getValue()
				+ "%");
		evapPanel.repaint();
		
	}

}
