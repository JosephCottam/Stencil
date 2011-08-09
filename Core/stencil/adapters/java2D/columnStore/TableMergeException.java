package stencil.adapters.java2D.columnStore;

public class TableMergeException extends RuntimeException {
	public TableMergeException(String name, String message) {super(String.format("%1$s: %2$s", name, message));}
	public TableMergeException(String name, String message, Exception e) {super(String.format("%1$s: %2$s", name, message), e);}

}
