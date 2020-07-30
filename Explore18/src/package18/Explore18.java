package package18;

import java.io.File;

/**
 * @author bakis
 *
 */
public class Explore18 implements Runnable {
	/**
	 * Name of directory containing the data files in the git repository
	 */
	private static final String dataDirName = "csse_covid_19_data";

	/**
	 * The name of the git repository containing the data for this application.
	 */
	private static final String repositoryName = "COVID-19";

	/**
	 * Name of the directory containing the time series files within the "data"
	 * directory within the repository.
	 */
	private static final String timeSeriesDirName = "csse_covid_19_time_series";

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
		final var repository = new File(gitDir, Explore18.repositoryName);
		if (!repository.exists()) {
			throw new RuntimeException("Repository not found:  " + repository);
		}
		if (!repository.canRead()) {
			throw new RuntimeException("Unable to read repository " + repository);
		}

		final var dataDir = new File(repository, Explore18.dataDirName);
		if (!dataDir.exists()) {
			throw new RuntimeException("data directory not found:  " + dataDir);
		}
		if (!dataDir.canRead()) {
			throw new RuntimeException("Unable to read data directory " + dataDir);
		}

		final var timeSeriesDir = new File(dataDir, Explore18.timeSeriesDirName);
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

		final var list1 = timeSeriesDir.listFiles();
		System.out.format("%n%,d files in timeSeries folder:%n", list1.length);
		for (final File s : list1) {
			System.out.println(s);
		}
	}

}
