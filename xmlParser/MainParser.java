import java.io.FileWriter;
import java.util.*;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import models.Movie;
import models.Star;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import helpers.Constants;

public class MainParser extends DefaultHandler {

    // The main function of the parser
    public static void main(String[] args) {

        MainParser parser = new MainParser();

        parser.parseDocument(Constants.movieFileName);

        parser.parseDocument(Constants.castFileName);

        parser.parseDocument(Constants.actorFileName);
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

    // For parsing - casts124.xml
    private String tempMovieID;
    private String tempStarName;
    private HashMap<String, String> parsedCasts;  // key = star name , value = movie id

    // For parsing - actors63.xml
    private HashSet<Star> parsedStars;
    private Set<String> parsedStarNames;
    private Star tempStar;
    private int currentStarId;

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

            if (Objects.equals(this.currentDocument, Constants.movieFileName)) {
                this.inconsistencyReportWriter = new FileWriter("xmlParser/MovieInconsistencyReport.txt");

            } else if (Objects.equals(this.currentDocument, Constants.castFileName)) {
                this.inconsistencyReportWriter = new FileWriter("xmlParser/CastInconsistencyReport.txt");

            } else if (Objects.equals(this.currentDocument, Constants.actorFileName)) {
                this.inconsistencyReportWriter = new FileWriter("xmlParser/StarInconsistencyReport.txt");
            }

            // get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            InputSource source = new InputSource(currentDocument);

            source.setEncoding("ISO-8859-1");

            // parse the file and also register this class for call backs
            sp.parse(source, this);

            if (this.inconsistencyReportWriter != null) {

                try {
                    this.inconsistencyReportWriter.close();
                } catch (IOException e) {
                    System.err.println("An error occurred.");
                    e.printStackTrace();
                }
            }

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

                tempMovie.setDirector(tempDirector);

                if (tempMovie.validate()) {
                    parsedMovies.put(tempMovie.getId(), tempMovie);

                } else {
                    // Handle Movie Inconsistencies
                    try {
                        inconsistencyReportWriter.write("Movie Parsing Failed Because Of Missing Required Field(s):\n");
                        inconsistencyReportWriter.write(tempMovie.GetMissingRequiredFieldsDetail() + "\n");
                    } catch (IOException e) {
                        System.err.println("An error occurred: ");
                        e.printStackTrace();
                    }
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

                    if (!parsedStarNames.contains(tempStar.getName())) {

                        tempStar.setId("p-" + (this.currentStarId++)); // p stands for parsed

                        parsedStars.add(tempStar);

                        parsedStarNames.add(tempStar.getName());

                        // parsedCasts key = star name , value = movie id

                        if (parsedCasts.containsKey(tempStar.getName())) {

                            String castMovieId = parsedCasts.get(tempStar.getName());

                            if (parsedMovies.containsKey(castMovieId)) {

                                parsedMovies.get(castMovieId).addStarId(tempStar.getId());

                            } else {

                                // Handle Inconsistency: Movie ID from cast does not exist in parsedMovies
                                try {
                                    inconsistencyReportWriter.write(String.format("Movie ID %s from casts124.xml does not exists in mains243.xml.\n\n", castMovieId));
                                } catch (IOException e) {
                                    System.err.println("An error occurred: ");
                                    e.printStackTrace();
                                }
                            }

                        } else {

                            // Handle Inconsistency: Star name from actor does not exist in the cast
                            try {
                                inconsistencyReportWriter.write(String.format("Star %s from actors63.xml does not exists in casts124.xml.\n\n", tempStar.getName()));
                            } catch (IOException e) {
                                System.err.println("An error occurred: ");
                                e.printStackTrace();
                            }

                        }

                    } else {

                        // Handle Inconsistency: Star already existed in the database
                        try {
                            inconsistencyReportWriter.write(String.format("Star %s already existed in the database.\n\n", tempStar.getName()));
                        } catch (IOException e) {
                            System.err.println("An error occurred: ");
                            e.printStackTrace();
                        }
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

    public void updateDatabase() {

    }

    private float generatePrice() {
        // Range: 5 - 20
        return (float) ((float)Math.round((5 + random.nextFloat() * (20 - 5)) * 100) / 100.0);
    }

}