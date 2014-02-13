package turtlekit.kernel;

import java.util.TimerTask;

import madkit.kernel.Scheduler;
import madkit.simulation.activator.GenericBehaviorActivator;
import turtlekit.agr.TKOrganization;
import turtlekit.cuda.CudaEngine;
import turtlekit.viewer.AbstractViewer;

public class TKScheduler extends Scheduler {

	protected String community;
	private GenericBehaviorActivator<TKEnvironment> environmentUpdateActivator;
	GenericBehaviorActivator<AbstractViewer> viewerActivator;
	private TurtleActivator turtleActivator;
	private GenericBehaviorActivator<TKEnvironment> pheroMaxReset;
	private java.util.Timer timer;
	private double statesPerSecond;
				
	public TKScheduler() {
	}
	
	@Override
	protected void activate() {
//		community = getMadkitProperty(TurtleKit.Option.community);
		//TODO timer sts
//		new java.util.Timer(true).schedule(new TimerTask() {
//			private double last = 0;
//
//			@Override
//			public void run() {
//				final double gvt = getGVT();
//				System.err.println(gvt - last);
//				last = gvt;
//			}
//		}, 0, 1000);
//		setLogLevel(Level.ALL);
		community = getMadkitProperty(TurtleKit.Option.community);
		requestRole(
				community, 
				TKOrganization.ENGINE_GROUP, 
				TKOrganization.SCHEDULER_ROLE);
//		
		pheroMaxReset = new GenericBehaviorActivator<TKEnvironment>(community, TKOrganization.MODEL_GROUP, TKOrganization.ENVIRONMENT_ROLE, "resetPheroMaxValues");
		addActivator(pheroMaxReset);
		turtleActivator = new TurtleActivator(community);
		addActivator(turtleActivator);
////		turtles.setMulticore(4);
		environmentUpdateActivator = new GenericBehaviorActivator<TKEnvironment>(community, TKOrganization.MODEL_GROUP, TKOrganization.ENVIRONMENT_ROLE, "update");
		addActivator(environmentUpdateActivator);
		viewerActivator = new GenericBehaviorActivator<AbstractViewer>(community, TKOrganization.ENGINE_GROUP, TKOrganization.VIEWER_ROLE, "observe");
		addActivator(viewerActivator);
	}
	
	@Override
	protected void end() {
		super.end();
		if(timer != null){
			timer = null;//stops the timer from within its run
		}
		if (isMadkitPropertyTrue(TurtleKit.Option.cuda)) {
			CudaEngine.stop();
			if (logger != null)
				logger.fine("cuda freed");
		}
//		pause(10000);

//		killAgent(model.getEnvironment());
//		CudaEngine.stop();
//		sendMessage(
//				LocalCommunity.NAME, 
//				Groups.SYSTEM, 
//				Organization.GROUP_MANAGER_ROLE, 
//				new KernelMessage(KernelAction.EXIT));//TODO work with AA but this is probably worthless	
	}
	
	/**
	 * 
	 */
	public void launchStepPerSecondMeter() {
		if (timer == null) {
			timer = new java.util.Timer(true);
			timer.scheduleAtFixedRate(new TimerTask() {
				private double last = 0;

				@Override
				public void run() {
					if (timer == null) {
						cancel();
					}
					final double gvt = getGVT();
					statesPerSecond = (gvt - last)/2;
					//			System.err.println("\n\ngvt = "+gvt);
					//			System.err.println("last = "+last);
					System.err.println("sps=" + statesPerSecond);
					//			if (last != 0) {
					//			}
					last = gvt;
					//			System.err.println("last = "+last);
				}
			}, 0, 2000);
		}
	}


	/**
	 * @return the turtleActivator
	 */
	public TurtleActivator getTurtleActivator() {
		return turtleActivator;
	}

	/**
	 * @return the environmentUpdateActivator
	 */
	public GenericBehaviorActivator<TKEnvironment> getEnvironmentUpdateActivator() {
		return environmentUpdateActivator;
	}

	/**
	 * @return the viewerActivator
	 */
	public GenericBehaviorActivator<AbstractViewer> getViewerActivator() {
		return viewerActivator;
	}

	/**
	 * @return the pheroMaxReset
	 */
	public GenericBehaviorActivator<TKEnvironment> getPheroMaxReset() {
		return pheroMaxReset;
	}
}
