package package18;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * @author bakis
 *
 */
public class TrackCovid19 extends JFrame implements Runnable {

	public enum ADMIN {
		country, county, province, state
	}

	/**
	 * @author bakis
	 *
	 */
	private class ControlPanel extends JInternalFrame {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * @param string
		 * @param parent The button which caused this panel to be displayed. Should be
		 *               <code>null</code> if the panel was not created in response to a
		 *               button action.
		 */
		public ControlPanel(final String string, final JButton2 parent) {
			super(string);
			this.setIconifiable(true);
			this.init();
		}

		private void init() {
			this.addComponentListener(new ComponentListener() {

				@Override
				public void componentHidden(final ComponentEvent e) {
				}

				@Override
				public void componentMoved(final ComponentEvent e) {
				}

				/**
				 * Assume that when the user resizes the component, the new size becomes the new
				 * preferred size. When components are re-arranged by the
				 * <code>arrangeWindows()</code> method, they retain their sizes.
				 */
				@Override
				public void componentResized(final ComponentEvent e) {
					final var component = e.getComponent();
					component.setPreferredSize(component.getSize());
				}

				@Override
				public void componentShown(final ComponentEvent e) {
				}
			});
		}

	}

	/**
	 * The icon for this application.
	 *
	 * @author bakis
	 *
	 */
	public static final class Icon1 extends BufferedImage {
		/**
		 * Constructor. Paints the image. Currently a white capital "V" on a green
		 * background.
		 *
		 * @param width     Width of the icon in pixels. Current design requires a value
		 *                  of 128.
		 * @param height    Height of the icon in pixels. Current design requires a
		 *                  value of 128.
		 * @param imageType The image type.
		 */
		public Icon1(final int width, final int height, final int imageType) {
			super(width, height, imageType);
			final var g = this.createGraphics();
			g.setColor(Color.GREEN);
			g.fillRect(0, 0, width, height);
			final var size = 140;
			g.setFont(new Font("Times new Roman", Font.BOLD, size));
			g.setColor(Color.BLACK);
			final var x = 6;
			final var y = 110;
			g.drawString("C", x, y);
		}
	}

	/**
	 * @author bakis
	 *
	 */
	public class JButton2 extends JToggleButton implements Comparable<JButton2> {

		private static final long serialVersionUID = 1L;

		private final ADMIN admin;

		/**
		 * The panel on which this button is displayed
		 */
		private final ControlPanel parentPanel;

		private final JPopupMenu popup = new JPopupMenu();

		/**
		 * <p>
		 * The only public constructor.
		 * </p>
		 * <p>
		 * The button has a built-in {@link java.awt.event#ActionListener} which invokes
		 * {@link TrackCovid19#button2Action(String, ControlPanel)}, passing to it the
		 * text prefixed with the <code>charOn</code> or <code>charOff</code> character
		 * depending on whether this action turned the button on or off.
		 * </p>
		 *
		 * @param text    Text to appear on the button
		 * @param charOn  The prefix to the text indicating the button is selected
		 * @param charOff The prefix to the text indicating the button is not selected
		 * @param parent  The ControlPanel object on which this button appears.
		 * @param adm     The type of the administrative region.
		 */
		public JButton2(final String text, final char charOn, final char charOff, final ControlPanel parent,
				final ADMIN adm) {
			super(text);
			this.admin = adm;
			this.setToolTipText(this.admin.toString());
			this.parentPanel = parent;
			this.addActionListener(
					e -> TrackCovid19.this.button2Action((this.isSelected() ? charOn : charOff) + text, this));

			this.setComponentPopupMenu(this.popup);
			final var menuItem = new JMenuItem("Add to pool");
			menuItem.addActionListener(e -> TrackCovid19.this.addToPool(JButton2.this));
			this.popup.add(menuItem);
			final var menuItem2 = new JMenuItem("Remove from pool");
			menuItem2.addActionListener(e -> TrackCovid19.this.removeFromPool(JButton2.this));
			this.popup.add(menuItem2);
			final var menuItem3 = new JMenuItem("Analyze now");
			menuItem3.addActionListener(e -> TrackCovid19.this.analyzeNow(JButton2.this));
			this.popup.add(menuItem3);

		}

		@Override
		public int compareTo(final JButton2 o) {
			if (o.getText() != null && this.getText() != null) {
				return this.getText().compareTo(o.getText());
			}
			return 0;
		}

		/**
		 * @return the parentPanel
		 */
		public ControlPanel getParentPanel() {
			return this.parentPanel;
		}

	}

	@SuppressWarnings("unused")
	private class PopulationTable {
		private final Map<String, Long> combinedKeyMap = new HashMap<>();
		private java.util.List<CSVRecord> records;
		private ControlPanel tablePanel;

