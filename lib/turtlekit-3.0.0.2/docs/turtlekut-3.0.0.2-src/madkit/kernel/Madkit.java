/*
 * Copyright 1997-2014 Fabien Michel, Olivier Gutknecht, Jacques Ferber
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
package madkit.kernel;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import madkit.action.KernelAction;
import madkit.gui.AgentFrame;
import madkit.gui.ConsoleAgent;
import madkit.gui.MDKDesktopFrame;
import madkit.i18n.ErrorMessages;
import madkit.i18n.I18nUtilities;
import madkit.message.KernelMessage;
import madkit.util.MadkitProperties;

/**
 * MaDKit 5 booter class.
 * <p>
 * <h2>MaDKit v.5 new features</h2>
 * <p>
 * <ul>
 * <li>One big change that comes with version 5 is how agents are identified and localized within the artificial society. An agent is no longer binded to a single agent address but has as many agent
 * addresses as holden positions in the artificial society. see {@link AgentAddress} for more information.</li>
 * <br>
 * <li>With respect to the previous change, a <code><i>withRole</i></code> version of all the messaging methods has been added. See
 * {@link AbstractAgent#sendMessageWithRole(AgentAddress, Message, String)} for an example of such a method.</li>
 * <br>
 * <li>A replying mechanism has been introduced through <code><i>SendReply</i></code> methods. It enables the agent with the possibility of replying directly to a given message. Also, it is now
 * possible to get the reply to a message, or to wait for a reply ( for {@link Agent} subclasses only as they are threaded) See {@link AbstractAgent#sendReply(Message, Message)} for more details.</li>
 * <br>
 * <li>Agents now have a <i>formal</i> state during a MaDKit session. See the {@link AbstractAgent#getState()} method for detailed information.</li>
 * <br>
 * <li>One of the most convenient improvement of v.5 is the logging mechanism which is provided. See the {@link AbstractAgent#logger} attribute for more details.</li>
 * <br>
 * <li>Internationalization is being made (fr_fr and en_us for now).</li>
 * <p>
 * 
 * @author Fabien Michel
 * @author Jacques Ferber
 * @since MaDKit 4.0
 * @version 5.2
 */

final public class Madkit {

	private final static String	MDK_LOGGER_NAME		= "[* MADKIT *] ";
	final static MadkitProperties			defaultConfig			= new MadkitProperties();
	final static SimpleDateFormat	dateFormat				= new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
	static {
		// System.setProperty("sun.java2d.xrender", "True"); //TODO
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {// just in case (like ctrl+c)
				AgentLogger.resetLoggers();
			}
		});
		// no need to externalize because it is used only here
		try {
			defaultConfig.loadPropertiesFromPropertiesFile("madkit/kernel/madkit.properties");
		} catch (IOException e) {
		}
	}

	public final static String		VERSION					= defaultConfig.getProperty("madkit.version");
	public final static String		BUILD_ID					= defaultConfig.getProperty("build.id");
	public final static String		WEB						= defaultConfig.getProperty("madkit.web");

	final private MadkitProperties		madkitConfig			= new MadkitProperties();
