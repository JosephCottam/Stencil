package stencil.explore.ui.components.sources;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

import stencil.explore.model.sources.DBSource;

public class Database extends SourceEditor {
	private static final long serialVersionUID = 4615703311382583200L;

	private final JTextField size = new JTextField();
	private final JTextField query = new JTextField();
	private final JTextField connect = new JTextField();
	private final JTextField driver = new JTextField();

	public Database(DBSource source) {
		this();
		set(source);
	}

	private Database() {
		super("");
		this.add(labeledPanel("Size: ", size));
		this.add(labeledPanel("Query: ", query));
		this.add(labeledPanel("Connection Info: ", connect));
		this.add(labeledPanel("Driver: ", driver));
	
		FocusListener fl = new FocusListener() {
			public void focusGained(FocusEvent arg0) {/*No action taken on event.*/}
			public void focusLost(FocusEvent arg0) {saveValues();}
		};

		size.addFocusListener(fl);
		query.addFocusListener(fl);
		connect.addFocusListener(fl);
		driver.addFocusListener(fl);
	}

	/**Sets the passed file source. Sources cannot be null.*/
	public DBSource get() {
		DBSource s = new DBSource(name, Integer.parseInt(size.getText()), query.getText(), connect.getText(), driver.getText());
		return s;
	}

	/**Set the current state to match the source passed.
	 * Sources cannot be null.
	 **/
	public void set (DBSource source) {
		assert source != null : "Cannot pass a null to set.";
		super.set(source);
		size.setText(Integer.toString(source.size()));
		query.setText(source.query());
		connect.setText(source.connect());
		driver.setText(source.driver());
	}
}