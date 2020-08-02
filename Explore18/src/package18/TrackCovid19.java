package package18;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * @author bakis
 *
 */
public class TrackCovid19 extends JFrame implements Runnable {
	private class ControlPanel extends JInternalFrame {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public ControlPanel(final String string) {
			super(string);
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
			final var size = 160;
			g.setFont(new Font("Times new Roman", Font.BOLD, size));
			g.setColor(Color.WHITE);
			final var x = 6;
			final var y = 120;
			g.drawString("V", x, y);
		}
	}

	/**
	 * @author bakis
	 *
	 */
	public class JButton2 extends JToggleButton {

		private static final long serialVersionUID = 1L;

		/**
		 * @param text
		 */
		public JButton2(final String text, final char charOn, final char charOff) {
			super(text);
			this.addActionListener(
					e -> TrackCovid19.this.countryButtonPushed((this.isSelected() ? charOn : charOff) + text));
		}

	}

	private static final String CONFIRMED_GLOBAL_FILE_NAME = "time_series_covid19_confirmed_global.csv";

	private static final String CONFIRMED_US_FILE_NAME = "time_series_covid19_confirmed_US.csv";

	/**
	 * Name of directory containing the data files in the git repository
	 */
	private static final String DATA_DIR_NAME = "csse_covid_19_data";

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

	private final File confirmedGlobalFile;

	private Map<String, Integer> confirmedGlobalheaderMap;

	private List<CSVRecord> confirmedGlobalRecords;

	private final File confirmedUSFile;

	private Map<String, Integer> confirmedUSheaderMap;

	private List<CSVRecord> confirmedUSRecords;

	private Map<String, Set<Long>> countryNames;

	private final File deathsGlobalFile;

	private Map<String, Integer> deathsGlobalheaderMap;

	private List<CSVRecord> deathsGlobalRecords;

	private final File deathsUSFile;

	private Map<String, Integer> deathsUSheaderMap;

	private List<CSVRecord> deathsUSRecords;

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

	private Map<String, Integer> lookupheaderMap;

	@SuppressWarnings("unused")
	private List<CSVRecord> lookupRecords;

