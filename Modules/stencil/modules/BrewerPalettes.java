package stencil.modules;


import java.awt.Color;
import java.lang.reflect.*;
import java.util.*;

import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.Tuple;

@Module
@Description("Encoding of the color palette found at ColorBrewer.org (by Cynthia A. Brewer, Penn State).")
public class BrewerPalettes  extends BasicModule {

	@Operator(spec="[palette: \"Set1\", reserve: \"BLACK\"]")
	public static final class BrewerColors extends AbstractOperator {
		private static String PALETTE = "palette";
		private static String RESERVE = "reserve";
		
		private ArrayList seen = new ArrayList();
		private final String rootPallet;
		private final Tuple reserve;

		public BrewerColors(OperatorData od, Specializer spec) {
			super(od);
			rootPallet = ((String) spec.get(PALETTE)).toUpperCase();
			reserve = stencil.types.color.ColorCache.get((String) spec.get(RESERVE));
		}

		@Facet(memUse="WRITER", prototype="(Color color)")
		public Tuple map(Object value) {
			int idx = Collections.binarySearch(seen, value);
			if (idx<0) {
		    	seen = new ArrayList(seen);  //Yeah, copy on write!
				seen.add(value);
		    	Collections.sort(seen);
				idx = Collections.binarySearch(seen, value);
			} 
			Color[] colors = getSet();
			return stencil.types.color.ColorCache.get(colors[idx]);  
		}
		
		@Facet(memUse="READER", prototype="(int VALUE)")
		public int StateID() {return seen.size();}

		@Facet(memUse="READER", prototype="(Color color)")
		public Tuple query(Object value) {
			int idx = Collections.binarySearch(seen, value);

			if (idx < 0) {
				return reserve;
			} else {
				return stencil.types.color.ColorCache.get(getSet()[idx]);
			}
		}		

		private Color[] getSet() {
			Field pallet;
			int size = seen.size();
			do {
				String attempt = rootPallet + size;
				pallet = pallets.get(attempt);
				size++;
			} while (pallet == null && size < MAX_PALLET_SIZE);
			
			if (pallet == null) {throw new RuntimeException("No brewer pallet " + rootPallet + " of size " + seen.size() + " or larger found.");}

			try {return (Color[]) pallet.get(null);}
			catch (Exception e) {throw new RuntimeException("Error getting value for pallet " + pallet.getName());}
		}

		static final int MAX_PALLET_SIZE=12;
		static final Map<String, Field> pallets = new HashMap();
		static {
			Class c = BrewerColors.class;
			Field[] fs = c.getFields();
			for (Field f:fs) {
				if (Modifier.isStatic(f.getModifiers()) && f.getType().equals(Color[].class)) {
					pallets.put(f.getName(), f);
				}
			}
			
			if (pallets.size() <1) {throw new Error("Error intializing BrewerColors.");}
		}


		// qual pallet of 3 elements
		public static final Color[] ACCENT3 = new Color[]{
			new Color(127, 201, 127) ,
			new Color(190, 174, 212) ,
			new Color(253, 192, 134) };

		// qual pallet of 4 elements
		public static final Color[] ACCENT4 = new Color[]{
			new Color(127, 201, 127) ,
			new Color(190, 174, 212) ,
			new Color(253, 192, 134) ,
			new Color(255, 255, 153) };


		// qual pallet of 5 elements
		public static final Color[] ACCENT5 = new Color[]{
			new Color(127, 201, 127) ,
			new Color(190, 174, 212) ,
			new Color(253, 192, 134) ,
			new Color(255, 255, 153) ,
			new Color(56, 108, 176) };


		// qual pallet of 6 elements
		public static final Color[] ACCENT6 = new Color[]{
			new Color(127, 201, 127) ,
			new Color(190, 174, 212) ,
			new Color(253, 192, 134) ,
			new Color(255, 255, 153) ,
			new Color(56, 108, 176) ,
			new Color(240, 2, 127) };


		// qual pallet of 7 elements
		public static final Color[] ACCENT7 = new Color[]{
			new Color(127, 201, 127) ,
			new Color(190, 174, 212) ,
			new Color(253, 192, 134) ,
			new Color(255, 255, 153) ,
			new Color(56, 108, 176) ,
			new Color(240, 2, 127) ,
			new Color(191, 91, 23) };


		// qual pallet of 8 elements
		public static final Color[] ACCENT8 = new Color[]{
			new Color(127, 201, 127) ,
			new Color(190, 174, 212) ,
			new Color(253, 192, 134) ,
			new Color(255, 255, 153) ,
			new Color(56, 108, 176) ,
			new Color(240, 2, 127) ,
			new Color(191, 91, 23) ,
			new Color(102, 102, 102) };


		// seq pallet of 3 elements
		public static final Color[] BLUES3 = new Color[]{
			new Color(222, 235, 247) ,
			new Color(158, 202, 225) ,
			new Color(49, 130, 189) };


		// seq pallet of 4 elements
		public static final Color[] BLUES4 = new Color[]{
			new Color(239, 243, 255) ,
			new Color(189, 215, 231) ,
			new Color(107, 174, 214) ,
			new Color(33, 113, 181) };


		// seq pallet of 5 elements
		public static final Color[] BLUES5 = new Color[]{
			new Color(239, 243, 255) ,
			new Color(189, 215, 231) ,
			new Color(107, 174, 214) ,
			new Color(49, 130, 189) ,
			new Color(8, 81, 156) };


		// seq pallet of 6 elements
		public static final Color[] BLUES6 = new Color[]{
			new Color(239, 243, 255) ,
			new Color(198, 219, 239) ,
			new Color(158, 202, 225) ,
			new Color(107, 174, 214) ,
			new Color(49, 130, 189) ,
			new Color(8, 81, 156) };


		// seq pallet of 7 elements
		public static final Color[] BLUES7 = new Color[]{
			new Color(239, 243, 255) ,
			new Color(198, 219, 239) ,
			new Color(158, 202, 225) ,
			new Color(107, 174, 214) ,
			new Color(66, 146, 198) ,
			new Color(33, 113, 181) ,
			new Color(8, 69, 148) };


		// seq pallet of 8 elements
		public static final Color[] BLUES8 = new Color[]{
			new Color(247, 251, 255) ,
			new Color(222, 235, 247) ,
			new Color(198, 219, 239) ,
			new Color(158, 202, 225) ,
			new Color(107, 174, 214) ,
			new Color(66, 146, 198) ,
			new Color(33, 113, 181) ,
			new Color(8, 69, 148) };


		// seq pallet of 9 elements
		public static final Color[] BLUES9 = new Color[]{
			new Color(247, 251, 255) ,
			new Color(222, 235, 247) ,
			new Color(198, 219, 239) ,
			new Color(158, 202, 225) ,
			new Color(107, 174, 214) ,
			new Color(66, 146, 198) ,
			new Color(33, 113, 181) ,
			new Color(8, 81, 156) ,
			new Color(8, 48, 107) };


		// div pallet of 3 elements
		public static final Color[] BRBG3 = new Color[]{
			new Color(216, 179, 101) ,
			new Color(245, 245, 245) ,
			new Color(90, 180, 172) };


		// div pallet of 4 elements
		public static final Color[] BRBG4 = new Color[]{
			new Color(166, 97, 26) ,
			new Color(223, 194, 125) ,
			new Color(128, 205, 193) ,
			new Color(1, 133, 113) };


		// div pallet of 5 elements
		public static final Color[] BRBG5 = new Color[]{
			new Color(166, 97, 26) ,
			new Color(223, 194, 125) ,
			new Color(245, 245, 245) ,
			new Color(128, 205, 193) ,
			new Color(1, 133, 113) };


		// div pallet of 6 elements
		public static final Color[] BRBG6 = new Color[]{
			new Color(140, 81, 10) ,
			new Color(216, 179, 101) ,
			new Color(246, 232, 195) ,
			new Color(199, 234, 229) ,
			new Color(90, 180, 172) ,
			new Color(1, 102, 94) };


		// div pallet of 7 elements
		public static final Color[] BRBG7 = new Color[]{
			new Color(140, 81, 10) ,
			new Color(216, 179, 101) ,
			new Color(246, 232, 195) ,
			new Color(245, 245, 245) ,
			new Color(199, 234, 229) ,
			new Color(90, 180, 172) ,
			new Color(1, 102, 94) };


		// div pallet of 8 elements
		public static final Color[] BRBG8 = new Color[]{
			new Color(140, 81, 10) ,
			new Color(191, 129, 45) ,
			new Color(223, 194, 125) ,
			new Color(246, 232, 195) ,
			new Color(199, 234, 229) ,
			new Color(128, 205, 193) ,
			new Color(53, 151, 143) ,
			new Color(1, 102, 94) };


		// div pallet of 9 elements
		public static final Color[] BRBG9 = new Color[]{
			new Color(140, 81, 10) ,
			new Color(191, 129, 45) ,
			new Color(223, 194, 125) ,
			new Color(246, 232, 195) ,
			new Color(245, 245, 245) ,
			new Color(199, 234, 229) ,
			new Color(128, 205, 193) ,
			new Color(53, 151, 143) ,
			new Color(1, 102, 94) };


		// div pallet of 10 elements
		public static final Color[] BRBG10 = new Color[]{
			new Color(84, 48, 5) ,
			new Color(140, 81, 10) ,
			new Color(191, 129, 45) ,
			new Color(223, 194, 125) ,
			new Color(246, 232, 195) ,
			new Color(199, 234, 229) ,
			new Color(128, 205, 193) ,
			new Color(53, 151, 143) ,
			new Color(1, 102, 94) ,
			new Color(0, 60, 48) };


		// div pallet of 11 elements
		public static final Color[] BRBG11 = new Color[]{
			new Color(84, 48, 5) ,
			new Color(140, 81, 10) ,
			new Color(191, 129, 45) ,
			new Color(223, 194, 125) ,
			new Color(246, 232, 195) ,
			new Color(245, 245, 245) ,
			new Color(199, 234, 229) ,
			new Color(128, 205, 193) ,
			new Color(53, 151, 143) ,
			new Color(1, 102, 94) ,
			new Color(0, 60, 48) };


		// seq pallet of 3 elements
		public static final Color[] BUGN3 = new Color[]{
			new Color(229, 245, 249) ,
			new Color(153, 216, 201) ,
			new Color(44, 162, 95) };


		// seq pallet of 4 elements
		public static final Color[] BUGN4 = new Color[]{
			new Color(237, 248, 251) ,
			new Color(178, 226, 226) ,
			new Color(102, 194, 164) ,
			new Color(35, 139, 69) };


		// seq pallet of 5 elements
		public static final Color[] BUGN5 = new Color[]{
			new Color(237, 248, 251) ,
			new Color(178, 226, 226) ,
			new Color(102, 194, 164) ,
			new Color(44, 162, 95) ,
			new Color(0, 109, 44) };


		// seq pallet of 6 elements
		public static final Color[] BUGN6 = new Color[]{
			new Color(237, 248, 251) ,
			new Color(204, 236, 230) ,
			new Color(153, 216, 201) ,
			new Color(102, 194, 164) ,
			new Color(44, 162, 95) ,
			new Color(0, 109, 44) };


		// seq pallet of 7 elements
		public static final Color[] BUGN7 = new Color[]{
			new Color(237, 248, 251) ,
			new Color(204, 236, 230) ,
			new Color(153, 216, 201) ,
			new Color(102, 194, 164) ,
			new Color(65, 174, 118) ,
			new Color(35, 139, 69) ,
			new Color(0, 88, 36) };


