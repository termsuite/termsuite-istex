package fr.univnantes.termsuite.istex.service;

import java.net.URI;
import java.net.URISyntaxException;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

import fr.univnantes.termsuite.istex.IstexDriverException;

public class IstexModule extends AbstractModule {
	
	private URI api;
	
	public IstexModule() {
		super();
		try {
			this.api = new URI("https://api.istex.fr/");
		} catch (URISyntaxException e) {
			throw new IstexDriverException(e);
		}
	}


	@Override
	protected void configure() {
		
		bind(URI.class).annotatedWith(Names.named("istex.api")).toInstance(api);
		bind(IstexService.class).in(Singleton.class);
		
		install(new FactoryModuleBuilder()
			     .build(GetTaskFactory.class));
		install(new FactoryModuleBuilder()
			     .build(AbstractCorpusFactory.class));
		install(new FactoryModuleBuilder()
			     .build(FulltextCorpusFactory.class));
	}
}
