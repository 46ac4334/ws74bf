package package18;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
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

import package18.SevenDayAverage.EndPad;
import package18.SevenDayAverage.StartPad;

/**
 * @author 46ac4334
 *
 */
public class TrackCovid19 extends JFrame implements Runnable {

	/**
	 * Type of administrative division
	 *
	 * @author 46ac4334
	 *
	 */
	public enum ADMIN {
		country, county, province, state
	}

	/**
	 * Information about a geographic area. The area is defined by up to three names
	 * of type {@link String}:
	 * <dl>
	 * <dt>Country/Region</dt>
	 * <dd>Required. Generally the name of a country. Examples: US, Germany.</dd>
	 * <dt>Province/State</dt>
	 * <dd>Optional, but required if <code>Admin2</code> is specified. Examples: New
	 * York, Baden-Wurttemberg.</dd>
	 * <dt>Admin2</dt>
	 * <dd>Currently only used to specify a county within a U.S. state. Examples:
	 * Westchester, ‎Multnomah.</dd>
	 * </dl>
	 *
	 * @author 46ac4334
	 *
	 */
	@SuppressWarnings("unused")
	private static class Area {

		/**
		 * <p>
		 * A <code>Key</code> instance specifies an administrative division and consists
		 * of three names of type {@link String}:
		 * <ul>
		 * <li><code>country_Region</code></li>
		 * <li><code>province_State</code></li>
		 * <li><code>admin2</code>.</li>
		 * </ul>
		 * </p>
		 * <p>
		 * If any of these fields is blank or null, the key matches only rows in which
		 * the corresponding cell is empty. If the value is an asterisk "*", it matches
		 * all rows.
		 * </p>
		 * <p>
		 * Instances of this type are comparable, with the <code>country_Region</code>
		 * being the primary sort key, followed by <code>province_State</code> and then
		 * <code>admin2</code>.
		 * </p>
		 *
		 * @author 46ac4334
		 *
		 */
		private static class Key implements Comparable<Key> {

			private static String getAdmin2(final CSVRecord record) {
				return record.isMapped("Admin2") ? record.get("Admin2") : null;
			}

			/**
			 * @param record
			 * @return
			 */
			public static String getCountry(final CSVRecord record) {
				return record.isMapped("Country/Region") ? record.get("Country/Region")
						: record.isMapped("Country_Region") ? record.get("Country_Region") : null;
			}

			/**
			 * @param record
			 * @return
			 */
			public static String getState(final CSVRecord record) {
				return record.isMapped("Province/State") ? record.get("Province/State")
						: record.isMapped("Province_State") ? record.get("Province_State") : null;
			}

			/**
			 * Name of the County in a state in U.S.
			 */
			private final String admin2;

			/**
			 * Name of the country or region
			 */
			private final String country_Region;

			/**
			 * The name of the province or state
			 */
			private final String province_State;

			public Key(final CSVRecord record) {
				this(Key.getAdmin2(record), Key.getState(record), Key.getCountry(record));
			}

			/**
			 * Constructor
			 *
			 * @param admin2         The county
			 * @param province_State The state or province
			 * @param country_Region The country
			 */
			public Key(final String admin2, final String province_State, final String country_Region) {
				this.admin2 = admin2 == null || admin2.isBlank() ? null : admin2;
				this.province_State = province_State == null || province_State.isBlank() ? null : province_State;
				this.country_Region = country_Region == null || country_Region.isBlank() ? null : country_Region;
			}

			/**
			 * <p>
			 * Compare two keys, with the country as the primary comparison key, followed by
			 * province or state, followed by county. <b>null</b> is treated as an empty
			 * string.
			 * </p>
			 * <p>
			 * Note that comparing as equal is not the same as matching. In particular, a
			 * field specified as "*" matches any content, but compares equal only to
			 * another field containing "*".
			 * </p>
			 */
			@Override
			public int compareTo(final Key other) {

				// Compare country names first
				final String thisCountry = this.country_Region == null ? "" : this.country_Region;
				final String otherCountry = other.country_Region == null ? "" : other.country_Region;
				final int cCountry = thisCountry.compareTo(otherCountry);

				if (cCountry != 0) {
					// If country names differ, return that comparison
					return cCountry;

				} else {
					// But if the country names are the same, compare state/province names
					final String thisState = this.province_State == null ? "" : this.province_State;
					final String otherState = other.province_State == null ? "" : other.province_State;
					final int cProvince = thisState.compareTo(otherState);

					if (cProvince != 0) {
						// If state or province names differ, sort by those names
						return cProvince;

					} else {
						// If state or province names also are the same, compare county names
						final String thisCounty = this.admin2 == null ? "" : this.admin2;
						final String otherCounty = other.admin2 == null ? "" : other.admin2;
						return thisCounty.compareTo(otherCounty);
					}

				}
			}// end of compareTo

			/**
			 * @param records the records to be searched
			 * @return the subset of the argument records which match this <code>Key</code>
			 */
			public Collection<CSVRecord> getMatchingRecords(final Collection<CSVRecord> records) {
				final Collection<CSVRecord> result = new HashSet<>();
				for (final CSVRecord record : records) {
					if (this.matches(record)) {
						result.add(record);
					}
				}
				return result;

			}

			/**
			 * @param record A <code>CSVRecord</code> object
			 * @return true if the given record matches this key.
			 */
			public boolean matches(final CSVRecord record) {

				final String state = Key.getState(record);
				final String country = Key.getCountry(record);
				final String admin2 = record.isMapped("Admin2") ? record.get("Admin2") : null;

				final boolean countryMatches = this.regionNameMatch(country, this.country_Region);
				final boolean stateMatches = this.regionNameMatch(state, this.province_State);
				final boolean admin2Matches = this.regionNameMatch(admin2, this.admin2);

				return countryMatches && stateMatches && admin2Matches;
			}

			/**
			 * @param yours The name of a region being compared
			 * @param mine  The corresponding name in this key
			 * @return <code>true</code> if either both arguments are <code>null</code> or
			 *         both arguments are non-<code>null</code> and then either the second
			 *         argument is an asterisk or both strings are equal.
			 */
			private boolean regionNameMatch(final String yours, final String mine) {
				final boolean nameMatches = yours == null && mine == null
						|| yours != null && mine != null && (mine.contentEquals("*") || mine.contentEquals(yours));
				return nameMatches;
			}

		}// end of class Key

