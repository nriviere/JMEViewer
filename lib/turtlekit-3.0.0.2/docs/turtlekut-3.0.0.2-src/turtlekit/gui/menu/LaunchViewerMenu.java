package turtlekit.gui.menu;

import java.lang.reflect.Modifier;

import madkit.gui.menu.AgentClassFilter;
import madkit.gui.menu.LaunchAgentsMenu;
import madkit.kernel.AbstractAgent;
import madkit.kernel.MadkitClassLoader;
import turtlekit.viewer.GenericViewer;

public class LaunchViewerMenu extends LaunchAgentsMenu {

	public LaunchViewerMenu(final AbstractAgent agent) {
		super(agent, "Viewers", new AgentClassFilter() {
			@Override
			public boolean accept(String agentClass) {
				try {
					Class<?> c = MadkitClassLoader.getLoader().loadClass(agentClass);
					return ! Modifier.isAbstract(c.getModifiers()) && c.isAnnotationPresent(GenericViewer.class);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				return false;

			}
		});
	}
}