		// seq pallet of 8 elements
		public static final Color[] BUGN8 = new Color[]{
			new Color(247, 252, 253) ,
			new Color(229, 245, 249) ,
			new Color(204, 236, 230) ,
			new Color(153, 216, 201) ,
			new Color(102, 194, 164) ,
			new Color(65, 174, 118) ,
			new Color(35, 139, 69) ,
			new Color(0, 88, 36) };


		// seq pallet of 9 elements
		public static final Color[] BUGN9 = new Color[]{
			new Color(247, 252, 253) ,
			new Color(229, 245, 249) ,
			new Color(204, 236, 230) ,
			new Color(153, 216, 201) ,
			new Color(102, 194, 164) ,
			new Color(65, 174, 118) ,
			new Color(35, 139, 69) ,
			new Color(0, 109, 44) ,
			new Color(0, 68, 27) };


		// seq pallet of 3 elements
		public static final Color[] BUPU3 = new Color[]{
			new Color(224, 236, 244) ,
			new Color(158, 188, 218) ,
			new Color(136, 86, 167) };


		// seq pallet of 4 elements
		public static final Color[] BUPU4 = new Color[]{
			new Color(237, 248, 251) ,
			new Color(179, 205, 227) ,
			new Color(140, 150, 198) ,
			new Color(136, 65, 157) };


		// seq pallet of 5 elements
		public static final Color[] BUPU5 = new Color[]{
			new Color(237, 248, 251) ,
			new Color(179, 205, 227) ,
			new Color(140, 150, 198) ,
			new Color(136, 86, 167) ,
			new Color(129, 15, 124) };


		// seq pallet of 6 elements
		public static final Color[] BUPU6 = new Color[]{
			new Color(237, 248, 251) ,
			new Color(191, 211, 230) ,
			new Color(158, 188, 218) ,
			new Color(140, 150, 198) ,
			new Color(136, 86, 167) ,
			new Color(129, 15, 124) };


		// seq pallet of 7 elements
		public static final Color[] BUPU7 = new Color[]{
			new Color(237, 248, 251) ,
			new Color(191, 211, 230) ,
			new Color(158, 188, 218) ,
			new Color(140, 150, 198) ,
			new Color(140, 107, 177) ,
			new Color(136, 65, 157) ,
			new Color(110, 1, 107) };


		// seq pallet of 8 elements
		public static final Color[] BUPU8 = new Color[]{
			new Color(247, 252, 253) ,
			new Color(224, 236, 244) ,
			new Color(191, 211, 230) ,
			new Color(158, 188, 218) ,
			new Color(140, 150, 198) ,
			new Color(140, 107, 177) ,
			new Color(136, 65, 157) ,
			new Color(110, 1, 107) };


		// seq pallet of 9 elements
		public static final Color[] BUPU9 = new Color[]{
			new Color(247, 252, 253) ,
			new Color(224, 236, 244) ,
			new Color(191, 211, 230) ,
			new Color(158, 188, 218) ,
			new Color(140, 150, 198) ,
			new Color(140, 107, 177) ,
			new Color(136, 65, 157) ,
			new Color(129, 15, 124) ,
			new Color(77, 0, 75) };


		// qual pallet of 3 elements
		public static final Color[] DARK23 = new Color[]{
			new Color(27, 158, 119) ,
			new Color(217, 95, 2) ,
			new Color(117, 112, 179) };


		// qual pallet of 4 elements
		public static final Color[] DARK24 = new Color[]{
			new Color(27, 158, 119) ,
			new Color(217, 95, 2) ,
			new Color(117, 112, 179) ,
			new Color(231, 41, 138) };


		// qual pallet of 5 elements
		public static final Color[] DARK25 = new Color[]{
			new Color(27, 158, 119) ,
			new Color(217, 95, 2) ,
			new Color(117, 112, 179) ,
			new Color(231, 41, 138) ,
			new Color(102, 166, 30) };


		// qual pallet of 6 elements
		public static final Color[] DARK26 = new Color[]{
			new Color(27, 158, 119) ,
			new Color(217, 95, 2) ,
			new Color(117, 112, 179) ,
			new Color(231, 41, 138) ,
			new Color(102, 166, 30) ,
			new Color(230, 171, 2) };


		// qual pallet of 7 elements
		public static final Color[] DARK27 = new Color[]{
			new Color(27, 158, 119) ,
			new Color(217, 95, 2) ,
			new Color(117, 112, 179) ,
			new Color(231, 41, 138) ,
			new Color(102, 166, 30) ,
			new Color(230, 171, 2) ,
			new Color(166, 118, 29) };


		// qual pallet of 8 elements
		public static final Color[] DARK28 = new Color[]{
			new Color(27, 158, 119) ,
			new Color(217, 95, 2) ,
			new Color(117, 112, 179) ,
			new Color(231, 41, 138) ,
			new Color(102, 166, 30) ,
			new Color(230, 171, 2) ,
			new Color(166, 118, 29) ,
			new Color(102, 102, 102) };


		// seq pallet of 3 elements
		public static final Color[] GNBU3 = new Color[]{
			new Color(224, 243, 219) ,
			new Color(168, 221, 181) ,
			new Color(67, 162, 202) };


		// seq pallet of 4 elements
		public static final Color[] GNBU4 = new Color[]{
			new Color(240, 249, 232) ,
			new Color(186, 228, 188) ,
			new Color(123, 204, 196) ,
			new Color(43, 140, 190) };


		// seq pallet of 5 elements
		public static final Color[] GNBU5 = new Color[]{
			new Color(240, 249, 232) ,
			new Color(186, 228, 188) ,
			new Color(123, 204, 196) ,
			new Color(67, 162, 202) ,
			new Color(8, 104, 172) };


		// seq pallet of 6 elements
		public static final Color[] GNBU6 = new Color[]{
			new Color(240, 249, 232) ,
			new Color(204, 235, 197) ,
			new Color(168, 221, 181) ,
			new Color(123, 204, 196) ,
			new Color(67, 162, 202) ,
			new Color(8, 104, 172) };


		// seq pallet of 7 elements
		public static final Color[] GNBU7 = new Color[]{
			new Color(240, 249, 232) ,
			new Color(204, 235, 197) ,
			new Color(168, 221, 181) ,
			new Color(123, 204, 196) ,
			new Color(78, 179, 211) ,
			new Color(43, 140, 190) ,
			new Color(8, 88, 158) };


		// seq pallet of 8 elements
		public static final Color[] GNBU8 = new Color[]{
			new Color(247, 252, 240) ,
			new Color(224, 243, 219) ,
			new Color(204, 235, 197) ,
			new Color(168, 221, 181) ,
			new Color(123, 204, 196) ,
			new Color(78, 179, 211) ,
			new Color(43, 140, 190) ,
			new Color(8, 88, 158) };


		// seq pallet of 9 elements
		public static final Color[] GNBU9 = new Color[]{
			new Color(247, 252, 240) ,
			new Color(224, 243, 219) ,
			new Color(204, 235, 197) ,
			new Color(168, 221, 181) ,
			new Color(123, 204, 196) ,
			new Color(78, 179, 211) ,
			new Color(43, 140, 190) ,
			new Color(8, 104, 172) ,
			new Color(8, 64, 129) };


		// seq pallet of 3 elements
		public static final Color[] GREENS3 = new Color[]{
			new Color(229, 245, 224) ,
			new Color(161, 217, 155) ,
			new Color(49, 163, 84) };


		// seq pallet of 4 elements
		public static final Color[] GREENS4 = new Color[]{
			new Color(237, 248, 233) ,
			new Color(186, 228, 179) ,
			new Color(116, 196, 118) ,
			new Color(35, 139, 69) };


		// seq pallet of 5 elements
		public static final Color[] GREENS5 = new Color[]{
			new Color(237, 248, 233) ,
			new Color(186, 228, 179) ,
			new Color(116, 196, 118) ,
			new Color(49, 163, 84) ,
			new Color(0, 109, 44) };


		// seq pallet of 6 elements
		public static final Color[] GREENS6 = new Color[]{
			new Color(237, 248, 233) ,
			new Color(199, 233, 192) ,
			new Color(161, 217, 155) ,
			new Color(116, 196, 118) ,
			new Color(49, 163, 84) ,
			new Color(0, 109, 44) };


		// seq pallet of 7 elements
		public static final Color[] GREENS7 = new Color[]{
			new Color(237, 248, 233) ,
			new Color(199, 233, 192) ,
			new Color(161, 217, 155) ,
			new Color(116, 196, 118) ,
			new Color(65, 171, 93) ,
			new Color(35, 139, 69) ,
			new Color(0, 90, 50) };


		// seq pallet of 8 elements
		public static final Color[] GREENS8 = new Color[]{
			new Color(247, 252, 245) ,
			new Color(229, 245, 224) ,
			new Color(199, 233, 192) ,
			new Color(161, 217, 155) ,
			new Color(116, 196, 118) ,
			new Color(65, 171, 93) ,
			new Color(35, 139, 69) ,
			new Color(0, 90, 50) };


		// seq pallet of 9 elements
		public static final Color[] GREENS9 = new Color[]{
			new Color(247, 252, 245) ,
			new Color(229, 245, 224) ,
			new Color(199, 233, 192) ,
			new Color(161, 217, 155) ,
			new Color(116, 196, 118) ,
			new Color(65, 171, 93) ,
			new Color(35, 139, 69) ,
			new Color(0, 109, 44) ,
			new Color(0, 68, 27) };


		// seq pallet of 3 elements
		public static final Color[] GREYS3 = new Color[]{
			new Color(240, 240, 240) ,
			new Color(189, 189, 189) ,
			new Color(99, 99, 99) };


		// seq pallet of 4 elements
		public static final Color[] GREYS4 = new Color[]{
			new Color(247, 247, 247) ,
			new Color(204, 204, 204) ,
			new Color(150, 150, 150) ,
			new Color(82, 82, 82) };


		// seq pallet of 5 elements
		public static final Color[] GREYS5 = new Color[]{
			new Color(247, 247, 247) ,
			new Color(204, 204, 204) ,
			new Color(150, 150, 150) ,
			new Color(99, 99, 99) ,
			new Color(37, 37, 37) };


		// seq pallet of 6 elements
		public static final Color[] GREYS6 = new Color[]{
			new Color(247, 247, 247) ,
			new Color(217, 217, 217) ,
			new Color(189, 189, 189) ,
			new Color(150, 150, 150) ,
			new Color(99, 99, 99) ,
			new Color(37, 37, 37) };


		// seq pallet of 7 elements
		public static final Color[] GREYS7 = new Color[]{
			new Color(247, 247, 247) ,
			new Color(217, 217, 217) ,
			new Color(189, 189, 189) ,
			new Color(150, 150, 150) ,
			new Color(115, 115, 115) ,
			new Color(82, 82, 82) ,
			new Color(37, 37, 37) };


		// seq pallet of 8 elements
		public static final Color[] GREYS8 = new Color[]{
			new Color(255, 255, 255) ,
			new Color(240, 240, 240) ,
			new Color(217, 217, 217) ,
			new Color(189, 189, 189) ,
			new Color(150, 150, 150) ,
			new Color(115, 115, 115) ,
			new Color(82, 82, 82) ,
			new Color(37, 37, 37) };


		// seq pallet of 9 elements
		public static final Color[] GREYS9 = new Color[]{
			new Color(255, 255, 255) ,
			new Color(240, 240, 240) ,
			new Color(217, 217, 217) ,
			new Color(189, 189, 189) ,
			new Color(150, 150, 150) ,
			new Color(115, 115, 115) ,
			new Color(82, 82, 82) ,
			new Color(37, 37, 37) ,
			new Color(0, 0, 0) };


