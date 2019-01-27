package com.brassratdev.io.googlefit;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.DigesterLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import com.brassratdev.data.fb.FbData;
import com.brassratdev.data.fb.FbResponse;
import com.brassratdev.data.fb.IPAddress;
import com.brassratdev.data.fb.IPAddresses;
import com.brassratdev.data.garmin.ActivityLapT;
import com.brassratdev.data.garmin.ActivityListT;
import com.brassratdev.data.garmin.ActivityT;
import com.brassratdev.data.garmin.IntensityT;
import com.brassratdev.data.garmin.PositionT;
import com.brassratdev.data.garmin.TrackT;
import com.brassratdev.data.garmin.TrackpointT;
import com.brassratdev.data.garmin.TrainingCenterDatabaseT;
import com.brassratdev.io.XMLDigester;

/**
 * A {@link Digester} that parses Google Fit .tcx files, which are actually Garmin 
 * xml sport files that use the TrainingCenterDatabase as the root element.
 * 
 * @see http://www.garmin.com/xmlschemas
 * @see com.brassratdev.data.garmin
 * @author rob
 *
 */
public class GoogleFitXmlDigester extends XMLDigester {
	private final static Logger log = LogManager.getLogger(GoogleFitXmlDigester.class);
	private Options options = new Options();
	
	public GoogleFitXmlDigester() {
		init();
	}

	protected void init() {
		log.info("init, using GPXTrackModule");
		
		digester = DigesterLoader.newLoader(new GPXTrackModule()).newDigester();
		Option input = Option.builder("file").hasArg().argName("input").desc("input garmin data file to parse").build();
		Option kml = Option.builder("kml").desc("parse input as kml output, generating a file with same name and kml suffix").build();
		
		options.addOption(input).addOption(kml);
	}

	public TrainingCenterDatabaseT parse(Path path) {
		try {
			log.info("parsing " + path.toString());
			return digester.parse(path.toFile());
		} catch (IOException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

/**
 * generates the rules for the elements in the .tcx file
 * @author rob
 *
 */
class GPXTrackModule extends AbstractRulesModule {
	@Override
	protected void configure() {
		forPattern("TrainingCenterDatabase").createObject().ofType(TrainingCenterDatabaseT.class).then()
				.setProperties();

		forPattern("TrainingCenterDatabase/Activities").createObject().ofType(ActivityListT.class).then()
				.setProperties().then().setNext("setActivities");
		// Activity and its elements: Id, Notes
		forPattern("TrainingCenterDatabase/Activities/Activity").createObject().ofType(ActivityT.class).then()
				.setProperties().then().setNext("addActivity");
		forPattern("TrainingCenterDatabase/Activities/Activity/Id").setBeanProperty().withName("id");
		forPattern("TrainingCenterDatabase/Activities/Activity/Notes").setBeanProperty().withName("notes");
		// Lap needs to be created and properties set
		forPattern("TrainingCenterDatabase/Activities/Activity/Lap").createObject().ofType(ActivityLapT.class).then()
				.setProperties().addAlias("StartTime").forProperty("startTime").then().setNext("addLap");
		forPattern("TrainingCenterDatabase/Activities/Activity/Lap/DistanceMeters").setBeanProperty()
				.withName("distanceMeters");
		forPattern("TrainingCenterDatabase/Activities/Activity/Lap/TotalTimeSeconds").setBeanProperty()
				.withName("totalTimeSeconds");
		forPattern("TrainingCenterDatabase/Activities/Activity/Lap/Calories").setBeanProperty().withName("calories");

		forPattern("TrainingCenterDatabase/Activities/Activity/Lap/Track").createObject().ofType(TrackT.class).then()
				.setProperties().then().setNext("addTrack");
		forPattern("TrainingCenterDatabase/Activities/Activity/Lap/Track/Trackpoint").createObject()
				.ofType(TrackpointT.class).then().setProperties().then().setNext("addPoint");
		forPattern("TrainingCenterDatabase/Activities/Activity/Lap/Track/Trackpoint/DistanceMeters").setBeanProperty()
				.withName("distanceMeters");
		forPattern("TrainingCenterDatabase/Activities/Activity/Lap/Track/Trackpoint/Time").setBeanProperty()
				.withName("time");
		forPattern("TrainingCenterDatabase/Activities/Activity/Lap/Track/Trackpoint/AltitudeMeters").setBeanProperty()
				.withName("altitudeMeters");

		forPattern("TrainingCenterDatabase/Activities/Activity/Lap/Track/Trackpoint/Position").createObject()
				.ofType(PositionT.class).then().setProperties().then().setNext("setPosition");
		forPattern("TrainingCenterDatabase/Activities/Activity/Lap/Track/Trackpoint/Position/LatitudeDegrees")
				.setBeanProperty().withName("latitudeDegrees");
		forPattern("TrainingCenterDatabase/Activities/Activity/Lap/Track/Trackpoint/Position/LongitudeDegrees")
				.setBeanProperty().withName("longitudeDegrees");

	}
}