		/**
		 * Constructor
		 *
		 * @param lookupTable A CSV file listing populations of regions
		 */
		public PopulationTable(final File lookupTable) {
			if (!lookupTable.exists()) {
				JOptionPane.showInternalMessageDialog(TrackCovid19.this.scrollPane, "File does not exist",
						lookupTable.getName(), JOptionPane.ERROR_MESSAGE);
			}
			if (!lookupTable.canRead()) {
				JOptionPane.showInternalMessageDialog(TrackCovid19.this.scrollPane,
						"File exists but is not read-accessible", lookupTable.getName(), JOptionPane.ERROR_MESSAGE);
			}
			FileReader reader;
			try {
				reader = new FileReader(lookupTable);
				final var parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
				this.records = parser.getRecords();
				parser.close();

				final var rowData = new Vector<Vector<String>>();
				final var columnNames = new Vector<>(
						Arrays.asList("Admin2", "Province_State", "Country_Region", "Combined_Key", "Population"));

				for (final CSVRecord record : this.records) {
					final var popString = record.get("Population");
					if (popString != null && !popString.isBlank()) {
						final Long population = Long.parseLong(popString);
						final var combined_Key = record.get("Combined_Key");
						this.combinedKeyMap.put(combined_Key, population);
						final var country_Region = record.get("Country_Region");
						final var admin2 = record.get("Admin2");
						final var province_State = record.get("Province_State");
						final var row = new Vector<>(
								Arrays.asList(admin2, province_State, country_Region, combined_Key, popString));
						rowData.add(row);
					}
				}

				final var table = new JTable(rowData, columnNames);
				this.tablePanel = new ControlPanel("populations", null);
				this.tablePanel.setTitle("Populations");
				final var scrollPane = new JScrollPane(table);
				scrollPane.setPreferredSize(new Dimension(300, 400));
				this.tablePanel.getContentPane().add(scrollPane);
				this.tablePanel.pack();
				this.tablePanel.setResizable(true);

				this.tablePanel.setIconifiable(true);
				this.tablePanel.setClosable(true);

			} catch (final IOException e) {
				e.printStackTrace();
			}

		}

		/**
		 * @param combinedKey The combined key for the region of interest
		 * @return The population of the regions specified by the combined key.
		 */
		public long getPopulationFromCombinedKey(final String combinedKey) {
			final var population = this.combinedKeyMap.get(combinedKey);
			return population == null ? 0 : population;
		}

		/**
		 * @return the tablePanel The population table displayable in a JDesktop.
		 */
		public ControlPanel getTablePanel() {
			return this.tablePanel;
		}
	}// End of PopulationTable class definition.

	private static final String CONFIRMED_GLOBAL_FILE_NAME = "time_series_covid19_confirmed_global.csv";

	private static final String CONFIRMED_US_FILE_NAME = "time_series_covid19_confirmed_US.csv";

	/**
	 * Name of directory containing the data files in the git repository
	 */
	private static final String DATA_DIR_NAME = "csse_covid_19_data";

	/**
	 * Parser for column names which represent dates
	 */
	private static final DateFormat dateInstance = DateFormat.getDateInstance(DateFormat.SHORT);

	/**
	 * Pattern for column names which represent dates: one or two digits followed by
	 * a slash, another one or two digits, a slash, and a final sequence of one or
	 * two digits.
	 */
	private static final String datePattern = "\\d{1,2}\\/\\d{1,2}\\/\\d{1,2}";

	private static final String DEATHS_GLOBAL_FILE_NAME = "time_series_covid19_deaths_global.csv";

	private static final String DEATHS_US_FILE_NAME = "time_series_covid19_deaths_US.csv";

	/**
	 * The icon for this application.
	 */
	public final static Image iconImage = new Icon1(128, 128, BufferedImage.TYPE_INT_RGB);

	private static final String RECOVERED_GLOBAL_FILE_NAME = "time_series_covid19_recovered_global.csv";