		// seq pallet of 3 elements
		public static final Color[] ORANGES3 = new Color[]{
			new Color(254, 230, 206) ,
			new Color(253, 174, 107) ,
			new Color(230, 85, 13) };


		// seq pallet of 4 elements
		public static final Color[] ORANGES4 = new Color[]{
			new Color(254, 237, 222) ,
			new Color(253, 190, 133) ,
			new Color(253, 141, 60) ,
			new Color(217, 71, 1) };


		// seq pallet of 5 elements
		public static final Color[] ORANGES5 = new Color[]{
			new Color(254, 237, 222) ,
			new Color(253, 190, 133) ,
			new Color(253, 141, 60) ,
			new Color(230, 85, 13) ,
			new Color(166, 54, 3) };


		// seq pallet of 6 elements
		public static final Color[] ORANGES6 = new Color[]{
			new Color(254, 237, 222) ,
			new Color(253, 208, 162) ,
			new Color(253, 174, 107) ,
			new Color(253, 141, 60) ,
			new Color(230, 85, 13) ,
			new Color(166, 54, 3) };


		// seq pallet of 7 elements
		public static final Color[] ORANGES7 = new Color[]{
			new Color(254, 237, 222) ,
			new Color(253, 208, 162) ,
			new Color(253, 174, 107) ,
			new Color(253, 141, 60) ,
			new Color(241, 105, 19) ,
			new Color(217, 72, 1) ,
			new Color(140, 45, 4) };


		// seq pallet of 8 elements
		public static final Color[] ORANGES8 = new Color[]{
			new Color(255, 245, 235) ,
			new Color(254, 230, 206) ,
			new Color(253, 208, 162) ,
			new Color(253, 174, 107) ,
			new Color(253, 141, 60) ,
			new Color(241, 105, 19) ,
			new Color(217, 72, 1) ,
			new Color(140, 45, 4) };


		// seq pallet of 9 elements
		public static final Color[] ORANGES9 = new Color[]{
			new Color(255, 245, 235) ,
			new Color(254, 230, 206) ,
			new Color(253, 208, 162) ,
			new Color(253, 174, 107) ,
			new Color(253, 141, 60) ,
			new Color(241, 105, 19) ,
			new Color(217, 72, 1) ,
			new Color(166, 54, 3) ,
			new Color(127, 39, 4) };


		// seq pallet of 3 elements
		public static final Color[] ORRD3 = new Color[]{
			new Color(254, 232, 200) ,
			new Color(253, 187, 132) ,
			new Color(227, 74, 51) };


		// seq pallet of 4 elements
		public static final Color[] ORRD4 = new Color[]{
			new Color(254, 240, 217) ,
			new Color(253, 204, 138) ,
			new Color(252, 141, 89) ,
			new Color(215, 48, 31) };


		// seq pallet of 5 elements
		public static final Color[] ORRD5 = new Color[]{
			new Color(254, 240, 217) ,
			new Color(253, 204, 138) ,
			new Color(252, 141, 89) ,
			new Color(227, 74, 51) ,
			new Color(179, 0, 0) };


		// seq pallet of 6 elements
		public static final Color[] ORRD6 = new Color[]{
			new Color(254, 240, 217) ,
			new Color(253, 212, 158) ,
			new Color(253, 187, 132) ,
			new Color(252, 141, 89) ,
			new Color(227, 74, 51) ,
			new Color(179, 0, 0) };


		// seq pallet of 7 elements
		public static final Color[] ORRD7 = new Color[]{
			new Color(254, 240, 217) ,
			new Color(253, 212, 158) ,
			new Color(253, 187, 132) ,
			new Color(252, 141, 89) ,
			new Color(239, 101, 72) ,
			new Color(215, 48, 31) ,
			new Color(153, 0, 0) };


		// seq pallet of 8 elements
		public static final Color[] ORRD8 = new Color[]{
			new Color(255, 247, 236) ,
			new Color(254, 232, 200) ,
			new Color(253, 212, 158) ,
			new Color(253, 187, 132) ,
			new Color(252, 141, 89) ,
			new Color(239, 101, 72) ,
			new Color(215, 48, 31) ,
			new Color(153, 0, 0) };


		// seq pallet of 9 elements
		public static final Color[] ORRD9 = new Color[]{
			new Color(255, 247, 236) ,
			new Color(254, 232, 200) ,
			new Color(253, 212, 158) ,
			new Color(253, 187, 132) ,
			new Color(252, 141, 89) ,
			new Color(239, 101, 72) ,
			new Color(215, 48, 31) ,
			new Color(179, 0, 0) ,
			new Color(127, 0, 0) };


		// qual pallet of 3 elements
		public static final Color[] PAIRED3 = new Color[]{
			new Color(166, 206, 227) ,
			new Color(31, 120, 180) ,
			new Color(178, 223, 138) };


		// qual pallet of 4 elements
		public static final Color[] PAIRED4 = new Color[]{
			new Color(166, 206, 227) ,
			new Color(31, 120, 180) ,
			new Color(178, 223, 138) ,
			new Color(51, 160, 44) };


		// qual pallet of 5 elements
		public static final Color[] PAIRED5 = new Color[]{
			new Color(166, 206, 227) ,
			new Color(31, 120, 180) ,
			new Color(178, 223, 138) ,
			new Color(51, 160, 44) ,
			new Color(251, 154, 153) };


		// qual pallet of 6 elements
		public static final Color[] PAIRED6 = new Color[]{
			new Color(166, 206, 227) ,
			new Color(31, 120, 180) ,
			new Color(178, 223, 138) ,
			new Color(51, 160, 44) ,
			new Color(251, 154, 153) ,
			new Color(227, 26, 28) };


		// qual pallet of 7 elements
		public static final Color[] PAIRED7 = new Color[]{
			new Color(166, 206, 227) ,
			new Color(31, 120, 180) ,
			new Color(178, 223, 138) ,
			new Color(51, 160, 44) ,
			new Color(251, 154, 153) ,
			new Color(227, 26, 28) ,
			new Color(253, 191, 111) };


		// qual pallet of 8 elements
		public static final Color[] PAIRED8 = new Color[]{
			new Color(166, 206, 227) ,
			new Color(31, 120, 180) ,
			new Color(178, 223, 138) ,
			new Color(51, 160, 44) ,
			new Color(251, 154, 153) ,
			new Color(227, 26, 28) ,
			new Color(253, 191, 111) ,
			new Color(255, 127, 0) };


		// qual pallet of 9 elements
		public static final Color[] PAIRED9 = new Color[]{
			new Color(166, 206, 227) ,
			new Color(31, 120, 180) ,
			new Color(178, 223, 138) ,
			new Color(51, 160, 44) ,
			new Color(251, 154, 153) ,
			new Color(227, 26, 28) ,
			new Color(253, 191, 111) ,
			new Color(255, 127, 0) ,
			new Color(202, 178, 214) };


		// qual pallet of 10 elements
		public static final Color[] PAIRED10 = new Color[]{
			new Color(166, 206, 227) ,
			new Color(31, 120, 180) ,
			new Color(178, 223, 138) ,
			new Color(51, 160, 44) ,
			new Color(251, 154, 153) ,
			new Color(227, 26, 28) ,
			new Color(253, 191, 111) ,
			new Color(255, 127, 0) ,
			new Color(202, 178, 214) ,
			new Color(106, 61, 154) };


		// qual pallet of 11 elements
		public static final Color[] PAIRED11 = new Color[]{
			new Color(166, 206, 227) ,
			new Color(31, 120, 180) ,
			new Color(178, 223, 138) ,
			new Color(51, 160, 44) ,
			new Color(251, 154, 153) ,
			new Color(227, 26, 28) ,
			new Color(253, 191, 111) ,
			new Color(255, 127, 0) ,
			new Color(202, 178, 214) ,
			new Color(106, 61, 154) ,
			new Color(255, 255, 153) };


		// qual pallet of 12 elements
		public static final Color[] PAIRED12 = new Color[]{
			new Color(166, 206, 227) ,
			new Color(31, 120, 180) ,
			new Color(178, 223, 138) ,
			new Color(51, 160, 44) ,
			new Color(251, 154, 153) ,
			new Color(227, 26, 28) ,
			new Color(253, 191, 111) ,
			new Color(255, 127, 0) ,
			new Color(202, 178, 214) ,
			new Color(106, 61, 154) ,
			new Color(255, 255, 153) ,
			new Color(177, 89, 40) };


		// qual pallet of 3 elements
		public static final Color[] PASTEL13 = new Color[]{
			new Color(251, 180, 174) ,
			new Color(179, 205, 227) ,
			new Color(204, 235, 197) };


		// qual pallet of 4 elements
		public static final Color[] PASTEL14 = new Color[]{
			new Color(251, 180, 174) ,
			new Color(179, 205, 227) ,
			new Color(204, 235, 197) ,
			new Color(222, 203, 228) };


		// qual pallet of 5 elements
		public static final Color[] PASTEL15 = new Color[]{
			new Color(251, 180, 174) ,
			new Color(179, 205, 227) ,
			new Color(204, 235, 197) ,
			new Color(222, 203, 228) ,
			new Color(254, 217, 166) };


		// qual pallet of 6 elements
		public static final Color[] PASTEL16 = new Color[]{
			new Color(251, 180, 174) ,
			new Color(179, 205, 227) ,
			new Color(204, 235, 197) ,
			new Color(222, 203, 228) ,
			new Color(254, 217, 166) ,
			new Color(255, 255, 204) };


		// qual pallet of 7 elements
		public static final Color[] PASTEL17 = new Color[]{
			new Color(251, 180, 174) ,
			new Color(179, 205, 227) ,
			new Color(204, 235, 197) ,
			new Color(222, 203, 228) ,
			new Color(254, 217, 166) ,
			new Color(255, 255, 204) ,
			new Color(229, 216, 189) };


		// qual pallet of 8 elements
		public static final Color[] PASTEL18 = new Color[]{
			new Color(251, 180, 174) ,
			new Color(179, 205, 227) ,
			new Color(204, 235, 197) ,
			new Color(222, 203, 228) ,
			new Color(254, 217, 166) ,
			new Color(255, 255, 204) ,
			new Color(229, 216, 189) ,
			new Color(253, 218, 236) };


		// qual pallet of 9 elements
		public static final Color[] PASTEL19 = new Color[]{
			new Color(251, 180, 174) ,
			new Color(179, 205, 227) ,
			new Color(204, 235, 197) ,
			new Color(222, 203, 228) ,
			new Color(254, 217, 166) ,
			new Color(255, 255, 204) ,
			new Color(229, 216, 189) ,
			new Color(253, 218, 236) ,
			new Color(242, 242, 242) };


		// qual pallet of 3 elements
		public static final Color[] PASTEL23 = new Color[]{
			new Color(179, 226, 205) ,
			new Color(253, 205, 172) ,
			new Color(203, 213, 232) };


		// qual pallet of 4 elements
		public static final Color[] PASTEL24 = new Color[]{
			new Color(179, 226, 205) ,
			new Color(253, 205, 172) ,
			new Color(203, 213, 232) ,
			new Color(244, 202, 228) };


		// qual pallet of 5 elements
		public static final Color[] PASTEL25 = new Color[]{
			new Color(179, 226, 205) ,
			new Color(253, 205, 172) ,
			new Color(203, 213, 232) ,
			new Color(244, 202, 228) ,
			new Color(230, 245, 201) };


