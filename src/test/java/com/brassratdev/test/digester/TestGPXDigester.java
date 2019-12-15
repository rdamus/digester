package com.brassratdev.test.digester;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.GPX.Builder;
import io.jenetics.jpx.GPX.Reader.Mode;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.KmlFactory;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.Placemark;

public class TestGPXDigester {
	private static final Logger log = LogManager.getLogger(TestGPXDigester.class);
	
	Path path;
	Path kmlPath;
	Builder gpxBuilder;
	GPX gpx;
	Stream<Track> tracks;
	
	@Before
	public void setUp() throws Exception {
		gpxBuilder = GPX.builder();
		path = Paths.get("d:\\data\\tracks\\bright-hour", "spincup-2019.gpx");
		kmlPath = Paths.get("d:\\data\\tracks\\bright-hour");
		gpx = GPX.reader(Mode.LENIENT).read(path);
		tracks = gpx.tracks();
	}

	@After
	public void tearDown() throws Exception {
	}


	@Ignore
	public void testGPXRead() throws IOException {
		tracks.flatMap(Track::segments)
		.flatMap(TrackSegment::points)
		.forEach(System.out::println);
		
	}
	
	@Ignore
	public void testGPXReadTrackNames() throws IOException {
		tracks.forEach(track->System.out.println("track name: " + track.getName()));
	}
	
	@Ignore
	public void testWithKml() throws IOException, SAXException {
		writeKmlLineStringPath();
	}
	
	@Test
	public void testWithColoredKml() throws IOException, SAXException {
		
		for (Track t : GPX.reader(Mode.LENIENT).read(path).getTracks()) {
			String name = t.getName().get();
			writeColorizedKml(
					t.segments()
					.flatMap(TrackSegment::points)
					.collect(Collectors.toList()), name,"kml");
		}
	}

	@Ignore
	public void testMaxVel() throws IOException, SAXException {
			maxVel();
	}
	
	@Ignore
	public void testColorRange() {
		float maxSpeed = 7.5f;
		colorForSpeed2(0,maxSpeed);
		colorForSpeed2(1.0f,maxSpeed);
		colorForSpeed2(2.0f,maxSpeed);
		colorForSpeed2(3.5f,maxSpeed);
		colorForSpeed2(6.0f,maxSpeed);
		colorForSpeed2(7.0f,maxSpeed);
		colorForSpeed2(7.5f,maxSpeed);
	}

	
	void writeKmlLineStringPath()
			throws FileNotFoundException {
		Kml kml = KmlFactory.createKml();
		Placemark p = kml.createAndSetPlacemark();
		
		for (Track t : gpx.getTracks()) {
			String name = t.getName().get();
			final LineString l = p.createAndSetLineString();
			log.info("writing track " + name);
			t.segments()
			.flatMap(TrackSegment::points)
			.forEach(point ->l.addToCoordinates(point.getLongitude().doubleValue(), point.getLatitude().doubleValue()));
			kml.marshal(kmlPath.resolve(name).toFile());	
		}
		
	}

//	void filter(List<Trackpoint> points){
//		ArrayList<Trackpoint> pointsToRemove = new ArrayList<>();
//		for (Trackpoint t : points) {
//			if( t.getSpeed() == 0f ){
//				pointsToRemove.add( t );
//			}
//		}
//		System.out.println("removing " + pointsToRemove.size());
//		points.removeAll( pointsToRemove );
//	}
//	

	void writeColorizedKml(List<WayPoint> points, String name, String ext) throws FileNotFoundException {
		Kml kml = KmlFactory.createKml();
		Float maxSpeed = maxVelocityFor(points);
		log.info("writing colorized track " + name + " with max speed: "+ maxSpeed);
		
		Document d = kml.createAndSetDocument();
		for (int i = 1; i < points.size(); i++){
			String wptname = "trackpoint_" + i;
			WayPoint start = points.get(i-1);
			WayPoint end = points.get(i);
			
			Placemark p = d.createAndAddPlacemark().withName(wptname);
			p.createAndAddStyle().createAndSetLineStyle()
					.withColor( colorForSpeed2( end.getSpeed().get().floatValue(), maxSpeed ) )
					.withWidth(2.0);
			LineString ls = p.createAndSetLineString();
			ls.addToCoordinates(start.getLongitude().doubleValue(), start.getLatitude().doubleValue());
			ls.addToCoordinates(end.getLongitude().doubleValue(), end.getLatitude().doubleValue());
		}
		kml.marshal(kmlPath.resolve(name + "." + ext).toFile());
	}
	
	void maxVel() {
		
		for (Track t : gpx.getTracks()) {
			String name = t.getName().get();
			
			log.info("reading track " + name);
			List<Float> speeds = t.segments()
			.flatMap(TrackSegment::points)
			.map(p->p.getSpeed().get().floatValue())
			.collect(Collectors.toList());
			
			Float maxVel = speeds.stream().max(Comparator.comparing(Float::valueOf)).get();
			log.info("maxVel is: " + maxVel + "m/s for track " + name);
		}
	}
	
	float maxVelocityFor(List<WayPoint> points) {
		List<Float> speeds = points
		.stream()
		.map(p->p.getSpeed().get().floatValue())
		.collect(Collectors.toList());
		
		return speeds.stream().max(Comparator.comparing(Float::valueOf)).get();
	}

	// /@return hex color string
	String colorForSpeed(float vel, float maxVel) {
		int rgb = (int) ((vel / maxVel) * Integer.MAX_VALUE);
		Color c = new Color(rgb);
		String hex = Integer.toHexString(c.getRGB());
		//System.out.println("rgb: " + rgb + " as hex: " + hex);
		
		return hex;
	}

	// /@return hex color string
		String colorForSpeed2(float vel, float maxVel) {
			float value = vel / maxVel;
			float minHue = 120f/255; //corresponds to green
			float maxHue = 0; //corresponds to red
			float hue = value*maxHue + (1-value)*minHue; 
			Color c = new Color(Color.HSBtoRGB(hue, 1, 0.5f));
			
			String hex = Integer.toHexString(c.getRGB());
			//System.out.println("rgb: " + c.toString() + " as hex: " + hex);
			
			return hex;
		}

//	void print(VelocitekControlCenter vcc) {
//		System.out.println("downloaded: " + vcc.getTrack().getDownloadedOn());
//		System.out.println("name      : " + vcc.getTrack().getName());
//		System.out.println("# trk pts : " + vcc.getTrack().getNumberTrkpts());
//		// System.out.println("max lat   : " + vcc.getTrack().getMaxLatitude());
//		// System.out.println("min lat   : " + vcc.getTrack().getMinLatitude());
//		// System.out.println("max lon   : " +
//		// vcc.getTrack().getMaxLongitude());
//		// System.out.println("min lon   : " +
//		// vcc.getTrack().getMinLongitude());
//		System.out.println("trkpts sze: "
//				+ vcc.getTrack().getTrackPoints().getTrackPoints().size());
//		for (Trackpoint t : vcc.getTrack().getTrackPoints().getTrackPoints()) {
//			System.out.println("trackpoint hdg: " + t.getHeading());
//			System.out.println("trackpoint lat: " + t.getLatitude());
//			System.out.println("trackpoint lon: " + t.getLongitude());
//			System.out.println("trackpoint vel: " + t.getSpeed());
//		}
//	}
}


