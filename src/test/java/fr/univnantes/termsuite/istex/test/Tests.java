package fr.univnantes.termsuite.istex.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.google.common.base.Preconditions;

import eu.project.ttc.api.Traverser;
import eu.project.ttc.models.Term;
import eu.project.ttc.models.TermIndex;

@RunWith(Suite.class)
@SuiteClasses(
		{IstexLauncherSpec.class,
		IstexTerminoExtractorSpec.class})
public class Tests {
	public static Object getConfigProperty( String propName) {
		InputStream is = IstexTerminoExtractorSpec.class.getClassLoader().getResourceAsStream("termsuite-test.properties");
		Properties properties = new Properties();
		try {
			properties.load(is);
			is.close();
			Preconditions.checkArgument(!properties.contains(propName), "No such property in config file %s: %s", "termsuite-test.properties", propName);
			return properties.get(propName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void testTermino(TermIndex termIndex) {
		List<Term> list = Traverser.by("sp desc").toList(termIndex);
		assertEquals("n: eng", list.get(0).getGroupingKey());
		assertEquals("nn: murray hill", list.get(10).getGroupingKey());
	}
	

}