		// qual pallet of 6 elements
		public static final Color[] PASTEL26 = new Color[]{
			new Color(179, 226, 205) ,
			new Color(253, 205, 172) ,
			new Color(203, 213, 232) ,
			new Color(244, 202, 228) ,
			new Color(230, 245, 201) ,
			new Color(255, 242, 174) };


		// qual pallet of 7 elements
		public static final Color[] PASTEL27 = new Color[]{
			new Color(179, 226, 205) ,
			new Color(253, 205, 172) ,
			new Color(203, 213, 232) ,
			new Color(244, 202, 228) ,
			new Color(230, 245, 201) ,
			new Color(255, 242, 174) ,
			new Color(241, 226, 204) };


		// qual pallet of 8 elements
		public static final Color[] PASTEL28 = new Color[]{
			new Color(179, 226, 205) ,
			new Color(253, 205, 172) ,
			new Color(203, 213, 232) ,
			new Color(244, 202, 228) ,
			new Color(230, 245, 201) ,
			new Color(255, 242, 174) ,
			new Color(241, 226, 204) ,
			new Color(204, 204, 204) };


		// div pallet of 3 elements
		public static final Color[] PIYG3 = new Color[]{
			new Color(233, 163, 201) ,
			new Color(247, 247, 247) ,
			new Color(161, 215, 106) };


		// div pallet of 4 elements
		public static final Color[] PIYG4 = new Color[]{
			new Color(208, 28, 139) ,
			new Color(241, 182, 218) ,
			new Color(184, 225, 134) ,
			new Color(77, 172, 38) };


		// div pallet of 5 elements
		public static final Color[] PIYG5 = new Color[]{
			new Color(208, 28, 139) ,
			new Color(241, 182, 218) ,
			new Color(247, 247, 247) ,
			new Color(184, 225, 134) ,
			new Color(77, 172, 38) };


		// div pallet of 6 elements
		public static final Color[] PIYG6 = new Color[]{
			new Color(197, 27, 125) ,
			new Color(233, 163, 201) ,
			new Color(253, 224, 239) ,
			new Color(230, 245, 208) ,
			new Color(161, 215, 106) ,
			new Color(77, 146, 33) };


		// div pallet of 7 elements
		public static final Color[] PIYG7 = new Color[]{
			new Color(197, 27, 125) ,
			new Color(233, 163, 201) ,
			new Color(253, 224, 239) ,
			new Color(247, 247, 247) ,
			new Color(230, 245, 208) ,
			new Color(161, 215, 106) ,
			new Color(77, 146, 33) };


		// div pallet of 8 elements
		public static final Color[] PIYG8 = new Color[]{
			new Color(197, 27, 125) ,
			new Color(222, 119, 174) ,
			new Color(241, 182, 218) ,
			new Color(253, 224, 239) ,
			new Color(230, 245, 208) ,
			new Color(184, 225, 134) ,
			new Color(127, 188, 65) ,
			new Color(77, 146, 33) };


		// div pallet of 9 elements
		public static final Color[] PIYG9 = new Color[]{
			new Color(197, 27, 125) ,
			new Color(222, 119, 174) ,
			new Color(241, 182, 218) ,
			new Color(253, 224, 239) ,
			new Color(247, 247, 247) ,
			new Color(230, 245, 208) ,
			new Color(184, 225, 134) ,
			new Color(127, 188, 65) ,
			new Color(77, 146, 33) };


		// div pallet of 10 elements
		public static final Color[] PIYG10 = new Color[]{
			new Color(142, 1, 82) ,
			new Color(197, 27, 125) ,
			new Color(222, 119, 174) ,
			new Color(241, 182, 218) ,
			new Color(253, 224, 239) ,
			new Color(230, 245, 208) ,
			new Color(184, 225, 134) ,
			new Color(127, 188, 65) ,
			new Color(77, 146, 33) ,
			new Color(39, 100, 25) };


		// div pallet of 11 elements
		public static final Color[] PIYG11 = new Color[]{
			new Color(142, 1, 82) ,
			new Color(197, 27, 125) ,
			new Color(222, 119, 174) ,
			new Color(241, 182, 218) ,
			new Color(253, 224, 239) ,
			new Color(247, 247, 247) ,
			new Color(230, 245, 208) ,
			new Color(184, 225, 134) ,
			new Color(127, 188, 65) ,
			new Color(77, 146, 33) ,
			new Color(39, 100, 25) };


		// div pallet of 3 elements
		public static final Color[] PRGN3 = new Color[]{
			new Color(175, 141, 195) ,
			new Color(247, 247, 247) ,
			new Color(127, 191, 123) };


		// div pallet of 4 elements
		public static final Color[] PRGN4 = new Color[]{
			new Color(123, 50, 148) ,
			new Color(194, 165, 207) ,
			new Color(166, 219, 160) ,
			new Color(0, 136, 55) };


		// div pallet of 5 elements
		public static final Color[] PRGN5 = new Color[]{
			new Color(123, 50, 148) ,
			new Color(194, 165, 207) ,
			new Color(247, 247, 247) ,
			new Color(166, 219, 160) ,
			new Color(0, 136, 55) };


		// div pallet of 6 elements
		public static final Color[] PRGN6 = new Color[]{
			new Color(118, 42, 131) ,
			new Color(175, 141, 195) ,
			new Color(231, 212, 232) ,
			new Color(217, 240, 211) ,
			new Color(127, 191, 123) ,
			new Color(27, 120, 55) };


		// div pallet of 7 elements
		public static final Color[] PRGN7 = new Color[]{
			new Color(118, 42, 131) ,
			new Color(175, 141, 195) ,
			new Color(231, 212, 232) ,
			new Color(247, 247, 247) ,
			new Color(217, 240, 211) ,
			new Color(127, 191, 123) ,
			new Color(27, 120, 55) };


		// div pallet of 8 elements
		public static final Color[] PRGN8 = new Color[]{
			new Color(118, 42, 131) ,
			new Color(153, 112, 171) ,
			new Color(194, 165, 207) ,
			new Color(231, 212, 232) ,
			new Color(217, 240, 211) ,
			new Color(166, 219, 160) ,
			new Color(90, 174, 97) ,
			new Color(27, 120, 55) };


		// div pallet of 9 elements
		public static final Color[] PRGN9 = new Color[]{
			new Color(118, 42, 131) ,
			new Color(153, 112, 171) ,
			new Color(194, 165, 207) ,
			new Color(231, 212, 232) ,
			new Color(247, 247, 247) ,
			new Color(217, 240, 211) ,
			new Color(166, 219, 160) ,
			new Color(90, 174, 97) ,
			new Color(27, 120, 55) };


		// div pallet of 10 elements
		public static final Color[] PRGN10 = new Color[]{
			new Color(64, 0, 75) ,
			new Color(118, 42, 131) ,
			new Color(153, 112, 171) ,
			new Color(194, 165, 207) ,
			new Color(231, 212, 232) ,
			new Color(217, 240, 211) ,
			new Color(166, 219, 160) ,
			new Color(90, 174, 97) ,
			new Color(27, 120, 55) ,
			new Color(0, 68, 27) };


		// div pallet of 11 elements
		public static final Color[] PRGN11 = new Color[]{
			new Color(64, 0, 75) ,
			new Color(118, 42, 131) ,
			new Color(153, 112, 171) ,
			new Color(194, 165, 207) ,
			new Color(231, 212, 232) ,
			new Color(247, 247, 247) ,
			new Color(217, 240, 211) ,
			new Color(166, 219, 160) ,
			new Color(90, 174, 97) ,
			new Color(27, 120, 55) ,
			new Color(0, 68, 27) };


		// seq pallet of 3 elements
		public static final Color[] PUBU3 = new Color[]{
			new Color(236, 231, 242) ,
			new Color(166, 189, 219) ,
			new Color(43, 140, 190) };


		// seq pallet of 4 elements
		public static final Color[] PUBU4 = new Color[]{
			new Color(241, 238, 246) ,
			new Color(189, 201, 225) ,
			new Color(116, 169, 207) ,
			new Color(5, 112, 176) };


		// seq pallet of 5 elements
		public static final Color[] PUBU5 = new Color[]{
			new Color(241, 238, 246) ,
			new Color(189, 201, 225) ,
			new Color(116, 169, 207) ,
			new Color(43, 140, 190) ,
			new Color(4, 90, 141) };


		// seq pallet of 6 elements
		public static final Color[] PUBU6 = new Color[]{
			new Color(241, 238, 246) ,
			new Color(208, 209, 230) ,
			new Color(166, 189, 219) ,
			new Color(116, 169, 207) ,
			new Color(43, 140, 190) ,
			new Color(4, 90, 141) };


		// seq pallet of 7 elements
		public static final Color[] PUBU7 = new Color[]{
			new Color(241, 238, 246) ,
			new Color(208, 209, 230) ,
			new Color(166, 189, 219) ,
			new Color(116, 169, 207) ,
			new Color(54, 144, 192) ,
			new Color(5, 112, 176) ,
			new Color(3, 78, 123) };


		// seq pallet of 8 elements
		public static final Color[] PUBU8 = new Color[]{
			new Color(255, 247, 251) ,
			new Color(236, 231, 242) ,
			new Color(208, 209, 230) ,
			new Color(166, 189, 219) ,
			new Color(116, 169, 207) ,
			new Color(54, 144, 192) ,
			new Color(5, 112, 176) ,
			new Color(3, 78, 123) };


		// seq pallet of 9 elements
		public static final Color[] PUBU9 = new Color[]{
			new Color(255, 247, 251) ,
			new Color(236, 231, 242) ,
			new Color(208, 209, 230) ,
			new Color(166, 189, 219) ,
			new Color(116, 169, 207) ,
			new Color(54, 144, 192) ,
			new Color(5, 112, 176) ,
			new Color(4, 90, 141) ,
			new Color(2, 56, 88) };


		// seq pallet of 3 elements
		public static final Color[] PUBUGN3 = new Color[]{
			new Color(236, 226, 240) ,
			new Color(166, 189, 219) ,
			new Color(28, 144, 153) };


		// seq pallet of 4 elements
		public static final Color[] PUBUGN4 = new Color[]{
			new Color(246, 239, 247) ,
			new Color(189, 201, 225) ,
			new Color(103, 169, 207) ,
			new Color(2, 129, 138) };


		// seq pallet of 5 elements
		public static final Color[] PUBUGN5 = new Color[]{
			new Color(246, 239, 247) ,
			new Color(189, 201, 225) ,
			new Color(103, 169, 207) ,
			new Color(28, 144, 153) ,
			new Color(1, 108, 89) };


		// seq pallet of 6 elements
		public static final Color[] PUBUGN6 = new Color[]{
			new Color(246, 239, 247) ,
			new Color(208, 209, 230) ,
			new Color(166, 189, 219) ,
			new Color(103, 169, 207) ,
			new Color(28, 144, 153) ,
			new Color(1, 108, 89) };


		// seq pallet of 7 elements
		public static final Color[] PUBUGN7 = new Color[]{
			new Color(246, 239, 247) ,
			new Color(208, 209, 230) ,
			new Color(166, 189, 219) ,
			new Color(103, 169, 207) ,
			new Color(54, 144, 192) ,
			new Color(2, 129, 138) ,
			new Color(1, 100, 80) };


		// seq pallet of 8 elements
		public static final Color[] PUBUGN8 = new Color[]{
			new Color(255, 247, 251) ,
			new Color(236, 226, 240) ,
			new Color(208, 209, 230) ,
			new Color(166, 189, 219) ,
			new Color(103, 169, 207) ,
			new Color(54, 144, 192) ,
			new Color(2, 129, 138) ,
			new Color(1, 100, 80) };