//	private Element					madkitXMLConfigFile	= null;
	// private FileHandler madkitLogFileHandler;
	final private MadkitKernel		myKernel;
	private Logger						logger;
	// TODO Remove unused code found by UCDetector
	// String cmdLine;
	String[]								args						= null;

	/**
	 * This main could be used to
	 * launch a new kernel using predefined options.
	 * The new kernel automatically ends when all
	 * the agents living on this kernel are done.
	 * So the JVM automatically quits if there is no
	 * other remaining threads.
	 * 
	 * Basically this call just instantiates a new kernel like this:
	 * 
	 * <pre>
	 * 
	 * 
	 * public static void main(String[] options) {
	 * 	new Madkit(options);
	 * }
	 * </pre>
	 * 
	 * So, this main can be used as a MAS application entry point
	 * in two ways :
	 * <p>
	 * (1) From the command line:
	 * <p>
	 * For instance, assuming that your classpath is already set correctly:
	 * <p>
	 * <tt>>java madkit.kernel.Madkit agentLogLevel INFO --launchAgents
	 * madkit.marketorg.Client,20,true;madkit.marketorg.Broker,10,true;madkit.marketorg.Provider,20,true;</tt>
	 * <p>
	 * (2) It can be used programmatically anywhere, especially within main method of agent classes to ease their launch within an IDE.
	 * <p>
	 * Here is an example of how it can be used in this way:
	 * <p>
	 * 
	 * <pre>
	 * 
	 * 
	 * 
	 * public static void main(String[] args) {
	 * 	String[] argss = { LevelOption.agentLogLevel.toString(), &quot;FINE&quot;, Option.launchAgents.toString(),// gets the -- launchAgents string
	 * 			Client.class.getName() + &quot;,true,20;&quot; + Broker.class.getName() + &quot;,true,10;&quot; + Provider.class.getName() + &quot;,false,20&quot; };
	 * 	Madkit.main(argss);// launching the application
	 * }
	 * </pre>
	 * 
	 * @param options the options which should be used to launch Madkit:
	 *           see {@link LevelOption}, {@link BooleanOption} and {@link Option}
	 */
	@SuppressWarnings("unused")
	public static void main(String[] options) {
		new Madkit(options);
	}

	/**
	 * Makes the kernel do the corresponding action. This is done
	 * by sending a message directly to the kernel.
	 * This should not be used intensively since it is better to control
	 * the execution flow of the application using the agents running in the kernel.
	 * Still it provides a way to launch and manage a kernel from any java application as
	 * a third party service.
	 * 
	 * <pre>
	 * 
	 * public void somewhereInYourCode() {
	 * 				...
	 * 				Madkit m = new Madkit(args);
	 * 				...
	 * 				m.doAction(KernelAction.LAUNCH_NETWORK);
	 * 				...
	 * }
	 * </pre>
	 * 
	 * 
	 * @param action the action to request
	 * @param parameters the parameters of the request
	 */
	public void doAction(KernelAction action, Object... parameters) {
		if (myKernel.isAlive()) {
			myKernel.receiveMessage(new KernelMessage(action, parameters));
		}
		else {
			logger.severe("my kernel is terminated...");
		}
	}

	/**
	 * Launch a new kernel with predefined options.
	 * The call returns when the new kernel has finished to take
	 * care of all options. Moreover the kernel automatically ends when all
	 * the agents living on this kernel are done.
	 * <p>
	 * 
	 * Here is an example of use:
	 * <p>
	 * 
	 * <pre>
	 * 
	 * 
	 * public void somewhereInYourCode() {
	 * 	new Madkit(Option.launchAgents.toString(),// gets the --launchAgents string
	 * 			Client.class.getName() + &quot;,true,20;&quot; + Broker.class.getName() + &quot;,true,10;&quot; + Provider.class.getName() + &quot;,false,20&quot;);
	 * }
	 * </pre>
	 * 
	 * @param options the options which should be used to launch Madkit.
	 *           If <code>null</code>, the dektop mode is automatically used.
	 * 
	 * @see Option
	 * @see BooleanOption
	 * @see LevelOption
	 */

	public Madkit(String... options) {
		final ArrayList<String> argsList = new ArrayList<>();
		if (options != null) {
			for (String string : options) {
				argsList.addAll(Arrays.asList(string.trim().split("\\s+")));
			}
			this.args = argsList.toArray(new String[argsList.size()]);
		}

		madkitConfig.putAll(defaultConfig);
		final Properties fromArgs = buildConfigFromArgs(args);
		madkitConfig.putAll(fromArgs);
		initMadkitLogging();
		logger.finest("command line args : " + fromArgs);
		loadJarFileArguments();
		if (loadConfigFiles())// overriding config file
			loadJarFileArguments();
		logger.fine("** OVERRIDING WITH COMMAND LINE ARGUMENTS **");
		madkitConfig.putAll(fromArgs);

		I18nUtilities.setI18nDirectory(madkitConfig.getProperty(Option.i18nDirectory.name()));
		
		logger.finest(MadkitClassLoader.getLoader().toString());

		// activating desktop if no agent at this point
		if (madkitConfig.get(Option.launchAgents.name()).equals("null") && madkitConfig.get(Option.configFile.name()).equals("null")) {
			logger.fine(Option.launchAgents.name() + " && "+Option.configFile.name()+" == null : Activating desktop");
			madkitConfig.setProperty(BooleanOption.desktop.name(), "true");
		}

		myKernel = new MadkitKernel(this);
		
		logger.finer("**  MADKIT KERNEL CREATED **");

		printWelcomeString();
		logSessionConfig(madkitConfig, Level.FINER);
		// if(madkitClassLoader.getAvailableConfigurations().isEmpty() //TODO
		// && ! madkitConfig.get(Option.launchAgents.name()).equals("null")){
		// madkitClassLoader.addMASConfig(new MASModel(Words.INITIAL_CONFIG.toString(), args, "desc"));
		// }

		// this.cmdLine = System.getProperty("java.home")+File.separatorChar+"bin"+File.separatorChar+"java -cp "+System.getProperty("java.class.path")+" madkit.kernel.Madkit ";

		startKernel();
	}

	/**
	 * 
	 */
	private void loadJarFileArguments() {
		String[] options = null;
		logger.fine("** LOADING JAR FILE ARGUMENTS **");
		try {
			for (Enumeration<URL> urls = Madkit.class.getClassLoader().getResources("META-INF/MANIFEST.MF"); urls.hasMoreElements();) {
				Manifest manifest = new Manifest(urls.nextElement().openStream());
				// if(logger != null)
				// logger.fine(manifest.toString());
				// for (Map.Entry<String, Attributes> e : manifest.getEntries().entrySet()) {
				// System.err.println("\n"+e.getValue().values());
				// }
				Attributes projectInfo = manifest.getAttributes("MaDKit-Project-Info");
				if (projectInfo != null) {
					logger.finest("found project info \n\t" + projectInfo.keySet() + "\n\t" + projectInfo.values());
					options = projectInfo.getValue("MaDKit-Args").trim().split("\\s+");
					logger.finer("JAR FILE ARGUMENTS = "+Arrays.deepToString(options));
					Map<String, String> projectInfos = new HashMap<>();
					projectInfos.put("Project-Code-Name", projectInfo.getValue("Project-Code-Name"));
					projectInfos.put("Project-Version", projectInfo.getValue("Project-Version"));
					madkitConfig.putAll(buildConfigFromArgs(options));
					madkitConfig.putAll(projectInfos);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initMadkitLogging() {
		final Level l = LevelOption.madkitLogLevel.getValue(madkitConfig);
		logger = Logger.getLogger(MDK_LOGGER_NAME);
		logger.setUseParentHandlers(false);
		logger.setLevel(l);
		ConsoleHandler cs = new ConsoleHandler();
		cs.setLevel(l);
		cs.setFormatter(AgentLogger.AGENT_FORMATTER);
		logger.addHandler(cs);
		logger.fine("** LOGGING INITIALIZED **");
	}

	private boolean loadConfigFiles() {// TODO
		final String filesName = madkitConfig.getProperty(Option.configFile.name());
		if (! filesName.equals("null")) {
			for (String fileName : filesName.split(";")) {
				logger.fine("** Loading config file " + fileName + " **");
				try {
					madkitConfig.loadPropertiesFromFile(fileName.trim());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 */
	private void startKernel() {
		// starting the kernel agent and waiting the end of its activation
		logger.fine("** LAUNCHING KERNEL AGENT **");
		myKernel.launchAgent(myKernel, myKernel, Integer.MAX_VALUE, false);
	}
	
	@Override
	public String toString() {
		return myKernel.toString()+" @ "+myKernel.getKernelAddress();
	}

	/**
	 * 
	 */
	private void printWelcomeString() {
		if (!(LevelOption.madkitLogLevel.getValue(madkitConfig) == Level.OFF)) {
			System.out.println("\n\t---------------------------------------" + "\n\t                MaDKit" + "\n\t           version: "
					+ VERSION + "\n\t        build-id: " + defaultConfig.getProperty("build.id") + "\n\t       MaDKit Team (c) 1997-"
					+ Calendar.getInstance().get(Calendar.YEAR) + "\n\t---------------------------------------\n");
		}
	}

	private void logSessionConfig(Properties session, Level lvl) {
		String message = "MaDKit current configuration is\n\n";
		message += "\t--- MaDKit regular options ---\n";
		for (String option : defaultConfig.stringPropertyNames()) {
			message += "\t" + String.format("%-" + 30 + "s", option) + session.getProperty(option) + "\n";
		}
		Set<Object> tmp = new HashSet<>(session.keySet());
		tmp.removeAll(defaultConfig.keySet());
		if (tmp.size() > 0) {
			message += "\n\t--- Additional non MaDKit options ---\n";
			for (Object o : tmp)
				message += "\t" + String.format("%-" + 25 + "s", o) + session.get(o) + "\n";
		}
		logger.log(lvl, message);
	}

	Properties buildConfigFromArgs(final String[] options) {
		Properties currentMap = new Properties();
		if (options != null && options.length > 0) {
			String parameters = "";
			String currentOption = null;
			for (int i = 0; i < options.length; i++) {
				if (!options[i].trim().isEmpty()) {
					if (options[i].startsWith("--")) {
						currentOption = options[i].substring(2);
						currentMap.put(currentOption, "true");
						parameters = "";
					}
					else {
						if (currentOption == null) {
							System.err
									.println("\n\t\t!!!!! MADKIT WARNING !!!!!!!!!!!\n\t\tNeeds an option with -- to start with\n\t\targs was : "
											+ Arrays.deepToString(options));
							return currentMap;
						}
						parameters += options[i] + " ";
						if (i + 1 == options.length || options[i + 1].startsWith("--")) {
							String currentValue = currentMap.getProperty(currentOption);
							if (currentOption.equals(Option.configFile.name()) && !currentValue.equals("true")) {
								currentMap.put(currentOption, currentValue + ';' + parameters.trim());// TODO bug on "-" use
							}
							else {
								currentMap.put(currentOption, parameters.trim());// TODO bug on "-" use
							}
						}
					}
				}
			}
		}
		return currentMap;
	}

	MadkitProperties getConfigOption() {
		return madkitConfig;
	}

	/**
	 * only for junit
	 * 
	 * @return the kernel
	 */
	MadkitKernel getKernel() {
		return myKernel;
	}

	/**
	 * Option used to activate or disable features on startup.
	 * These options can be used
	 * from the command line or using the main method of MaDKit.
	 * 
	 * <pre>
	 * SYNOPSIS
	 * </pre>
	 * 
	 * <code><b>--optionName</b></code> [true|false]
	 * 
	 * <pre>
	 * DESCRIPTION
	 * </pre>
	 * 
	 * If no boolean value is specified,
	 * the option is considered as being set to true.
	 * 
	 * <pre>
	 * EXAMPLES
	 * </pre>
	 * <ul>
	 * <li>--optionName false</li>
	 * <li>--optionName (equivalent to)</li>
	 * <li>--optionName true</li>
	 * 
	 * @author Fabien Michel
	 * @since MaDKit 5
	 * @version 0.9
	 * 
	 */
	public static enum BooleanOption implements MadkitOption {
		/**
		 * Starts the desktop mode.
		 * Default value is "false".
		 */
		desktop,
		/**
		 * Connect to the MaDKit repository on startup.
		 * Default value is "false".
		 */
		autoConnectMadkitWebsite,
		/**
		 * Starts the network on startup.
		 * Default value is "false".
		 */
		network,
		/**
		 * If activated, MaDKit will create a log file for every agent which has
		 * a log level greater than {@link Level#OFF}.
		 * Default value is "false".
		 * 
		 * @see Madkit.Option#logDirectory
		 */
		createLogFiles,
		// /**
		// * not functional yet
		// */
		// noGUIManager,
		/**
		 * Defines if agent logging should be quiet in the
		 * default console.
		 * Default value is "false".
		 */
		noAgentConsoleLog,
		/**
		 * Launches the {@link ConsoleAgent} before any other.
		 */
		console,
		/**
		 * Loads all the jar files which are in the demos directory on startup.
		 * Default value is "false".
		 */
		loadLocalDemos;

		/**
		 * Tells if this option is activated for this session.
		 * 
		 * @param session
		 * @return <code>true</code> if this boolean option
		 * is set to <code>true</code> for this config
		 */
		public boolean isActivated(Properties session) {
			return Boolean.parseBoolean(session.getProperty(this.name()));
		}

		/**
		 * Returns the constant's name prefixed by "<code>--</code>" so that
		 * it could interpreted as an option of the command line or
		 * in {@link Madkit#Madkit(String...)}.
		 */
		@Override
		public String toString() {
			return "--" + name();
		}

	}

	/**
	 * MaDKit options which are valued with a string representing parameters.
	 * These options could be used from the command line or using the main method of MaDKit.
	 * 
	 * @author Fabien Michel
	 * @since MaDKit 5.0.0.10
	 * @version 0.9
	 * 
	 */
	public static enum Option implements MadkitOption {
		/**
		 * Used to launch agents at start up.
		 * This option can be used
		 * from the command line or using the main method of MaDKit.
		 * 
		 * <pre>
		 * SYNOPSIS
		 * </pre>
		 * 
		 * <code><b>--launchAgents</b></code> AGENT_CLASS_NAME[,GUI][,NB][;OTHERS]
		 * <p>
		 * <ul>
		 * <li><i>AGENT_CLASS_NAME</i>: the agent class to launch</li>
		 * <li><i>GUI</i> (boolean optional): with a default GUI if <code>true</code></li>
		 * <li><i>NB</i> (integer optional): number of desired instances</li>
		 * </ul>
		 * 
		 * <pre>
		 * DESCRIPTION
		 * </pre>
		 * 
		 * The optional parameters could be used to (1) launch several different types of agents, (2) launch the agents with a default GUI and/or (3) specify the number of desired instances of each
		 * type.
		 * 
		 * <pre>
		 * DEFAULT VALUE
		 * </pre>
		 * 
		 * Default value is <i>"null"</i>, meaning that no agent has to be launched.
		 * <p>
		 * Default values for the optional parameters are
		 * <ul>
		 * <li><i>GUI</i> : <code>false</code></li>
		 * <li><i>NB</i> : 1</li>
		 * </ul>
		 * 
		 * <pre>
		 * EXAMPLES
		 * </pre>
		 * 
		 * <ul>
		 * <li>--launchAgents myPackage.MyAgent</li>
		 * <li>--launchAgents myPackage.MyAgent,true</li>
		 * <li>--launchAgents myPackage.MyAgent,false,3</li>
		 * <li>--launchAgents myPackage.MyAgent;other.OtherAgent</li>
		 * <li>--launchAgents myPackage.MyAgent,true;other.OtherAgent,true</li>
		 * <li>--launchAgents myPackage.MyAgent;other.OtherAgent,true,3;madkit.kernel.Agent</li>
		 * </ul>
		 */
		launchAgents,
		/**
		 * Used to specify the directory wherein the logs should be done
		 * when the {@link BooleanOption#createLogFiles} is activated.
		 * 
		 * <pre>
		 * SYNOPSIS
		 * </pre>
		 * 
		 * <code><b>--logDirectory</b></code> DIRECTORY_NAME
		 * 
		 * <pre>
		 * DESCRIPTION
		 * </pre>
		 * 
		 * Specify the desired directory. It could be an absolute
		 * or a relative path. At runtime, a log directory named with
		 * the current date (second precision) will be created in the log directory for each MaDKit session.
		 * E.g. /home/neo/madkit_5/logs/2012.02.23.16.23.53
		 * 
		 * <pre>
		 * DEFAULT VALUE
		 * </pre>
		 * 
		 * Default value is <i>"logs"</i>, so that a directory named
		 * "logs" will be created in the application working directory.
		 * 
		 * <pre>
		 * EXAMPLES
		 * </pre>
		 * <ul>
		 * <li>--logDirectory bin</li>
		 * <li>--logDirectory /home/neo/madkit_logs</li>
		 * </ul>
		 * 
		 * @see BooleanOption#createLogFiles
		 */
		logDirectory,

		/**
		 * the desktop frame class which should be used, default is {@link MDKDesktopFrame}
		 */
		desktopFrameClass,

		/**
		 * the directory containing the MDK i18n files
		 */
		i18nDirectory,
		/**
		 * Can be used to specify multiple properties at once,
		 * using a regular properties file
		 */
		configFile,

		/**
		 * the agent frame class which should be used by the GUI manager,
		 * default is {@link AgentFrame}
		 */
		agentFrameClass;

		/**
		 * Returns the constant's name prefixed by "<code>--</code>" so that
		 * it could interpreted as an option of the command line or
		 * in {@link Madkit#Madkit(String...)}.
		 */
		@Override
		public String toString() {
			return "--" + name();
		}

	}

	/**
	 * MaDKit options valued with a string representing a {@link Level} value.
	 * These options could be used from the command line or using the main method of MaDKit.
	 * 
	 * @author Fabien Michel
	 * @since MaDKit 5.0.0.10
	 * @version 0.9
	 * 
	 */
	public static enum LevelOption implements MadkitOption {
		/**
		 * Option defining the default agent log level for newly
		 * launched agents.
		 * Default value is "INFO". This value could be overridden
		 * individually by agents using {@link AbstractAgent#setLogLevel(Level)}.
		 * <p>
		 * Example:
		 * <ul>
		 * <li>--agentLogLevel OFF</li>
		 * <li>--agentLogLevel ALL</li>
		 * <li>--agentLogLevel FINE</li>
		 * </ul>
		 * 
		 * @see AbstractAgent#logger
		 * @see java.util.logging.Logger
		 * @see AbstractAgent#getMadkitProperty(String)
		 * @see AbstractAgent#setMadkitProperty(String, String)
		 */
		agentLogLevel,
		/**
		 * Only useful for kernel developers
		 */
		kernelLogLevel,
		/**
		 * Only useful for kernel developers
		 */
		guiLogLevel,
		/**
		 * Can be used to make MaDKit quiet
		 */
		madkitLogLevel,
		/**
		 * Option defining the default warning log level for newly
		 * launched agents.
		 * Default value is "FINE". This value could be changed
		 * individually by the agents using {@link AgentLogger#setWarningLogLevel(Level)} on their personal logger.
		 * <p>
		 * Example:
		 * <ul>
		 * <li>--warningLogLevel OFF</li>
		 * <li>--warningLogLevel ALL</li>
		 * <li>--warningLogLevel FINE</li>
		 * </ul>
		 * 
		 * @see AbstractAgent#logger
		 * @see java.util.logging.Logger
		 * @see AbstractAgent#getMadkitProperty(String)
		 * @see AbstractAgent#setMadkitProperty(String, String)
		 * @since MaDKit 5
		 */
		warningLogLevel, networkLogLevel;

		Level getValue(Properties session) {
			try {
				return Level.parse(session.getProperty(name()));
			} catch (IllegalArgumentException e) {//TODO simplify this log
				Logger.getLogger(MDK_LOGGER_NAME).log(Level.SEVERE,
						ErrorMessages.OPTION_MISUSED.toString() + " " + name() + " : " + session.getProperty(name()), e);
			}
			return Level.ALL;
		}

		/**
		 * Returns the constant's name prefixed by "<code>--</code>" so that
		 * it could interpreted as an option of the command line or
		 * in {@link Madkit#Madkit(String...)}.
		 */
		@Override
		public String toString() {
			return "--" + name();
		}

	}

}
