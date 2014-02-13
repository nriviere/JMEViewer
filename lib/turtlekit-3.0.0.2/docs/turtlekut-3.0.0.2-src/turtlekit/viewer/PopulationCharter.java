package turtlekit.viewer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import madkit.agr.Organization;
import madkit.kernel.Probe;

import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import turtlekit.agr.TKOrganization;
import turtlekit.kernel.Turtle;

@GenericViewer
public class PopulationCharter extends AbstractChartViewer{
	
	private XYSeriesCollection dataset = new XYSeriesCollection();
	private int index=0;
	private Map<Probe<Turtle>, XYSeries> series = new HashMap<>();
	private Set<String> handledRoles = new HashSet<>();
	private int timeFrame = 0;
	private boolean monitorTurtle;

	@Override
	protected void activate() {
		setLogLevel(Level.ALL);
		super.activate();
		observe();
	}
	
	@Override
	public void setupFrame(JFrame frame) {
		final ChartPanel chartPanel = createChartPanel(dataset, "Population", null, null);
		chartPanel.setPreferredSize(new java.awt.Dimension(550, 250));
		frame.setContentPane(chartPanel);
		frame.setLocation(50, 0);
	}
	
	/**
	 * @param role
	 */
	private void addSerie(String role) {
		final Probe<Turtle> probe = new Probe<Turtle>(getCommunity(), TKOrganization.TURTLES_GROUP, role);
		addProbe(probe);
		XYSeries serie = new XYSeries(role);
		series.put(probe, serie);
		dataset.addSeries(serie);
		handledRoles.add(role);
	}

	@Override
	protected void observe() {
		updateSeries();
		SwingUtilities.invokeLater(new Runnable() {//avoiding null pointers on the awt thread
			@Override
			public void run() {
				for(Entry<Probe<Turtle>, XYSeries> entry : series.entrySet()) {
					entry.getValue().add(index, entry.getKey().size());
				}
				index++;
				if(timeFrame > 0 && index % timeFrame == 0){
					for (XYSeries serie : series.values()) {
						serie.clear();
					}
				}
			}
		});
	}
	
	public void setTimeFrame(int interval){
		timeFrame = interval;
	}
	
	public void setMonitorTurtleRole(boolean b){
		monitorTurtle = b;
		if (isAlive()) {
			updateSeries();
		}
	}

	/**
	 * 
	 */
	protected void updateSeries() {
		TreeSet<String> roles = getExistingRoles();
		if(roles != null && roles.size() != handledRoles.size()){
			for (String role : roles) {
				if(handledRoles.add(role)){
					addSerie(role);
				}
			}
		}
	}

	/**
	 * @return
	 */
	private TreeSet<String> getExistingRoles() {
		TreeSet<String> roles = getExistingRoles(getCommunity(), TKOrganization.TURTLES_GROUP);
		if (roles != null) {
			roles.remove(Organization.GROUP_MANAGER_ROLE);
			if (! monitorTurtle) {
				roles.remove(TKOrganization.TURTLE_ROLE);
			}
		}
		return roles;
	}
	
}