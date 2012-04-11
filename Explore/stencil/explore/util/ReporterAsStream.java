package stencil.explore.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ReporterAsStream extends OutputStream {
	ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	final MessageReporter r;
	public ReporterAsStream(MessageReporter r) {
		super();
		this.r=r;
	}

	@Override
	public synchronized void flush() {
		String s= buffer.toString();
		buffer.reset();
		r.addError(s);
	}
	
	@Override
	public synchronized void write(int arg0) throws IOException {buffer.write(arg0);}
}
