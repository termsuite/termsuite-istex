package fr.univnantes.termsuite.istex.cli;

import fr.univnantes.termsuite.tools.opt.CliOption;
import fr.univnantes.termsuite.tools.opt.OptType;

public enum IstexCliOption implements CliOption {
	// GATHERER 
	DOCUMENT_IDS("doc-id", 		null, 	OptType.T_STRING, 	"The \",\"-separated list of ids of ISTEX documents"),
	ID_FILE("id-file", 		null, 	OptType.T_FILE, 	"A file containing the list of ISTEX document ids of the corpus"),
	FULLTEXT("fulltext", 		null, 	OptType.T_NONE, 	"Runs on documents' fulltexts instead of abstract only" ),
	FAIL_ON_MISSING("fail-on-missing", 		null, 	OptType.T_NONE, 	"Aborts the process when the fulltext of a document is not available" ),
	;
	
	private String optName;
	private String optShortName;
	private OptType argType;
	private String description;
	
	private IstexCliOption(String optName, String optShortName, OptType argType, String description) {
		this.optName = optName;
		this.optShortName = optShortName;
		this.argType = argType;
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see fr.univnantes.termsuite.tools.opt.CliOption#getOptName()
	 */
	@Override
	public String getOptName() {
		return optName;
	}

	/* (non-Javadoc)
	 * @see fr.univnantes.termsuite.tools.opt.CliOption#getArgType()
	 */
	@Override
	public OptType getArgType() {
		return argType;
	}
	
	/* (non-Javadoc)
	 * @see fr.univnantes.termsuite.tools.opt.CliOption#getOptShortName()
	 */
	@Override
	public String getOptShortName() {
		return optShortName;
	}

	/* (non-Javadoc)
	 * @see fr.univnantes.termsuite.tools.opt.CliOption#hasArg()
	 */
	@Override
	public boolean hasArg() {
		return argType != OptType.T_NONE;
	}

	/* (non-Javadoc)
	 * @see fr.univnantes.termsuite.tools.opt.CliOption#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}
}