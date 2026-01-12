package net.buildtheearth.buildteamtools.utils.raycast;

import org.bukkit.util.Vector;

public class RaycastAPIMath {
    public static double cos(double a) {
        return Math.cos(a);
    }

    public static double sin(double a) {
        return Math.sin(a);
    }

    public static double tan(double a) {
        return Math.tan(a);
    }

    public static double arccos(double a) {
        return Math.acos(a);
    }

    public static double arcsin(double a) {
        return Math.asin(a);
    }

    public static double arctan(double a) {
        return Math.atan(a);
    }

    public static double toRadians(double angdeg) {
        return Math.toRadians(angdeg);
    }

    public static double toDeg(double angrad) {
        return Math.toDegrees(angrad);
    }

    public static double getAngle(double width, double height) {
        if (width < 0.0D)
            width *= -1.0D;
        if (height < 0.0D)
            height *= -1.0D;
        if (width == 0.0D || height == 0.0D)
            return 0.0D;
        return arctan(height / width);
    }

    public static Vector rotate(Vector vect, double yaw, double pitch) {
        yaw = toRadians(yaw);
        pitch = toRadians(pitch);
        vect = rotateX(vect, pitch);
        vect = rotateY(vect, -yaw);
        return vect;
    }

    public static Vector rotateX(Vector vect, double a) {
        double y = cos(a) * vect.getY() - sin(a) * vect.getZ();
        double z = sin(a) * vect.getY() + cos(a) * vect.getZ();
        return vect.setY(y).setZ(z);
    }

    public static Vector rotateY(Vector vect, double b) {
        double x = cos(b) * vect.getX() + sin(b) * vect.getZ();
        double z = -sin(b) * vect.getX() + cos(b) * vect.getZ();
        return vect.setX(x).setZ(z);
    }

    public static Vector rotateZ(Vector vect, double c) {
        double x = cos(c) * vect.getX() - sin(c) * vect.getY();
        double y = sin(c) * vect.getX() + cos(c) * vect.getY();
        return vect.setX(x).setY(y);
    }
}
