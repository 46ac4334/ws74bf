package package18;

import java.io.File;

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

	/**
	 * constructor
	 */
	public Explore18() {
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
		final var timeSeriesDir = this.getDataFolder();

		final var confirmedGlobalFile = new File(timeSeriesDir, Explore18.CONFIRMED_GLOBAL_FILE_NAME);
		if (confirmedGlobalFile.canRead()) {
			System.out.println("✔ Confirmed Global file found.");
		} else {
			System.out.println("🗙 Confirmed Global file NOT found.");
		}

		final var confirmedUSFile = new File(timeSeriesDir, Explore18.CONFIRMED_US_FILE_NAME);
		if (confirmedUSFile.canRead()) {
			System.out.println("✔ Confirmed US file found.");
		} else {
			System.out.println("🗙 Confirmed US file NOT found.");
		}

		final var deathsGlobalFile = new File(timeSeriesDir, Explore18.DEATHS_GLOBAL_FILE_NAME);
		if (deathsGlobalFile.canRead()) {
			System.out.println("✔ Deaths Global file found.");
		} else {
			System.out.println("🗙 Deaths Global file NOT found.");
		}

		final var deathsUSFile = new File(timeSeriesDir, Explore18.DEATHS_US_FILE_NAME);
		if (deathsUSFile.canRead()) {
			System.out.println("✔ Deaths US file found.");
		} else {
			System.out.println("🗙 Deaths US file NOT found.");
		}

		final var recoveredGlobalFile = new File(timeSeriesDir, Explore18.RECOVERED_GLOBAL_FILE_NAME);
		if (recoveredGlobalFile.canRead()) {
			System.out.println("✔ Recovered Global file found.");
		} else {
			System.out.println("🗙 Recovered Global file NOT found.");
		}

	}

}