		// seq pallet of 9 elements
		public static final Color[] PUBUGN9 = new Color[]{
			new Color(255, 247, 251) ,
			new Color(236, 226, 240) ,
			new Color(208, 209, 230) ,
			new Color(166, 189, 219) ,
			new Color(103, 169, 207) ,
			new Color(54, 144, 192) ,
			new Color(2, 129, 138) ,
			new Color(1, 108, 89) ,
			new Color(1, 70, 54) };


		// div pallet of 3 elements
		public static final Color[] PUOR3 = new Color[]{
			new Color(241, 163, 64) ,
			new Color(247, 247, 247) ,
			new Color(153, 142, 195) };


		// div pallet of 4 elements
		public static final Color[] PUOR4 = new Color[]{
			new Color(230, 97, 1) ,
			new Color(253, 184, 99) ,
			new Color(178, 171, 210) ,
			new Color(94, 60, 153) };


		// div pallet of 5 elements
		public static final Color[] PUOR5 = new Color[]{
			new Color(230, 97, 1) ,
			new Color(253, 184, 99) ,
			new Color(247, 247, 247) ,
			new Color(178, 171, 210) ,
			new Color(94, 60, 153) };


		// div pallet of 6 elements
		public static final Color[] PUOR6 = new Color[]{
			new Color(179, 88, 6) ,
			new Color(241, 163, 64) ,
			new Color(254, 224, 182) ,
			new Color(216, 218, 235) ,
			new Color(153, 142, 195) ,
			new Color(84, 39, 136) };


		// div pallet of 7 elements
		public static final Color[] PUOR7 = new Color[]{
			new Color(179, 88, 6) ,
			new Color(241, 163, 64) ,
			new Color(254, 224, 182) ,
			new Color(247, 247, 247) ,
			new Color(216, 218, 235) ,
			new Color(153, 142, 195) ,
			new Color(84, 39, 136) };


		// div pallet of 8 elements
		public static final Color[] PUOR8 = new Color[]{
			new Color(179, 88, 6) ,
			new Color(224, 130, 20) ,
			new Color(253, 184, 99) ,
			new Color(254, 224, 182) ,
			new Color(216, 218, 235) ,
			new Color(178, 171, 210) ,
			new Color(128, 115, 172) ,
			new Color(84, 39, 136) };


		// div pallet of 9 elements
		public static final Color[] PUOR9 = new Color[]{
			new Color(179, 88, 6) ,
			new Color(224, 130, 20) ,
			new Color(253, 184, 99) ,
			new Color(254, 224, 182) ,
			new Color(247, 247, 247) ,
			new Color(216, 218, 235) ,
			new Color(178, 171, 210) ,
			new Color(128, 115, 172) ,
			new Color(84, 39, 136) };


		// div pallet of 10 elements
		public static final Color[] PUOR10 = new Color[]{
			new Color(127, 59, 8) ,
			new Color(179, 88, 6) ,
			new Color(224, 130, 20) ,
			new Color(253, 184, 99) ,
			new Color(254, 224, 182) ,
			new Color(216, 218, 235) ,
			new Color(178, 171, 210) ,
			new Color(128, 115, 172) ,
			new Color(84, 39, 136) ,
			new Color(45, 0, 75) };


		// div pallet of 11 elements
		public static final Color[] PUOR11 = new Color[]{
			new Color(127, 59, 8) ,
			new Color(179, 88, 6) ,
			new Color(224, 130, 20) ,
			new Color(253, 184, 99) ,
			new Color(254, 224, 182) ,
			new Color(247, 247, 247) ,
			new Color(216, 218, 235) ,
			new Color(178, 171, 210) ,
			new Color(128, 115, 172) ,
			new Color(84, 39, 136) ,
			new Color(45, 0, 75) };


		// seq pallet of 3 elements
		public static final Color[] PURD3 = new Color[]{
			new Color(231, 225, 239) ,
			new Color(201, 148, 199) ,
			new Color(221, 28, 119) };


		// seq pallet of 4 elements
		public static final Color[] PURD4 = new Color[]{
			new Color(241, 238, 246) ,
			new Color(215, 181, 216) ,
			new Color(223, 101, 176) ,
			new Color(206, 18, 86) };


		// seq pallet of 5 elements
		public static final Color[] PURD5 = new Color[]{
			new Color(241, 238, 246) ,
			new Color(215, 181, 216) ,
			new Color(223, 101, 176) ,
			new Color(221, 28, 119) ,
			new Color(152, 0, 67) };


		// seq pallet of 6 elements
		public static final Color[] PURD6 = new Color[]{
			new Color(241, 238, 246) ,
			new Color(212, 185, 218) ,
			new Color(201, 148, 199) ,
			new Color(223, 101, 176) ,
			new Color(221, 28, 119) ,
			new Color(152, 0, 67) };


		// seq pallet of 7 elements
		public static final Color[] PURD7 = new Color[]{
			new Color(241, 238, 246) ,
			new Color(212, 185, 218) ,
			new Color(201, 148, 199) ,
			new Color(223, 101, 176) ,
			new Color(231, 41, 138) ,
			new Color(206, 18, 86) ,
			new Color(145, 0, 63) };


		// seq pallet of 8 elements
		public static final Color[] PURD8 = new Color[]{
			new Color(247, 244, 249) ,
			new Color(231, 225, 239) ,
			new Color(212, 185, 218) ,
			new Color(201, 148, 199) ,
			new Color(223, 101, 176) ,
			new Color(231, 41, 138) ,
			new Color(206, 18, 86) ,
			new Color(145, 0, 63) };


		// seq pallet of 9 elements
		public static final Color[] PURD9 = new Color[]{
			new Color(247, 244, 249) ,
			new Color(231, 225, 239) ,
			new Color(212, 185, 218) ,
			new Color(201, 148, 199) ,
			new Color(223, 101, 176) ,
			new Color(231, 41, 138) ,
			new Color(206, 18, 86) ,
			new Color(152, 0, 67) ,
			new Color(103, 0, 31) };


		// seq pallet of 3 elements
		public static final Color[] PURPLES3 = new Color[]{
			new Color(239, 237, 245) ,
			new Color(188, 189, 220) ,
			new Color(117, 107, 177) };


		// seq pallet of 4 elements
		public static final Color[] PURPLES4 = new Color[]{
			new Color(242, 240, 247) ,
			new Color(203, 201, 226) ,
			new Color(158, 154, 200) ,
			new Color(106, 81, 163) };


		// seq pallet of 5 elements
		public static final Color[] PURPLES5 = new Color[]{
			new Color(242, 240, 247) ,
			new Color(203, 201, 226) ,
			new Color(158, 154, 200) ,
			new Color(117, 107, 177) ,
			new Color(84, 39, 143) };


		// seq pallet of 6 elements
		public static final Color[] PURPLES6 = new Color[]{
			new Color(242, 240, 247) ,
			new Color(218, 218, 235) ,
			new Color(188, 189, 220) ,
			new Color(158, 154, 200) ,
			new Color(117, 107, 177) ,
			new Color(84, 39, 143) };


		// seq pallet of 7 elements
		public static final Color[] PURPLES7 = new Color[]{
			new Color(242, 240, 247) ,
			new Color(218, 218, 235) ,
			new Color(188, 189, 220) ,
			new Color(158, 154, 200) ,
			new Color(128, 125, 186) ,
			new Color(106, 81, 163) ,
			new Color(74, 20, 134) };


		// seq pallet of 8 elements
		public static final Color[] PURPLES8 = new Color[]{
			new Color(252, 251, 253) ,
			new Color(239, 237, 245) ,
			new Color(218, 218, 235) ,
			new Color(188, 189, 220) ,
			new Color(158, 154, 200) ,
			new Color(128, 125, 186) ,
			new Color(106, 81, 163) ,
			new Color(74, 20, 134) };


		// seq pallet of 9 elements
		public static final Color[] PURPLES9 = new Color[]{
			new Color(252, 251, 253) ,
			new Color(239, 237, 245) ,
			new Color(218, 218, 235) ,
			new Color(188, 189, 220) ,
			new Color(158, 154, 200) ,
			new Color(128, 125, 186) ,
			new Color(106, 81, 163) ,
			new Color(84, 39, 143) ,
			new Color(63, 0, 125) };


		// div pallet of 3 elements
		public static final Color[] RDBU3 = new Color[]{
			new Color(239, 138, 98) ,
			new Color(247, 247, 247) ,
			new Color(103, 169, 207) };


		// div pallet of 4 elements
		public static final Color[] RDBU4 = new Color[]{
			new Color(202, 0, 32) ,
			new Color(244, 165, 130) ,
			new Color(146, 197, 222) ,
			new Color(5, 113, 176) };


		// div pallet of 5 elements
		public static final Color[] RDBU5 = new Color[]{
			new Color(202, 0, 32) ,
			new Color(244, 165, 130) ,
			new Color(247, 247, 247) ,
			new Color(146, 197, 222) ,
			new Color(5, 113, 176) };


		// div pallet of 6 elements
		public static final Color[] RDBU6 = new Color[]{
			new Color(178, 24, 43) ,
			new Color(239, 138, 98) ,
			new Color(253, 219, 199) ,
			new Color(209, 229, 240) ,
			new Color(103, 169, 207) ,
			new Color(33, 102, 172) };


		// div pallet of 7 elements
		public static final Color[] RDBU7 = new Color[]{
			new Color(178, 24, 43) ,
			new Color(239, 138, 98) ,
			new Color(253, 219, 199) ,
			new Color(247, 247, 247) ,
			new Color(209, 229, 240) ,
			new Color(103, 169, 207) ,
			new Color(33, 102, 172) };


		// div pallet of 8 elements
		public static final Color[] RDBU8 = new Color[]{
			new Color(178, 24, 43) ,
			new Color(214, 96, 77) ,
			new Color(244, 165, 130) ,
			new Color(253, 219, 199) ,
			new Color(209, 229, 240) ,
			new Color(146, 197, 222) ,
			new Color(67, 147, 195) ,
			new Color(33, 102, 172) };


		// div pallet of 9 elements
		public static final Color[] RDBU9 = new Color[]{
			new Color(178, 24, 43) ,
			new Color(214, 96, 77) ,
			new Color(244, 165, 130) ,
			new Color(253, 219, 199) ,
			new Color(247, 247, 247) ,
			new Color(209, 229, 240) ,
			new Color(146, 197, 222) ,
			new Color(67, 147, 195) ,
			new Color(33, 102, 172) };


		// div pallet of 10 elements
		public static final Color[] RDBU10 = new Color[]{
			new Color(103, 0, 31) ,
			new Color(178, 24, 43) ,
			new Color(214, 96, 77) ,
			new Color(244, 165, 130) ,
			new Color(253, 219, 199) ,
			new Color(209, 229, 240) ,
			new Color(146, 197, 222) ,
			new Color(67, 147, 195) ,
			new Color(33, 102, 172) ,
			new Color(5, 48, 97) };


		// div pallet of 11 elements
		public static final Color[] RDBU11 = new Color[]{
			new Color(103, 0, 31) ,
			new Color(178, 24, 43) ,
			new Color(214, 96, 77) ,
			new Color(244, 165, 130) ,
			new Color(253, 219, 199) ,
			new Color(247, 247, 247) ,
			new Color(209, 229, 240) ,
			new Color(146, 197, 222) ,
			new Color(67, 147, 195) ,
			new Color(33, 102, 172) ,
			new Color(5, 48, 97) };


