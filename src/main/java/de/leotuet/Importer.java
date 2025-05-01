package de.leotuet;

import java.sql.Connection;

public class Importer {
	private static final String[] USER_SELECTION = new String[] {
			"Tutor",
			"Schüler",
	};
	private final Connection conn;


	public Importer(Connection conn) {
		this.conn = conn;
	}

	public void importCSV() {

	}

	public void importManual() {

	}

}
