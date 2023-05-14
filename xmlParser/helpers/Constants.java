package helpers;

import java.util.Map;

public class Constants {

    public static final String actorFileName = "stanford-movies/actors63.xml";

    public static final String castFileName = "stanford-movies/casts124.xml";

    public static final String movieFileName = "stanford-movies/mains243.xml";

    public static final Map<String, String> genreMapping = Map.<String, String>ofEntries(

            Map.entry("dram", "Drama"),
            Map.entry("draam", "Drama"),
            Map.entry("dramn", "Drama"),
            Map.entry("dramd", "Drama"),
            Map.entry("dram>", "Drama"),

            Map.entry("actn", "Action"),
            Map.entry("axtn", "Action"),

            Map.entry("advt", "Adventure"),
            Map.entry("adctx", "Adventure"),

            Map.entry("avga", "Avant Garde"),

            Map.entry("biop", "Biography"),
            Map.entry("biog", "Biography"),
            Map.entry("bio", "Biography"),
            Map.entry("biopx", "Biography"),
            Map.entry("biob", "Biography"),
            Map.entry("biopp", "Biography"),

            Map.entry("cart", "Cartoon"),

            Map.entry("cnrbb", "Cnrb"),
            Map.entry("CnRb", "CnRb"),
            Map.entry("cnr", "Cnrb"),

            Map.entry("comd", "Comedy"),

            Map.entry("crim", "Crime"),
            Map.entry("cmR","Crime"),
            Map.entry("noir","Crime"),

            Map.entry("disa", "Disaster"),

            Map.entry("docu", "Documentary"),

            Map.entry("epic","Epic"),

            Map.entry("faml", "Family"),

            Map.entry("fant", "Fantasy"),

            Map.entry("tv", "Reality-TV"),
            Map.entry("tvmini", "Reality-TV"),

            Map.entry("hist", "History"),

            Map.entry("psyc", "Psychological"),
            Map.entry("psych", "Psychological"),

            Map.entry("hor", "Horror"),
            Map.entry("horr", "Horror"),

            Map.entry("musc", "Music"),
            Map.entry("muusc", "Music"),
            Map.entry("Scat","Music"),

            Map.entry("muscl", "Musical"),

            Map.entry("myst", "Mystery"),

            Map.entry("natu", "Nature"),

            Map.entry("romt", "Romance"),
            Map.entry("romtx", "Romance"),

            Map.entry("scfi", "Sci-Fi"),
            Map.entry("scif", "Sci-Fi"),
            Map.entry("sxfi", "Sci-Fi"),

            Map.entry("sports", "Sport"),

            Map.entry("surr", "Surreal"),
            Map.entry("surl", "Surreal"),

            Map.entry("susp", "Suspense"),

            Map.entry("west", "Western"),
            Map.entry("west1", "Western"),

            Map.entry("kinky", "Adult"),
            Map.entry("porn", "Adult"),
            Map.entry("porb", "Adult")
    );

}