		// div pallet of 3 elements
		public static final Color[] RDGY3 = new Color[]{
			new Color(239, 138, 98) ,
			new Color(255, 255, 255) ,
			new Color(153, 153, 153) };


		// div pallet of 4 elements
		public static final Color[] RDGY4 = new Color[]{
			new Color(202, 0, 32) ,
			new Color(244, 165, 130) ,
			new Color(186, 186, 186) ,
			new Color(64, 64, 64) };


		// div pallet of 5 elements
		public static final Color[] RDGY5 = new Color[]{
			new Color(202, 0, 32) ,
			new Color(244, 165, 130) ,
			new Color(255, 255, 255) ,
			new Color(186, 186, 186) ,
			new Color(64, 64, 64) };


		// div pallet of 6 elements
		public static final Color[] RDGY6 = new Color[]{
			new Color(178, 24, 43) ,
			new Color(239, 138, 98) ,
			new Color(253, 219, 199) ,
			new Color(224, 224, 224) ,
			new Color(153, 153, 153) ,
			new Color(77, 77, 77) };


		// div pallet of 7 elements
		public static final Color[] RDGY7 = new Color[]{
			new Color(178, 24, 43) ,
			new Color(239, 138, 98) ,
			new Color(253, 219, 199) ,
			new Color(255, 255, 255) ,
			new Color(224, 224, 224) ,
			new Color(153, 153, 153) ,
			new Color(77, 77, 77) };


		// div pallet of 8 elements
		public static final Color[] RDGY8 = new Color[]{
			new Color(178, 24, 43) ,
			new Color(214, 96, 77) ,
			new Color(244, 165, 130) ,
			new Color(253, 219, 199) ,
			new Color(224, 224, 224) ,
			new Color(186, 186, 186) ,
			new Color(135, 135, 135) ,
			new Color(77, 77, 77) };


		// div pallet of 9 elements
		public static final Color[] RDGY9 = new Color[]{
			new Color(178, 24, 43) ,
			new Color(214, 96, 77) ,
			new Color(244, 165, 130) ,
			new Color(253, 219, 199) ,
			new Color(255, 255, 255) ,
			new Color(224, 224, 224) ,
			new Color(186, 186, 186) ,
			new Color(135, 135, 135) ,
			new Color(77, 77, 77) };


		// div pallet of 10 elements
		public static final Color[] RDGY10 = new Color[]{
			new Color(103, 0, 31) ,
			new Color(178, 24, 43) ,
			new Color(214, 96, 77) ,
			new Color(244, 165, 130) ,
			new Color(253, 219, 199) ,
			new Color(224, 224, 224) ,
			new Color(186, 186, 186) ,
			new Color(135, 135, 135) ,
			new Color(77, 77, 77) ,
			new Color(26, 26, 26) };


		// div pallet of 11 elements
		public static final Color[] RDGY11 = new Color[]{
			new Color(103, 0, 31) ,
			new Color(178, 24, 43) ,
			new Color(214, 96, 77) ,
			new Color(244, 165, 130) ,
			new Color(253, 219, 199) ,
			new Color(255, 255, 255) ,
			new Color(224, 224, 224) ,
			new Color(186, 186, 186) ,
			new Color(135, 135, 135) ,
			new Color(77, 77, 77) ,
			new Color(26, 26, 26) };


		// seq pallet of 3 elements
		public static final Color[] RDPU3 = new Color[]{
			new Color(253, 224, 221) ,
			new Color(250, 159, 181) ,
			new Color(197, 27, 138) };


		// seq pallet of 4 elements
		public static final Color[] RDPU4 = new Color[]{
			new Color(254, 235, 226) ,
			new Color(251, 180, 185) ,
			new Color(247, 104, 161) ,
			new Color(174, 1, 126) };


		// seq pallet of 5 elements
		public static final Color[] RDPU5 = new Color[]{
			new Color(254, 235, 226) ,
			new Color(251, 180, 185) ,
			new Color(247, 104, 161) ,
			new Color(197, 27, 138) ,
			new Color(122, 1, 119) };


		// seq pallet of 6 elements
		public static final Color[] RDPU6 = new Color[]{
			new Color(254, 235, 226) ,
			new Color(252, 197, 192) ,
			new Color(250, 159, 181) ,
			new Color(247, 104, 161) ,
			new Color(197, 27, 138) ,
			new Color(122, 1, 119) };


		// seq pallet of 7 elements
		public static final Color[] RDPU7 = new Color[]{
			new Color(254, 235, 226) ,
			new Color(252, 197, 192) ,
			new Color(250, 159, 181) ,
			new Color(247, 104, 161) ,
			new Color(221, 52, 151) ,
			new Color(174, 1, 126) ,
			new Color(122, 1, 119) };


		// seq pallet of 8 elements
		public static final Color[] RDPU8 = new Color[]{
			new Color(255, 247, 243) ,
			new Color(253, 224, 221) ,
			new Color(252, 197, 192) ,
			new Color(250, 159, 181) ,
			new Color(247, 104, 161) ,
			new Color(221, 52, 151) ,
			new Color(174, 1, 126) ,
			new Color(122, 1, 119) };


		// seq pallet of 9 elements
		public static final Color[] RDPU9 = new Color[]{
			new Color(255, 247, 243) ,
			new Color(253, 224, 221) ,
			new Color(252, 197, 192) ,
			new Color(250, 159, 181) ,
			new Color(247, 104, 161) ,
			new Color(221, 52, 151) ,
			new Color(174, 1, 126) ,
			new Color(122, 1, 119) ,
			new Color(73, 0, 106) };


		// seq pallet of 3 elements
		public static final Color[] REDS3 = new Color[]{
			new Color(254, 224, 210) ,
			new Color(252, 146, 114) ,
			new Color(222, 45, 38) };


		// seq pallet of 4 elements
		public static final Color[] REDS4 = new Color[]{
			new Color(254, 229, 217) ,
			new Color(252, 174, 145) ,
			new Color(251, 106, 74) ,
			new Color(203, 24, 29) };


		// seq pallet of 5 elements
		public static final Color[] REDS5 = new Color[]{
			new Color(254, 229, 217) ,
			new Color(252, 174, 145) ,
			new Color(251, 106, 74) ,
			new Color(222, 45, 38) ,
			new Color(165, 15, 21) };


		// seq pallet of 6 elements
		public static final Color[] REDS6 = new Color[]{
			new Color(254, 229, 217) ,
			new Color(252, 187, 161) ,
			new Color(252, 146, 114) ,
			new Color(251, 106, 74) ,
			new Color(222, 45, 38) ,
			new Color(165, 15, 21) };


		// seq pallet of 7 elements
		public static final Color[] REDS7 = new Color[]{
			new Color(254, 229, 217) ,
			new Color(252, 187, 161) ,
			new Color(252, 146, 114) ,
			new Color(251, 106, 74) ,
			new Color(239, 59, 44) ,
			new Color(203, 24, 29) ,
			new Color(153, 0, 13) };


		// seq pallet of 8 elements
		public static final Color[] REDS8 = new Color[]{
			new Color(255, 245, 240) ,
			new Color(254, 224, 210) ,
			new Color(252, 187, 161) ,
			new Color(252, 146, 114) ,
			new Color(251, 106, 74) ,
			new Color(239, 59, 44) ,
			new Color(203, 24, 29) ,
			new Color(153, 0, 13) };


		// seq pallet of 9 elements
		public static final Color[] REDS9 = new Color[]{
			new Color(255, 245, 240) ,
			new Color(254, 224, 210) ,
			new Color(252, 187, 161) ,
			new Color(252, 146, 114) ,
			new Color(251, 106, 74) ,
			new Color(239, 59, 44) ,
			new Color(203, 24, 29) ,
			new Color(165, 15, 21) ,
			new Color(103, 0, 13) };


		// div pallet of 3 elements
		public static final Color[] RDYLBU3 = new Color[]{
			new Color(252, 141, 89) ,
			new Color(255, 255, 191) ,
			new Color(145, 191, 219) };


		// div pallet of 4 elements
		public static final Color[] RDYLBU4 = new Color[]{
			new Color(215, 25, 28) ,
			new Color(253, 174, 97) ,
			new Color(171, 217, 233) ,
			new Color(44, 123, 182) };


		// div pallet of 5 elements
		public static final Color[] RDYLBU5 = new Color[]{
			new Color(215, 25, 28) ,
			new Color(253, 174, 97) ,
			new Color(255, 255, 191) ,
			new Color(171, 217, 233) ,
			new Color(44, 123, 182) };


		// div pallet of 6 elements
		public static final Color[] RDYLBU6 = new Color[]{
			new Color(215, 48, 39) ,
			new Color(252, 141, 89) ,
			new Color(254, 224, 144) ,
			new Color(224, 243, 248) ,
			new Color(145, 191, 219) ,
			new Color(69, 117, 180) };


		// div pallet of 7 elements
		public static final Color[] RDYLBU7 = new Color[]{
			new Color(215, 48, 39) ,
			new Color(252, 141, 89) ,
			new Color(254, 224, 144) ,
			new Color(255, 255, 191) ,
			new Color(224, 243, 248) ,
			new Color(145, 191, 219) ,
			new Color(69, 117, 180) };


		// div pallet of 8 elements
		public static final Color[] RDYLBU8 = new Color[]{
			new Color(215, 48, 39) ,
			new Color(244, 109, 67) ,
			new Color(253, 174, 97) ,
			new Color(254, 224, 144) ,
			new Color(224, 243, 248) ,
			new Color(171, 217, 233) ,
			new Color(116, 173, 209) ,
			new Color(69, 117, 180) };


		// div pallet of 9 elements
		public static final Color[] RDYLBU9 = new Color[]{
			new Color(215, 48, 39) ,
			new Color(244, 109, 67) ,
			new Color(253, 174, 97) ,
			new Color(254, 224, 144) ,
			new Color(255, 255, 191) ,
			new Color(224, 243, 248) ,
			new Color(171, 217, 233) ,
			new Color(116, 173, 209) ,
			new Color(69, 117, 180) };


		// div pallet of 10 elements
		public static final Color[] RDYLBU10 = new Color[]{
			new Color(165, 0, 38) ,
			new Color(215, 48, 39) ,
			new Color(244, 109, 67) ,
			new Color(253, 174, 97) ,
			new Color(254, 224, 144) ,
			new Color(224, 243, 248) ,
			new Color(171, 217, 233) ,
			new Color(116, 173, 209) ,
			new Color(69, 117, 180) ,
			new Color(49, 54, 149) };


		// div pallet of 11 elements
		public static final Color[] RDYLBU11 = new Color[]{
			new Color(165, 0, 38) ,
			new Color(215, 48, 39) ,
			new Color(244, 109, 67) ,
			new Color(253, 174, 97) ,
			new Color(254, 224, 144) ,
			new Color(255, 255, 191) ,
			new Color(224, 243, 248) ,
			new Color(171, 217, 233) ,
			new Color(116, 173, 209) ,
			new Color(69, 117, 180) ,
			new Color(49, 54, 149) };


		// div pallet of 3 elements
		public static final Color[] RDYLGN3 = new Color[]{
			new Color(252, 141, 89) ,
			new Color(255, 255, 191) ,
			new Color(145, 207, 96) };


		// div pallet of 4 elements
		public static final Color[] RDYLGN4 = new Color[]{
			new Color(215, 25, 28) ,
			new Color(253, 174, 97) ,
			new Color(166, 217, 106) ,
			new Color(26, 150, 65) };


