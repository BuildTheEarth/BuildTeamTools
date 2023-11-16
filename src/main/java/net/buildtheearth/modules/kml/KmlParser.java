package net.buildtheearth.modules.kml;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.bukkit.Bukkit;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Geometry;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.MultiGeometry;
import de.micromata.opengis.kml.v_2_2_0.Placemark;

public class KmlParser {
    public KmlParser(){}

    public List<LineString> extractLinestrings(String kmlString)
    {
        Bukkit.getServer().broadcastMessage(
                    String.format("Parsing kml string of length %d", kmlString.length())); 
        
        List<LineString> linestrings = new ArrayList<>();

        //https://github.com/micromata/javaapiforkml

        Kml kml = Kml.unmarshal(kmlString);
        //Top-level element will be a document
        Document doc = (Document) kml.getFeature();
        List<Placemark> placemarks = findPlacemarks(doc);

        //Placemark placemark = (Placemark) kml.getFeature();
        //Polygon geom = (Polygon) placemark.getGeometry();
        //List<Coordinate> coordinates = linearRing.getCoordinates();

        //}

        for (Placemark placemark : placemarks){
            // #extract coordinates assuming geometry is linestring
            List<LineString> lines = findLineStrings(placemark);
            linestrings.addAll(lines);
        }

        return linestrings;
            
    }

    private List<Placemark> findPlacemarks(Document container){

        //recursive search for placemarks in the document,            returns list of placemarks
        List<Placemark> placemarks = new ArrayList<>();

         for (Feature feature : container.getFeature())
         {
            Bukkit.getServer().broadcastMessage(
                    String.format("Feature of type %s: %s", feature.getClass().getName(), feature.getName())); 
            if (feature instanceof Placemark)  
                placemarks.add((Placemark)feature);
            //else if (feature instanceof Folder)
            //    placemarks += findPlacemarks(feature);
            else if (feature instanceof Document)
            {
                placemarks.addAll(findPlacemarks((Document) feature));
            }
        }

        
        return placemarks;
    }

    private List<LineString> findLineStrings(Placemark placemark)
    {
        List<LineString> lines = new ArrayList<>();

    //     """parse geometry of the placemark.
    //        return list of points as 2-tuple (lat lon) or 3-tuble(lat lon altitude)
    //     """
        Geometry geom = placemark.getGeometry();

        
        if (geom instanceof LineString)
        {
            lines.add( (LineString) geom);
        }      
        else if (geom instanceof MultiGeometry){
            MultiGeometry mg = (MultiGeometry)geom;
            for (Geometry subgeom : mg.getGeometry())
            {
                if (subgeom instanceof LineString)
                {
                    lines.add( (LineString) subgeom);
                } 
            }
        }
        else {
            Bukkit.getServer().broadcastMessage(
                    String.format("Placemark has geometry of unsupported type %s", geom.getClass().getName())); 
        }

        return lines;
    }

}
