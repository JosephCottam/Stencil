package stencil.modules;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.util.*;
import stencil.module.util.ann.*;
import stencil.types.Converter;
import stencil.interpreter.tree.Specializer;

import stencil.explore.util.NeedsPanel;

/**
 * A with special operators for making presentations.
 */
@Module
public class Presentation extends BasicModule {
		
	@Operator(spec="[label: \"True/False\"]")
	public static class SelectTrues extends AbstractOperator.Statefull<StencilOperator> implements NeedsPanel {
		public static final String NAMES_KEY = "names";
		
		
		protected final List<String> fields = new ArrayList();
		protected final List<Boolean> vals= new ArrayList();
		protected final Panel panel;
		protected final String label;
		
		public SelectTrues(SelectTrues op) {
			super(op.operatorData);
			this.fields.addAll(op.fields);
			this.vals.addAll(op.vals);
			this.panel = op.panel;
			this.stateID = op.stateID;
			this.label = op.label;
		}
		public SelectTrues(OperatorData opData, Specializer spec) {
			super(opData);
			label = Converter.toString(spec.get("label"));
			this.panel = new Panel(this);
		}

		@Facet(memUse="WRITER", counterpart="query")
		public boolean map(String v) {
			if (!fields.contains(v)) {panel.augment(v);}
			int i = fields.indexOf(v);
			return vals.get(i);
		}
		
		@Facet(memUse="READER")
		public boolean query(String v) {
			int i = fields.indexOf(v);
			if (i<0) {return false;}
			return vals.get(i);
		}
		
		@Override
		public SelectTrues viewpoint() {
			return new SelectTrues(this);
		}
		
		@Override
		public JPanel panel() {return panel;}
		
		private static final class Panel extends JPanel {
			protected final SelectTrues operator;
			public Panel(SelectTrues op) {
				super();
				super.setBorder(new TitledBorder(new LineBorder(Color.BLACK), op.label));
				operator=op;
				this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			}
			
			@SuppressWarnings("synthetic-access")
			public void augment(String name) {
				final int idx = operator.vals.size();

				operator.fields.add(name);
				operator.vals.add(true);
				operator.stateID++;
				
				JCheckBox box = new JCheckBox();
				box.setSelected(true);
				box.setVisible(true);
				box.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent ev) {
						synchronized(operator) {
							operator.stateID++;
							operator.vals.set(idx, ev.getStateChange() == ItemEvent.SELECTED);
						}
					}
				});
				
				JLabel label = new JLabel(name);

				JPanel p = new JPanel();
				p.add(box);
				p.add(label);
				this.add(p);
				this.revalidate();
			}
		}
	}

	@Operator(spec="[label: \"Color\"]")
	public static class SelectColors extends AbstractOperator.Statefull<StencilOperator> implements NeedsPanel {
		public static final String NAMES_KEY = "names";
		
		
		protected final List<String> fields = new ArrayList();
		protected final List<Color> vals= new ArrayList();
		protected final Panel panel;
		protected final String label;
		
		protected SelectColors(SelectColors op) {
			super(op.operatorData);
			fields.addAll(op.fields);
			vals.addAll(op.vals);
			panel = op.panel;
			stateID = op.stateID;
			label = op.label;
		}
		public SelectColors(OperatorData opData, Specializer spec) {
			super(opData);
			label = Converter.toString(spec.get("label"));
			panel = new Panel(this);
		}

		@Facet(memUse="WRITER", counterpart="query")
		public Color map(String v) {
			if (!fields.contains(v)) {panel.augment(v);}
			int i = fields.indexOf(v);
			return vals.get(i);
		}
		
		@Facet(memUse="READER")
		public Color query(String v) {
			int i = fields.indexOf(v);
			if (i<0) {return Color.GRAY;}//TODO: Verify this is CLEAR???
			return vals.get(i);
		}
		
		@Override
		public SelectColors viewpoint() {
			return new SelectColors(this);
		}
		
		@Override
		public JPanel panel() {return panel;}
		
		private static final class Panel extends JPanel {
			protected final SelectColors operator;
			public Panel(SelectColors op) {
				super();
				super.setBorder(new TitledBorder(new LineBorder(Color.BLACK), op.label));

				operator=op;
				this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			}
			
			@SuppressWarnings("synthetic-access")
			public void augment(String name) {
				final int idx = operator.vals.size();
				final JColorChooser colorChooser = new JColorChooser();
				colorChooser .setPreviewPanel(new JPanel());
				
				operator.fields.add(name);
				operator.vals.add(Color.BLUE);
				operator.stateID++;
				
				final JButton chooser = new JButton("...");
				chooser.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ev) {
						synchronized(operator) {
							JButton source = (JButton) ev.getSource();
							Color newColor = JColorChooser.showDialog(
									colorChooser,
				                     "Choose Color",
				                     source.getBackground());
							operator.stateID++;
							operator.vals.set(idx, newColor);
							source.setBackground(newColor);
						}						
					}
				});
				
				JLabel label = new JLabel(name);

				JPanel p = new JPanel();
				p.add(chooser);
				p.add(label);
				this.add(p);
				this.revalidate();
			}
		}
	}

	
	@Operator(spec="[map:\"map\", query:\"query\"]")
	@Description("Runs a passed operator, but on query will alternate between the map and query facets.")
	public static class BreakBinding extends AbstractOperator {
		boolean doMap = false;
		final String mapFacet;
		final String queryFacet;
		
		public BreakBinding(OperatorData opData, Specializer spec) {
			super(opData);
			mapFacet = Converter.toString(spec.get("map"));
			queryFacet = Converter.toString(spec.get("map"));
		}
		
		@Facet(memUse="WRITER", counterpart="query")
		public Object map(StencilOperator op, Object... args) {
			Invokeable map = op.getFacet(mapFacet);
			return map.invoke(args);
		}

		@Facet(memUse="READER")
		public Object query(StencilOperator op, Object... args) {
			doMap = !doMap;
			if (doMap) {return map(op, args);}
			else {
				return op.getFacet(queryFacet).invoke(args);
			}
		}
	}
}
