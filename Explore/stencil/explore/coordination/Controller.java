package stencil.explore.coordination;

import java.util.Set;
import java.util.HashSet;

import stencil.explore.coordination.StencilEvent;
import stencil.explore.model.AdapterOpts;
import static stencil.explore.coordination.StencilEvent.*;

public class Controller implements StencilListener.ConfigChanged, StencilListener.StencilChanged {
	protected Set<StencilMutable.Config> configReceivers = new HashSet();
	protected Set<StencilMutable.Stencil> stencilReceivers = new HashSet();

	/**Set to indicate that events should not be propagated.*/
	protected boolean suspendEvents = false;

	public void removeMutable(StencilMutable receiver, StencilEvent.Type type) {
		switch(type) {
			case Config: configReceivers.remove(receiver); break;
			case Stencil: stencilReceivers.remove(receiver); break;
			case All:
				configReceivers.remove(receiver);
				stencilReceivers.remove(receiver);
		}
	}


	public void addMutable(StencilMutable receiver, StencilEvent.Type type) {
		switch(type) {
			case Config: configReceivers.add((StencilMutable.Config) receiver); break;
			case Stencil: stencilReceivers.add((StencilMutable.Stencil) receiver); break;
			case All:
				configReceivers.add((StencilMutable.Config) receiver);
				stencilReceivers.add((StencilMutable.Stencil) receiver);
		}
	}

	public void configChanged(ConfigChanged configUpdate) {
		if (suspendEvents) {return;}
		AdapterOpts opts = configUpdate.getValue();
		for (StencilMutable.Config reciver: configReceivers) {
			if (reciver.equals(configUpdate.getSource())) {continue;}
			reciver.setAdapterOpts(opts);
		}
	}

	public void stencilChanged(StencilChanged stencilUpdate) {
		if (suspendEvents) {return;}
		for (StencilMutable.Stencil reciver: stencilReceivers) {
			if (reciver.equals(stencilUpdate.getSource())) {continue;}
			String stencil = stencilUpdate.getValue();
			reciver.setStencil(stencil);
		}
	}

	/**Indicate that events should not be propagated for a time.*/
	public void suspendEvents() {suspendEvents = true;}

	/**Resume event propagation.*/
	public void resumeEvents() {suspendEvents = false;}
}
