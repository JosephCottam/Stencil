package stencil.util.streams.sql;

import java.sql.*;

import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;
import stencil.tuple.TupleStream;

/**Converts a query and connect string to a stream of tuples.
 * Connection will always be verified as having the correct number of columns,
 * but no other meta-data validation is performed.
 */
public class QueryTuples implements TupleStream {
	protected Connection connection;
	protected Statement statement;

	protected String name;
	protected String separator;
	protected String[] fields;
	protected String query;
	protected ResultSet results;
	protected int columnCount =0;


	public QueryTuples(String name, String driver, String connect, String query, String header, String separator) throws Exception {
		this.fields = header.split(separator);
		this.name = name;

		connection = DriverManager.connect(driver, connect);
		statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		this.query = query;

		reset();

		if (results.getMetaData().getColumnCount() != fields.length) {throw new RuntimeException("Query does not return as many columns as field specified in header.");}
	}

	/**Closes connection.
	 * Connection cannot be reset after this, it must be recreated.
	 **/
	public void close() throws Exception {
		try {
			results.close();
			statement.close();
			connection.close();
		} catch (Exception e) {
			throw new Exception("Error closing SQL stream.", e);
		}
	}

	public Tuple next() {
		String[] values = new String[columnCount];

		try {results.next();}
		catch (Exception e) {throw new RuntimeException("Error advancing to next row.", e);}

		for (int i=1; i<= columnCount; i++) {
			try {values[i-1] = results.getString(i);}
			catch (Exception e) {throw new RuntimeException(String.format("Error retrieving value %1$d for tuples.", i),e);}
		}
		return new PrototypedTuple(name, fields, values);
	}

	public void reset() throws Exception {
		results = statement.executeQuery(query);
		columnCount = results.getMetaData().getColumnCount();
	}

	public boolean hasNext() {
		try {return !results.isLast();}
		catch (Exception e) {throw new RuntimeException(e);}
	}


	public boolean ready() {
		return hasNext();
	}

	public void remove() {throw new UnsupportedOperationException("Remove not supported on Query TupleStream.");}
}
