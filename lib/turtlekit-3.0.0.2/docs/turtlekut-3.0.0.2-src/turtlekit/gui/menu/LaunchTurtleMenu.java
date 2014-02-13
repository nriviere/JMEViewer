package turtlekit.gui.menu;

import java.lang.reflect.Modifier;

import madkit.gui.menu.AgentClassFilter;
import madkit.gui.menu.LaunchAgentsMenu;
import madkit.kernel.AbstractAgent;
import madkit.kernel.MadkitClassLoader;
import turtlekit.kernel.Turtle;

public class LaunchTurtleMenu extends LaunchAgentsMenu {

	public LaunchTurtleMenu(final AbstractAgent agent) {
		super(agent, "Turtles", new AgentClassFilter() {
			@Override
			public boolean accept(String agentClass) {
				try {
					Class<?> c = MadkitClassLoader.getLoader().loadClass(agentClass);
					return ! Modifier.isAbstract(c.getModifiers()) && Turtle.class.isAssignableFrom(c);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				return false;

			}
		});
	}
}
