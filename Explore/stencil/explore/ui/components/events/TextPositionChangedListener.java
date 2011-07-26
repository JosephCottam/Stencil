package stencil.explore.ui.components.events;

import java.util.EventListener;

public interface TextPositionChangedListener extends EventListener {
	public void textPositionChanged(int line, int charNum);
}