	private final File lookupTable;

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
	 * Action when a country button is pushed
	 *
	 * @param text
	 */
	private void countryButtonPushed(final String text) {
		final var buttonText = text.substring(1);

		// The first character of text is a code indicating the type of button and
		// action

		final var firstChar = text.charAt(0);

		switch (firstChar) {
		case '+':// selected country button
			final var recordIndices = this.countryNames.get(buttonText);
			if (recordIndices == null) {
				return;
			}
			System.out.format("selected country %s%n", buttonText);

			if (recordIndices.size() > 1) {// If there is more than one province
				final var showProvinces = this.showProvinces(recordIndices, this.confirmedGlobalRecords);
				showProvinces.setName(buttonText);
				this.provinceWindows.add(showProvinces);
			} else if (buttonText.equalsIgnoreCase("US")) {
				final var showStates = this.showStates(this.confirmedUSRecords, buttonText);
				showStates.setName(buttonText);
				this.provinceWindows.add(showStates);
			}
			break;
		case '-':// deselected country button
			System.out.format("de-selected country %s%n", buttonText);
			final Set<ControlPanel> delenda = new HashSet<>();
			for (final ControlPanel w : this.provinceWindows) {
				if (w.getName().equalsIgnoreCase(buttonText)) {
					delenda.add(w);
				}
			}
			for (final ControlPanel d : delenda) {
				this.provinceWindows.remove(d);
				d.dispose();
			}
			break;
		case '0':// selected state button
			System.out.format("selected state %s%n", buttonText);
			break;
		case '1':// de-selected state button
			System.out.format("de-selected state %s%n", buttonText);
			break;
		case '2':// selected county button
			System.out.format("selected county %s%n", buttonText);
			break;
		case '3':// de-selected county button
			System.out.format("de-selected county %s%n", buttonText);
			break;
		case '4':// selected province button
			System.out.format("selected province %s%n", buttonText);
			break;
		case '5':// de-selected province button
			System.out.format("de-selected province %s%n", buttonText);
			break;
		default:
			throw new RuntimeException("unimplemented case:  '" + firstChar + "' " + buttonText);
		}

//		if (text.startsWith("+")) {// If country button is now selected
//			final var recordIndices = this.countryNames.get(buttonText);
//			if (recordIndices == null) {
//				return;
//			}
//			if (recordIndices.size() > 1) {// If there is more than one province
//				final var showProvinces = this.showProvinces(recordIndices, this.confirmedGlobalRecords);
//				showProvinces.setName(buttonText);
//				this.provinceWindows.add(showProvinces);
//			} else if (buttonText.equalsIgnoreCase("US")) {
//				final var showStates = this.showStates(this.confirmedUSRecords, buttonText);
//				showStates.setName(buttonText);
//				this.provinceWindows.add(showStates);
//			}
//
//		} else
//
//		{// if country button is deselected
//
//			/*
//			 * If country button is de-selected, remove the provinces window.
//			 */
//			final Set<ControlPanel> delenda = new HashSet<>();
//			for (final ControlPanel w : this.provinceWindows) {
//				if (w.getName().equalsIgnoreCase(buttonText)) {
//					delenda.add(w);
//				}
//			}
//			for (final ControlPanel d : delenda) {
//				this.provinceWindows.remove(d);
//				d.dispose();
//			}
//		}

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

	private void minimize() {
		this.setExtendedState(Frame.ICONIFIED);
	}

	private void printHeaderMap(final Map<String, Integer> headerMap) {
		System.out.format("%5s %s%n", "value", "key");
		System.out.format("%5s %s%n", "\u2500\u2500\u2500\u2500\u2500", "\u2500\u2500\u2500");
		for (final String key : headerMap.keySet()) {
			if (!key.matches(TrackCovid19.datePattern)) {
				System.out.format("%,5d %s%n", headerMap.get(key), key);
			} else {
				System.out.format("%5s %s%n", "\u2022", " ");
				System.out.format("%5s %s%n", "\u2022", "Dates");
				System.out.format("%5s %s%n", "\u2022", " ");
				break;
			}
		}
	}

	@Override
	public void run() {

		final var countryButtons = new ControlPanel("Select Country");
		this.desktop.add(countryButtons);
		countryButtons.setPreferredSize(new Dimension(860, 800));
		countryButtons.setResizable(false);
		countryButtons.setTitle("Countries");
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
			countryButtonsPanel.add(new JButton2(country, '+', '-'));
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

	/**
	 * Invoked when a country has more than one province.
	 *
	 * @param recordIndices indices of all records for the selected country.
	 * @return
	 */
	private ControlPanel showProvinces(final Set<Long> recordIndices, final List<CSVRecord> records) {
		final var provinceButtons = new ControlPanel("Select Province");
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
		provinceButtonsPanel.add(new JButton2(name1a, '4', '5'));
		provinceButtons.setContentPane(provinceButtonsPanel);
		while (iterator.hasNext()) {
			final var recordIndex1 = iterator.next().intValue() - 1;
			final var csvRecord2 = records.get(recordIndex1);
			final var name1 = csvRecord2.get("Province/State");
			final var name1b = name1.isEmpty() ? countryName : name1;
			provinceButtonsPanel.add(new JButton2(name1b, '4', '5'));
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
	private ControlPanel showStates(final List<CSVRecord> records, final String countryName) {
		if (!"US".contentEquals(countryName)) {
			throw new RuntimeException("Internal error:  country name is not \"US\"");
		}
		final var stateButtons = new ControlPanel("Select State");
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
			stateButtonsPanel.add(new JButton2(name1a, '0', '1'));
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
		final var answer = JOptionPane.showInternalConfirmDialog(this.scrollPane, message, title, optionType);
		if (answer == JOptionPane.YES_OPTION) {
			this.dispose();
		}
	}

}
