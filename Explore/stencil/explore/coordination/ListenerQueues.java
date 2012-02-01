package stencil.explore.coordination;

import java.util.Set;
import java.util.HashSet;

import stencil.explore.model.AdapterOpts;

public class ListenerQueues {
	protected Set<StencilListener.ConfigChanged> configReceivers = new HashSet();
	protected Set<StencilListener.StencilChanged> stencilReceivers = new HashSet();

	public void addListener(StencilListener.ConfigChanged l) {configReceivers.add(l);}
	public void addListener(StencilListener.StencilChanged l) {stencilReceivers.add(l);}

	public void fireConfigChanged(Object source, AdapterOpts opts) {
		StencilEvent.ConfigChanged c = new StencilEvent.ConfigChanged(source, opts);
		for (StencilListener.ConfigChanged l:configReceivers) {
			l.configChanged(c);
		}
	}

	public void fireStencilChanged(Object source, String stencil) {
		StencilEvent.StencilChanged c = new StencilEvent.StencilChanged(source, stencil);
		for (StencilListener.StencilChanged l:stencilReceivers) {
			l.stencilChanged(c);
		}
	}
}
