package xmlParser;

import java.io.FileWriter;
import java.util.*;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import xmlParser.helpers.StarPair;
import xmlParser.helpers.XMLDatabaseHandler;
import xmlParser.models.Movie;
import xmlParser.models.Star;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import xmlParser.helpers.Constants;

public class MainParser extends DefaultHandler {

    // The main function of the parser
    public static void main(String[] args) {

        MainParser parser = new MainParser();

        parser.parseDocument(Constants.movieFileName);
        parser.parseDocument(Constants.castFileName);
        parser.parseDocument(Constants.actorFileName);

        parser.handleMoviesWithNoStars();

        parser.updateDatabase();

        parser.generateSummaryReport();

        parser.generateInconsistencyReport();
    }

    // For parsing - universal
    private String tempVal;
    private String currentDocument;
    private FileWriter inconsistencyReportWriter;

    // For parsing - mains243.xml
    private Random random = new Random();
    private HashMap<String, Movie> parsedMovies; // key = movie id , value = movie object
    private Movie tempMovie;
    private String tempDirector;
    private ArrayList<String> movieErrors = new ArrayList<>();

    // For parsing - casts124.xml
    private String tempMovieID;
    private String tempStarName;
    private HashMap<String, String> parsedCasts;  // key = star name , value = movie id
    private ArrayList<String> castErrors = new ArrayList<>();

    // For parsing - actors63.xml
    private HashSet<Star> parsedStars;
    private Set<StarPair> parsedStarNames;
    private Star tempStar;
    private FileWriter csvWriter;
    private int currentStarId;
    private ArrayList<String> actorErrors = new ArrayList<>();


    // Genres
    Set<String> seenGenres = new HashSet<>();
    HashMap<String, Integer> genresMapping = new HashMap();


    public MainParser() {

        this.tempVal = "";
        this.currentDocument = "";
        this.tempDirector = "";

        this.parsedMovies = new HashMap<>();
        this.parsedCasts = new HashMap<>();
        this.parsedStars = new HashSet<>();
        this.parsedStarNames = new HashSet<>();

        this.currentStarId = 0;
    }

    private void parseDocument(String fileName) {

        this.currentDocument = fileName;

        // get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();

        try {

            // get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            InputSource source = new InputSource(currentDocument);

            source.setEncoding("ISO-8859-1");

            // parse the file and also register this class for call backs
            sp.parse(source, this);

        } catch (SAXException | ParserConfigurationException | IOException error) {
            error.printStackTrace();
        }

    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (currentDocument.equals(Constants.movieFileName)) {

            if (qName.equalsIgnoreCase("film")) {
                tempMovie = new Movie();
            }

        } else if (currentDocument.equals(Constants.castFileName)) {

            // No action needed

        } else if (currentDocument.equals(Constants.actorFileName)) {
            if (qName.equalsIgnoreCase("actor")) {
                tempStar = new Star();
            }
        }
    }

