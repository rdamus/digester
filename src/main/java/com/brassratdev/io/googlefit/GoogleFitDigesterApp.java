package com.brassratdev.io.googlefit;

import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class GoogleFitDigesterApp {
	private final static Logger log = LogManager.getLogger(GoogleFitDigesterApp.class);

	public static void main(String[] args) {
		GoogleFitXmlDigester d = new GoogleFitXmlDigester();
		CommandLineParser parser = new DefaultParser();
		Options options = d.getOptions();
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);
			if(line.hasOption("input")) {
				d.parse(Paths.get(line.getOptionValue("input")));
			}
		} catch (ParseException exp) {
			// oops, something went wrong
			log.error("Parsing failed.  Reason: " + exp.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "GoogleFitDigesterApp", options );
		}
	}

}
