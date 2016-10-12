package fr.univnantes.termsuite.istex;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;

import ch.qos.logback.classic.Level;
import eu.project.ttc.api.TerminoExtractor.ContextualizerMode;
import eu.project.ttc.api.TerminoFilterConfig;
import eu.project.ttc.engines.cleaner.TermProperty;
import eu.project.ttc.engines.desc.Lang;
import eu.project.ttc.models.TermIndex;
import eu.project.ttc.termino.export.JsonExporter;
import eu.project.ttc.termino.export.TbxExporter;
import eu.project.ttc.termino.export.TsvExporter;
import eu.project.ttc.tools.cli.TermSuiteCLIUtils;

public class IstexLauncher {

	private static final Logger LOGGER = LoggerFactory.getLogger(IstexLauncher.class);
	
	public static final String TREE_TAGGER = "tree-tagger";
	public static final String TREE_TAGGER_SHORT = "t";
	public static final String LANG = "lang";
	public static final String LANG_SHORT = "l";

	public static final String DOC_ID_FILE = "id-file";
	public static final String DOC_ID = "doc-id";
	
	public static final String FREQUENCY_THRESHOLD = "f";
	public static final String TOP_N = "n";
	public static final String CONTEXTUALIZE = "contextualize";


	public static final String OUTPUT = "out";
	public static final String OUTPUT_SHORT = "o";

	public IstexLauncher() {}
	
	public void run(String... args) {
//		TermSuiteCLIUtils.setGlobalLogLevel(Level.INFO);
		
		PosixParser parser = new PosixParser();

		// create the Options
		Options options = new Options()
				.addOption(TREE_TAGGER_SHORT, TREE_TAGGER, true, "Path to Tree Tagger home")
				.addOption(LANG_SHORT, LANG, true, "Corpus language")
				.addOption(null, DOC_ID, true, "Comma-separated list of ISTEX document ids")
				.addOption(null, DOC_ID_FILE, true, "Path to file containing line-separated document ids")
				.addOption(FREQUENCY_THRESHOLD, FREQUENCY_THRESHOLD, true, "Frequency threshold")
				.addOption(TOP_N, TOP_N, true, "Keeps only top n terms")
				.addOption(null, CONTEXTUALIZE, false, "Activates the contextualizer")
				.addOption(OUTPUT_SHORT, OUTPUT, true, "Output files bas")
			;
		
		try {
			// Parse and set CL options
			CommandLine line = parser.parse(options, args, false);
			
			
			String fileBase = checkedOpt(line, OUTPUT, OUTPUT_SHORT).toString();
			File tsvFile = new File(fileBase + ".tsv");
			File tbxFile = new File(fileBase + ".tbx");
			File jsonFile = new File(fileBase + ".json");

			
			IstexTerminoExtractor extractor = null;
			
			Lang lang = Lang.forName(checkedOpt(line, LANG, LANG_SHORT).toString());
			
			if(line.hasOption(DOC_ID)) {
				if(line.hasOption(DOC_ID_FILE))
					throw new IstexLauncherException("Only one of --%s and --%s must be provided", DOC_ID, DOC_ID_FILE);
				
				extractor = IstexTerminoExtractor.fromDocumentIds(
						lang, 
						Splitter.on(",").split(line.getOptionValue(DOC_ID).toString()));
			} else if(line.hasOption(DOC_ID_FILE))
				extractor = IstexTerminoExtractor.fromIdFile(
						lang, 
						new File(line.getOptionValue(DOC_ID_FILE)));
			else
				throw new IstexLauncherException("Expected one of --%s or --%s options", DOC_ID, DOC_ID_FILE);
			
			extractor.setTreeTaggerHome(checkedOpt(line, TREE_TAGGER, TREE_TAGGER_SHORT).toString());
			
			if(line.hasOption(TOP_N) || line.hasOption(FREQUENCY_THRESHOLD)) {
				TerminoFilterConfig config = line.hasOption(TOP_N) ?
						new TerminoFilterConfig()
							.by(TermProperty.SPECIFICITY)
							.keepTopN(Integer.parseInt(line.getOptionValue(TOP_N))) :
						new TerminoFilterConfig()
							.by(TermProperty.FREQUENCY)
							.keepOverTh(Integer.parseInt(line.getOptionValue(FREQUENCY_THRESHOLD)));
				extractor.postFilter(config);
			}
			
			
			if(line.hasOption(CONTEXTUALIZE))
				extractor.useContextualizer(3, ContextualizerMode.ON_SWT_TERMS);
			
			
			TermIndex termIndex = extractor.execute();
			
			LOGGER.info("Exporting TermIndex {} to {}", termIndex.getName(), tsvFile.getPath());
			TsvExporter.export(termIndex, new FileWriter(tsvFile));
			LOGGER.info("Exporting TermIndex {} to {}", termIndex.getName(), tbxFile.getPath());
			TbxExporter.export(termIndex, new FileWriter(tbxFile));
			LOGGER.info("Exporting TermIndex {} to {}", termIndex.getName(), jsonFile.getPath());
			JsonExporter.export(termIndex, new FileWriter(jsonFile));
		} catch (ParseException | IOException e) {
			throw new IstexLauncherException(e);
		}

	}
	
	public static void main(String[] args) {
		try {
			new IstexLauncher().run(args);
		} catch(Exception e) {
			LOGGER.error("An error occurred: " + e.getMessage(), e);
			System.exit(1);
		}
	}
	
	public static Object checkedOpt(CommandLine line, String opt, String shortOpt) {
		if(line.hasOption(opt))
			return line.getOptionValue(opt);
		throw new IstexLauncherException("Expected option --%s%s", opt, shortOpt == null ? "": String.format(" [-%s]", shortOpt));
	}
}
