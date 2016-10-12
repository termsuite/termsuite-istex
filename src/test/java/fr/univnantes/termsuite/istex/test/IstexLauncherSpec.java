package fr.univnantes.termsuite.istex.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import eu.project.ttc.api.TermIndexIO;
import eu.project.ttc.models.TermIndex;
import fr.univnantes.termsuite.istex.IstexLauncher;
import fr.univnantes.termsuite.istex.IstexLauncherException;

public class IstexLauncherSpec {

	private String id1;
	private String id2;
	private String treeTaggerHome;
	private Path outPath;
	
	@Rule 
	public ExpectedException thrown = ExpectedException.none();
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	

	@Before
	public void setup() {
		id1 = "F697EDBD85006E482CD1AC91DE9D40F6C629727A";
		id2 = "15101397F055B3A872D495F7405D0A3F3E195E0F";
		treeTaggerHome = Tests.getConfigProperty("treetagger.home.path").toString();
		outPath = Paths.get(folder.getRoot().getAbsolutePath(), "termino");
		assertThat(outPath.toFile()).doesNotExist();
	}

	@Test
	public void testArgLangMissing() {
		thrown.expect(IstexLauncherException.class);
		thrown.expectMessage("Expected option --lang [-l]");
		
		new IstexLauncher().run(
				"-t", treeTaggerHome,
				"--doc-id", String.format("%s,%s", id1,id2),
				"-o", outPath.toString()
				);
	}
	
	@Test
	public void testArgOutMissing() {
		thrown.expect(IstexLauncherException.class);
		thrown.expectMessage("Expected option --out [-o]");
		
		new IstexLauncher().run(
				"-l", "en",
				"-t", treeTaggerHome,
				"--doc-id", String.format("%s,%s", id1,id2)
				);
	}

	@Test
	public void testArgCannotHaveBothDocIdAndDocFile() {
		thrown.expect(IstexLauncherException.class);
		thrown.expectMessage("Only one of --doc-id and --id-file must be provided");
		
		new IstexLauncher().run(
				"-l", "en",
				"-t", treeTaggerHome,
				"--doc-id", String.format("%s,%s", id1,id2),
				"--id-file", new File("src/test/resources/id-file").getAbsolutePath(),
				"-o", outPath.toString()
				);
	}

	@Test
	public void testArgDocIdOrDocFileMissing() {
		thrown.expect(IstexLauncherException.class);
		thrown.expectMessage("Expected one of --doc-id or --id-file options");
		
		new IstexLauncher().run(
				"-l", "en",
				"-t", treeTaggerHome,
				"-o", outPath.toString()
				);
	}


	@Test
	public void testArgTreeTaggerMissing() {
		thrown.expect(IstexLauncherException.class);
		thrown.expectMessage("Expected option --tree-tagger [-t]");
		
		new IstexLauncher().run(
				"-l", "en",
				"--doc-id", String.format("%s,%s", id1,id2),
				"-o", outPath.toString()
				);
	}

	@Test
	public void testValidFromDocId() {
		new IstexLauncher().run(
				"-t", treeTaggerHome,
				"-l", "en",
				"--doc-id", String.format("%s,%s", id1,id2),
				"-o", outPath.toString()
				);
		
		Tests.testTermino(TermIndexIO.fromJson(outPath.toString() + ".json"));

	}

	@Test
	public void testValidFromDocIdWithContexts() {
		new IstexLauncher().run(
				"-t", treeTaggerHome,
				"-l", "en",
				"--doc-id", String.format("%s,%s", id1,id2),
				"--contextualize",
				"-o", outPath.toString()
				);
		
		Tests.testTermino(TermIndexIO.fromJson(outPath.toString() + ".json"));

	}

	@Test
	public void testValidFilteredFreq() {
		new IstexLauncher().run(
				"-t", treeTaggerHome,
				"-l", "en",
				"--doc-id", String.format("%s,%s", id1,id2),
				"-o", outPath.toString(),
				"-f", "12"
				);
		TermIndex fromJson = TermIndexIO.fromJson(outPath.toString() + ".json");
		assertThat(fromJson.getTerms()).hasSize(11);
	}
	
	@Test
	public void testValidFilteredTop10() {
		new IstexLauncher().run(
				"-t", treeTaggerHome,
				"-l", "en",
				"--doc-id", String.format("%s,%s", id1,id2),
				"-o", outPath.toString(),
				"-n", "10"
				);
		TermIndex fromJson = TermIndexIO.fromJson(outPath.toString() + ".json");
		assertThat(fromJson.getTerms()).hasSize(10);
	}



	@Test
	public void testValidFromDocFile() {
		new IstexLauncher().run(
				"-t", treeTaggerHome,
				"-l", "en",
				"--id-file", new File("src/test/resources/id-file").getAbsolutePath(),
				"-o", outPath.toString()
				);
		
		Tests.testTermino(TermIndexIO.fromJson(outPath.toString() + ".json"));
	}

}