		// div pallet of 5 elements
		public static final Color[] RDYLGN5 = new Color[]{
			new Color(215, 25, 28) ,
			new Color(253, 174, 97) ,
			new Color(255, 255, 191) ,
			new Color(166, 217, 106) ,
			new Color(26, 150, 65) };


		// div pallet of 6 elements
		public static final Color[] RDYLGN6 = new Color[]{
			new Color(215, 48, 39) ,
			new Color(252, 141, 89) ,
			new Color(254, 224, 139) ,
			new Color(217, 239, 139) ,
			new Color(145, 207, 96) ,
			new Color(26, 152, 80) };


		// div pallet of 7 elements
		public static final Color[] RDYLGN7 = new Color[]{
			new Color(215, 48, 39) ,
			new Color(252, 141, 89) ,
			new Color(254, 224, 139) ,
			new Color(255, 255, 191) ,
			new Color(217, 239, 139) ,
			new Color(145, 207, 96) ,
			new Color(26, 152, 80) };


		// div pallet of 8 elements
		public static final Color[] RDYLGN8 = new Color[]{
			new Color(215, 48, 39) ,
			new Color(244, 109, 67) ,
			new Color(253, 174, 97) ,
			new Color(254, 224, 139) ,
			new Color(217, 239, 139) ,
			new Color(166, 217, 106) ,
			new Color(102, 189, 99) ,
			new Color(26, 152, 80) };


		// div pallet of 9 elements
		public static final Color[] RDYLGN9 = new Color[]{
			new Color(215, 48, 39) ,
			new Color(244, 109, 67) ,
			new Color(253, 174, 97) ,
			new Color(254, 224, 139) ,
			new Color(255, 255, 191) ,
			new Color(217, 239, 139) ,
			new Color(166, 217, 106) ,
			new Color(102, 189, 99) ,
			new Color(26, 152, 80) };


		// div pallet of 10 elements
		public static final Color[] RDYLGN10 = new Color[]{
			new Color(165, 0, 38) ,
			new Color(215, 48, 39) ,
			new Color(244, 109, 67) ,
			new Color(253, 174, 97) ,
			new Color(254, 224, 139) ,
			new Color(217, 239, 139) ,
			new Color(166, 217, 106) ,
			new Color(102, 189, 99) ,
			new Color(26, 152, 80) ,
			new Color(0, 104, 55) };


		// div pallet of 11 elements
		public static final Color[] RDYLGN11 = new Color[]{
			new Color(165, 0, 38) ,
			new Color(215, 48, 39) ,
			new Color(244, 109, 67) ,
			new Color(253, 174, 97) ,
			new Color(254, 224, 139) ,
			new Color(255, 255, 191) ,
			new Color(217, 239, 139) ,
			new Color(166, 217, 106) ,
			new Color(102, 189, 99) ,
			new Color(26, 152, 80) ,
			new Color(0, 104, 55) };


		// qual pallet of 3 elements
		public static final Color[] SET13 = new Color[]{
			new Color(228, 26, 28) ,
			new Color(55, 126, 184) ,
			new Color(77, 175, 74) };


		// qual pallet of 4 elements
		public static final Color[] SET14 = new Color[]{
			new Color(228, 26, 28) ,
			new Color(55, 126, 184) ,
			new Color(77, 175, 74) ,
			new Color(152, 78, 163) };


		// qual pallet of 5 elements
		public static final Color[] SET15 = new Color[]{
			new Color(228, 26, 28) ,
			new Color(55, 126, 184) ,
			new Color(77, 175, 74) ,
			new Color(152, 78, 163) ,
			new Color(255, 127, 0) };


		// qual pallet of 6 elements
		public static final Color[] SET16 = new Color[]{
			new Color(228, 26, 28) ,
			new Color(55, 126, 184) ,
			new Color(77, 175, 74) ,
			new Color(152, 78, 163) ,
			new Color(255, 127, 0) ,
			new Color(255, 255, 51) };


		// qual pallet of 7 elements
		public static final Color[] SET17 = new Color[]{
			new Color(228, 26, 28) ,
			new Color(55, 126, 184) ,
			new Color(77, 175, 74) ,
			new Color(152, 78, 163) ,
			new Color(255, 127, 0) ,
			new Color(255, 255, 51) ,
			new Color(166, 86, 40) };


		// qual pallet of 8 elements
		public static final Color[] SET18 = new Color[]{
			new Color(228, 26, 28) ,
			new Color(55, 126, 184) ,
			new Color(77, 175, 74) ,
			new Color(152, 78, 163) ,
			new Color(255, 127, 0) ,
			new Color(255, 255, 51) ,
			new Color(166, 86, 40) ,
			new Color(247, 129, 191) };


		// qual pallet of 9 elements
		public static final Color[] SET19 = new Color[]{
			new Color(228, 26, 28) ,
			new Color(55, 126, 184) ,
			new Color(77, 175, 74) ,
			new Color(152, 78, 163) ,
			new Color(255, 127, 0) ,
			new Color(255, 255, 51) ,
			new Color(166, 86, 40) ,
			new Color(247, 129, 191) ,
			new Color(153, 153, 153) };


		// qual pallet of 3 elements
		public static final Color[] SET23 = new Color[]{
			new Color(102, 194, 165) ,
			new Color(252, 141, 98) ,
			new Color(141, 160, 203) };


		// qual pallet of 4 elements
		public static final Color[] SET24 = new Color[]{
			new Color(102, 194, 165) ,
			new Color(252, 141, 98) ,
			new Color(141, 160, 203) ,
			new Color(231, 138, 195) };


		// qual pallet of 5 elements
		public static final Color[] SET25 = new Color[]{
			new Color(102, 194, 165) ,
			new Color(252, 141, 98) ,
			new Color(141, 160, 203) ,
			new Color(231, 138, 195) ,
			new Color(166, 216, 84) };


		// qual pallet of 6 elements
		public static final Color[] SET26 = new Color[]{
			new Color(102, 194, 165) ,
			new Color(252, 141, 98) ,
			new Color(141, 160, 203) ,
			new Color(231, 138, 195) ,
			new Color(166, 216, 84) ,
			new Color(255, 217, 47) };


		// qual pallet of 7 elements
		public static final Color[] SET27 = new Color[]{
			new Color(102, 194, 165) ,
			new Color(252, 141, 98) ,
			new Color(141, 160, 203) ,
			new Color(231, 138, 195) ,
			new Color(166, 216, 84) ,
			new Color(255, 217, 47) ,
			new Color(229, 196, 148) };


		// qual pallet of 8 elements
		public static final Color[] SET28 = new Color[]{
			new Color(102, 194, 165) ,
			new Color(252, 141, 98) ,
			new Color(141, 160, 203) ,
			new Color(231, 138, 195) ,
			new Color(166, 216, 84) ,
			new Color(255, 217, 47) ,
			new Color(229, 196, 148) ,
			new Color(179, 179, 179) };


		// qual pallet of 3 elements
		public static final Color[] SET33 = new Color[]{
			new Color(141, 211, 199) ,
			new Color(255, 255, 179) ,
			new Color(190, 186, 218) };


		// qual pallet of 4 elements
		public static final Color[] SET34 = new Color[]{
			new Color(141, 211, 199) ,
			new Color(255, 255, 179) ,
			new Color(190, 186, 218) ,
			new Color(251, 128, 114) };


		// qual pallet of 5 elements
		public static final Color[] SET35 = new Color[]{
			new Color(141, 211, 199) ,
			new Color(255, 255, 179) ,
			new Color(190, 186, 218) ,
			new Color(251, 128, 114) ,
			new Color(128, 177, 211) };


		// qual pallet of 6 elements
		public static final Color[] SET36 = new Color[]{
			new Color(141, 211, 199) ,
			new Color(255, 255, 179) ,
			new Color(190, 186, 218) ,
			new Color(251, 128, 114) ,
			new Color(128, 177, 211) ,
			new Color(253, 180, 98) };


		// qual pallet of 7 elements
		public static final Color[] SET37 = new Color[]{
			new Color(141, 211, 199) ,
			new Color(255, 255, 179) ,
			new Color(190, 186, 218) ,
			new Color(251, 128, 114) ,
			new Color(128, 177, 211) ,
			new Color(253, 180, 98) ,
			new Color(179, 222, 105) };


		// qual pallet of 8 elements
		public static final Color[] SET38 = new Color[]{
			new Color(141, 211, 199) ,
			new Color(255, 255, 179) ,
			new Color(190, 186, 218) ,
			new Color(251, 128, 114) ,
			new Color(128, 177, 211) ,
			new Color(253, 180, 98) ,
			new Color(179, 222, 105) ,
			new Color(252, 205, 229) };


		// qual pallet of 9 elements
		public static final Color[] SET39 = new Color[]{
			new Color(141, 211, 199) ,
			new Color(255, 255, 179) ,
			new Color(190, 186, 218) ,
			new Color(251, 128, 114) ,
			new Color(128, 177, 211) ,
			new Color(253, 180, 98) ,
			new Color(179, 222, 105) ,
			new Color(252, 205, 229) ,
			new Color(217, 217, 217) };


		// qual pallet of 10 elements
		public static final Color[] SET310 = new Color[]{
			new Color(141, 211, 199) ,
			new Color(255, 255, 179) ,
			new Color(190, 186, 218) ,
			new Color(251, 128, 114) ,
			new Color(128, 177, 211) ,
			new Color(253, 180, 98) ,
			new Color(179, 222, 105) ,
			new Color(252, 205, 229) ,
			new Color(217, 217, 217) ,
			new Color(188, 128, 189) };


		// qual pallet of 11 elements
		public static final Color[] SET311 = new Color[]{
			new Color(141, 211, 199) ,
			new Color(255, 255, 179) ,
			new Color(190, 186, 218) ,
			new Color(251, 128, 114) ,
			new Color(128, 177, 211) ,
			new Color(253, 180, 98) ,
			new Color(179, 222, 105) ,
			new Color(252, 205, 229) ,
			new Color(217, 217, 217) ,
			new Color(188, 128, 189) ,
			new Color(204, 235, 197) };


		// qual pallet of 12 elements
		public static final Color[] SET312 = new Color[]{
			new Color(141, 211, 199) ,
			new Color(255, 255, 179) ,
			new Color(190, 186, 218) ,
			new Color(251, 128, 114) ,
			new Color(128, 177, 211) ,
			new Color(253, 180, 98) ,
			new Color(179, 222, 105) ,
			new Color(252, 205, 229) ,
			new Color(217, 217, 217) ,
			new Color(188, 128, 189) ,
			new Color(204, 235, 197) ,
			new Color(255, 237, 111) };


		// div pallet of 3 elements
		public static final Color[] SPECTRAL3 = new Color[]{
			new Color(252, 141, 89) ,
			new Color(255, 255, 191) ,
			new Color(153, 213, 148) };


		// div pallet of 4 elements
		public static final Color[] SPECTRAL4 = new Color[]{
			new Color(215, 25, 28) ,
			new Color(253, 174, 97) ,
			new Color(171, 221, 164) ,
			new Color(43, 131, 186) };


		// div pallet of 5 elements
		public static final Color[] SPECTRAL5 = new Color[]{
			new Color(215, 25, 28) ,
			new Color(253, 174, 97) ,
			new Color(255, 255, 191) ,
			new Color(171, 221, 164) ,
			new Color(43, 131, 186) };


