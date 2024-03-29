/*
 * Copyright 1997-2013 Fabien Michel, Olivier Gutknecht, Jacques Ferber
 * 
 * This file is part of MaDKit.
 * 
 * MaDKit is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * MaDKit is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MaDKit. If not, see <http://www.gnu.org/licenses/>.
 */
package turtlekit.gui.menu;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import madkit.gui.menu.LaunchMDKConfigurations;
import madkit.kernel.Madkit.BooleanOption;
import turtlekit.kernel.TurtleKit.Option;

/**
 * This class builds a {@link JMenu} containing all the 
 * MDK configuration files found on the class path.
 * Each item will launch a separate instance of MaDKit
 * using the corresponding configuration files
 * 
 * @author Fabien Michel
 * @since TurtleKit 3.0.0.1
 * @version 0.9
 * 
 */
public class LaunchTKConfigurations extends LaunchMDKConfigurations {

	/**
	 * Builds a new menu.
	 * @param agent the agent according 
	 * to which this menu should be created, i.e. the
	 * agent that will be responsible of the launch.
	 * @param title the title to use
	 */
	public LaunchTKConfigurations(final String title) {
		super(title);
		setMnemonic(KeyEvent.VK_M);
	}

	public void update() {
		super.update();
		for (int i = 0; i < getItemCount(); i++) {
			JMenuItem item = getItem(i);
			item.setActionCommand("turtlekit/kernel/turtlekit.properties "//+";"+item.getActionCommand()+
					+Option.model.toString()+" "+item.getActionCommand()+
					" "+BooleanOption.desktop.toString()+" false "
//							+ "--madkitLogLevel ALL"
					);
//			System.err.println(item.getActionCommand());
		}
	}

}
