/* Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * - Neither the Indiana University nor the names of its contributors may be used
 *  to endorse or promote products derived from this software without specific
 *  prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package stencil.explore.ui.components.sources;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

import stencil.explore.model.sources.DBSource;

public class Database extends SourceEditor {
	private static final long serialVersionUID = 4615703311382583200L;

	private DBSource saveTarget;

	private JTextField header = new JTextField();
	private JTextField separator = new JTextField();
	private JTextField query = new JTextField();
	private JTextField connect = new JTextField();
	private JTextField driver = new JTextField();

	public Database(DBSource source) {
		this();
		setSaveTarget(source);
	}

	private Database() {
		this.add(labeledPanel("Header: ", header));
		this.add(labeledPanel("Separator: ", separator));
		this.add(labeledPanel("Query: ", query));
		this.add(labeledPanel("Connection Info: ", connect));
		this.add(labeledPanel("Driver: ", driver));
	
		FocusListener fl = new FocusListener() {
			public void focusGained(FocusEvent arg0) {/*No action taken on event.*/}
			public void focusLost(FocusEvent arg0) {saveValues();}
		};

		header.addFocusListener(fl);
		separator.addFocusListener(fl);
		query.addFocusListener(fl);
		connect.addFocusListener(fl);
		driver.addFocusListener(fl);
	}

	/**Automatically save to the given element when changes are made to the control state.*/
	public void setSaveTarget(DBSource source) {
		set(source);
		saveTarget = source;
		fireChangeEvent();
	}

	/**Perform an auto-save.*/
	public void saveValues() {
		if (saveTarget != null) {get(saveTarget);}
		this.fireChangeEvent();
	}

	/**Sets the passed file source. Sources cannot be null.*/
	public DBSource get(DBSource target) {
		assert target != null : "Cannot pass a null to 1-argument get.";

		target.setHeader(header.getText());
		target.setSeparator(separator.getText());
		target.setQuery(query.getText());
		target.setConnect(connect.getText());
		target.setDriver(driver.getText());

		return target;
	}

	/**Set the current state to match the source passed.
	 * Sources cannot be null.
	 **/
	public void set (DBSource source) {
		assert source != null : "Cannot pass a null to set.";
		
		header.setText(source.getHeader());
		separator.setText(source.getSeparator());
		query.setText(source.getQuery());
		connect.setText(source.getConnect());
		driver.setText(source.getDriver());
	}
}