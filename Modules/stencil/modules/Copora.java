package stencil.modules;

import java.util.Arrays;

import stencil.interpreter.tree.Specializer;
import stencil.module.operator.util.AbstractOperator;
import stencil.module.util.BasicModule;
import stencil.module.util.OperatorData;
import stencil.module.util.ann.*;
import stencil.types.Converter;

@Module
public class Copora extends BasicModule {
	/**A list of stop words for text analysis.
	 * List taken from http://www.textfixer.com/resources/common-english-words.txt.
	 * 
	 **/
	@Operator(spec="[add:\"\"]")
	public static class StopWords extends AbstractOperator {
		static final String[] ENGLISH_WORDS = new String[]{"a","able","about","across","after","all","almost","also","am","among","an","and","any","are","as","at","be","because","been","but","by","can","cannot","could","dear","did","do","does","either","else","ever","every","for","from","get","got","had","has","have","he","her","hers","him","his","how","however","i","if","in","into","is","it","its","just","least","let","like","likely","may","me","might","most","must","my","neither","no","nor","not","of","off","often","on","only","or","other","our","own","rather","said","say","says","she","should","since","so","some","than","that","the","their","them","then","there","these","they","this","tis","to","too","twas","us","wants","was","we","were","what","when","where","which","while","who","whom","why","will","with","would","yet","you","your"};
		
		private final String[] words;
		public StopWords(OperatorData opData, Specializer spec) {
			super(opData);
			String add = Converter.toString(spec.get("add"));
			words = add.split("\\s*,\\s*");
			for (int i=0; i< words.length;i++) {words[i] = words[i].toLowerCase();}
			Arrays.sort(words);
		}
		
		@Facet(memUse="FUNCTION", prototype="(boolean stopWord)", alias={"map","query"})
		public boolean query(String word) {
			word = word.toLowerCase();
			return (Arrays.binarySearch(ENGLISH_WORDS, word) >= 0)
					|| (Arrays.binarySearch(words, word) >= 0);
			} 		
	}

}
