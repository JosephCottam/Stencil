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
package stencil.explore.coordination;

import java.util.List;
import java.util.Set;
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
import java.util.HashSet;

import stencil.explore.coordination.StencilEvent;
import stencil.explore.model.AdapterOpts;
import stencil.explore.model.sources.StreamSource;
import static stencil.explore.coordination.StencilEvent.*;

public class Controller implements StencilListener.ConfigChanged, StencilListener.StencilChanged, StencilListener.SourcesChanged {
	protected Set<StencilMutable.Config> configReceivers = new HashSet();
	protected Set<StencilMutable.Stencil> stencilReceivers = new HashSet();
	protected Set<StencilMutable.Sources> sourceReceivers = new HashSet();

	/**Set to indicate that events should not be propagated.*/
	protected boolean suspendEvents = false;

	public void removeMutable(StencilMutable receiver, StencilEvent.Type type) {
		switch(type) {
			case Config: configReceivers.remove(receiver); break;
			case Stencil: stencilReceivers.remove(receiver); break;
			case Sources: sourceReceivers.remove(receiver); break;
			case All:
				configReceivers.remove(receiver);
				stencilReceivers.remove(receiver);
				sourceReceivers.remove(receiver);
		}
	}


	public void addMutable(StencilMutable receiver, StencilEvent.Type type) {
		switch(type) {
			case Config: configReceivers.add((StencilMutable.Config) receiver); break;
			case Stencil: stencilReceivers.add((StencilMutable.Stencil) receiver); break;
			case Sources: sourceReceivers.add((StencilMutable.Sources) receiver); break;
			case All:
				configReceivers.add((StencilMutable.Config) receiver);
				stencilReceivers.add((StencilMutable.Stencil) receiver);
				sourceReceivers.add((StencilMutable.Sources) receiver);
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

	public void sourceChanged(SourcesChanged sourceUpdate) {
		if (suspendEvents) {return;}
		for (StencilMutable.Sources reciver: sourceReceivers) {
			if (reciver.equals(sourceUpdate.getSource())) {continue;}
			List<StreamSource> sources = sourceUpdate.getValue();
			reciver.setSources(sources);
		}
	}


	/**Indicate that events should not be propagated for a time.*/
	public void suspendEvents() {suspendEvents = true;}

	/**Resume event propagation.*/
	public void resumeEvents() {suspendEvents = false;}
}
