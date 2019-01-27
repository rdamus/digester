package com.brassratdev.io;

import org.apache.commons.cli.Options;
import org.apache.commons.digester3.Digester;

public class XMLDigester {
	protected Digester digester;
	protected Options options;
	
	public XMLDigester(){
		digester = new Digester();
		digester.setValidating( false );
	}

	public Options getOptions() {
		return options;
	}
	
	
}
