package dso.tools.chat.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

import dso.tools.chat.Helpers;

/**
 * The Class <code>DatabaseManager</code> brings the support for connecting to a
 * local SQLite database.
 */
public class DatabaseManager {
	/**
	 * The filename of the database.
	 */
	public static final String DB_FILE_NAME = "db.sqlite";

	/**
	 * If the file does not exist in the preferences directory of the
	 * application, then it is created.
	 * 
	 * @return a <code>File</code> object pointing to the database file.
	 */
	public static File getDatabaseFile() {
		File dbFile = new File(Helpers.getPreferencesDirFile(), DB_FILE_NAME);
		if (!dbFile.exists())
			try {
				dbFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return dbFile;
	}

	private static Session session;

	/**
	 * Returns a session for querying the database.
	 * 
	 * @return a session for querying the database.
	 */
	public static Session currentSession() {
		if (session == null)
			openSession();
		return session;
	}

	private static void openSession() {
		File dbFile = getDatabaseFile();

		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:"
					+ dbFile.getAbsolutePath());
			Statement stat = conn.createStatement();
			conn.setAutoCommit(true); // only required if autocommit state not
										// known
			stat.executeUpdate("PRAGMA synchronous = OFF;");
			Configuration conf = new AnnotationConfiguration();
			conf.setProperty("hibernate.connection.url", "jdbc:sqlite:"
					+ dbFile.getAbsolutePath());
			session = conf.configure().buildSessionFactory().openSession(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		System.out.println("db-connection established with file: "
				+ dbFile.getAbsolutePath());
	}

	/**
	 * Closes the connection to the database file. This method should be called
	 * when the program exits.
	 */
	public static void close() {
		session.close();
	}

	/**
	 * Saves an object persistently in the database.
	 * <p>
	 * The Object which can be saved must be defined in the hibernate.cfg.xml
	 * file.
	 * 
	 * @param toSave
	 *            the object to save
	 */
	public static void save(Object toSave) {
		Session session = currentSession();
		Transaction tx = session.beginTransaction();
		session.save(toSave);
		tx.commit();
	}

	/**
	 * Deletes an object from the database.
	 * 
	 * @param toDelete
	 *            the object to delete
	 */
	public static void delete(Object toDelete) {
		Session session = currentSession();
		Transaction tx = session.beginTransaction();
		session.delete(toDelete);
		tx.commit();
	}

	/**
	 * The function <code>update</code> can be used to persistently save a data
	 * object, if there was a change to it.
	 * <p>
	 * If the data object with the id does not exist, it will be created. So it
	 * is also possible to use this method for saving a data object for first
	 * time.
	 * 
	 * @param toUpdate
	 *            the object to save or update
	 */
	public static void update(Object toUpdate) {
		Session session = currentSession();
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(toUpdate);
		tx.commit();
	}
}