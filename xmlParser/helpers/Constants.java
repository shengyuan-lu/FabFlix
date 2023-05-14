package helpers;

import java.util.Map;

public class Constants {

    public static final String actorFileName = "xmlParser/stanford-movies/actors63.xml";

    public static final String castFileName = "xmlParser/stanford-movies/casts124.xml";

    public static final String movieFileName = "xmlParser/stanford-movies/mains243.xml";

    public static final Map<String, String> genreMapping = Map.<String, String>ofEntries(

            Map.entry("actn", "Action"),
            Map.entry("act", "Action"),
            Map.entry("axtn", "Action"),

            Map.entry("advt", "Adventure"),
            Map.entry("adctx", "Adventure"),

            Map.entry("avga", "Avant Garde"), // New
            Map.entry("avant", "Avant Garde"),
            Map.entry("garde", "Avant Garde"),

            Map.entry("biop", "Biography"),
            Map.entry("biog", "Biography"),
            Map.entry("bio", "Biography"),
            Map.entry("biopx", "Biography"),
            Map.entry("biob", "Biography"),
            Map.entry("biopp", "Biography"),

            Map.entry("cart", "Animation"),

            Map.entry("cnrbb", "CnRb"), // New
            Map.entry("cnrb", "CnRb"),
            Map.entry("cnr", "CnRb"),

            Map.entry("comd", "Comedy"),
            Map.entry("cond", "Comedy"),
            Map.entry("comdx", "Comedy"),

            Map.entry("crim", "Crime"),
            Map.entry("cmR","Crime"),
            Map.entry("noir","Crime"),

            Map.entry("ctxx", "CTXX"), // New
            Map.entry("txx", "CTXX"),
            Map.entry("ctxxx", "CTXX"),
            Map.entry("ctcxx", "CTXX"),

            Map.entry("camp", "Camp"), // New

            Map.entry("dram", "Drama"),
            Map.entry("draam", "Drama"),
            Map.entry("dramn", "Drama"),
            Map.entry("dramd", "Drama"),
            Map.entry("dram>", "Drama"),
            Map.entry("drama", "Drama"),

            Map.entry("disa", "Disaster"), // New
            Map.entry("dist", "Disaster"),

            Map.entry("docu", "Documentary"),
            Map.entry("duco", "Documentary"),
            Map.entry("ducu", "Documentary"),
            Map.entry("dicu", "Documentary"),

            Map.entry("epic","Epic"), // New

            Map.entry("faml", "Family"), // New

            Map.entry("fant", "Fantasy"),
            Map.entry("fanth*", "Fantasy"),

            Map.entry("tv", "Reality-TV"),
            Map.entry("tvmini", "Reality-TV"),
            Map.entry("bnw tv", "Reality-TV"),
            Map.entry("col tv", "Reality-TV"),

            Map.entry("hist", "History"),

            Map.entry("hor", "Horror"),
            Map.entry("horr", "Horror"),
            Map.entry("h", "Horror"),
            Map.entry("homo", "Horror"),
            Map.entry("h0", "Horror"),
            Map.entry("h**", "Horror"),
            Map.entry("h*", "Horror"),

            Map.entry("psyc", "Psychological"), // New
            Map.entry("psych", "Psychological"),

            Map.entry("musc", "Music"),
            Map.entry("muusc", "Music"),
            Map.entry("scat","Music"),

            Map.entry("viol","Violence"), // New

            Map.entry("muscl", "Musical"),
            Map.entry("musical", "Musical"),
            Map.entry("stage musical", "Musical"),

            Map.entry("myst", "Mystery"),
            Map.entry("mystp", "Mystery"),
            Map.entry("weird", "Mystery"),

            Map.entry("road", "Road"), // New

            Map.entry("cult", "Cult"), // New

            Map.entry("natu", "Nature"), // New

            Map.entry("romt", "Romance"),
            Map.entry("romt.", "Romance"),
            Map.entry("ront", "Romance"),
            Map.entry("romtx", "Romance"),

            Map.entry("scfi", "Sci-Fi"),
            Map.entry("scif", "Sci-Fi"),
            Map.entry("sxfi", "Sci-Fi"),

            Map.entry("sports", "Sport"),

            Map.entry("surr", "Surreal"), // New
            Map.entry("surl", "Surreal"),
            Map.entry("surreal", "Surreal"),

            Map.entry("susp", "Suspense"), // New

            Map.entry("west", "Western"),
            Map.entry("west1", "Western"),

            Map.entry("kinky", "Adult"),
            Map.entry("porn", "Adult"),
            Map.entry("adct", "Adult"),
            Map.entry("porb", "Adult")
    );

}
