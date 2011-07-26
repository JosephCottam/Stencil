package stencil.explore.ui.components;

import java.awt.Component;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;

public class StatusBar extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final Font MESSAGE_FONT = new Font("Sans-serif", Font.PLAIN, 11);
	private JLabel message;
	private JLabel position;

	private String line;
	private String charNum;

	public StatusBar() {
		super();
		BorderLayout l = new BorderLayout();
		this.setLayout(l);

		message = new JLabel("Ready");
		message.setFont(MESSAGE_FONT);
		message.setBorder(BorderFactory.createEmptyBorder(0,8,0,8));

		position = new JLabel("0:0");
		position.setFont(MESSAGE_FONT);
		position.setAlignmentY(Component.LEFT_ALIGNMENT);
		position.setMinimumSize(new Dimension(100, 20));
		position.setPreferredSize(new Dimension(100, 20));

		this.add(message, BorderLayout.CENTER);
		this.add(position, BorderLayout.EAST);

	}

	public void setLineNum(int line) {
		if (line <0) {this.line = "?";}
		else {this.line = Integer.toString(line);}
		updatePosition();
	}

	public void setCharNum(int charNum) {
		if (charNum <0) {this.charNum = "?";}
		else {this.charNum = Integer.toString(charNum);}
		updatePosition();
	}

	private void updatePosition() {
		position.setText(String.format("%1$s : %2$s", line, charNum));
		message.repaint();
	}

	public void setMessage(String text) {
		message.setText(text);
		this.repaint();
	}

	public void clearMessage() {
		message.setText("");
		message.repaint();
	}
}
