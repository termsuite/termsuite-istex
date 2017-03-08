package fr.univnantes.termsuite.istex.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import fr.univnantes.termsuite.api.IndexedCorpusIO;
import fr.univnantes.termsuite.index.Terminology;
import fr.univnantes.termsuite.model.Term;

@RunWith(Suite.class)
@SuiteClasses({
	IstexLauncherSpec.class,
	IstexServiceSpec.class,
	IstexCorpusSpec.class,
	FunctionalTests.class
})
public class Tests {
	
	public static final Path RESOURCES = Paths.get("src", "test", "resources");
	public static final Path CONFIG_PATH = RESOURCES.resolve("termsuite-test.properties");
	public static final Path DOC1_PATH = RESOURCES.resolve("doc1.json");
	public static final String DOC_ID1 = "F697EDBD85006E482CD1AC91DE9D40F6C629727A";
	public static final String DOC_ID2 = "15101397F055B3A872D495F7405D0A3F3E195E0F";

	
	public static String readFile(Path file) {
		try {
			return Files.toString(file.toFile(), Charsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Object getConfigProperty( String propName) {
		try {
			InputStream is = new FileInputStream(CONFIG_PATH.toFile());
			Properties properties = new Properties();
			properties.load(is);
			is.close();
			Preconditions.checkArgument(!properties.contains(propName), "No such property in config file %s: %s", "termsuite-test.properties", propName);
			return properties.get(propName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void testTerminoWithoutPostProc(Path jsonPath) {
		Tests.testTerminoWithoutPostProc(IndexedCorpusIO.fromJson(jsonPath).getTerminology());
	}
	
	public static void testTermino(Path jsonPath) {
		Tests.testTermino(IndexedCorpusIO.fromJson(jsonPath).getTerminology());
	}
	
	public static Comparator<Term> TERM_COMPARATOR = new Comparator<Term>() {
		@Override
		public int compare(Term o1, Term o2) {
			return ComparisonChain.start()
					.compare(o2.getSpecificity(), o1.getSpecificity())
					.compare(o1.getGroupingKey(), o1.getGroupingKey())
					.result()
					;
		}
	};

	public static void testTerminoWithoutPostProc(Terminology terminology) {
		List<Term> terms = Lists.newArrayList(terminology.getTerms().values());
		Collections.sort(terms, TERM_COMPARATOR);
		assertThat(terms).isNotEmpty();
		assertEquals("n: web", terms.get(0).getGroupingKey());
	}

	public static void testTermino(Terminology terminology) {
		List<Term> terms = Lists.newArrayList(terminology.getTerms().values());
		Collections.sort(terms, TERM_COMPARATOR);
		assertThat(terms).isNotEmpty();
		assertEquals("n: bookmark", terms.get(0).getGroupingKey());
		assertEquals("ann: mutual information theory", terms.get(5).getGroupingKey());
	}
	

}
