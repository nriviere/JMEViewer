/*
 * Copyright 1997-2012 Fabien Michel, Olivier Gutknecht, Jacques Ferber
 * 
 * This file is part of MaDKit.
 * 
 * MaDKit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * MaDKit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MaDKit. If not, see <http://www.gnu.org/licenses/>.
 */
package turtlekit.gui.toolbar;

import javax.swing.JToolBar;

import madkit.action.GUIManagerAction;
import madkit.action.KernelAction;
import madkit.gui.SwingUtil;
import madkit.kernel.AbstractAgent;
import turtlekit.kernel.TurtleKit;

/**
 * An out of the box toolbar for MaDKit based applications.
 * 
 * @author Fabien Michel
 * @since MaDKit 5.0.0.9
 * @version 0.9
 * 
 */
public class TKToolBar extends JToolBar {// TODO i18n

	/**
	 * 
	 */
	private static final long serialVersionUID = -700298646422969523L;

	/**
	 * Creates a {@link JToolBar} featuring: 
	 * <ul>
	 * <li> {@link KernelAction#EXIT}
	 * <li> {@link KernelAction#COPY}
	 * <li> {@link KernelAction#RESTART}
	 * <li> {@link KernelAction#CONSOLE}
	 * <li> {@link KernelAction#LOAD_LOCAL_DEMOS}
	 * <li> {@link GUIManagerAction#LOAD_JAR_FILE}
	 * <li> {@link GUIManagerAction#ICONIFY_ALL}
	 * <li> {@link GUIManagerAction#DEICONIFY_ALL}
	 * </ul>
	 * 
	 * @param agent the agent for which this menu is created
	 */
	public TKToolBar(final AbstractAgent agent) {
		super("TurtleKit");
		TurtleKit.addTurleKitActionsTo(this, agent);
		SwingUtil.scaleAllAbstractButtonIcons(this, 22);
	}
}
