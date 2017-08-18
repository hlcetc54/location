package com.example.administrator.client;


public class WifiLib
{
    public static final int numberOfLevels = 100;

    public static int calculateSignalLevel(int rssi, int numLevels)
    {
        if (numLevels < 1)
            return 0;

        final int MIN_RSSI = -100;
        final int MAX_RSSI = -55;

        if(rssi <= MIN_RSSI)
        {
            return 0;
        }
        else if(rssi >= MAX_RSSI)
        {
            return numLevels - 1;
        }
        else
        {
            float inputRange = (MAX_RSSI - MIN_RSSI);
            float outputRange = (numLevels - 1);
            if(0 != inputRange)
                return (int) ((float) (rssi - MIN_RSSI) * outputRange / inputRange);
        }
        return 0;
    }
}