		/**
		 * The cumulative number of confirmed cases at each date
		 */
		private List<Long> confirmed;

		/**
		 * The dates for which data is provided
		 */
		private List<Long> dates;

		/**
		 * The cumulative number of deaths at each date
		 */
		private List<Long> deaths;

		/**
		 * The first date for which data is provided
		 */
		private long firstDateMillis;

		/**
		 * The set of keys which define this area.
		 */
		private final Set<Key> keys = new HashSet<>();

		/**
		 * The population of the area
		 */
		private long population;

		/**
		 * The cumulative number of recovered patients at each date
		 */
		private List<Long> recovered;

		/**
		 * Constructor from the names of the counties, states, and countries.
		 *
		 *
		 * @param admin2         County name(s).
		 * @param province_State State name(s).
		 * @param country_Region Country name(s).
		 */
		// TODO this is not useful. The argument should be a list of Keys.
		public Area(final Object admin2, final Object province_State, final Object country_Region) {
			@SuppressWarnings("unchecked")
			final Collection<String> admin2C = admin2 instanceof String ? Arrays.asList(admin2.toString())
					: (Collection<String>) admin2;

			@SuppressWarnings("unchecked")
			final Collection<String> province_StateC = province_State instanceof String
					? Arrays.asList(province_State.toString())
					: (Collection<String>) province_State;

			@SuppressWarnings("unchecked")
			final Collection<String> country_RegionC = admin2 instanceof String
					? Arrays.asList(country_Region.toString())
					: (Collection<String>) country_Region;

			for (final String country_Region2 : country_RegionC) {
				for (final String province_State2 : province_StateC) {
					for (final String admin22 : admin2C) {
						this.keys.add(new Key(admin22, province_State2, country_Region2));
					}
				}
			}
		}
//
//		/**
//		 * Returns the name of the county.
//		 *
//		 * @return the admin2
//		 */
//		public Collection<String> getAdmin2() {
//			return this.keys.admin2;
//		}

		/**
		 * Returns a list containing the cumulative count of confirmed cases at each
		 * date.
		 *
		 * @return the confirmed
		 */
		public List<Long> getConfirmed() {
			return Collections.unmodifiableList(this.confirmed);
		}
//
//		/**
//		 * @return the country_Region
//		 */
//		public String getCountry() {
//			return this.key.country_Region;
//		}

//		/**
//		 * @return the country_Region
//		 */
//		public String getCountry_Region() {
//			return this.key.country_Region;
//		}
//
//		/**
//		 * @return the admin2
//		 */
//		public String getCounty() {
//			return this.key.admin2;
//		}

		/**
		 * @return the dates
		 */
		public List<Long> getDates() {
			return Collections.unmodifiableList(this.dates);
		}

		/**
		 * @return the deaths
		 */
		public List<Long> getDeaths() {
			return Collections.unmodifiableList(this.deaths);
		}

		/**
		 * @return the population
		 */
		public long getPopulation() {
			return this.population;
		}
//
//		/**
//		 * @return the province_State
//		 */
//		public String getProvince() {
//			return this.key.province_State;
//		}
//
//		/**
//		 * @return the province_State
//		 */
//		public String getProvince_State() {
//			return this.key.province_State;
//		}

		/**
		 * @return the recovered
		 */
		public List<Long> getRecovered() {
			return Collections.unmodifiableList(this.recovered);
		}

//		/**
//		 * @return the country_Region
//		 */
//		public String getRegion() {
//			return this.key.country_Region;
//		}
//
//		/**
//		 * @return the province_State
//		 */
//		public String getState() {
//			return this.key.province_State;
//		}

		/**
		 * @param index     the day index relative to the start date
		 * @param confirmed the confirmed count to set.
		 */
		public void setConfirmed(final int index, final long confirmed) {
			if (index < this.confirmed.size()) {
				this.confirmed.set(index, confirmed);
			} else {
				final String msg = String.format("index = %,d but list size is %,d", index, this.confirmed.size());
				throw new IllegalArgumentException(msg);
			}

		}

		/**
		 * @param dates the dates to set
		 */
		public void setDates(final List<Long> dates) {
			this.dates = dates;
		}

		/**
		 * @param deaths the deaths to set
		 */
		public void setDeaths(final List<Long> deaths) {
			this.deaths = deaths;
		}

		/**
		 * @param population the population to set
		 */
		public void setPopulation(final long population) {
			this.population = population;
		}

		/**
		 * @param recovered the recovered to set
		 */
		public void setRecovered(final List<Long> recovered) {
			this.recovered = recovered;
		}
	}

	/**
	 * @author 46ac4334
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
	 * @author 46ac4334
	 *
	 */
	public class GetDataForRegion {

		public GetDataForRegion(final String country, final String provinceState, final String admin2) {
			// TODO Auto-generated constructor stub
		}

	}

	/**
	 * The icon for this application.
	 *
	 * @author 46ac4334
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
	 * @author 46ac4334
	 *
	 */
	public class JButton2 extends JToggleButton implements Comparable<JButton2> {

		private static final long serialVersionUID = 1L;

		private final ADMIN admin;

		/**
		 * The panel on which this button is displayed
		 */
		private final ControlPanel parentPanel;

		private final long population;

		private final JPopupMenu popup = new JPopupMenu();

		private final String stateName;

