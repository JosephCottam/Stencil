package stencil.util.streams.sql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import stencil.tuple.SourcedTuple;
import stencil.tuple.TupleStream;
import stencil.tuple.instances.PrototypedTuple;
import stencil.tuple.prototype.SimplePrototype;
import stencil.tuple.prototype.TuplePrototype;
import stencil.util.streams.QueuedStream;

/**Converts a query and connect string to a stream of tuples.
 * Connection will always be verified as having the correct number of columns,
 * but no other meta-data validation is performed.
 */
public class QueryTuples implements TupleStream, QueuedStream.Queable {
	protected Connection connection;
	protected Statement statement;

	protected final String name;
	protected final TuplePrototype prototype;
	protected final String[] fields;
	protected final String query;
	protected int columnCount =0;
	protected ResultSet results;


	public QueryTuples(String name, String driver, String connect, String query, String header, String separator) throws Exception {
		this.fields = header.split(separator);
		this.prototype = new SimplePrototype(fields); 
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

	public SourcedTuple next() {
		List values = new ArrayList(columnCount);

		try {results.next();}
		catch (Exception e) {throw new RuntimeException("Error advancing to next row.", e);}

		for (int i=1; i<= columnCount; i++) {
			try {values.set(i-1, results.getString(i));}
			catch (Exception e) {throw new RuntimeException(String.format("Error retrieving value %1$d for tuples.", i),e);}
		}
		return new SourcedTuple.Wrapper(name, new PrototypedTuple(prototype, values));
	}

	public void reset() throws Exception {
		results = statement.executeQuery(query);
		columnCount = results.getMetaData().getColumnCount();
	}

	public boolean hasNext() {
		try {return !results.isLast();}
		catch (Exception e) {throw new RuntimeException(e);}
	}


	public void remove() {throw new UnsupportedOperationException("Remove not supported on Query TupleStream.");}
}
