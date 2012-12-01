package ida.Utilities;

import java.io.StringReader;
import java.util.Collection;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

class ParserDemo {

	public static void main(String[] args) {
		LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		if (args.length > 0) {
			demoDP(lp, args[0]);
		} else {
			demoAPI(lp);
		}
	}

	public static void demoDP(LexicalizedParser lp, String filename) {
		// This option shows loading and sentence-segment and tokenizing
		// a file using DocumentPreprocessor
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		// You could also create a tokenizer here (as below) and pass it
		// to DocumentPreprocessor
		for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
			Tree parse = lp.apply(sentence);
			parse.pennPrint();
			System.out.println();

			GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
			Collection<?> tdl = gs.typedDependenciesCCprocessed(true);
			System.out.println(tdl);
			System.out.println();
		}
	}

	public static void demoAPI(LexicalizedParser lp) {
		// This option shows parsing a list of correctly tokenized words
//		String[] sent = { "This", "is", "an", "easy", "sentence", "." };
//		List<CoreLabel> rawWords = Sentence.toCoreLabelList(sent);
//		Tree parse = lp.apply(rawWords);
//		parse.pennPrint();
//		System.out.println();

		// This option shows loading and using an explicit tokenizer
		String sent2 = "I hate Pinterest.";
		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
		List<CoreLabel> rawWords2 = tokenizerFactory.getTokenizer(new StringReader(sent2)).tokenize();
		Tree parse = lp.apply(rawWords2);

		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
		System.out.println(tdl);
		System.out.println();

		TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
		tp.printTree(parse);
	}

	private ParserDemo() {
	} // static methods only

}