    // <directorfilms> contains the information of a director (when ended, means we need to update the new director, so clear the tempDirector)
    // <dirname> contains the name of the director
    // <film> an instance of movie
    // <cat> a genre, if it doesn't exist in genre table must be updated, genre_in_movie must be updated
    // <fid> movie's id (need to add a prefix)
    // <t> movie's title
    // <year> movie's year (throw away all nullable data)

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (currentDocument.equals(Constants.movieFileName)) {

            if (qName.equalsIgnoreCase("directorfilms")) {
                tempDirector = null;

            } else if (qName.equalsIgnoreCase("dirname")) {
                tempDirector = tempVal;

            } else if (qName.equalsIgnoreCase("fid")) {

                // Add a prefix to simplify the removal process
                tempMovie.setId("p-" + tempVal);

            } else if (qName.equalsIgnoreCase("t")) {
                tempMovie.setTitle(tempVal);

            } else if (qName.equalsIgnoreCase("dirn")) {
                tempMovie.setDirector(tempVal);

            } else if (qName.equalsIgnoreCase("year")) {

                try {
                    tempMovie.setYear(Integer.parseInt(tempVal));

                } catch (NumberFormatException e) {
                    tempMovie.setYear(null);

                }

            } else if (qName.equalsIgnoreCase("cat")) {

                String[] result = tempVal.trim().toLowerCase().split(" ");

                for (String r : result) {
                    if (Constants.genreMapping.containsKey(r)) {

                        String mappedGenre = Constants.genreMapping.get(r);

                        tempMovie.addGenre(mappedGenre);
                    }
                }

            } else if (qName.equalsIgnoreCase("film")) {

                // Finished an entire film

                // But set price, director first
                // Then validate the movie

                tempMovie.setPrice(generatePrice());

                if (tempMovie.getDirector() == null && tempDirector != null) {
                    tempMovie.setDirector(tempDirector);
                }

                if (tempMovie.validate()) {
                    parsedMovies.put(tempMovie.getId(), tempMovie);

                } else {
                    // Handle Movie Inconsistencies
                    movieErrors.add("Movie Parsing Failed Because Of Missing Required Field(s):\n");
                    movieErrors.add(tempMovie.GetMissingRequiredFieldsDetail() + "\n");
                }

            }

        } else if (currentDocument.equals(Constants.castFileName)) {
            if (qName.equalsIgnoreCase("f")) {
                tempMovieID = "p-" + tempVal;
            } else if (qName.equalsIgnoreCase("a")) {
                tempStarName = tempVal;
            } else if (qName.equalsIgnoreCase("m")) {
                parsedCasts.put(tempStarName, tempMovieID);
            }


        } else if (currentDocument.equals(Constants.actorFileName)) {

            if (qName.equalsIgnoreCase("actor")) {
                //add it to the list
                if (tempStar.validate()) {

                    StarPair starNameBirthYearPair = new StarPair(tempStar.getName(), tempStar.getBirthYear());

                    if (!parsedStarNames.contains(starNameBirthYearPair)) {

                        tempStar.setId("p-" + (this.currentStarId++)); // p stands for parsed
                        parsedStars.add(tempStar);
                        parsedStarNames.add(starNameBirthYearPair);

                        // parsedCasts key = star name , value = movie id
                        if (parsedCasts.containsKey(tempStar.getName())) {
                            String castMovieId = parsedCasts.get(tempStar.getName());

                            if (parsedMovies.containsKey(castMovieId)) {
                                parsedMovies.get(castMovieId).addStarId(tempStar.getId());
                            } else {
                                // Handle Inconsistency: Movie ID from cast does not exist in parsedMovies
                                castErrors.add(String.format("Movie ID %s from casts124.xml does not exists in mains243.xml.\n\n", castMovieId));
                            }

                        } else {
                            // Handle Inconsistency: Star name from actor does not exist in the cast
                            castErrors.add(String.format("Star %s from actors63.xml does not exists in casts124.xml.\n\n", tempStar.getName()));
                        }

                    } else {
                        // Handle Inconsistency: Star already existed in the database
                        actorErrors.add(String.format("Star %s already existed in the database.\n\n", tempStar.getName()));
                    }
                }

            } else if (qName.equalsIgnoreCase("stagename")) {
                tempStar.setName(tempVal);
            } else if (qName.equalsIgnoreCase("dob")) {
                try {
                    tempStar.setBirthYear(Integer.parseInt(tempVal));
                } catch (NumberFormatException e) {
                    tempStar.setBirthYear(null);
                }
            }

        }
    }

    private void handleMoviesWithNoStars() {
        Set<String> allMovieIDs = new HashSet<>(this.parsedMovies.keySet());

        Set<String> movieIDsWithStars = new HashSet<>(this.parsedCasts.values());

        Set<String> movieIDsWithoutStars = new HashSet<>(allMovieIDs);
        movieIDsWithoutStars.removeAll(movieIDsWithStars);

        Iterator<String> iterator = movieIDsWithoutStars.iterator();

        while (iterator.hasNext()) {

            String mId = iterator.next();

            this.movieErrors.add(String.format("Movie with ID %s has no star.\n\n", mId));
        }

        /*
        Map<String, Movie> filteredMap = this.parsedMovies.entrySet()
                .stream()
                .filter(entry -> entry.getValue().hasStar())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        this.parsedMovies = (HashMap<String, Movie>) filteredMap;
        */
    }

    private void updateDatabase() {
        writeMoviesToDB();
        writeStarsToDB();
        writeStarsInMoviesToDB();
        writeGenresToDB();
        writeGenresInMoviesToDB();
        writeRatingsToDB();
    }

    private void writeMoviesToDB() {
        // Write Star CSV
        try {
            this.csvWriter = new FileWriter("src/xmlParser/movies.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, Movie> entry : this.parsedMovies.entrySet()) {

            Movie movie = entry.getValue();

            String movieId = movie.getId();

            String title = movie.getTitle();

            int year = movie.getYear();

            String director = movie.getDirector();

            float price = movie.getPrice();

            try {

                this.csvWriter.write(String.format("%s$%s$%d$%s$%f\n", movieId, title, year, director, price));
                this.csvWriter.flush();

            } catch (IOException e) {
                System.err.println("An error occurred.");
                e.printStackTrace();
            }

            // Genre in movies

            Set<String> genres = movie.getGenres();

            Iterator<String> iterator = genres.iterator();


            while (iterator.hasNext()) {

                String genre = iterator.next();

                if (!seenGenres.contains(genre)) {
                    seenGenres.add(genre);
                }
            }

        }

        if (this.csvWriter != null) {
            try {
                this.csvWriter.close();
            } catch (IOException e) {
                System.err.println("An error occurred.");
                e.printStackTrace();
            }
        }

        String loadStarsQuery = "load data local infile 'src/xmlParser/movies.csv' into table movies\n" +
                "fields terminated by '$'\n" +
                "lines terminated by '\n';";
        try {
            new XMLDatabaseHandler().executeDataLoadQuery(loadStarsQuery);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void writeStarsToDB() {
        // Write Star CSV
        try {
            this.csvWriter = new FileWriter("src/xmlParser/stars.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Star star : parsedStars) {
            try {
                this.csvWriter.write(star.getCSVLine());
                this.csvWriter.flush();
            } catch (IOException e) {
                System.err.println("An error occurred.");
                e.printStackTrace();
            }
        }

        if (this.csvWriter != null) {
            try {
                this.csvWriter.close();
            } catch (IOException e) {
                System.err.println("An error occurred.");
                e.printStackTrace();
            }
        }

        String loadStarsQuery = "load data local infile 'src/xmlParser/stars.csv' into table stars\n" +
                "fields terminated by ','\n" +
                "lines terminated by '\n';";
        try {
            new XMLDatabaseHandler().executeDataLoadQuery(loadStarsQuery);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void writeStarsInMoviesToDB() {

        // Write Star CSV
        try {
            this.csvWriter = new FileWriter("src/xmlParser/sim.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, Movie> entry : this.parsedMovies.entrySet()) {

            Movie movie = entry.getValue();

            String movieId = movie.getId();

            Set<String> starIDs = movie.getStarIds();

            Iterator<String> iterator = starIDs.iterator();

            while (iterator.hasNext()) {

                String starId = iterator.next();

                try {
                    this.csvWriter.write(String.format("%s,%s\n", starId, movieId));
                    this.csvWriter.flush();

                } catch (IOException e) {
                    System.err.println("An error occurred.");
                    e.printStackTrace();
                }

            }
        }

        if (this.csvWriter != null) {
            try {
                this.csvWriter.close();
            } catch (IOException e) {
                System.err.println("An error occurred.");
                e.printStackTrace();
            }
        }

        String loadStarsQuery = "load data local infile 'src/xmlParser/sim.csv' into table stars_in_movies\n" +
                "fields terminated by ','\n" +
                "lines terminated by '\n';";
        try {
            new XMLDatabaseHandler().executeDataLoadQuery(loadStarsQuery);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    private void writeGenresToDB() {
        String genreUpdate = "INSERT IGNORE INTO genres SELECT null, ? WHERE NOT EXISTS (SELECT * FROM genres WHERE name= ? )";

        XMLDatabaseHandler dbh = new XMLDatabaseHandler();

        Iterator<String> iterator = this.seenGenres.iterator();

        while (iterator.hasNext()) {

            String genre = iterator.next();

            try {
                dbh.executeUpdate(genreUpdate, genre, genre);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String getGenresMapping = "SELECT id, name FROM genres";

        try {
            List<HashMap<String, String>> result = dbh.executeQuery(getGenresMapping);

            // System.out.println(result.toString());

            for (HashMap<String, String> hm : result) {

                Integer id = Integer.parseInt(hm.get("id"));
                String name = hm.get("name");

                this.genresMapping.put(name, id);
            }

            // System.out.println(this.genresMapping.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void writeGenresInMoviesToDB() {

        try {
            this.csvWriter = new FileWriter("src/xmlParser/gim.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, Movie> entry : this.parsedMovies.entrySet()) {

            Movie movie = entry.getValue();

            String movieId = movie.getId();

            Set<String> genres = movie.getGenres();

            Iterator<String> iterator = genres.iterator();

            // System.out.println(this.genresMapping.toString());

            while (iterator.hasNext()) {

                String name = iterator.next();

                // System.out.println(name);

                Integer id = this.genresMapping.get(name);

                // System.out.println(id);

                try {
                    this.csvWriter.write(String.format("%d,%s\n", id, movieId));
                    this.csvWriter.flush();

                } catch (IOException e) {
                    System.err.println("An error occurred.");
                    e.printStackTrace();
                }

            }
        }

        if (this.csvWriter != null) {
            try {
                this.csvWriter.close();
            } catch (IOException e) {
                System.err.println("An error occurred.");
                e.printStackTrace();
            }
        }

        String loadStarsQuery = "load data local infile 'src/xmlParser/gim.csv' into table genres_in_movies\n" +
                "fields terminated by ','\n" +
                "lines terminated by '\n';";
        try {
            new XMLDatabaseHandler().executeDataLoadQuery(loadStarsQuery);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    private void writeRatingsToDB() {

        try {
            this.csvWriter = new FileWriter("src/xmlParser/ratings.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, Movie> entry : this.parsedMovies.entrySet()) {

            Movie movie = entry.getValue();

            String movieId = movie.getId();

            try {
                this.csvWriter.write(String.format("%s,%f,%d\n", movieId, 0.0, 100));
                this.csvWriter.flush();

            } catch (IOException e) {
                System.err.println("An error occurred.");
                e.printStackTrace();
            }
        }

        if (this.csvWriter != null) {
            try {
                this.csvWriter.close();
            } catch (IOException e) {
                System.err.println("An error occurred.");
                e.printStackTrace();
            }
        }

        String loadStarsQuery = "load data local infile 'src/xmlParser/ratings.csv' into table ratings\n" +
                "fields terminated by ','\n" +
                "lines terminated by '\n';";
        try {
            new XMLDatabaseHandler().executeDataLoadQuery(loadStarsQuery);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    private void generateSummaryReport() {

        System.out.println("Parsed Movie Count: " + parsedMovies.size());
        System.out.println("Parsed Casts Count: " + parsedCasts.size());
        System.out.println("Parsed Stars Count: " + parsedStars.size());

        /*

        System.out.println("\n10 Sample Movies:");
        int printedMovieCount = 0;
        for (Map.Entry<String, Movie> entry : parsedMovies.entrySet()) {
            if (printedMovieCount >= 10) {
                break;
            }
            System.out.println("Key (Movie ID): " + entry.getKey() + "\nValue (Movie): \n" + entry.getValue().GetDetails());
            printedMovieCount++;
        }

        System.out.println("\n10 Sample Casts:");
        int printedCastCount = 0;
        for (Map.Entry<String, String> entry : parsedCasts.entrySet()) {
            if (printedCastCount >= 10) {
                break;
            }
            System.out.println("Key (Star Name): " + entry.getKey() + ", Value (Movie ID): " + entry.getValue());
            printedCastCount++;
        }

        System.out.println("\n10 Sample Stars:");

        Iterator<Star> iterator = parsedStars.iterator();
        for (int i = 0; i < 10 && iterator.hasNext(); i++) {
            Star star = iterator.next();
            System.out.println(star.getDetails());
        }

         */

    }

    private void generateInconsistencyReport() {

        ArrayList<String> reports = new ArrayList<>();
        reports.add("src/xmlParser/MovieInconsistencyReport.txt");
        reports.add("src/xmlParser/CastInconsistencyReport.txt");
        reports.add("src/xmlParser/StarInconsistencyReport.txt");

        for (String report : reports) {

            try {

                this.inconsistencyReportWriter = new FileWriter(report);

                if (report.equals("src/xmlParser/MovieInconsistencyReport.txt")) {
                    for (String err : this.movieErrors) {
                        try {
                            inconsistencyReportWriter.write(err);
                        } catch (IOException e) {
                            System.err.println("An error occurred: ");
                            e.printStackTrace();
                        }
                    }

                } else if (report.equals("src/xmlParser/CastInconsistencyReport.txt")) {
                    for (String err : this.castErrors) {
                        try {
                            inconsistencyReportWriter.write(err);
                        } catch (IOException e) {
                            System.err.println("An error occurred: ");
                            e.printStackTrace();
                        }
                    }
                } else if (report.equals("src/xmlParser/StarInconsistencyReport.txt")) {
                    for (String err : this.actorErrors) {
                        try {
                            inconsistencyReportWriter.write(err);
                        } catch (IOException e) {
                            System.err.println("An error occurred: ");
                            e.printStackTrace();
                        }
                    }
                }

                if (this.inconsistencyReportWriter != null) {
                    try {
                        this.inconsistencyReportWriter.close();
                    } catch (IOException e) {
                        System.err.println("An error occurred.");
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private Float generatePrice() {
        // Range: 5 - 20
        return (float) Math.round((5 + random.nextFloat() * (20 - 5)) * 100) / 100;
    }

}