		public JButton2(final String text, final char charOn, final char charOff, final ControlPanel parent,
				final ADMIN adm, final String stateName) {
			this(text, charOn, charOff, parent, adm, stateName, 0);
		}

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
		 * @param text       Text to appear on the button
		 * @param charOn     The prefix to the text indicating the button is selected
		 * @param charOff    The prefix to the text indicating the button is not
		 *                   selected
		 * @param parent     The ControlPanel object on which this button appears.
		 * @param adm        The type of the administrative region.
		 * @param population The population of the unit if available, otherwise 0.
		 */
		public JButton2(final String text, final char charOn, final char charOff, final ControlPanel parent,
				final ADMIN adm, final String stateName, final long population) {
			super(text);
			this.population = population;
			this.stateName = stateName;
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
			final var menuItem3 = new JMenuItem("Show Chart");
			menuItem3.addActionListener(e -> TrackCovid19.this.analyzeNow(JButton2.this));
			this.popup.add(menuItem3);

			if (population > 0) {
				this.setToolTipText(String.format("pop. %,d", population));
			}

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

		public String getStateName() {
			return this.stateName;
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

	private final Color areaColor = Color.getHSBColor(0f, 0.05f, 0.9f);

	private boolean buildStateHeaderList = true;

	private final File confirmedGlobalFile;

	private Map<String, Integer> confirmedGlobalheaderMap;

	private List<CSVRecord> confirmedGlobalRecords;

	private final File confirmedUSFile;

	private Map<String, Integer> confirmedUSheaderMap;

	private List<CSVRecord> confirmedUSRecords;

	private Map<String, Set<Long>> countryNames;

	private final Set<ControlPanel> countyWindows = new HashSet<>();

	GetDataForRegion data = new GetDataForRegion("US", "New York", "Westchester");

	private final List<String> dateHeaderList = new ArrayList<>();

	private final File deathsGlobalFile;

	private Map<String, Integer> deathsGlobalheaderMap;

	private List<CSVRecord> deathsGlobalRecords;

	private final File deathsUSFile;

	private Map<String, Integer> deathsUSheaderMap;

	private List<CSVRecord> deathsUSRecords;

	// private final Dimension defaultPlotterSize = new Dimension(468, 375);
	private final Dimension defaultPlotterSize = new Dimension(468, 353);

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

	private Map<String, Integer> lookupHeaderMap;

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
			System.out.println("✔ Confirmed Global file found. " + new Date(this.confirmedGlobalFile.lastModified())
					+ " " + this.confirmedGlobalFile.getName());
		} else {
			System.out.println("🗙 Confirmed Global file NOT found.");
		}

		this.confirmedUSFile = new File(this.timeSeriesDir, TrackCovid19.CONFIRMED_US_FILE_NAME);
		if (this.confirmedUSFile.canRead()) {
			System.out.println("✔ Confirmed US file found.     " + new Date(this.confirmedUSFile.lastModified()) + " "
					+ this.confirmedUSFile.getName());
		} else {
			System.out.println("🗙 Confirmed US file NOT found.");
		}

		this.deathsGlobalFile = new File(this.timeSeriesDir, TrackCovid19.DEATHS_GLOBAL_FILE_NAME);
		if (this.deathsGlobalFile.canRead()) {
			System.out.println("✔ Deaths Global file found.    " + new Date(this.deathsGlobalFile.lastModified()) + " "
					+ this.deathsGlobalFile.getName());
		} else {
			System.out.println("🗙 Deaths Global file NOT found.");
		}

		this.deathsUSFile = new File(this.timeSeriesDir, TrackCovid19.DEATHS_US_FILE_NAME);
		if (this.deathsUSFile.canRead()) {
			System.out.println("✔ Deaths US file found.        " + new Date(this.deathsUSFile.lastModified()) + " "
					+ this.deathsUSFile.getName());
		} else {
			System.out.println("🗙 Deaths US file NOT found.");
		}

		this.recoveredGlobalFile = new File(this.timeSeriesDir, TrackCovid19.RECOVERED_GLOBAL_FILE_NAME);
		if (this.recoveredGlobalFile.canRead()) {
			System.out.println("✔ Recovered Global file found. " + new Date(this.recoveredGlobalFile.lastModified())
					+ " " + this.recoveredGlobalFile.getName());
		} else {
			System.out.println("🗙 Recovered Global file NOT found.");
		}

		final var parentFile = this.timeSeriesDir.getParentFile();
		this.lookupTable = new File(parentFile, "UID_ISO_FIPS_LookUp_Table.csv");
		if (this.lookupTable.canRead()) {
			System.out.println("✔ Lookup table file found.     " + new Date(this.lookupTable.lastModified()) + " "
					+ this.lookupTable.getName());
		} else {
			System.out.println("🗙 Lookup table file NOT found.");
		}

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
			this.lookupHeaderMap = parser.getHeaderMap();

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

	/**
	 *
	 * @param string               comma-separated names of header maps and records.
	 * @param headerMapsAndRecords header maps followed by csv records.
	 */
	private void analyzeCombinedKeys(final String string, final Object... headerMapsAndRecords) {
		// TODO Auto-generated method stub
		final String[] split = string.split(",");
		System.out.println(split.length + " files");
		int i = 0;
		for (final String s : split) {
			System.out.println(s);
			@SuppressWarnings("unchecked")
			final Map<String, Integer> headerMap = (Map<String, Integer>) headerMapsAndRecords[i];
			final Object combinedKeyColumn = headerMap.get("Combined_Key");
			System.out.format("  Combined_Key in column %s%n%n", combinedKeyColumn);
			++i;
		}

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
//		this.showPlot(countryName, cumulativeCounts);

//		final Plotter6165i showPlot = this.showPlot(countryName, cumulativeCounts);
//		showPlot.setCaptionText(String.format("population %,d", button.population));
//		showPlot.pack();
//		showPlot.setVisible(true);
//		showPlot.menuItemToggleLog.doClick();

//		showPlot.getPlotterPane().    setSemiLog(false);

		final Plotter6165i showNormalizedPlot = this.showNormalizedPlot(countryName, cumulativeCounts,
				button.population * 1e-5);
		showNormalizedPlot.setCaptionText(String.format("Population %,d", button.population));

		return;
	}

	private void analyzeCounty(final JButton2 button) {
		final var countyName = button.getText();
		final String stateName = button.getStateName();
		final List<Integer> cumulativeCounts = this.getCountsForCounty(countyName, stateName);
		if (cumulativeCounts == null) {
			System.out.format("counts are null for county %s%n", countyName);
		}
		if (cumulativeCounts.isEmpty()) {
			System.out.format("counts are empty for county %s%n", countyName);
		}

//		final Plotter6165i showPlot = this.showPlot(countyName + " county of " + stateName, cumulativeCounts);
//		showPlot.setCaptionText(String.format("population %,d", button.population));
//		showPlot.pack();
//		showPlot.setVisible(true);
//		showPlot.menuItemToggleLog.doClick();

//		showPlot.getPlotterPane().    setSemiLog(false);

		final Plotter6165i showNormalizedPlot = this.showNormalizedPlot(countyName + " county, " + stateName,
				cumulativeCounts, button.population * 1e-5);
		showNormalizedPlot.setCaptionText(String.format("Population %,d", button.population));
		return;
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
//		this.showPlot(String.format("%s (pop. %,d)", stateName, button.population), cumulativeCounts);

		final Plotter6165i showNormalizedPlot = this.showNormalizedPlot(stateName, cumulativeCounts,
				button.population * 1e-5);
		showNormalizedPlot.setCaptionText(String.format("Population %,d", button.population));
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

		final JMenuItem saveImageItem = new JMenuItem("Save image");
		saveImageItem.addActionListener(e -> {
			try {
				this.copyImageToFile();
			} catch (final IOException e1) {
				e1.printStackTrace();
			}
		});
		popup.add(saveImageItem);

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

	protected void copyImageToFile() throws IOException {
		final var imageWidth = this.getWidth();
		final var imageHeight = this.getHeight();
		final var bi = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

		this.paint(bi.getGraphics());

		final var outFileName = Plotter6165i.getTimeStamp() + ".png";
		final String simpleName = this.getClass().getSimpleName();
		final Component parentComponent = this.scrollPane;
		final Object message = "Edit the name of image output file:";
		final String title = "Output File Name";
		final int type = JOptionPane.QUESTION_MESSAGE;
		final Icon icon = null;
		final Object[] selectionValues = null;
		final Object initialSelectionValue = simpleName + outFileName;

		final var outFileName2 = (String) JOptionPane.showInternalInputDialog(parentComponent, message, title, type,
				icon, selectionValues, initialSelectionValue);

		if (outFileName2 == null) {
			return;
		}
		final var outDir = new File("data", "out");
		outDir.mkdirs();
		final var outFile = new File(outDir,
				outFileName2 == null || outFileName2.isBlank() ? outFileName : outFileName2);
		ImageIO.write(bi, "png", outFile);
	}

	/**
	 *
	 */
	private void createCountryButtonsPanel() {
		final var countryButtons = new ControlPanel("Countries", null);
		this.desktop.add(countryButtons);
		countryButtons.setPreferredSize(new Dimension(860, 820));
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

		this.countryNames = this.getCountryNames(this.confirmedGlobalheaderMap, this.confirmedGlobalRecords);
		for (final String country : this.countryNames.keySet()) {
			countryButtonsPanel.add(new JButton2(country, '+', '-', countryButtons, ADMIN.country, null,
					this.getPopulationOfCountry(country)));
		}
	}

	private void examineCountryRecords(final Collection<CSVRecord> countryRecords) {
		// TODO Auto-generated method stub
		long countryOnlySum = 0;
		int blankCount = 0;
		long countryProvinceSum = 0;
		long countryProvinceCasesSum = 0;
		int countryProvinceCount = 0;
		long countySum = 0;
		int countyCount = 0;
		long countyCasesSum = 0;
		long countryOnlyCasesSum = 0;
		final Iterator<CSVRecord> iterator = countryRecords.iterator();
		while (iterator.hasNext()) {
			final CSVRecord record = iterator.next();

			final boolean populationColumnExists = record.isMapped("Population");
			final int populationColumn = populationColumnExists ? record.getParser().getHeaderMap().get("Population")
					: -1;
			final int lastColumn = record.size() - 1;
			final boolean popIsLast = populationColumn == lastColumn;
			final String popString = populationColumnExists ? record.get("Population") : null;
			final String caseString = record.get(lastColumn);

			final long pop = popString == null ? -1 : popString.isBlank() ? 0 : Long.parseLong(popString);
			final long cases = caseString == null ? -1
					: caseString.isBlank() || popIsLast ? 0 : Long.parseLong(caseString);
			final String provinceStateKey = record.isMapped("Province/State") ? "Province/State" : "Province_State";
			if (record.get(provinceStateKey).isBlank()) {
				countryOnlySum += pop;
				countryOnlyCasesSum += cases;
				blankCount++;
			} else if (record.isMapped("Admin2") && record.get("Admin2").isBlank()) {
				countryProvinceSum += pop;
				countryProvinceCasesSum += cases;
				countryProvinceCount++;
			} else {
				countySum += pop;
				countyCasesSum += cases;
				countyCount++;
			}
		}
		System.out.format("                     %14s %14s%n", "population", "cases");
		System.out.format(
				"    countryOnlySum = %,14d %,14d (%,d)%ncountryProvinceSum = %,14d %,14d (%,d)%n"
						+ "              diff = %,14d %14d%n" + "             diff%% = %,14.1f%n",
				countryOnlySum, countryOnlyCasesSum, blankCount, countryProvinceSum, countryProvinceCasesSum,
				countryProvinceCount, countryOnlySum - countryProvinceSum,
				countryOnlyCasesSum - countryProvinceCasesSum,
				1e2 * (countryOnlySum - countryProvinceSum) / countryOnlySum);
		if (countyCount > 0) {
			System.out.format("         countySum = %,14d %,14d (%,d)%n", countySum, countyCasesSum, countyCount);
		}

	}

	/**
	 * @param countryMap
	 */
	private void examineCountryRecords0(final Map<String, Collection<CSVRecord>> countryMap) {
		System.out.format("%,d countries:%n", countryMap.size());
		int count = 0;// count of countries with more than one row
		for (final String countryName : countryMap.keySet()) {
			final Collection<CSVRecord> countryRecords = countryMap.get(countryName);
			final int size = countryRecords.size();
			if (size > 1) {
				++count;
				System.out.format("%n%,8d %s%n", size, countryName);
				this.examineCountryRecords(countryRecords);
			}
		}
		System.out.format("%n%,d countries with more than one entry.%n", count);
	}

	@SuppressWarnings("unused")
	private void examineKeys() {
		/*
		 * TODO the Global file does not have a combined key, but US and lookup do have
		 * combined keys. Find the differences between the combined keys constructed by
		 * getCombinedKey from the US file and the lookup file, and also between the
		 * combined keys constructed from the global file and the lookup file. Also
		 * compare each constructed combined key with the one listed in column 10 in the
		 * US and lookup files.
		 */

	}

	@SuppressWarnings("unused")
	private String getCombinedKey(final String Admin2, final String Province_State, final String Country_Region) {
		final StringBuilder sb = new StringBuilder();
		if (!Admin2.strip().isEmpty()) {
			sb.append(Admin2);
			sb.append(",");
		}
		if (!Province_State.strip().isEmpty()) {
			sb.append(Province_State);
			sb.append(",");
		}
		if (!Admin2.strip().isEmpty()) {
			sb.append(Admin2);
			sb.append(",");
		}
		if (!Country_Region.strip().isEmpty()) {
			sb.append(Country_Region);
		}
		final String result = sb.toString();
		return result;
	}

	/**
	 * @param records The CSV records to be analyzed
	 * @return a map in which each key is the name of a country and the value is a
	 *         collection of records for this country.
	 */
	private Map<String, Collection<CSVRecord>> getCountryMap(final List<CSVRecord> records) {
		final Map<String, Collection<CSVRecord>> countryMap0 = new TreeMap<>();
		final Iterator<CSVRecord> recordsIterator = records.iterator();
		while (recordsIterator.hasNext()) {
			final CSVRecord record = recordsIterator.next();
			final String country = Area.Key.getCountry(record);
			final Collection<CSVRecord> countryRecords = countryMap0.containsKey(country) ? countryMap0.get(country)
					: new HashSet<>();
			countryRecords.add(record);
			countryMap0.put(country, countryRecords);
		}
		return countryMap0;
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
		if (columnIndex == null) {
			throw new RuntimeException("no Country/Region column");
		}
		final Map<String, Set<Long>> result = new TreeMap<>();
		for (final CSVRecord r : records) {
			if (r.size() > columnIndex) {
				final var rowIndex = r.getRecordNumber();
				final var countryName = r.get(columnIndex);
				final var value = result.containsKey(countryName) ? result.get(countryName) : new TreeSet<Long>();
				value.add(rowIndex);
				result.put(countryName, value);
			}
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

		List<CSVRecord> records;

		records = this.lookupRecords;
		System.out.format("%n%s%n", "lookup");
		this.printPopulations(countryName, records);

		records = this.deathsUSRecords;
		System.out.format("%n%s%n", "deaths US");
		this.printPopulations(countryName, records);

		System.out.println();

		return result;
	}

	private List<Integer> getCountsForCounty(final String countyName, final String stateName) {
		final Integer stateColumnIndex = this.confirmedUSheaderMap.get("Province_State");
		final Integer countyColumnIndex = this.confirmedUSheaderMap.get("Admin2");
		final List<Integer> dailyCounts = new ArrayList<>();

		final Map<Date, Integer> dateCountMap = new TreeMap<>();

		for (final CSVRecord record : this.confirmedUSRecords) {
			// search through all US records. Pick the ones for the requested state and
			// county
			final var string = record.get(stateColumnIndex);
			final var string2 = record.get(countyColumnIndex);
			if (string.equalsIgnoreCase(stateName) && string2.equalsIgnoreCase(countyName)) {// If this record is for
																								// the requested county
																								// in the requested
																								// state:
				// sum over all such counties.
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
			throw new RuntimeException("Unable to read timeSeries directory " + timeSeriesDir);
		}
		return timeSeriesDir;
	}

	/**
	 * <p>
	 * Accepts the name of the region in some semi-standardized but hopefully
	 * tolerant format, returns the population of that region. Region may be:
	 * <ol>
	 * <li>state, country</li>
	 * <li>province, country</li>
	 * <li>county, state, country</li>
	 * <li>country</li>
	 * <li>&ldquo;world&rdquo;</li>
	 * </ol>
	 * Some countries have a home region and territories. In that case, for example
	 * <b>Denmark</b> refers to the home country and <b>Great Denmark</b> or
	 * <b>Greater Denmark</b> or <b>Gr. Denmark</b> includes the remote parts.
	 * </p>
	 * <p>
	 * In order to do this, the method first translates the argument into a
	 * standard, internal format, not necessarily serializable. Then it compiles a
	 * list of population-table entries to be summed, and returns the total.
	 * </p>
	 * <p>
	 * The first version will not tolerate spelling errors but will accept
	 * abbreviations in the form of truncation.
	 * </p>
	 * <p>
	 * Actually, since it will only be invoked programmatically, it need not be very
	 * tolerant.
	 * </p>
	 *
	 * @param name Name of region
	 * @return population of region
	 */
	long getPopulation(final String name) {//

		/*
		 * Compile a list of all entities mentioned
		 */
		// TODO

		/*
		 * Study: Does the total for e.g. Mexico equal the components Aguascalientes,
		 * Baja California, ... ?
		 *
		 * The relevant columns in the lookup file are: 5 Admin2, 6 Province_State, and
		 * 7 Country_Region
		 *
		 * Population is in column 11 as an integer without grouping commas or spaces.
		 *
		 */
		/*
		 * We cannot rely on the records in the source file being in any particular
		 * order. Therefore, I will generate a new Map in which each key is a country
		 * and the value is the set of all those records which have that name in the
		 * "Country_Region" column.
		 */

//-----------------------------------------------------
		System.out.format("%n===> Lookup: <===%n%n");
		this.examineCountryRecords0(this.getCountryMap(this.lookupRecords));
//-----------------------------------------------------
		System.out.format("%n===> ConfirmedGlobal: <===%n%n");
		this.examineCountryRecords0(this.getCountryMap(this.confirmedGlobalRecords));
// -----------------------------------------------------
		System.out.format("%n===> DeathsGlobal: <===%n%n");
		this.examineCountryRecords0(this.getCountryMap(this.deathsGlobalRecords));
// -----------------------------------------------------

		final long result = 0;
		return result;

	}// end of getPopulation

	/**
	 * @param country Name of the country or region. Must be specified, can not be
	 *                empty.
	 * @param state   Name of the province or state. If "*", sums over all non-blank
	 *                states. If blank, reads only rows where the Province/State
	 *                column is blank or absent.
	 * @param county  Name of the Admin2 entity. If "*", sums over all non-blank
	 *                counties. If blank, reads only rows where the Admin2 column is
	 *                blank or absent.
	 * @param records
	 * @return The total population in the area or areas specified by the first
	 *         three arguments as described above.
	 */
	private long[] getPopulation(final String country, final String state, final String county,
			final List<CSVRecord> records) {

		final List<String> msgs = new ArrayList<>();
		if (country == null) {
			msgs.add("first argument \"country\" is null.");
		} else if (country.isBlank()) {
			msgs.add("first argument \"country\" is blank.");
		}
		if (county != null && !county.isBlank() && (state == null || state.isBlank())) {
			msgs.add(String.format("County is specified as \"%s\" but state is missing.", county));
		}
		if (records == null) {
			msgs.add("Fourth argument \"records\" is null.");
		} else if (records.isEmpty()) {
			msgs.add("Fourth argument \"records\" is empty.");
		}

		if (!msgs.isEmpty()) {
			for (final String msg : msgs) {
				System.err.println(msg);
			}
			throw new IllegalArgumentException("See above message(s).");
		}

		final boolean PopulationMapped = records.get(0).isMapped("Population");
		if (!PopulationMapped) {
			msgs.add("the CSV records do not contain a \"Population\" column.");
		}
		final boolean provinceStateMapped = records.get(0).isMapped("Province/State");
		final boolean province_StateMapped = records.get(0).isMapped("Province_State");

		final boolean countryRegionMapped = records.get(0).isMapped("Country/Region");
		final boolean country_RegionMapped = records.get(0).isMapped("Country_Region");
		if (!countryRegionMapped && !country_RegionMapped) {
			msgs.add("Neither Country/Region nor Country_Region column found");
		}

		final boolean admin2Mapped = records.get(0).isMapped("Admin2");
		if (county != null && !county.isBlank() && !admin2Mapped) {
			msgs.add(String.format("County \"%s\" specified, but no Admin2 column", county));
		}

		if (state != null && !state.isBlank() && !provinceStateMapped && !province_StateMapped) {
			msgs.add(String.format("State or province \"%s\" specified, but no such column", state));
		}

		if (!msgs.isEmpty()) {
			for (final String msg : msgs) {
				System.err.println(msg);
			}
			throw new IllegalArgumentException("See above message(s).");
		}

		long result = 0;
		int count = 0;

		for (final CSVRecord r : records) {

			final boolean countryMatches = country.contentEquals("*")
					|| country.contentEquals(r.get(countryRegionMapped ? "Country/Region" : "Country_Region"));
			if (countryMatches) {
				final String stateString = province_StateMapped ? r.get("Province_State")
						: provinceStateMapped ? r.get("Province/State") : null;

				final boolean stateMatches = this.targetMatch(state, stateString);

				if (stateMatches) {
					final String countyString = admin2Mapped ? r.get("Admin2") : null;

					final boolean countyMatches = this.targetMatch(county, countyString);

					if (countyMatches) {
						final String popString = r.get("Population");
						if (popString != null && !popString.isBlank()) {
							final long pop = Long.parseLong(popString);
							result += pop;
							++count;
						}
					}
				}

			}
		}

		return new long[] { result, count };

	}// end of getPopulation(final String country, final String State, final String

	/**
	 * Calculates the population of the country by summing all entries in the lookup
	 * table file that have the specified state name in the Country_Region column
	 * and a blank in the <code>Admin2</code> column.
	 *
	 * @param countryName The name of a country.
	 * @return The population of the specified country obtained from lookup table
	 *         file.
	 */
	final long getPopulationOfCountry(final String countryName) {
		final int provinceStateIndex = this.lookupHeaderMap.get("Province_State");
		this.lookupHeaderMap.get("Admin2");
		final int countryRegionIndex = this.lookupHeaderMap.get("Country_Region");
		final int populationIndex = this.lookupHeaderMap.get("Population");
		int population = 0;

		/*
		 * TODO get population of country. Handle special cases where provinces are
		 * listed separately and homeland count may or may not include provinces.
		 */
		for (final CSVRecord record : this.lookupRecords) {
			if (countryName.equalsIgnoreCase(record.get(countryRegionIndex))
					&& record.get(provinceStateIndex).isBlank()) {
				final String populationString = record.get(populationIndex);
				population += populationString == null || populationString.isBlank() ? 0
						: Integer.parseInt(populationString);
			}
		}

		return population;
	}

	/**
	 * Calculates the population of the county by summing all entries in the lookup
	 * table file that have the specified state name in the
	 * <code>Province_State</code> column, the county name in the
	 * <code>Admin2</code> column, and "US" in the <code>Country_Region</code>
	 * column.
	 *
	 * @param stateName  The name of a state of the United States of America.
	 * @param countyName The name of a county within the specified state.
	 * @return The population of the specified county in the specified state
	 *         obtained from lookup table file.
	 */
	final long getPopulationOfCounty(final String stateName, final String countyName) {
		final int provinceStateColumn = this.lookupHeaderMap.get("Province_State");
		final int admin2Column = this.lookupHeaderMap.get("Admin2");
		final int countryRegionColumn = this.lookupHeaderMap.get("Country_Region");
		final int populationColumn = this.lookupHeaderMap.get("Population");
		int population = 0;
		for (final CSVRecord record : this.lookupRecords) {
			if (stateName.equalsIgnoreCase(record.get(provinceStateColumn))
					&& countyName.equalsIgnoreCase(record.get(admin2Column))
					&& "US".equals(record.get(countryRegionColumn))) {
				final String populationString = record.get(populationColumn);
				population += populationString == null || populationString.isBlank() ? 0
						: Integer.parseInt(populationString);
			}
		}
		return population;
	}

	/**
	 * Calculates the population of the state by summing all entries in the lookup
	 * table file that have the specified state name in the
	 * <code>Province_State</code> column, "US" in the Country_Region column and a
	 * blank in the <code>Admin2</code> column.
	 *
	 * @param stateName The name of a state of the United States of America.
	 * @return The population of the specified state obtained from lookup table
	 *         file.
	 */
	final long getPopulationOfState(final String stateName) {
		final int provinceStateIndex = this.lookupHeaderMap.get("Province_State");
		final int admin2Index = this.lookupHeaderMap.get("Admin2");
		final int countryRegionIndex = this.lookupHeaderMap.get("Country_Region");
		final int populationIndex = this.lookupHeaderMap.get("Population");
		int population = 0;
		for (final CSVRecord record : this.lookupRecords) {
			if (stateName.equalsIgnoreCase(record.get(provinceStateIndex)) && record.get(admin2Index).isBlank()
					&& "US".equals(record.get(countryRegionIndex))) {
				final String populationString = record.get(populationIndex);
				population += populationString == null || populationString.isBlank() ? 0
						: Integer.parseInt(populationString);
			}
		}
		return population;
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
	private List<Double> hypothesize(final List<Number> dailyCounts) {
		final var sevenDayAverage = new SevenDayAverage(StartPad.ZERO, EndPad.LAST_AVERAGE);
		final List<Double> result = sevenDayAverage.apply(dailyCounts);
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

	/**
	 * @param countryName
	 * @param records
	 */
	private void printPopulations(final String countryName, final List<CSVRecord> records) {
		long[] population;
		population = this.getPopulation(countryName, null, null, records);// TODO for testing only!!!
		System.out.format("1) Population of %s, null, null is %,d, count = %,d%n", countryName, population[0],
				population[1]);

		population = this.getPopulation(countryName, "*", null, records);// TODO for testing only!!!
		System.out.format("2) Population of %s, *, null is %,d, count = %,d%n", countryName, population[0],
				population[1]);

		population = this.getPopulation(countryName, "*", "*", records);// TODO for testing only!!!
		System.out.format("3) Population of %s, *, * is %,d, count = %,d%n", countryName, population[0], population[1]);
	}

	public Object removeFromPool(final JButton2 jButton2) {
		this.pool.remove(jButton2);
		this.showPool();
		return null;
	}

	@Override
	public void run() {

		this.createCountryButtonsPanel();

		System.out.println();
		System.out.format("%,8d confirmed global records%n", this.confirmedGlobalRecords.size());
		System.out.format("%,8d confirmed us records%n", this.confirmedUSRecords.size());
		System.out.format("%,8d deaths global records%n", this.deathsGlobalRecords.size());
		System.out.format("%,8d deaths us records%n", this.deathsUSRecords.size());
		System.out.format("%,8d recovered global records%n", this.recoveredGlobalRecords.size());
		System.out.format("%,8d lookup records%n", this.lookupRecords.size());
		System.out.println();
		System.out.format("%,8d entries in confirmedGlobalheaderMap%n", this.confirmedGlobalheaderMap.size());
		System.out.format("%,8d entries in confirmedUSheaderMap%n", this.confirmedUSheaderMap.size());
		System.out.format("%,8d entries in deathsGlobalheaderMap%n", this.deathsGlobalheaderMap.size());
		System.out.format("%,8d entries in deathsUSheaderMap%n", this.deathsUSheaderMap.size());
		System.out.format("%,8d entries in recoveredGlobalheaderMap%n", this.recoveredGlobalheaderMap.size());
		System.out.format("%,8d entries in lookupheaderMap%n", this.lookupHeaderMap.size());
		System.out.println();

		System.out.println("Confirmed Global header map");
		this.printHeaderMap(this.confirmedGlobalheaderMap);
		System.out.println();
		System.out.println("Confirmed US header map");
		this.printHeaderMap(this.confirmedUSheaderMap);
		System.out.println();
		System.out.println("Deaths Global header map");
		this.printHeaderMap(this.deathsGlobalheaderMap);
		System.out.println();
		System.out.println("Deaths US header map");
		this.printHeaderMap(this.deathsUSheaderMap);
		System.out.println();
		System.out.println("Recovered Global header map");
		this.printHeaderMap(this.recoveredGlobalheaderMap);
		System.out.println();
		System.out.println("lookup header map");
		this.printHeaderMap(this.lookupHeaderMap);
		System.out.println();

		this.analyzeCombinedKeys(
				"confirmedGlobalheaderMap,confirmedUSheaderMap,deathsGlobalheaderMap,"
						+ "deathsUSheaderMap,recoveredGlobalheaderMap,lookupheaderMap",
				this.confirmedGlobalheaderMap, this.confirmedUSheaderMap, this.deathsGlobalheaderMap,
				this.deathsUSheaderMap, this.recoveredGlobalheaderMap, this.lookupHeaderMap,
				this.confirmedGlobalRecords, this.confirmedUSRecords, this.deathsGlobalRecords, this.deathsUSRecords,
				this.recoveredGlobalRecords, this.lookupRecords);

		this.stateNames = this.getStateNames(this.confirmedUSheaderMap, this.confirmedUSRecords);

		this.getPopulation("Angola");// currently only does testing of methods that are under development.

//		System.exit(7);

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
		final var stateName = csvRecord.get("Province_State");
		final var title = "Counties of " + parent.getText();
		final String state_Name = parent.getText();
		countyButtons.setTitle(title);
		final var countyButtonsPanel = new JPanel();
		countyButtonsPanel.setLayout(new FlowLayout());
		final var label = new JLabel(title);
		label.setPreferredSize(new Dimension(450, 55));
		label.setFont(label.getFont().deriveFont(30f));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		countyButtonsPanel.add(label);
		final var countyName = csvRecord.get("Admin2");
		final var name1a = countyName.isEmpty() ? stateName : countyName;
		final long population = this.getPopulationOfCounty(stateName, countyName);
		countyButtonsPanel.add(new JButton2(name1a, '2', '3', countyButtons, ADMIN.county, state_Name, population));
		countyButtons.setContentPane(countyButtonsPanel);
		while (iterator.hasNext()) {
			final var recordIndex1 = iterator.next().intValue() - 1;
			final var csvRecord2 = records.get(recordIndex1);
			final var name1 = csvRecord2.get("Admin2");
			final var name1b = name1.isEmpty() ? stateName : name1;
			final long population2 = this.getPopulationOfCounty(stateName, name1);
			countyButtonsPanel
					.add(new JButton2(name1b, '2', '3', countyButtons, ADMIN.county, state_Name, population2));
		}
		countyButtons.pack();
		countyButtons.setVisible(true);
		this.arrangeWindows();
		return countyButtons;
	}

	private Plotter6165i showNormalizedPlot(final String name, final List<Integer> cumulativeCounts,
			final double norm) {
		final var cumulCountsIterator = cumulativeCounts.iterator();
		final var plotter = new Plotter6165i();

		plotter.setCaptionText("");
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
		final var barPath = new Path2D.Double();
		barPath.moveTo(0, 0);
		final var areaPath = new Path2D.Double();
		areaPath.moveTo(0, 0);
		var i = 0;
		Integer oldCumul = 0;
		final List<Number> dailyCounts = new ArrayList<>();
		while (cumulCountsIterator.hasNext()) {
			final var cumulCount = cumulCountsIterator.next();
			final var dailyCount = cumulCount - oldCumul;
			dailyCounts.add(dailyCount);
			cumulPath.lineTo(i, cumulCount / norm);
			dailyPath.lineTo(i, dailyCount / norm);
			barPath.moveTo(i, 0);
			barPath.lineTo(i, dailyCount / norm);
			areaPath.lineTo(i - 0.5, dailyCount / norm);
			areaPath.lineTo(i + 0.5, dailyCount / norm);
			oldCumul = cumulCount;
			++i;
		}
		areaPath.lineTo(i - 0.5, 0);
		areaPath.lineTo(0, 0);
		final var hypothesizedRates = this.hypothesize(dailyCounts);
		final var iterator = hypothesizedRates.iterator();
		i = 0;
		while (iterator.hasNext()) {
			final double hypoRate = iterator.next();
			hypoPath.lineTo(i, hypoRate / norm);
			++i;
		}
//		plotter.setMainPlotPath(cumulPath);
//		plotter.addPlotPath(Color.green.darker(), hypoPath, new BasicStroke(1.5f));
//		plotter.setSemiLog(true);
		plotter.setyAxisLabelText2("Daily new cases per 100K");
		plotter.setMainPlotPath(hypoPath);
		plotter.zoomToYrange(0, 30);
		plotter.addPlotPath(new BasicStroke(1.0f), areaPath, null, this.areaColor);

		plotter.setIconifiable(true);
		plotter.setPlotTitle(name);
		plotter.setTitle(name);
		plotter.pack();
		plotter.setVisible(true);
		this.arrangeWindows();

		return plotter;
	}

	/**
	 * @param name             The title to appear on the plot
	 * @param cumulativeCounts The values to be plotted
	 * @return
	 */
	@SuppressWarnings("unused")
	private Plotter6165i showPlot(final String name, final List<Integer> cumulativeCounts) {
		final var cumulCountsIterator = cumulativeCounts.iterator();
		final var plotter = new Plotter6165i();
		plotter.setCaptionText("");
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
		final List<Number> dailyCounts = new ArrayList<>();
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
//		plotter.setMainPlotPath(cumulPath);
//		plotter.addPlotPath(Color.red.darker(), dailyPath, new BasicStroke(1));
//		plotter.addPlotPath(Color.green.darker(), hypoPath, new BasicStroke(1.5f));
//		plotter.setSemiLog(true);
		plotter.setMainPlotPath(hypoPath);
		plotter.setIconifiable(true);
		plotter.setPlotTitle(name);
		plotter.setTitle(name);
		plotter.pack();
		plotter.setVisible(true);
		this.arrangeWindows();
		return plotter;
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
		provinceButtonsPanel.add(new JButton2(name1a, '4', '5', provinceButtons, ADMIN.province, provinceName));
		provinceButtons.setContentPane(provinceButtonsPanel);
		while (iterator.hasNext()) {
			final var recordIndex1 = iterator.next().intValue() - 1;
			final var csvRecord2 = records.get(recordIndex1);
			final var name1 = csvRecord2.get("Province/State");
			final var name1b = name1.isEmpty() ? countryName : name1;
			provinceButtonsPanel.add(new JButton2(name1b, '4', '5', provinceButtons, ADMIN.province, provinceName));
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
			stateButtonsPanel.add(new JButton2(name1a, '0', '1', stateButtons, ADMIN.state, stateName,
					this.getPopulationOfState(stateName)));
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

	/**
	 * Match is <code>true</code> if and only if one of these conditions is
	 * satisfied:
	 * <ul>
	 * <li><code>target</code> is empty and <code>testString</code> is empty</li>
	 * <li><code>target</code> is <code>"*"</code> and <code>testString</code> is
	 * not empty</li>
	 * <li><code>target</code> is not empty and is not <code>"*"</code>, and matches
	 * the <code>testString</code> exactly.</li>
	 * </ul>
	 * In the above description, a <code>String</code> is considered empty if and
	 * only if it is either <code>null</code> or has length zero or contains only
	 * whitespace characters.
	 *
	 * @param target     The name of the region or <code>"*"</code>
	 * @param testString The name being tested
	 * @return true if match conditions are met.
	 */
	private boolean targetMatch(final String target, final String testString) {
		final boolean stateSpecifiedAsAsterisk = target != null && target.contentEquals("*");
		final boolean stateMatchesAsterisk = stateSpecifiedAsAsterisk && testString != null && !testString.isBlank();
		final boolean stateMatchesNull = (target == null || target.isBlank())
				&& (testString == null || testString.isBlank());
		final boolean stateMatchesExactly = target != null && !target.isBlank() && testString != null
				&& !testString.isBlank() && testString.contentEquals(target);
		final boolean stateMatches = stateMatchesAsterisk || stateMatchesNull || stateMatchesExactly;
		return stateMatches;
	}

}
