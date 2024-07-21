/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import de.micromata.opengis.kml.v_2_2_0.AltitudeMode;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.LineStyle;
import de.micromata.opengis.kml.v_2_2_0.Style;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author angel
 */
// clase para pintar en google earth una línea (objeto LineString)
// a través de la creación de un archivo kml
// se apoya en la clase JavaApiForKml
public class GoogleEarth {

    private final Kml kml;
    private final Document document;
    private final LineString lineStr;
    private final Style style;

    // nombre del archivo sin extensión, color en formato ABGR y anchura en pixels
    public GoogleEarth(String fileName, String color, int lineWidth) {
        kml = new Kml();
        document = kml.createAndSetDocument().withName(fileName + ".kml").withOpen(true);
        style = new Style();
        document.getStyleSelector().add(style);
        style.setId("linestyle");
        LineStyle linestyle = new LineStyle();
        style.setLineStyle(linestyle);
        linestyle.setColor(color);
        linestyle.setWidth(lineWidth);
        lineStr = document.createAndAddPlacemark().withStyleUrl("#linestyle").withName(fileName)
                .createAndSetLineString();
        lineStr.setAltitudeMode(AltitudeMode.RELATIVE_TO_SEA_FLOOR);
        lineStr.setExtrude(Boolean.FALSE);
        lineStr.setTessellate(Boolean.TRUE);
    }

    public LineString getLineStr() {
        return lineStr;
    }

    // crea el fichero y lo abre en google earth
    public void close() {

        // create file
        File file = new File(document.getName());
        try {
            kml.marshal(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GoogleEarth.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // launch google earth
        
        // launch google earth
        
     
        try {
            Process p = Runtime.getRuntime().exec(new String [] {"C:/Program Files/Google/Google Earth Pro/client/googleearth.exe " , file.getAbsolutePath()});

        } catch (IOException ex) {
            Logger.getLogger(GoogleEarth.class.getName()).log(Level.SEVERE, null, ex);
        }
               
    }

}
