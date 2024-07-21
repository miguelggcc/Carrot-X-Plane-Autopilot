package utils;



public class Coordinate {

    private String Name;
    private double latitude; // from 0 to 90 north and from -1 to -90 south
    private double longitude;// from 0 to 180 east and from -1 to -180 west
    private double altitude; // meters
    private double XE, XN;
    private static final int DISTANCE = 0; // used in  vincentyFormula method
    private static final int INITIAL_BEARING = 1;
    private static final int FINAL_BEARING = 2;

    public Coordinate(String name, double latitude, double longitude) {
        this(name, latitude, longitude, 0);
    }

    public Coordinate(String name, double latitude, double longitude, double altitude) {
        setLocationName(name);
        setLatitude(latitude);
        setLongitude(longitude);
        setAltitude(altitude);
    }

    public Coordinate(String name, double altitude) {
        setLocationName(name);
        setLatitude(latitude);
        setLongitude(longitude);
        setAltitude(altitude);
    }

    public void setLatitude(double latitude) {
        if (latitude > 90 || latitude < -90) {
            throw new IllegalArgumentException("Latitude must be between -90 and  90");
        }
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude) {
        if (longitude > 180 || longitude < -180) {
            throw new IllegalArgumentException("Longitude must be between -180 and  180");
        }
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getXE() {
        double earthRadius = 6378137;
        XE = earthRadius * Math.toRadians(this.getLongitude());
        return XE;
    }

    public double getXN() {
        double earthRadius = 6378137;
        XN = earthRadius * Math.log(Math.tan(Math.toRadians(this.getLatitude()) / 2 + Math.PI / 4));
        return XN;
    }
    
    public double getXFlat() {
        double earthRadius = 6378137;
        return earthRadius*Math.toRadians(this.getLongitude())*Math.cos(Math.toRadians(this.getLatitude()));
    }
    
    public double getYFlat(){
        double earthRadius = 6378137;
        return earthRadius*Math.toRadians(this.getLatitude());
    }

    public void setAltitude(double altitude) {
        if (altitude < 0) {
            throw new IllegalArgumentException("Elevation cannot be negative");
        }
        this.altitude = altitude;
    }

    public String getLocationName() {
        return Name;
    }

    public void setLocationName(String name) {
        this.Name = name;
    }

    public double getGreatCircleInitialBearing(Coordinate location) {
        return vincentyFormula(location, INITIAL_BEARING);
    }

    public double getGreatCircleFinalBearing(Coordinate location) {
        return vincentyFormula(location, FINAL_BEARING);
    }

    public double getGreatCircleDistance(Coordinate location) {
        return vincentyFormula(location, DISTANCE);
    }

    private double vincentyFormula(Coordinate location, int formula) {
        double a = 6378137;
        double b = 6356752.3142;
        double f = 1 / 298.257223563; // WGS-84 ellipsiod
        double L = Math.toRadians(location.getLongitude() - getLongitude());
        double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(getLatitude())));
        double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(location.getLatitude())));
        double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

        double lambda = L;
        double lambdaP = 2 * Math.PI;
        double iterLimit = 20;
        double sinLambda = 0;
        double cosLambda = 0;
        double sinSigma = 0;
        double cosSigma = 0;
        double sigma = 0;
        double sinAlpha = 0;
        double cosSqAlpha = 0;
        double cos2SigmaM = 0;
        double C;
        while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0) {
            sinLambda = Math.sin(lambda);
            cosLambda = Math.cos(lambda);
            sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda)
                    + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
            if (sinSigma == 0) {
                return 0; // co-incident points
            }
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
            if (Double.isNaN(cos2SigmaM)) {
                cos2SigmaM = 0; // equatorial line: cosSqAlpha=0 (§6)
            }
            C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
            lambdaP = lambda;
            lambda = L + (1 - C) * f * sinAlpha
                    * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
        }
        if (iterLimit == 0) {
            return Double.NaN; // formula failed to converge
        }
        double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double deltaSigma = B
                * sinSigma
                * (cos2SigmaM + B
                / 4
                * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
                * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
        double distance = b * A * (sigma - deltaSigma);

        // initial bearing
        double fwdAz = Math.toDegrees(Math.atan2(cosU2 * sinLambda, cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
        // final bearing
        double revAz = Math.toDegrees(Math.atan2(cosU1 * sinLambda, -sinU1 * cosU2 + cosU1 * sinU2 * cosLambda));
        if (formula == DISTANCE) {
            return distance;
        } else if (formula == INITIAL_BEARING) {
            return fwdAz;
        } else if (formula == FINAL_BEARING) {
            return revAz;
        } else { // should never happpen
            return Double.NaN;
        }
    }

    public Coordinate getGreatCircleDestination(double distance, double bearing) {
        /**
         * φ2 = asin( sin φ1 ⋅ cos δ + cos φ1 ⋅ sin δ ⋅ cos θ ) λ2 = λ1 + atan2(
         * sin θ ⋅ sin δ ⋅ cos φ1, cos δ − sin φ1 ⋅ sin φ2 )
         *
         * where φ is latitude, λ is longitude, θ is the bearing (clockwise from
         * north), δ is the angular distance d/R; d being the distance
         * travelled, R the earth’s radius
         */
        double earthRadius = 6378137; // Earth's radius in meters (WGS-84)
        double φ1 = Math.toRadians(latitude);
        double λ1 = Math.toRadians(longitude);
        double θ = Math.toRadians(bearing);
        double δ = distance / earthRadius; // normalize linear distance to radian angle

        double φ2 = Math.asin(Math.sin(φ1) * Math.cos(δ) + Math.cos(φ1) * Math.sin(δ) * Math.cos(θ));
        double λ2 = λ1 + Math.atan2(Math.sin(θ) * Math.sin(δ) * Math.cos(φ1), Math.cos(δ) - Math.sin(φ1) * Math.sin(φ2));

        double λ2_harmonised = (λ2 + 3 * Math.PI) % (2 * Math.PI) - Math.PI; // normalise to −180..+180°

        return new Coordinate("Target", Math.toDegrees(φ2), Math.toDegrees(λ2_harmonised));

    }

    public void updateGreatCirclePosition(double distance, double bearing) {
        /**
         * φ2 = asin( sin φ1 ⋅ cos δ + cos φ1 ⋅ sin δ ⋅ cos θ ) λ2 = λ1 + atan2(
         * sin θ ⋅ sin δ ⋅ cos φ1, cos δ − sin φ1 ⋅ sin φ2 )
         *
         * where φ is latitude, λ is longitude, θ is the bearing (clockwise from
         * north), δ is the angular distance d/R; d being the distance
         * travelled, R the earth’s radius
         */
        double earthRadius = 6378137; // Earth's radius in meters (WGS-84)
        double φ1 = Math.toRadians(latitude);
        double λ1 = Math.toRadians(longitude);
        double θ = Math.toRadians(bearing);
        double δ = distance / earthRadius; // normalize linear distance to radian angle

        double φ2 = Math.asin(Math.sin(φ1) * Math.cos(δ) + Math.cos(φ1) * Math.sin(δ) * Math.cos(θ));
        double λ2 = λ1 + Math.atan2(Math.sin(θ) * Math.sin(δ) * Math.cos(φ1), Math.cos(δ) - Math.sin(φ1) * Math.sin(φ2));

        double λ2_harmonised = (λ2 + 3 * Math.PI) % (2 * Math.PI) - Math.PI; // normalise to −180..+180°

        latitude = Math.toDegrees(φ2);
        longitude = Math.toDegrees(λ2_harmonised);

    }

    public double getRhumbLineBearing(Coordinate location) {
        double dLon = Math.toRadians(location.getLongitude() - getLongitude());
        double dPhi = Math.log(Math.tan(Math.toRadians(location.getLatitude()) / 2 + Math.PI / 4)
                / Math.tan(Math.toRadians(getLatitude()) / 2 + Math.PI / 4));
        if (Math.abs(dLon) > Math.PI) {
            dLon = dLon > 0 ? -(2 * Math.PI - dLon) : (2 * Math.PI + dLon);
        }
        return Math.toDegrees(Math.atan2(dLon, dPhi));
    }

    public double getRhumbLineDistance(Coordinate location) {
        double earthRadius = 6378137; // Earth's radius in meters (WGS-84)
        double dLat = Math.toRadians(location.getLatitude()) - Math.toRadians(getLatitude());
        double dLon = Math.abs(Math.toRadians(location.getLongitude()) - Math.toRadians(getLongitude()));
        double dPhi = Math.log(Math.tan(Math.toRadians(location.getLatitude()) / 2 + Math.PI / 4)
                / Math.tan(Math.toRadians(getLatitude()) / 2 + Math.PI / 4));
        double q = dLat / dPhi;

        if (!Double.isFinite(q)) {
            q = Math.cos(Math.toRadians(getLatitude()));
        }
        // if dLon over 180° take shorter rhumb across 180° meridian:
        if (dLon > Math.PI) {
            dLon = 2 * Math.PI - dLon;
        }
        double d = Math.sqrt(dLat * dLat + q * q * dLon * dLon);
        return d * earthRadius;
    }

    public Coordinate getRhumbLineDestination(double distance, double bearing) {
        /* all angles in radians, d es la distance, zeta is bearing, phi latitude, lambda longitude,  
            var δ = d/R;
            var Δφ = δ * Math.cos(θ); θ zeta
            var φ2 = φ1 + Δφ;

            var Δψ = Math.log(Math.tan(φ2/2+Math.PI/4)/Math.tan(φ1/2+Math.PI/4));
            var q = Math.abs(Δψ) > 10e-12 ? Δφ / Δψ : Math.cos(φ1); // E-W course becomes ill-conditioned with 0/0
            var Δλ = δ*Math.sin(θ)/q;
            var λ2 = λ1 + Δλ;

            // check for some daft bugger going past the pole, normalise latitude if so
            if (Math.abs(φ2) > Math.PI/2) φ2 = φ2>0 ? Math.PI-φ2 : -Math.PI-φ2;
            The longitude can be normalised to −180…+180 using (lon+540)%360-180
    
            φ (phi) is latitude, λ is longitude, Δλ is taking shortest route (<180°), ln is natural log, R is the earth’s radius
         */
        double earthRadius = 6378137; // Earth's radius in meters (WGS-84)
        double zeta = Math.toRadians(bearing);
        double lat1 = Math.toRadians(latitude);
        double delta = distance / earthRadius;
        double incLat = delta * Math.cos(zeta);
        double lat2 = lat1 + incLat;
        double dPsi = Math.log(Math.tan(lat2 / 2 + Math.PI / 4) / Math.tan(lat1 / 2 + Math.PI / 4));
        double q = Math.abs(dPsi) > 10e-12 ? incLat / dPsi : Math.cos(lat1); // E-W course becomes ill-conditioned with 0/0
        double lon1 = Math.toRadians(longitude);
        double incLon = delta * Math.sin(zeta) / q;
        double lon2 = lon1 + incLon;
        // check for some daft bugger going past the pole, normalise latitude if so
        if (Math.abs(lat2) > Math.PI / 2) {
            lat2 = lat2 > 0 ? Math.PI - lat2 : -Math.PI - lat2;
        }

        lat2 = Math.toDegrees(lat2);
        lon2 = Math.toDegrees(lon2);
        lon2 = (lon2 + 540) % 360 - 180; //The longitude can be normalised to −180…+180

        return new Coordinate("Target", lat2, lon2);
    }

    public void updateRhumbLinePosition(double distance, double bearing) {

        double earthRadius = 6378137; // Earth's radius in meters (WGS-84)
        double zeta = Math.toRadians(bearing);
        double lat1 = Math.toRadians(latitude);
        double delta = distance / earthRadius;
        double incLat = delta * Math.cos(zeta);
        double lat2 = lat1 + incLat;
        double dPsi = Math.log(Math.tan(lat2 / 2 + Math.PI / 4) / Math.tan(lat1 / 2 + Math.PI / 4));
        double q = Math.abs(dPsi) > 10e-12 ? incLat / dPsi : Math.cos(lat1); // E-W course becomes ill-conditioned with 0/0
        double lon1 = Math.toRadians(longitude);
        double incLon = delta * Math.sin(zeta) / q;
        double lon2 = lon1 + incLon;
        // check for some daft bugger going past the pole, normalise latitude if so
        if (Math.abs(lat2) > Math.PI / 2) {
            lat2 = lat2 > 0 ? Math.PI - lat2 : -Math.PI - lat2;
        }
        lat2 = Math.toDegrees(lat2);
        lon2 = Math.toDegrees(lon2);
        lon2 = (lon2 + 540) % 360 - 180; //The longitude can be normalised to −180…+180

        latitude = lat2;
        longitude = lon2;
    }
    public double bearing(Coordinate location) {

        double ang = Math.atan2(location.getXE() - this.getXE(), location.getXN() - this.getXN());
        return Math.toDegrees(ang);
    }

    public double bearing(double PE, double PN) {

        double ang = Math.atan2(PE - this.getXFlat(), PN - this.getYFlat());
        return Math.toDegrees(ang);
    }

    public double distance(Coordinate location) {
        double earthRadius = 6378137; 
        
        double x1 = earthRadius*Math.toRadians(this.getLongitude())*Math.cos(Math.toRadians(this.getLatitude()));
        double x2 = earthRadius*Math.toRadians(location.getLongitude())*Math.cos(Math.toRadians(location.getLatitude()));
        double y1 = earthRadius*Math.toRadians(this.getLatitude());
        double y2 = earthRadius*Math.toRadians(location.getLatitude());
        double dist = Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
        return dist;
    }

}
