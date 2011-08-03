package stencil.unittests.util.streams;

import stencil.util.streams.binary.BinaryTupleStream;
import stencil.util.streams.txt.DelimitedParser;

public class Util {
	public static final String COORD_FILE = "./TestData/RegressionImages/Sourceforge/vx_cluster_0_8.6_min_cuts.coord";
	public static final String COORD_FILE_HEADER = "./TestData/RegressionImages/Sourceforge/suppliments/header_coord.txt";
	public static final String TROVES_FILE = "./TestData/RegressionImages/Sourceforge/project_troves.txt";
	
	public static final String TROVES_TUPLES_FILE = "./TestData/RegressionImages/Sourceforge/project_troves.tuples";
	public static final String COORD_TUPLES_FILE = "./TestData/RegressionImages/Sourceforge/vx_cluster_0_8.6_min_cuts.tuples";
	public static final String COORD_TYPES = "sdd";
	public static final String TROVES_TYPES = "ss";

	public static DelimitedParser coordStream() throws Exception {
		return new DelimitedParser("CoordFile", COORD_FILE, "\\s+", 3, true,1);
	}
	
	public static DelimitedParser coordHeaderStream() throws Exception {
		return new DelimitedParser("CoordFile", COORD_FILE_HEADER, "\\s+", 3, true,1);
	}
	
	public static DelimitedParser trovesStream() throws Exception {
		return new DelimitedParser("Troves", TROVES_FILE, "\\|", 2, true,1);
	}

	public static BinaryTupleStream.Reader binaryTrovesStream() throws Exception{
		return new BinaryTupleStream.Reader("Troves", TROVES_TUPLES_FILE);
	}
	
}
