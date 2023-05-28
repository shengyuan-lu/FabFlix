package edu.uci.ics.fabflixmobile.data;

public class Constants {
    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
    public static final String host = "ec2-54-215-189-142.us-west-1.compute.amazonaws.com";
    public static final String port = "8443";
    public static final String domain = "cs122b_fabflix_war";
    public static final String baseURL = "https://" + host + ":" + port + "/" + domain;
}