		// div pallet of 6 elements
		public static final Color[] SPECTRAL6 = new Color[]{
			new Color(213, 62, 79) ,
			new Color(252, 141, 89) ,
			new Color(254, 224, 139) ,
			new Color(230, 245, 152) ,
			new Color(153, 213, 148) ,
			new Color(50, 136, 189) };


		// div pallet of 7 elements
		public static final Color[] SPECTRAL7 = new Color[]{
			new Color(213, 62, 79) ,
			new Color(252, 141, 89) ,
			new Color(254, 224, 139) ,
			new Color(255, 255, 191) ,
			new Color(230, 245, 152) ,
			new Color(153, 213, 148) ,
			new Color(50, 136, 189) };


		// div pallet of 8 elements
		public static final Color[] SPECTRAL8 = new Color[]{
			new Color(213, 62, 79) ,
			new Color(244, 109, 67) ,
			new Color(253, 174, 97) ,
			new Color(254, 224, 139) ,
			new Color(230, 245, 152) ,
			new Color(171, 221, 164) ,
			new Color(102, 194, 165) ,
			new Color(50, 136, 189) };


		// div pallet of 9 elements
		public static final Color[] SPECTRAL9 = new Color[]{
			new Color(213, 62, 79) ,
			new Color(244, 109, 67) ,
			new Color(253, 174, 97) ,
			new Color(254, 224, 139) ,
			new Color(255, 255, 191) ,
			new Color(230, 245, 152) ,
			new Color(171, 221, 164) ,
			new Color(102, 194, 165) ,
			new Color(50, 136, 189) };


		// div pallet of 10 elements
		public static final Color[] SPECTRAL10 = new Color[]{
			new Color(158, 1, 66) ,
			new Color(213, 62, 79) ,
			new Color(244, 109, 67) ,
			new Color(253, 174, 97) ,
			new Color(254, 224, 139) ,
			new Color(230, 245, 152) ,
			new Color(171, 221, 164) ,
			new Color(102, 194, 165) ,
			new Color(50, 136, 189) ,
			new Color(94, 79, 162) };


		// div pallet of 11 elements
		public static final Color[] SPECTRAL11 = new Color[]{
			new Color(158, 1, 66) ,
			new Color(213, 62, 79) ,
			new Color(244, 109, 67) ,
			new Color(253, 174, 97) ,
			new Color(254, 224, 139) ,
			new Color(255, 255, 191) ,
			new Color(230, 245, 152) ,
			new Color(171, 221, 164) ,
			new Color(102, 194, 165) ,
			new Color(50, 136, 189) ,
			new Color(94, 79, 162) };


		// seq pallet of 3 elements
		public static final Color[] YLGN3 = new Color[]{
			new Color(247, 252, 185) ,
			new Color(173, 221, 142) ,
			new Color(49, 163, 84) };


		// seq pallet of 4 elements
		public static final Color[] YLGN4 = new Color[]{
			new Color(255, 255, 204) ,
			new Color(194, 230, 153) ,
			new Color(120, 198, 121) ,
			new Color(35, 132, 67) };


		// seq pallet of 5 elements
		public static final Color[] YLGN5 = new Color[]{
			new Color(255, 255, 204) ,
			new Color(194, 230, 153) ,
			new Color(120, 198, 121) ,
			new Color(49, 163, 84) ,
			new Color(0, 104, 55) };


		// seq pallet of 6 elements
		public static final Color[] YLGN6 = new Color[]{
			new Color(255, 255, 204) ,
			new Color(217, 240, 163) ,
			new Color(173, 221, 142) ,
			new Color(120, 198, 121) ,
			new Color(49, 163, 84) ,
			new Color(0, 104, 55) };


		// seq pallet of 7 elements
		public static final Color[] YLGN7 = new Color[]{
			new Color(255, 255, 204) ,
			new Color(217, 240, 163) ,
			new Color(173, 221, 142) ,
			new Color(120, 198, 121) ,
			new Color(65, 171, 93) ,
			new Color(35, 132, 67) ,
			new Color(0, 90, 50) };


		// seq pallet of 8 elements
		public static final Color[] YLGN8 = new Color[]{
			new Color(255, 255, 229) ,
			new Color(247, 252, 185) ,
			new Color(217, 240, 163) ,
			new Color(173, 221, 142) ,
			new Color(120, 198, 121) ,
			new Color(65, 171, 93) ,
			new Color(35, 132, 67) ,
			new Color(0, 90, 50) };


		// seq pallet of 9 elements
		public static final Color[] YLGN9 = new Color[]{
			new Color(255, 255, 229) ,
			new Color(247, 252, 185) ,
			new Color(217, 240, 163) ,
			new Color(173, 221, 142) ,
			new Color(120, 198, 121) ,
			new Color(65, 171, 93) ,
			new Color(35, 132, 67) ,
			new Color(0, 104, 55) ,
			new Color(0, 69, 41) };


		// seq pallet of 3 elements
		public static final Color[] YLGNBU3 = new Color[]{
			new Color(237, 248, 177) ,
			new Color(127, 205, 187) ,
			new Color(44, 127, 184) };


		// seq pallet of 4 elements
		public static final Color[] YLGNBU4 = new Color[]{
			new Color(255, 255, 204) ,
			new Color(161, 218, 180) ,
			new Color(65, 182, 196) ,
			new Color(34, 94, 168) };


		// seq pallet of 5 elements
		public static final Color[] YLGNBU5 = new Color[]{
			new Color(255, 255, 204) ,
			new Color(161, 218, 180) ,
			new Color(65, 182, 196) ,
			new Color(44, 127, 184) ,
			new Color(37, 52, 148) };


		// seq pallet of 6 elements
		public static final Color[] YLGNBU6 = new Color[]{
			new Color(255, 255, 204) ,
			new Color(199, 233, 180) ,
			new Color(127, 205, 187) ,
			new Color(65, 182, 196) ,
			new Color(44, 127, 184) ,
			new Color(37, 52, 148) };


		// seq pallet of 7 elements
		public static final Color[] YLGNBU7 = new Color[]{
			new Color(255, 255, 204) ,
			new Color(199, 233, 180) ,
			new Color(127, 205, 187) ,
			new Color(65, 182, 196) ,
			new Color(29, 145, 192) ,
			new Color(34, 94, 168) ,
			new Color(12, 44, 132) };


		// seq pallet of 8 elements
		public static final Color[] YLGNBU8 = new Color[]{
			new Color(255, 255, 217) ,
			new Color(237, 248, 177) ,
			new Color(199, 233, 180) ,
			new Color(127, 205, 187) ,
			new Color(65, 182, 196) ,
			new Color(29, 145, 192) ,
			new Color(34, 94, 168) ,
			new Color(12, 44, 132) };


		// seq pallet of 9 elements
		public static final Color[] YLGNBU9 = new Color[]{
			new Color(255, 255, 217) ,
			new Color(237, 248, 177) ,
			new Color(199, 233, 180) ,
			new Color(127, 205, 187) ,
			new Color(65, 182, 196) ,
			new Color(29, 145, 192) ,
			new Color(34, 94, 168) ,
			new Color(37, 52, 148) ,
			new Color(8, 29, 88) };


		// seq pallet of 3 elements
		public static final Color[] YLORBR3 = new Color[]{
			new Color(255, 247, 188) ,
			new Color(254, 196, 79) ,
			new Color(217, 95, 14) };


		// seq pallet of 4 elements
		public static final Color[] YLORBR4 = new Color[]{
			new Color(255, 255, 212) ,
			new Color(254, 217, 142) ,
			new Color(254, 153, 41) ,
			new Color(204, 76, 2) };


		// seq pallet of 5 elements
		public static final Color[] YLORBR5 = new Color[]{
			new Color(255, 255, 212) ,
			new Color(254, 217, 142) ,
			new Color(254, 153, 41) ,
			new Color(217, 95, 14) ,
			new Color(153, 52, 4) };


		// seq pallet of 6 elements
		public static final Color[] YLORBR6 = new Color[]{
			new Color(255, 255, 212) ,
			new Color(254, 227, 145) ,
			new Color(254, 196, 79) ,
			new Color(254, 153, 41) ,
			new Color(217, 95, 14) ,
			new Color(153, 52, 4) };


		// seq pallet of 7 elements
		public static final Color[] YLORBR7 = new Color[]{
			new Color(255, 255, 212) ,
			new Color(254, 227, 145) ,
			new Color(254, 196, 79) ,
			new Color(254, 153, 41) ,
			new Color(236, 112, 20) ,
			new Color(204, 76, 2) ,
			new Color(140, 45, 4) };


		// seq pallet of 8 elements
		public static final Color[] YLORBR8 = new Color[]{
			new Color(255, 255, 229) ,
			new Color(255, 247, 188) ,
			new Color(254, 227, 145) ,
			new Color(254, 196, 79) ,
			new Color(254, 153, 41) ,
			new Color(236, 112, 20) ,
			new Color(204, 76, 2) ,
			new Color(140, 45, 4) };


		// seq pallet of 9 elements
		public static final Color[] YLORBR9 = new Color[]{
			new Color(255, 255, 229) ,
			new Color(255, 247, 188) ,
			new Color(254, 227, 145) ,
			new Color(254, 196, 79) ,
			new Color(254, 153, 41) ,
			new Color(236, 112, 20) ,
			new Color(204, 76, 2) ,
			new Color(153, 52, 4) ,
			new Color(102, 37, 6) };


		// seq pallet of 3 elements
		public static final Color[] YLORRD3 = new Color[]{
			new Color(255, 237, 160) ,
			new Color(254, 178, 76) ,
			new Color(240, 59, 32) };


		// seq pallet of 4 elements
		public static final Color[] YLORRD4 = new Color[]{
			new Color(255, 255, 178) ,
			new Color(254, 204, 92) ,
			new Color(253, 141, 60) ,
			new Color(227, 26, 28) };


		// seq pallet of 5 elements
		public static final Color[] YLORRD5 = new Color[]{
			new Color(255, 255, 178) ,
			new Color(254, 204, 92) ,
			new Color(253, 141, 60) ,
			new Color(240, 59, 32) ,
			new Color(189, 0, 38) };


		// seq pallet of 6 elements
		public static final Color[] YLORRD6 = new Color[]{
			new Color(255, 255, 178) ,
			new Color(254, 217, 118) ,
			new Color(254, 178, 76) ,
			new Color(253, 141, 60) ,
			new Color(240, 59, 32) ,
			new Color(189, 0, 38) };


		// seq pallet of 7 elements
		public static final Color[] YLORRD7 = new Color[]{
			new Color(255, 255, 178) ,
			new Color(254, 217, 118) ,
			new Color(254, 178, 76) ,
			new Color(253, 141, 60) ,
			new Color(252, 78, 42) ,
			new Color(227, 26, 28) ,
			new Color(177, 0, 38) };


		// seq pallet of 8 elements
		public static final Color[] YLORRD8 = new Color[]{
			new Color(255, 255, 204) ,
			new Color(255, 237, 160) ,
			new Color(254, 217, 118) ,
			new Color(254, 178, 76) ,
			new Color(253, 141, 60) ,
			new Color(252, 78, 42) ,
			new Color(227, 26, 28) ,
			new Color(177, 0, 38) };


		// seq pallet of 9 elements
		public static final Color[] YLORRD9 = new Color[]{
			new Color(255, 255, 204) ,
			new Color(255, 237, 160) ,
			new Color(254, 217, 118) ,
			new Color(254, 178, 76) ,
			new Color(253, 141, 60) ,
			new Color(252, 78, 42) ,
			new Color(227, 26, 28) ,
			new Color(189, 0, 38) ,
			new Color(128, 0, 38) };
	}
}