	/**
	 * The name of the git repository containing the data for this application.
	 */
	private static final String REPOSITORY_NAME = "COVID-19";

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the directory containing the time series files within the "data"
	 * directory within the repository.
	 */
	private static final String TIME_SERIES_DIR_NAME = "csse_covid_19_time_series";

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final var app = new TrackCovid19();
//		Plotter6165i.setRoundrangeDebug(false);
		EventQueue.invokeLater(app);
	}

	private boolean buildStateHeaderList = true;

	private final File confirmedGlobalFile;

	private Map<String, Integer> confirmedGlobalheaderMap;

	private List<CSVRecord> confirmedGlobalRecords;

	private final File confirmedUSFile;

	private Map<String, Integer> confirmedUSheaderMap;

	private List<CSVRecord> confirmedUSRecords;

	private Map<String, Set<Long>> countryNames;

	private final Set<ControlPanel> countyWindows = new HashSet<>();

	private final List<String> dateHeaderList = new ArrayList<>();

	private final File deathsGlobalFile;

	private Map<String, Integer> deathsGlobalheaderMap;

	private List<CSVRecord> deathsGlobalRecords;

	private final File deathsUSFile;

	private Map<String, Integer> deathsUSheaderMap;

	private List<CSVRecord> deathsUSRecords;

	private final Dimension defaultPlotterSize = new Dimension(468, 375);

	/**
	 * The desktop on which the charts and tables will be displayed
	 */
	private final JDesktopPane desktop = new JDesktopPane();

	/**
	 * The background color for the table-top: a dull cork-board color:
	 */
	private final Color desktopColor = Color.getHSBColor(0.1f, 0.25f, 0.8f);

	/**
	 * The popup menu for the desktop.
	 */
	private final JPopupMenu desktopPopup;

	private long firstDateMillis;

	private final Map<String, Date> headerDateMap = new HashMap<>();

	private Map<String, Integer> lookupheaderMap;

	@SuppressWarnings("unused")
	private List<CSVRecord> lookupRecords;

	private final File lookupTable;

	public Set<JButton2> pool = new TreeSet<>();

	private package18.TrackCovid19.ControlPanel poolFrame;

	private final List<JLabel> poolLabels = new ArrayList<>();

	private final JPanel poolPanel = new JPanel();

	/**
	 * An interface for retrieving population figures from the lookup table file.
	 */
	private final PopulationTable populationTable;

	/**
	 * The currently visible, active province windows
	 */
	private final Set<ControlPanel> provinceWindows = new HashSet<>();

	private final File recoveredGlobalFile;

	private Map<String, Integer> recoveredGlobalheaderMap;

	private List<CSVRecord> recoveredGlobalRecords;

	/**
	 * The scroll-pane for the desktop.
	 */
	private final JScrollPane scrollPane;

	private Map<String, List<Long>> stateNames;

	private final File timeSeriesDir;

	/**
	 * constructor
	 */
	public TrackCovid19() {

		if (GraphicsEnvironment.isHeadless()) {
			throw new RuntimeException("This application requires a display.");
		}
		final var graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final var defaultScreenDevice = graphicsEnvironment.getDefaultScreenDevice();
		final var maximumWindowBounds = graphicsEnvironment.getMaximumWindowBounds();

		final var fullScreenSupported = defaultScreenDevice.isFullScreenSupported();

		if (fullScreenSupported) {
			this.setUndecorated(true);
			this.setPreferredSize(new Dimension(maximumWindowBounds.width, maximumWindowBounds.height));
			defaultScreenDevice.setFullScreenWindow(this);
		} else {
			this.setPreferredSize(new Dimension(1200, 800));
		}
		this.setIconImage(TrackCovid19.iconImage);
		this.setTitle("COVID-19 Statistics");
		this.desktop.setPreferredSize(new Dimension(1800, 4000));
		this.desktop.setBackground(this.desktopColor);
		this.scrollPane = new JScrollPane(this.desktop);

		/*
		 * Set scroll pane horizontal and vertical scrolling speeds.
		 */
		this.setScrollSpeeds(this.scrollPane, 50);

		this.setContentPane(this.scrollPane);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		this.pack();
		this.setVisible(true);

		this.desktopPopup = this.buildDesktopPopup();
		this.desktop.setComponentPopupMenu(this.desktopPopup);

		this.timeSeriesDir = this.getDataFolder();

		this.confirmedGlobalFile = new File(this.timeSeriesDir, TrackCovid19.CONFIRMED_GLOBAL_FILE_NAME);
		if (this.confirmedGlobalFile.canRead()) {
			System.out.println("✔ Confirmed Global file found. " + new Date(this.confirmedGlobalFile.lastModified()));
		} else {
			System.out.println("🗙 Confirmed Global file NOT found.");
		}

		this.confirmedUSFile = new File(this.timeSeriesDir, TrackCovid19.CONFIRMED_US_FILE_NAME);
		if (this.confirmedUSFile.canRead()) {
			System.out.println("✔ Confirmed US file found.     " + new Date(this.confirmedUSFile.lastModified()));
		} else {
			System.out.println("🗙 Confirmed US file NOT found.");
		}

		this.deathsGlobalFile = new File(this.timeSeriesDir, TrackCovid19.DEATHS_GLOBAL_FILE_NAME);
		if (this.deathsGlobalFile.canRead()) {
			System.out.println("✔ Deaths Global file found.    " + new Date(this.deathsGlobalFile.lastModified()));
		} else {
			System.out.println("🗙 Deaths Global file NOT found.");
		}

		this.deathsUSFile = new File(this.timeSeriesDir, TrackCovid19.DEATHS_US_FILE_NAME);
		if (this.deathsUSFile.canRead()) {
			System.out.println("✔ Deaths US file found.        " + new Date(this.deathsUSFile.lastModified()));
		} else {
			System.out.println("🗙 Deaths US file NOT found.");
		}

		this.recoveredGlobalFile = new File(this.timeSeriesDir, TrackCovid19.RECOVERED_GLOBAL_FILE_NAME);
		if (this.recoveredGlobalFile.canRead()) {
			System.out.println("✔ Recovered Global file found. " + new Date(this.recoveredGlobalFile.lastModified()));
		} else {
			System.out.println("🗙 Recovered Global file NOT found.");
		}

		final var parentFile = this.timeSeriesDir.getParentFile();
		this.lookupTable = new File(parentFile, "UID_ISO_FIPS_LookUp_Table.csv");

		try {
			var reader = new FileReader(this.confirmedGlobalFile);
			var parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
			this.confirmedGlobalRecords = parser.getRecords();
			parser.close();
			this.confirmedGlobalheaderMap = parser.getHeaderMap();

			reader = new FileReader(this.confirmedUSFile);
			parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
			this.confirmedUSRecords = parser.getRecords();
			parser.close();
			this.confirmedUSheaderMap = parser.getHeaderMap();

			reader = new FileReader(this.deathsGlobalFile);
			parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
			this.deathsGlobalRecords = parser.getRecords();
			parser.close();
			this.deathsGlobalheaderMap = parser.getHeaderMap();

			reader = new FileReader(this.deathsUSFile);
			parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
			this.deathsUSRecords = parser.getRecords();
			parser.close();
			this.deathsUSheaderMap = parser.getHeaderMap();

			reader = new FileReader(this.recoveredGlobalFile);
			parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
			this.recoveredGlobalRecords = parser.getRecords();
			parser.close();
			this.recoveredGlobalheaderMap = parser.getHeaderMap();

			reader = new FileReader(this.lookupTable);
			parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
			this.lookupRecords = parser.getRecords();
			parser.close();
			this.lookupheaderMap = parser.getHeaderMap();

		} catch (final IOException e) {
			e.printStackTrace();
		}

		this.populationTable = new PopulationTable(this.lookupTable);

		final var populationTablePanel = this.populationTable.getTablePanel();
		this.desktop.add(populationTablePanel);
		populationTablePanel.setVisible(true);
		populationTablePanel.setPreferredSize(new Dimension(800, 300));
		populationTablePanel.pack();
		try {
			populationTablePanel.setIcon(true);
		} catch (final PropertyVetoException e1) {
			e1.printStackTrace();
		}

	}// End of constructor

	protected void addToPool(final JButton2 jButton2) {
		System.out.format("Adding to pool %s, %s%n", jButton2.getText(), jButton2.getParentPanel().getName());
		this.pool.add(jButton2);
//		for (final JButton2 b : this.pool) {
//			System.out.format(" \u2022 %s, %s%n", b.getText(), b.getParentPanel().getName());
//		}
		this.showPool();
	}

	@SuppressWarnings("unused")
	private void analyze() {
		Toolkit.getDefaultToolkit().beep();
		JOptionPane.showInternalMessageDialog(this.scrollPane, "Analysis not yet implemented.");
	}

	private void analyzeCountry(final JButton2 button) {
		final var countryName = button.getText();
		final var cumulativeCounts = this.getCountsForCountry(countryName);
		if (cumulativeCounts == null) {
			System.out.format("counts are null for country %s%n", countryName);
		}
		if (cumulativeCounts.isEmpty()) {
			System.out.format("counts are empty for country %s%n", countryName);
		}
		this.showPlot(countryName, cumulativeCounts);
		return;
	}

	private void analyzeCounty(final JButton2 button) {
		Toolkit.getDefaultToolkit().beep();
		JOptionPane.showInternalMessageDialog(this.scrollPane, "County analysis not yet implemented.");
	}

	public Object analyzeNow(final JButton2 button) {

		switch (button.admin) {
		case country:
			this.analyzeCountry(button);
			break;
		case county:
			this.analyzeCounty(button);
			break;
		case province:
			this.analyzeProvince(button);
			break;
		case state:
			this.analyzeState(button);
			break;
		default:
			throw new RuntimeException("Unimplemented case: " + button.admin);
		}

		return null;
	}

	private void analyzeProvince(final JButton2 button) {
		Toolkit.getDefaultToolkit().beep();
		JOptionPane.showInternalMessageDialog(this.scrollPane, "Province analysis not yet implemented.");
	}

	private void analyzeState(final JButton2 button) {
		final var stateName = button.getText();
		final var cumulativeCounts = this.getCountsForState(stateName);
		if (cumulativeCounts == null) {
			System.out.format("counts are null for state %s%n", stateName);
		}
		if (cumulativeCounts.isEmpty()) {
			System.out.format("counts are empty for state %s%n", stateName);
		}
		this.showPlot(stateName, cumulativeCounts);
		return;

	}

	private void arrangeWindows() {
		this.desktop.setLayout(new FlowLayout());
		final var layout = this.desktop.getLayout();
		if (layout instanceof FlowLayout) {
			this.repaint();
		}
		layout.layoutContainer(this.desktop);
		this.desktop.setLayout(null);
	}

	private JPopupMenu buildDesktopPopup() {
		final var popup = new JPopupMenu();

		final var arrangeItem = new JMenuItem("Arrange");
		arrangeItem.addActionListener(e -> TrackCovid19.this.arrangeWindows());
		popup.add(arrangeItem);

		final var minimizeItem = new JMenuItem("Minimize");
		minimizeItem.addActionListener(e -> TrackCovid19.this.minimize());
		popup.add(minimizeItem);

		popup.addSeparator();

		final var quitItem = new JMenuItem("Quit");
		quitItem.addActionListener(e -> TrackCovid19.this.shutdown());
		popup.add(quitItem);
		return popup;
	}

	/**
	 * Action when a button is pushed. Receives the button&rsquo;s text prefixed
	 * with that button&rsquo;s <code>charOn</code> or <code>charOff</code>
	 * character depending on whether the action turned the button on or off.
	 *
	 * @param text
	 */
	private void button2Action(final String text, final JButton2 jButton2) {

		// The first character of text is a code indicating the type of button and
		// action, handled by the switch statement below

		final var firstChar = text.charAt(0);
		final var buttonText = text.substring(1);

		switch (firstChar) {
		case '+':// selected country button
			this.buttonCountrySelected(buttonText, jButton2);
			break;
		case '-':// deselected country button
			this.buttonCountryDeselected(buttonText, jButton2);
			break;
		case '0':// selected state button
			this.buttonStateSelected(buttonText, jButton2);
			break;
		case '1':// de-selected state button
			this.buttonStateDeselected(buttonText, jButton2);
			break;
		case '2':// selected county button
			this.buttonCountySelected(buttonText, jButton2);
			break;
		case '3':// de-selected county button
			this.buttonCountyDeselecte(buttonText, jButton2);
			break;
		case '4':// selected province button
			this.buttonProvinceSelected(buttonText, jButton2);
			break;
		case '5':// de-selected province button
			this.buttonProvinceDeselected(buttonText, jButton2);
			break;
		default:
			throw new RuntimeException("unimplemented case:  '" + firstChar + "' " + buttonText);
		}

	}

	/**
	 * @param countryName
	 * @param jButton2
	 */
	private void buttonCountryDeselected(final String countryName, final JButton2 jButton2) {

		if (countryName.equalsIgnoreCase("US")) {// de-select all selected states
			final Set<ControlPanel> delenda = new HashSet<>();
			for (final ControlPanel window : this.countyWindows) {
				delenda.add(window);
			}
			for (final ControlPanel d : delenda) {
				this.countyWindows.remove(d);
				d.dispose();
			}

		}

		final Set<ControlPanel> delenda = new HashSet<>();
		for (final ControlPanel window : this.provinceWindows) {
			if (window.getName().equalsIgnoreCase(countryName)) {
				delenda.add(window);
			}
		}
		for (final ControlPanel d : delenda) {
			this.provinceWindows.remove(d);
			d.dispose();
		}
	}

	/**
	 * @param countryName
	 * @param jButton2
	 */
	private void buttonCountrySelected(final String countryName, final JButton2 jButton2) {
		final var recordIndices = this.countryNames.get(countryName);

		if (recordIndices.size() > 1) {// If there is more than one province
			final var showProvinces = this.showProvinces(recordIndices, this.confirmedGlobalRecords, jButton2);
			showProvinces.setName(countryName);
			this.provinceWindows.add(showProvinces);
		} else if (countryName.equalsIgnoreCase("US")) {
			final var showStates = this.showStates(this.confirmedUSRecords, countryName, jButton2);
			showStates.setName(countryName);
			this.provinceWindows.add(showStates);
		} else {
			this.analyzeCountry(jButton2);
		}
	}

	/**
	 * @param buttonText Name of county
	 * @param jButton2
	 */
	private void buttonCountyDeselecte(final String buttonText, final JButton2 jButton2) {
	}

	/**
	 * @param countyName Name of county
	 * @param jButton2
	 */
	private void buttonCountySelected(final String countyName, final JButton2 jButton2) {
		this.analyzeCounty(jButton2);
	}

	/**
	 * @param buttonText name of province
	 */
	private void buttonProvinceDeselected(final String buttonText, final JButton2 jButton2) {
	}

	/**
	 * @param provinceName name of province
	 * @param jButton2
	 */
	private void buttonProvinceSelected(final String provinceName, final JButton2 jButton2) {
		this.analyzeProvince(jButton2);
	}

	/**
	 * @param buttonText name of state
	 * @param jButton2
	 */
	private void buttonStateDeselected(final String buttonText, final JButton2 jButton2) {
		final Set<ControlPanel> delenda = new HashSet<>();
		for (final ControlPanel window : this.countyWindows) {
			if (window.getName().equalsIgnoreCase(buttonText)) {
				delenda.add(window);
			}
		}
		for (final ControlPanel d : delenda) {
			this.countyWindows.remove(d);
			d.dispose();
		}
	}

	/**
	 * @param stateName name of state
	 * @param jButton2  The state button
	 */
	private void buttonStateSelected(final String stateName, final JButton2 jButton2) {

		final var recordIndices = this.stateNames.get(stateName);

		final var countiesWindow = this.showCounties(recordIndices, this.confirmedUSRecords, jButton2);
		countiesWindow.setName(stateName);

		this.countyWindows.add(countiesWindow);

	}

	/**
	 * @param headerMap
	 * @param records
	 * @return Map in which the key is a country name and the value is a set of row
	 *         indices containing that country name.
	 */
	private Map<String, Set<Long>> getCountryNames(final Map<String, Integer> headerMap,
			final List<CSVRecord> records) {
		final var columnIndex = headerMap.get("Country/Region");
		final Map<String, Set<Long>> result = new TreeMap<>();
		for (final CSVRecord r : records) {
			final var rowIndex = r.getRecordNumber();
			final var countryName = r.get(columnIndex);

			final var value = result.containsKey(countryName) ? result.get(countryName) : new TreeSet<Long>();
			value.add(rowIndex);
			result.put(countryName, value);
		}
		return result;
	}

	private List<Integer> getCountsForCountry(final String countryName) {

		final var integer = this.confirmedGlobalheaderMap.get("Country/Region");
		final List<Integer> result = new ArrayList<>();

		for (final CSVRecord r : this.confirmedGlobalRecords) {
			final var string = r.get(integer);
			if (string.equalsIgnoreCase(countryName)) {

				for (final String dateHeader : this.dateHeaderList) {
					final var count = r.get(dateHeader);
					result.add(Integer.parseInt(count));
				}
				break;
			} else {
			}
		}
		return result;
	}

	private List<Integer> getCountsForState(final String stateName) {

		final var columnIndex = this.confirmedUSheaderMap.get("Province_State");
		final List<Integer> dailyCounts = new ArrayList<>();

		final Map<Date, Integer> dateCountMap = new TreeMap<>();

		for (final CSVRecord record : this.confirmedUSRecords) {
			// search through all US records. Pick the ones for the requested state
			final var string = record.get(columnIndex);
			if (string.equalsIgnoreCase(stateName)) {// If this record is for a county in the requested state:
				// sum over all counties.
				for (final String dateHeader : this.dateHeaderList) {// iterate through all date column headers
					final var dailyCountForCounty = Integer.parseInt(record.get(dateHeader));
					final var date = this.headerDateMap.get(dateHeader);
					final Integer sum = dateCountMap.containsKey(date) ? dateCountMap.get(date) : 0;
					final var newSum = sum + dailyCountForCounty;
					dateCountMap.put(date, newSum);
				}
			}
		}

		// Copy sums from dateCountMap to dailyCounts
		for (final Date key : dateCountMap.keySet()) {
			final var count = dateCountMap.get(key);
			dailyCounts.add(count);
		}

		return dailyCounts;
	}

	/**
	 * @return The directory containing the csse_covid_19_time_series files in the
	 *         local replica of the JHU csse_covid_19_data database.
	 */
	private File getDataFolder() {
		final var home = System.getProperty("user.home");
		if (home == null || home.isEmpty()) {
			throw new RuntimeException("Home directory name not found.");
		}
		final var homedir = new File(home);
		if (!homedir.exists()) {
			throw new RuntimeException("Home directory not found:  " + homedir);
		}
		if (!homedir.canRead()) {
			throw new RuntimeException("Unable to read home directory " + homedir);
		}
		final var gitDir = new File(homedir, "git");
		if (!gitDir.exists()) {
			throw new RuntimeException("Git directory not found:  " + gitDir);
		}
		if (!gitDir.canRead()) {
			throw new RuntimeException("Unable to read git directory " + gitDir);
		}
		final var repository = new File(gitDir, TrackCovid19.REPOSITORY_NAME);
		if (!repository.exists()) {
			throw new RuntimeException("Repository not found:  " + repository);
		}
		if (!repository.canRead()) {
			throw new RuntimeException("Unable to read repository " + repository);
		}

		final var dataDir = new File(repository, TrackCovid19.DATA_DIR_NAME);
		if (!dataDir.exists()) {
			throw new RuntimeException("data directory not found:  " + dataDir);
		}
		if (!dataDir.canRead()) {
			throw new RuntimeException("Unable to read data directory " + dataDir);
		}

		final var timeSeriesDir = new File(dataDir, TrackCovid19.TIME_SERIES_DIR_NAME);
		if (!timeSeriesDir.exists()) {
			throw new RuntimeException("timeSeries directory not found:  " + timeSeriesDir);
		}
		if (!timeSeriesDir.canRead()) {
			throw new RuntimeException("Unable to read timeSeries directory " + gitDir);
		}
		return timeSeriesDir;
	}

	/**
	 * @param headerMap
	 * @param records
	 * @return Map in which the key is a country name and the value is a set of row
	 *         indices containing that country name.
	 */
	private Map<String, List<Long>> getStateNames(final Map<String, Integer> headerMap, final List<CSVRecord> records) {
		final var columnIndex = headerMap.get("Province_State");
		final Map<String, List<Long>> result = new TreeMap<>();
		for (final CSVRecord r : records) {
			final var rowIndex = r.getRecordNumber();
			final var stateName = r.get(columnIndex);

			final var value = result.containsKey(stateName) ? result.get(stateName) : new ArrayList<Long>();
			value.add(rowIndex);
			result.put(stateName, value);
		}

		return result;
	}

	/**
	 * Generates a list of hypothesized underlying rate values, of which the values
	 * given in the argument are assumed to be noisy samples.
	 *
	 * @param dailyCounts
	 * @return
	 */
	private List<Double> hypothesize(final List<Integer> dailyCounts) {
		final var sevenDayAverage = new SevenDayAverage();
		final var result = sevenDayAverage.apply(dailyCounts);
		return result;
	}

	private void minimize() {
		this.setExtendedState(Frame.ICONIFIED);
	}

	private void printHeaderMap(final Map<String, Integer> headerMap) {
		System.out.format("%5s %s%n", "value", "key");
		System.out.format("%5s %s%n", "\u2500\u2500\u2500\u2500\u2500", "\u2500\u2500\u2500");
		var firstDate = true;
		for (final String key : headerMap.keySet()) {
			if (!key.matches(TrackCovid19.datePattern)) {
				System.out.format("%,5d %s%n", headerMap.get(key), key);
			} else {
				if (firstDate) {
					try {
						this.firstDateMillis = TrackCovid19.dateInstance.parse(key).getTime();
					} catch (final ParseException e) {
						e.printStackTrace();
					}
					System.out.format("%5s %s%n", "\u2022", " ");
					System.out.format("%5s %s%n", "\u2022", "Dates");
					System.out.format("%5s %s%n", "\u2022", " ");
					firstDate = false;
				}
				if (this.buildStateHeaderList) {
					this.dateHeaderList.add(key);
					try {
						this.headerDateMap.put(key, TrackCovid19.dateInstance.parse(key));
					} catch (final ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}
		this.buildStateHeaderList = false;
	}

	public Object removeFromPool(final JButton2 jButton2) {
		this.pool.remove(jButton2);
		this.showPool();
		return null;
	}

	@Override
	public void run() {

		final var countryButtons = new ControlPanel("Countries", null);
		this.desktop.add(countryButtons);
		countryButtons.setPreferredSize(new Dimension(860, 800));
		countryButtons.setResizable(false);
		final var countryButtonsPanel = new JPanel();
		countryButtonsPanel.setLayout(new FlowLayout());
		final var label = new JLabel("Countries");
		label.setPreferredSize(new Dimension(850, 55));
		label.setFont(label.getFont().deriveFont(42f));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		countryButtonsPanel.add(label);
		countryButtons.setContentPane(countryButtonsPanel);
		countryButtons.pack();
		countryButtons.setVisible(true);
		System.out.println();
		System.out.format("%,8d confirmed global records%n", this.confirmedGlobalRecords.size());
		System.out.format("%,8d confirmed us records%n", this.confirmedUSRecords.size());
		System.out.format("%,8d deaths global records%n", this.deathsGlobalRecords.size());
		System.out.format("%,8d deaths us records%n", this.deathsUSRecords.size());
		System.out.format("%,8d recovered global records%n", this.recoveredGlobalRecords.size());
		System.out.println();
		System.out.format("%,8d entries in confirmedGlobalheaderMap%n", this.confirmedGlobalheaderMap.size());
		System.out.format("%,8d entries in confirmedUSheaderMap%n", this.confirmedUSheaderMap.size());
		System.out.format("%,8d entries in deathsGlobalheaderMap%n", this.deathsGlobalheaderMap.size());
		System.out.format("%,8d entries in deathsUSheaderMap%n", this.deathsUSheaderMap.size());
		System.out.format("%,8d entries in recoveredGlobalheaderMap%n", this.recoveredGlobalheaderMap.size());
		System.out.println();

		System.out.println("Global header map");
		this.printHeaderMap(this.confirmedGlobalheaderMap);
		System.out.println();
		System.out.println("US header map");
		this.printHeaderMap(this.confirmedUSheaderMap);
		System.out.println();
		System.out.println("lookup header map");
		this.printHeaderMap(this.lookupheaderMap);
		System.out.println();

		this.countryNames = this.getCountryNames(this.confirmedGlobalheaderMap, this.confirmedGlobalRecords);
		for (final String country : this.countryNames.keySet()) {
			countryButtonsPanel.add(new JButton2(country, '+', '-', countryButtons, ADMIN.country));
		}

		this.stateNames = this.getStateNames(this.confirmedUSheaderMap, this.confirmedUSRecords);

	}

	/**
	 * Set scroll pane horizontal and vertical scrolling speeds.
	 *
	 * @param scrollPane     The scroll pane.
	 * @param blockIncrement The amount to scroll for each mouse wheel click.
	 */
	private void setScrollSpeeds(final JScrollPane scrollPane, final int blockIncrement) {
		scrollPane.getVerticalScrollBar().setBlockIncrement(blockIncrement);
		scrollPane.getHorizontalScrollBar().setBlockIncrement(blockIncrement);
		scrollPane.addMouseWheelListener(e -> {
			final var preciseWheelRotation = e.getPreciseWheelRotation();
			final var shiftDown = e.isShiftDown();
			if (shiftDown) {
				final var horizontalScrollBar = scrollPane.getHorizontalScrollBar();
				horizontalScrollBar
						.setValue((int) (horizontalScrollBar.getValue() + blockIncrement * preciseWheelRotation));
			} else {
				final var verticalScrollBar = scrollPane.getVerticalScrollBar();
				verticalScrollBar
						.setValue((int) (verticalScrollBar.getValue() + blockIncrement * preciseWheelRotation));
			}
		});
	}

	private ControlPanel showCounties(final List<Long> recordIndices, final List<CSVRecord> records,
			final JButton2 parent) {
		final var countyButtons = new ControlPanel("Select County", parent);
		this.desktop.add(countyButtons);
		countyButtons.setPreferredSize(new Dimension(460, 600));
		countyButtons.setResizable(false);
		final var iterator = recordIndices.iterator();
		if (!iterator.hasNext()) {
			throw new RuntimeException("Internal error:  recordIndices is empty");
		}

		final Long index = iterator.next() - 1;
		final var csvRecord = records.get(index.intValue());
		final var countryName = csvRecord.get("Province_State");
		final var title = "Counties of " + parent.getText();
		countyButtons.setTitle(title);
		final var countyButtonsPanel = new JPanel();
		countyButtonsPanel.setLayout(new FlowLayout());
		final var label = new JLabel(title);
		label.setPreferredSize(new Dimension(450, 55));
		label.setFont(label.getFont().deriveFont(30f));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		countyButtonsPanel.add(label);
		final var provinceName = csvRecord.get("Admin2");
		final var name1a = provinceName.isEmpty() ? countryName : provinceName;
		countyButtonsPanel.add(new JButton2(name1a, '2', '3', countyButtons, ADMIN.county));
		countyButtons.setContentPane(countyButtonsPanel);
		while (iterator.hasNext()) {
			final var recordIndex1 = iterator.next().intValue() - 1;
			final var csvRecord2 = records.get(recordIndex1);
			final var name1 = csvRecord2.get("Admin2");
			final var name1b = name1.isEmpty() ? countryName : name1;
			countyButtonsPanel.add(new JButton2(name1b, '2', '3', countyButtons, ADMIN.county));
		}
		countyButtons.pack();
		countyButtons.setVisible(true);
		this.arrangeWindows();
		return countyButtons;
	}

	/**
	 * @param name             The title to appear on the plot
	 * @param cumulativeCounts The values to be plotted
	 */
	private void showPlot(final String name, final List<Integer> cumulativeCounts) {
		final var cumulCountsIterator = cumulativeCounts.iterator();
		final var plotter = new Plotter6165i();
		plotter.setIconifiable(true);
		plotter.setPreferredSize(this.defaultPlotterSize);
		this.desktop.add(plotter);
		plotter.setXDateOrigin(this.firstDateMillis);
		final var cumulPath = new java.awt.geom.Path2D.Double();
		cumulPath.moveTo(0, 0);
		final var dailyPath = new java.awt.geom.Path2D.Double();
		dailyPath.moveTo(0, 0);
		final var hypoPath = new Path2D.Double();
		hypoPath.moveTo(0, 0);
		var i = 0;
		Integer oldCumul = 0;
		final List<Integer> dailyCounts = new ArrayList<>();
		while (cumulCountsIterator.hasNext()) {
			final var cumulCount = cumulCountsIterator.next();
			final var dailyCount = cumulCount - oldCumul;
			dailyCounts.add(dailyCount);
			cumulPath.lineTo(i, cumulCount);
			dailyPath.lineTo(i, dailyCount);
			oldCumul = cumulCount;
			++i;
		}
		final var hypothesizedRates = this.hypothesize(dailyCounts);
		final var iterator = hypothesizedRates.iterator();
		i = 0;
		while (iterator.hasNext()) {
			final double hypoRate = iterator.next();
			hypoPath.lineTo(i, hypoRate);
			++i;
		}
		plotter.setMainPlotPath(cumulPath);
		plotter.addPlotPath(Color.red.darker(), dailyPath, new BasicStroke(1));
		plotter.addPlotPath(Color.green.darker(), hypoPath, new BasicStroke(1.5f));
		plotter.setIconifiable(true);
		plotter.setPlotTitle(name);
		plotter.setTitle(name);
		plotter.pack();
		plotter.setVisible(true);
		this.arrangeWindows();
	}

	private ControlPanel showPool() {
		if (this.poolFrame == null) {
			this.poolFrame = new ControlPanel("Pool", null);
			this.desktop.add(this.poolFrame);
			this.poolFrame.setPreferredSize(new Dimension(460, 600));
			this.poolFrame.setResizable(false);
			final var title = "pool";
			this.poolFrame.setTitle(title);
			this.poolPanel.setLayout(new FlowLayout());
			final var label = new JLabel(title);
			label.setPreferredSize(new Dimension(450, 55));
			label.setFont(label.getFont().deriveFont(30f));
			label.setHorizontalAlignment(SwingConstants.CENTER);
			this.poolPanel.add(label);
			this.poolFrame.setContentPane(this.poolPanel);
			this.poolFrame.pack();
			this.poolFrame.setVisible(true);
			this.arrangeWindows();
		}
		for (final JLabel l : this.poolLabels) {
			this.poolPanel.remove(l);
		}
		this.poolLabels.clear();

		final var border = BorderFactory.createBevelBorder(BevelBorder.RAISED);
		for (final JButton2 p : this.pool) {
			final var e = new JLabel(p.getText());
			e.setBorder(border);
			this.poolLabels.add(e);
		}
		for (final JLabel l : this.poolLabels) {
			this.poolPanel.add(l);
		}
		this.poolPanel.setLayout(new FlowLayout());
		final var layout = this.poolPanel.getLayout();
		if (layout instanceof FlowLayout) {
			this.repaint();
		}
		layout.layoutContainer(this.poolPanel);
		this.poolPanel.setLayout(null);
		this.poolFrame.repaint();
		return this.poolFrame;

	}

	/**
	 * Invoked when a country has more than one province.
	 *
	 */
	/**
	 * @param recordIndices Set of row indices for which buttons are to be shown in
	 *                      the new panel.
	 * @param records       csv table rows
	 * @param parent        the button which was activated to create this
	 *                      ControlPanel
	 * @return the new ControlPanel
	 */
	private ControlPanel showProvinces(final Set<Long> recordIndices, final List<CSVRecord> records,
			final JButton2 parent) {
		final var provinceButtons = new ControlPanel("Select Province", parent);
		this.desktop.add(provinceButtons);
		provinceButtons.setPreferredSize(new Dimension(460, 600));
		provinceButtons.setResizable(false);
		final var iterator = recordIndices.iterator();
		if (!iterator.hasNext()) {
			throw new RuntimeException("Internal error:  recordIndices is empty");
		}
		final Long index = iterator.next() - 1;
		final var csvRecord = records.get(index.intValue());
		final var countryName = csvRecord.get("Country/Region");
		final var title = "Provinces of " + countryName;
		provinceButtons.setTitle(title);
		final var provinceButtonsPanel = new JPanel();
		provinceButtonsPanel.setLayout(new FlowLayout());
		final var label = new JLabel(title);
		label.setPreferredSize(new Dimension(450, 55));
		label.setFont(label.getFont().deriveFont(30f));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		provinceButtonsPanel.add(label);
		final var provinceName = csvRecord.get("Province/State");
		final var name1a = provinceName.isEmpty() ? countryName : provinceName;
		provinceButtonsPanel.add(new JButton2(name1a, '4', '5', provinceButtons, ADMIN.province));
		provinceButtons.setContentPane(provinceButtonsPanel);
		while (iterator.hasNext()) {
			final var recordIndex1 = iterator.next().intValue() - 1;
			final var csvRecord2 = records.get(recordIndex1);
			final var name1 = csvRecord2.get("Province/State");
			final var name1b = name1.isEmpty() ? countryName : name1;
			provinceButtonsPanel.add(new JButton2(name1b, '4', '5', provinceButtons, ADMIN.province));
		}
		provinceButtons.pack();
		provinceButtons.setVisible(true);
		this.arrangeWindows();
		return provinceButtons;
	}

	/**
	 * @param records     Records of confirmed US file
	 * @param countryName
	 * @return
	 */
	private ControlPanel showStates(final List<CSVRecord> records, final String countryName, final JButton2 jButton2) {
		if (!"US".contentEquals(countryName)) {
			throw new RuntimeException("Internal error:  country name is not \"US\"");
		}
		final var stateButtons = new ControlPanel("Select State", jButton2);
		this.desktop.add(stateButtons);
		stateButtons.setPreferredSize(new Dimension(460, 600));
		stateButtons.setResizable(false);
		final var iterator = this.stateNames.keySet().iterator();
		if (!iterator.hasNext()) {
			throw new RuntimeException("Internal error:  stateNames is empty");
		}

		final var title = "States of " + countryName;
		stateButtons.setTitle(title);
		final var stateButtonsPanel = new JPanel();
		stateButtons.setContentPane(stateButtonsPanel);
		stateButtonsPanel.setLayout(new FlowLayout());
		final var label = new JLabel(title);
		label.setPreferredSize(new Dimension(450, 55));
		label.setFont(label.getFont().deriveFont(30f));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		stateButtonsPanel.add(label);

		while (iterator.hasNext()) {
			final var stateName = iterator.next();
			final var name1a = stateName.isEmpty() ? countryName : stateName;
			stateButtonsPanel.add(new JButton2(name1a, '0', '1', stateButtons, ADMIN.state));
		}

		stateButtons.pack();
		stateButtons.setVisible(true);
		this.arrangeWindows();
		return stateButtons;

	}

	private void shutdown() {
		/*
		 * Ask for confirmation before shutting down
		 */
		final var optionType = JOptionPane.YES_NO_OPTION;
		final var title = "Confirm";
		final var message = "Exit?";
		Toolkit.getDefaultToolkit().beep();
		final var answer = JOptionPane.showInternalConfirmDialog(this.scrollPane, message, title, optionType);
		if (answer == JOptionPane.YES_OPTION) {
			this.dispose();
		}
	}
}
