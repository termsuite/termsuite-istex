package fr.univnantes.termsuite.istex.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import fr.univnantes.termsuite.api.IndexedCorpusIO;
import fr.univnantes.termsuite.index.Terminology;
import fr.univnantes.termsuite.istex.cli.IstexLauncher;
import fr.univnantes.termsuite.tools.TermSuiteCliException;

public class IstexLauncherSpec {

	private String id1 = Tests.DOC_ID1;
	private String id2 = Tests.DOC_ID2;
	private String treeTaggerHome;
	private Path tsvPath;
	private Path jsonPath;
	
	@Rule 
	public ExpectedException thrown = ExpectedException.none();
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Before
	public void setup() {
		treeTaggerHome = Tests.getConfigProperty("treetagger.home.path").toString();
		tsvPath = Paths.get(folder.getRoot().getAbsolutePath(), "termino.tsv");
		jsonPath = Paths.get(folder.getRoot().getAbsolutePath(), "termino.json");
		assertThat(tsvPath.toFile()).doesNotExist();
		assertThat(jsonPath.toFile()).doesNotExist();
	}

	@Test
	public void testArgLangMissing() throws Exception {
		thrown.expect(TermSuiteCliException.class);
		thrown.expectMessage(containsString("Option -l [--language] missing"));
		
		launch(
				"-t", treeTaggerHome,
				"--doc-id", String.format("%s,%s", id1,id2),
				"--json", jsonPath.toString(),
				"--tsv", tsvPath.toString()
		);
	}
	
	private void launch(String... args) throws Exception {
		IstexLauncher.main(args);
	}
	
	@Test
	public void testArgOutMissing() throws Exception {
		thrown.expect(TermSuiteCliException.class);
		thrown.expectMessage(containsString(
				"At least one option in --json, --tbx, --tsv must be set"
				));
		
		launch(
				"-l", "en",
				"-t", treeTaggerHome,
				"--doc-id", String.format("%s,%s", id1,id2)
			);
	}

	@Test
	public void testArgCannotHaveBothDocIdAndDocFile() throws Exception {
		thrown.expect(TermSuiteCliException.class);
		thrown.expectMessage(containsString("Exactly one exception in --doc-id, --id-file must be set"));
		
		launch(
				"-l", "en",
				"-t", treeTaggerHome,
				"--doc-id", String.format("%s,%s", id1,id2),
				"--id-file", new File("src/test/resources/id-file").getAbsolutePath(),
				"--tsv", tsvPath.toString()
				);
	}

	
	@Test
	public void testTsvAlone() throws Exception {
		launch(
				"-l", "en",
				"-t", treeTaggerHome,
				"--doc-id", String.format("%s,%s", id1,id2),
				"--tsv", tsvPath.toString()
				);
	}

	@Test
	public void testArgDocIdOrDocFileMissing() throws Exception {
		thrown.expect(TermSuiteCliException.class);
		thrown.expectMessage(containsString("Exactly one exception in --doc-id, --id-file must be set"));
		
		launch(
				"-l", "en",
				"-t", treeTaggerHome,
				"--tsv", tsvPath.toString()
				);
	}


	@Test
	public void testArgTreeTaggerMissing() throws Exception {
		thrown.expect(TermSuiteCliException.class);
		thrown.expectMessage(containsString("Option --tagger-home [-t] is mandatory"));
		
		launch(
				"-l", "en",
				"--doc-id", String.format("%s,%s", id1,id2),
				"--tsv", tsvPath.toString()
				);
	}

	@Test
	public void testValidFromDocId() throws Exception {
		launch(
				"--debug", "-t", treeTaggerHome,
				"-l", "en",
				"--doc-id", String.format("%s,%s", id1,id2),
				"--json", jsonPath.toString(),
				"--tsv", tsvPath.toString()
				);
		
		Tests.testTermino(jsonPath);
	}

	
	@Test
	public void testFulltextValidFromDocId() throws Exception {
		launch(
				"--debug", 
				"--fulltext", 
				"-t", treeTaggerHome,
				"-l", "en",
				"--doc-id", String.format("%s,%s", id1,id2),
				"--json", jsonPath.toString(),
				"--tsv", tsvPath.toString()
				);
		
		Tests.testTerminoFullText(IndexedCorpusIO.fromJson(jsonPath).getTerminology());

	}

	
	@Test
	public void testValidFromDocIdWithContexts() throws Exception {
		launch(
				"--debug",
				"-t", treeTaggerHome,
				"-l", "en",
				"--doc-id", String.format("%s,%s", id1,id2),
				"--contextualize",
				"--json", jsonPath.toString(),
				"--tsv", tsvPath.toString()
				);
		
		Tests.testTerminoWithoutPostProc(jsonPath);

	}
	
	
	@Test
	public void testValidFilteredFreq() throws Exception {
		launch(
				"-t", treeTaggerHome,
				"-l", "en",
				"--doc-id", String.format("%s,%s", id1,id2),
				"--tsv", tsvPath.toString(),
				"--json", jsonPath.toString(),
				"--post-filter-property", "frequency",
				"--post-filter-th", "5"
				);
		Terminology fromJson = IndexedCorpusIO.fromJson(jsonPath).getTerminology();
		assertThat(fromJson.getTerms()).hasSize(5);
	}
	
	
	@Test
	public void testValidFilteredTop10() throws Exception {
		launch(
			"-t", treeTaggerHome,
			"-l", "en",
			"--doc-id", String.format("%s,%s", id1,id2),
			"--tsv", tsvPath.toString(),
			"--json", jsonPath.toString(),
			"--post-filter-property", "frequency",
			"--post-filter-top-n", "10"
		);
		
		Terminology fromJson = IndexedCorpusIO.fromJson(jsonPath).getTerminology();
		assertThat(fromJson.getTerms()).hasSize(10);
	}
	
	@Test
	public void testValidFromDocFile() throws Exception {
		launch(
				"--debug",
				"-t", treeTaggerHome,
				"-l", "en",
				"--id-file", new File("src/test/resources/id-file").getAbsolutePath(),
				"--json", jsonPath.toString(),
				"--tsv", tsvPath.toString(),
				"--tsv-properties", "gkey,f,sp"
				);
		
		assertThat(jsonPath).exists();
		Tests.testTermino(jsonPath);
	}
}
