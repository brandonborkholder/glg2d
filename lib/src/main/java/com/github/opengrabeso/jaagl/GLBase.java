package com.github.opengrabeso.jaagl;

public abstract class GLBase implements GL {
    int versionMajor;
    int versionMinor;

    public GLBase(String strVersion) {
        // if anything else fails, assume 2.0 - we do not support anything below this anyway
        versionMajor = 2;
        versionMinor = 0;

        if (strVersion != null) {
            try {
                int number = 0;
                while (Character.isDigit(strVersion.charAt(number)) || strVersion.charAt(number) == '.') number++;
                String[] versions = strVersion.substring(0, number).split("\\.");
                if (versions.length >= 2) {
                    versionMajor = Integer.parseInt(versions[0]);
                    versionMinor = Integer.parseInt(versions[1]);
                }
            } catch (NumberFormatException ignored) {
            }
        }

    }

    @Override
    public boolean versionAtLeast(int major, int minor) {
        return versionMajor > major || versionMajor == major && versionMinor >= minor;
    }


}
