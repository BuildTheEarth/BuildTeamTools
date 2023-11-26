package net.buildtheearth.modules.kml;


import java.util.List;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Geometry;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.MultiGeometry;
import de.micromata.opengis.kml.v_2_2_0.Placemark;

public class KmlParser {
    private Player player;

    public KmlParser(Player player){
        this.player = player;
    }

    
    /** 
     * returns all line-strings (also called "paths" or "poly-lines") from the KML content.
     * A linestring is a list of geo-coordinates that are interpreted as beeing connected by straight lines.
     * Note that a linestring is not automatically "closed", startpoint and endpoint can be different.
     * 
     * Note: Each placemark can have multiple linestrings when using the type "MultiGeometry".
     * 
     * @param kmlString KML content
     * @return List<LineString>: A list of LineString objects, which themselves are lists of geocoordiantes.
     */
    public List<LineString> extractLinestrings(String kmlString)
    {       
        List<LineString> linestrings = new ArrayList<>();

        //https://github.com/micromata/javaapiforkml
        try {
            Kml kml = Kml.unmarshal(kmlString);
            //Top-level element will be a document
            Document doc = (Document) kml.getFeature();
            List<Placemark> placemarks = findPlacemarks(doc);


            for (Placemark placemark : placemarks){
                // #extract coordinates assuming geometry is linestring
                List<LineString> lines = findLineStrings(placemark);
                linestrings.addAll(lines);
            }

        } catch (Exception ex) {
            player.sendMessage(String.format("§cthere was an error parsing the kml string: '%s'", ex.toString()));
        }

        return linestrings;
            
    }

    
    /** 
     * Finds all placemarks in the KML Document
     * @param container the document to search
     * @return List<Placemark>: the list of Placemark objects in the document
     */
    private List<Placemark> findPlacemarks(Document container){

        //recursive search for placemarks in the document,            returns list of placemarks
        List<Placemark> placemarks = new ArrayList<>();

         for (Feature feature : container.getFeature())
         {
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

    
    /** 
     * returns all LineStrings in the given KML Placemark
     * A placemark can have different geometry types. The type MultiGeometry can be used to create
     * arbitrarily complex hierarchies of Geometries.
     * 
     * This method only cares about LineString and MultiGeometry.
     * 
     * A placemark can have multiple linestrings when using the type "MultiGeometry".
     * 
     * @param placemark the placemark to search
     * @return List<LineString>: A list of LineString objects, which themselves are lists of geocoordiantes.
     */
    private List<LineString> findLineStrings(Placemark placemark){
        List<LineString> lines = new ArrayList<>();

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
            this.player.sendMessage(
                String.format("§cPlacemark has geometry of unsupported type %s", geom.getClass().getName())); 
        }

        return lines;
    }

}
