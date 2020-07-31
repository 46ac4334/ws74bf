package package18;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * @author bakis
 *
 */
public class Explore18 implements Runnable {
	private static final String CONFIRMED_GLOBAL_FILE_NAME = "time_series_covid19_confirmed_global.csv";

	private static final String CONFIRMED_US_FILE_NAME = "time_series_covid19_confirmed_US.csv";

	/**
	 * Name of directory containing the data files in the git repository
	 */
	private static final String DATA_DIR_NAME = "csse_covid_19_data";

	private static final String DEATHS_GLOBAL_FILE_NAME = "time_series_covid19_deaths_global.csv";
	private static final String DEATHS_US_FILE_NAME = "time_series_covid19_deaths_US.csv";
	private static final String RECOVERED_GLOBAL_FILE_NAME = "time_series_covid19_recovered_global.csv";
	/**
	 * The name of the git repository containing the data for this application.
	 */
	private static final String REPOSITORY_NAME = "COVID-19";
	/**
	 * Name of the directory containing the time series files within the "data"
	 * directory within the repository.
	 */
	private static final String TIME_SERIES_DIR_NAME = "csse_covid_19_time_series";

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final var app = new Explore18();
		new Thread(app).start();
	}

	private final File confirmedGlobalFile;

	private Map<String, Integer> confirmedGlobalheaderMap;

	private List<CSVRecord> confirmedGlobalRecords;

	private final File confirmedUSFile;

	private Map<String, Integer> confirmedUSheaderMap;

	private List<CSVRecord> confirmedUSRecords;

	private final File deathsGlobalFile;

	private Map<String, Integer> deathsGlobalheaderMap;

	private List<CSVRecord> deathsGlobalRecords;

	private final File deathsUSFile;

	private Map<String, Integer> deathsUSheaderMap;

	private List<CSVRecord> deathsUSRecords;

	private final File recoveredGlobalFile;

	private Map<String, Integer> recoveredGlobalheaderMap;

	private List<CSVRecord> recoveredGlobalRecords;

	private final File timeSeriesDir;

	/**
	 * constructor
	 */
	public Explore18() {

		this.timeSeriesDir = this.getDataFolder();

		this.confirmedGlobalFile = new File(this.timeSeriesDir, Explore18.CONFIRMED_GLOBAL_FILE_NAME);
		if (this.confirmedGlobalFile.canRead()) {
			System.out.println("✔ Confirmed Global file found. " + new Date(this.confirmedGlobalFile.lastModified()));
		} else {
			System.out.println("🗙 Confirmed Global file NOT found.");
		}

		this.confirmedUSFile = new File(this.timeSeriesDir, Explore18.CONFIRMED_US_FILE_NAME);
		if (this.confirmedUSFile.canRead()) {
			System.out.println("✔ Confirmed US file found.     " + new Date(this.confirmedUSFile.lastModified()));
		} else {
			System.out.println("🗙 Confirmed US file NOT found.");
		}

		this.deathsGlobalFile = new File(this.timeSeriesDir, Explore18.DEATHS_GLOBAL_FILE_NAME);
		if (this.deathsGlobalFile.canRead()) {
			System.out.println("✔ Deaths Global file found.    " + new Date(this.deathsGlobalFile.lastModified()));
		} else {
			System.out.println("🗙 Deaths Global file NOT found.");
		}

		this.deathsUSFile = new File(this.timeSeriesDir, Explore18.DEATHS_US_FILE_NAME);
		if (this.deathsUSFile.canRead()) {
			System.out.println("✔ Deaths US file found.        " + new Date(this.deathsUSFile.lastModified()));
		} else {
			System.out.println("🗙 Deaths US file NOT found.");
		}

		this.recoveredGlobalFile = new File(this.timeSeriesDir, Explore18.RECOVERED_GLOBAL_FILE_NAME);
		if (this.recoveredGlobalFile.canRead()) {
			System.out.println("✔ Recovered Global file found. " + new Date(this.recoveredGlobalFile.lastModified()));
		} else {
			System.out.println("🗙 Recovered Global file NOT found.");
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

		} catch (final IOException e) {
			e.printStackTrace();
		}

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
		final var repository = new File(gitDir, Explore18.REPOSITORY_NAME);
		if (!repository.exists()) {
			throw new RuntimeException("Repository not found:  " + repository);
		}
		if (!repository.canRead()) {
			throw new RuntimeException("Unable to read repository " + repository);
		}

		final var dataDir = new File(repository, Explore18.DATA_DIR_NAME);
		if (!dataDir.exists()) {
			throw new RuntimeException("data directory not found:  " + dataDir);
		}
		if (!dataDir.canRead()) {
			throw new RuntimeException("Unable to read data directory " + dataDir);
		}

		final var timeSeriesDir = new File(dataDir, Explore18.TIME_SERIES_DIR_NAME);
		if (!timeSeriesDir.exists()) {
			throw new RuntimeException("timeSeries directory not found:  " + timeSeriesDir);
		}
		if (!timeSeriesDir.canRead()) {
			throw new RuntimeException("Unable to read timeSeries directory " + gitDir);
		}
		return timeSeriesDir;
	}

	@Override
	public void run() {
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

	}

}
