package core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.DigesterLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.brassratdev.data.garmin.ActivityLapT;
import com.brassratdev.data.garmin.ActivityListT;
import com.brassratdev.data.garmin.ActivityT;
import com.brassratdev.data.garmin.PositionT;
import com.brassratdev.data.garmin.TrackT;
import com.brassratdev.data.garmin.TrackpointT;
import com.brassratdev.data.garmin.TrainingCenterDatabaseT;
import com.brassratdev.io.googlefit.GoogleFitXmlDigester;

import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.KmlFactory;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.Placemark;

public class TestGoogleFitDigester {
	private static final Logger log = LogManager.getLogger(TestGoogleFitDigester.class);
	GoogleFitXmlDigester digester;
	Path path;
	Path kmlPath;

	@Before
	public void setUp() throws Exception {
		digester = new GoogleFitXmlDigester();
		path = Paths.get("d:\\", "data", "Google Fit", "Takeout", "Fit", "Activities",
				"2019-01-22T09_51_55-08_00_PT2H27M39S_Backcountry s.tcx");
		String prefix = path.toString().substring(0, path.toString().lastIndexOf("."));
		kmlPath = Paths.get(prefix + ".kml");
		log.info("track path is:" + path);
		log.info("kml path is:" + kmlPath);
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testGoogleFitDigester() throws IOException, SAXException {
		print(digester.parse(path));
	}

	@Test
	public void testDigesterWithKml() throws IOException, SAXException {		
		writeKmlColoredLineStringPath(digester.parse(path));
	}

	@Ignore
	public void testMaxVel() throws IOException, SAXException {
		maxVel(digester.parse(path));
	}

	
	void writeKmlLineStringPath(TrainingCenterDatabaseT t)
			throws FileNotFoundException {
		Kml kml = KmlFactory.createKml();
		Placemark p = kml.createAndSetPlacemark();
		LineString l = p.createAndSetLineString();
		
		int kCnt = 0;
		for (TrackpointT point : trackFor(t).getTrackpoint()) {
			log.info("writing trackpoint " + ++kCnt);
			l.addToCoordinates(point.getPosition().getLongitudeDegrees(),point.getPosition().getLatitudeDegrees()); 
		}
		kml.marshal(kmlPath.toFile());
	}

	void filter(List<TrackpointT> points){
		ArrayList<TrackpointT> pointsToRemove = new ArrayList<>();
		for (TrackpointT t : points) {
			
			if( t.getPosition() == null ){
				pointsToRemove.add( t );
			}
		}
		log.info("removing null positions: " + pointsToRemove.size());
		points.removeAll( pointsToRemove );
	}
	
	void writeKmlColoredLineStringPath(TrainingCenterDatabaseT t) throws FileNotFoundException {
		List<TrackpointT> points = trackFor(t).getTrackpoint();
		filter( points );
		writeKml( points );
	}
	
	void writeKml(List<TrackpointT> points) throws FileNotFoundException {
		Kml kml = KmlFactory.createKml();
		Document d = kml.createAndSetDocument();
		for (int i = 1; i < points.size(); i++){
			String name = "trackpoint_" + i;
			TrackpointT start = points.get(i-1);
			TrackpointT end = points.get(i);
			
			Placemark p = d.createAndAddPlacemark().withName(name);
			p.createAndAddStyle().createAndSetLineStyle()
					//.withColor( colorForSpeed( end.getSpeed() ) )
					.withWidth(2.0);
			LineString ls = p.createAndSetLineString();
			PositionT posStart = start.getPosition();
			if(posStart == null ) continue;
			PositionT posEnd = end.getPosition();
			if(posEnd == null ) continue;
			ls.addToCoordinates(posStart.getLongitudeDegrees(), posStart.getLatitudeDegrees());
			ls.addToCoordinates(posEnd.getLongitudeDegrees(), posEnd.getLatitudeDegrees());
		}
		kml.marshal(kmlPath.toFile());
	}
	
	void maxVel(TrainingCenterDatabaseT tcd) {
		
//		List<Trackpoint> points = tcd.getActivities().getActivity().get(0).getLap().get(0).get
//		float maxVel = 0.0f;
//		for (TrackpointT t:points){
//			if( t.getSpeed() > maxVel )
//				maxVel = t.getSpeed();
//		}
//		System.out.println("maxVel is: " + maxVel);
	}

	// /@return hex color string
	String colorForSpeed(float vel) {
		int rgb = (int) ((vel / 8.126f) * 1024);
		Color c = new Color(rgb);
		String hex = Integer.toHexString(c.getRGB());
		System.out.println("rgb: " + rgb + " as hex: " + hex);
		
		return hex;
	}
	
	private TrackT trackFor(TrainingCenterDatabaseT t) {
		ActivityT activity = t.getActivities().getActivity().get(0);
		ActivityLapT lap = activity.getLap().get(0);
		return lap.getTrack().get(0);
	}

	void print(TrainingCenterDatabaseT t) {
		//ActivityT activity = t.getActivities().getActivity();
		List<ActivityT> activity = t.getActivities().getActivity();
		assertNotNull(activity);
		assertTrue(activity.size() > 0);
		
		System.out.println("Activity Notes: " + activity.get(0).getNotes());
		System.out.println("# trk pts : " + trackFor(t).getTrackpoint().size());
		// System.out.println("max lat   : " + vcc.getTrack().getMaxLatitude());
		// System.out.println("min lat   : " + vcc.getTrack().getMinLatitude());
		// System.out.println("max lon   : " +
		// vcc.getTrack().getMaxLongitude());
		// System.out.println("min lon   : " +
		// vcc.getTrack().getMinLongitude());
//		for (Trackpoint t : vcc.getTrack().getTrackPoints().getTrackPoints()) {
//			System.out.println("trackpoint hdg: " + t.getHeading());
//			System.out.println("trackpoint lat: " + t.getLatitude());
//			System.out.println("trackpoint lon: " + t.getLongitude());
//			System.out.println("trackpoint vel: " + t.getSpeed());
//		}
	}
}

