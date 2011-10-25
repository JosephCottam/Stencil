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

import stencil.module.OperatorInstanceException;
import stencil.module.ModuleCache;
import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.operator.util.Invokeable;
import stencil.module.util.*;
import stencil.module.util.ann.*;
import stencil.parser.string.util.Context;
import stencil.types.Converter;
import stencil.interpreter.tree.MultiPartName;
import stencil.interpreter.tree.Specializer;

import stencil.explore.util.NeedsPanel;

import static stencil.parser.ParserConstants.EMPTY_SPECIALIZER;
import static stencil.parser.ParserConstants.OP_ARG_PREFIX;

/**
 * A with special operators for making presentations.
 */
@Module
public class Presentation extends BasicModule {
		
	@Operator(spec="[label: \"True/False\"]")
	public static class SelectTrues extends AbstractOperator.Statefull<StencilOperator> implements NeedsPanel {
		public static final String NAMES_KEY = "names";
		
		
		private final List<String> fields = new ArrayList();
		private final List<Boolean> vals= new ArrayList();
		private final Panel panel;
		private final String label;
		
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

		@Facet(memUse="WRITER")
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
			private final SelectTrues operator;
			public Panel(SelectTrues op) {
				super();
				super.setBorder(new TitledBorder(new LineBorder(Color.BLACK), op.label));
				operator=op;
				this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			}
			
			public void augment(String name) {
				final int idx = operator.vals.size();

				operator.fields.add(name);
				operator.vals.add(true);
				operator.stateID++;
				
				JCheckBox box = new JCheckBox();
				box.setSelected(true);
				box.setVisible(true);
				box.addItemListener(new ItemListener() {
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
		
		
		private final List<String> fields = new ArrayList();
		private final List<Color> vals= new ArrayList();
		private final Panel panel;
		private final String label;
		
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

		@Facet(memUse="WRITER")
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
			private final SelectColors operator;
			public Panel(SelectColors op) {
				super();
				super.setBorder(new TitledBorder(new LineBorder(Color.BLACK), op.label));

				operator=op;
				this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			}
			
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

	
	@Operator
	@Description("Runs a passed operator, but on query will alternate between the map and query facets.")
	public static class BreakBinding extends AbstractOperator {
		final Invokeable map;
		final Invokeable query;
		boolean doMap = false;
		
		public BreakBinding(OperatorData opData, StencilOperator op) {
			super(opData);
			map = op.getFacet("map");
			query = op.getFacet("query");
		}
		
		@Facet(memUse="WRITER")
		public Object map(Object... args) {return map.invoke(args);}

		@Facet(memUse="READER")
		public Object query(Object... args) {
			doMap = !doMap;
			if (doMap) {return map.invoke(args);}
			else {return query.invoke(args);}
		}
	}
	
	public StencilOperator instance(String name, Context context, Specializer specializer, ModuleCache modules) throws SpecializationException {
		List<StencilOperator> opArgs = new ArrayList();


		//TODO: This is a horrible way to resolve things, have the operator as value in a CONST in the specializer instead of the name
		for (String key: specializer.keySet()) {
			if (key.startsWith(OP_ARG_PREFIX)) {
				StencilOperator op;
				try {
					op = modules.instance((MultiPartName) specializer.get(key), null, EMPTY_SPECIALIZER, false);
					opArgs.add(op);
				} catch (OperatorInstanceException e) {throw new IllegalArgumentException("Error instantiate operator-as-argument " + specializer.get(key), e);}
			}
		}
		
		assert opArgs.size() >0;
		if (opArgs.size() >1) {throw new IllegalArgumentException(name + " can only accept one higher order arg, recieved " + opArgs.size());}
		
		
		if (name.equals("BreakBinding")) {
			return new BreakBinding(getOperatorData(name, specializer), opArgs.get(0));
		} else {
			return super.instance(name, context, specializer, modules);
		}
	}
}
