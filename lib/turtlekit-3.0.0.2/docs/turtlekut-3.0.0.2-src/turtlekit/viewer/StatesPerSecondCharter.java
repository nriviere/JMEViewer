package turtlekit.viewer;

import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import madkit.simulation.probe.SingleAgentProbe;

import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import turtlekit.agr.TKOrganization;
import turtlekit.kernel.TKScheduler;

@GenericViewer
public class StatesPerSecondCharter extends AbstractChartViewer{
	
	private XYSeriesCollection dataset = new XYSeriesCollection();
	private int timeFrame = 0;
	private XYSeries serie;
	private SingleAgentProbe<TKScheduler,Double> probe; 
	@Override
	protected void activate() {
		setLogLevel(Level.ALL);
		super.activate();
		probe = new SingleAgentProbe<>(getCommunity(), TKOrganization.ENGINE_GROUP, TKOrganization.SCHEDULER_ROLE,"statesPerSecond");
		addProbe(probe);
		serie = new XYSeries("States Per Second");
		dataset.addSeries(serie);
	}
	
	@Override
	public void setupFrame(JFrame frame) {
		final ChartPanel chartPanel = createChartPanel(dataset, "States Per Second", null, null);
		chartPanel.setPreferredSize(new java.awt.Dimension(550, 250));
		frame.setContentPane(chartPanel);
		frame.setLocation(50, 0);
	}
	
	@Override
	protected void observe() {
		SwingUtilities.invokeLater(new Runnable() {//avoiding null pointers on the awt thread
			@Override
			public void run() {
				final double gvt = probe.getProbedAgent().getGVT();
				final Double propertyValue = probe.getPropertyValue();
				if (propertyValue > 0) {
					serie.add(gvt, propertyValue);
				}
				if(timeFrame > 0 && gvt % timeFrame == 0){
						serie.clear();
				}
			}
		});
	}
	
	public void setTimeFrame(int interval){
		timeFrame = interval;
	}
	
}