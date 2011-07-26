package stencil.explore.model.sources;

import java.io.BufferedReader;
import java.io.IOException;

import stencil.explore.model.Model;
import stencil.explore.ui.components.sources.Database;
import stencil.explore.ui.components.sources.SourceEditor;
import stencil.tuple.stream.TupleStream;
import stencil.util.streams.sql.QueryTuples;

public class DBSource extends StreamSource {
	public static final String NAME = "Database";

	private final String query;
	private final String connect;
	private final String driver;

	public DBSource(String name) {this(name, 0, "","","");}
	
	public DBSource(String name, int size, String query, String connect, String driver) {
		super(name, size);
		this.query = query;
		this.connect = connect;
		this.driver = driver;
	}

	public TupleStream getStream(Model context) throws Exception {
		return new QueryTuples(name, tupleSize, driver, connect, query);
	}

	public SourceEditor getEditor() {return new Database(this);}

	public boolean isReady() {
		return query != null && !query.equals("") &&
			   connect != null && !connect.equals("") &&
			   driver != null && !driver.equals("");
	}

	public DBSource name(String name) {
		if (this.name.equals(name)) {return this;}
		return new DBSource(name, tupleSize, query, connect, driver);
	}

	public DBSource tupleSize(int size) {
		if (size == this.tupleSize) {return this;}
		return new DBSource(name, size, query, connect, driver);
	}

	
	public String query() {return query;}
	public DBSource query(String query) {
		if (this.query.equals(query)) {return this;}
		return new DBSource(name, tupleSize, query, connect, driver);
	}

	public String connect() {return connect;}
	public DBSource connect(String connect) {
		if (this.connect.equals(connect)) {return this;}
		return new DBSource(name, tupleSize, query, connect, driver);
	}


	public String driver() {return driver;}
	public DBSource driver(String driver) {
		if (this.driver.equals(driver)) {return this;}
		return new DBSource(name, tupleSize, query, connect, driver);
	}


	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("STREAM: ");
		b.append(NAME);
		b.append("\n");
		b.append("NAME: ");
		b.append(name);
		b.append("\n");
		b.append("TUPLE_SIZE: ");
		b.append(tupleSize);
		b.append("\n");
		b.append("CONNECT: ");
		b.append(connect);
		b.append("\n");
		b.append("QUERY: ");
		b.append(query);
		b.append("\n");
		b.append("DRIVER: ");
		b.append(driver);
		b.append("\n");
		return b.toString();
	}

	public DBSource restore(BufferedReader input) throws IOException {
		String line = input.readLine();
		DBSource result = this;
		while (line != null && !line.startsWith("STREAM") && !line.equals("")) {
			if (line.startsWith("NAME")) {
				String name = line.substring(line.indexOf(":") +2);
				result = result.name(name);
			} else if (line.startsWith("TUPLE_SIZE")) {
				String size = line.substring(line.indexOf(":")+2);
				result = result.tupleSize(Integer.parseInt(size));
			} else if (line.startsWith("CONNECT")) {
				String connect = line.substring(line.indexOf(":") +2);
				result = result.connect(connect);
			} else if (line.startsWith("QUERY")) {
				String query = line.substring(line.indexOf(":") +2);
				result = result.query(query);
			} else if (line.startsWith("DRIVER")) {
				String driver = line.substring(line.indexOf(":") +2);
				result = result.driver(driver);
			}
			input.mark(100);
			line = input.readLine();
		}
		input.reset();
		return result;
	}
}

