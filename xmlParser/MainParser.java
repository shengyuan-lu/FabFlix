import java.util.*;

import java.io.IOException;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import models.Movie;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import helpers.Constants;

public class MainParser extends DefaultHandler {

    private DataSource dataSource;

    // For generating random movie prices
    private Random random = new Random();


    // For parsing - universal
    private String tempVal;
    private String currentDocument;

    // For parsing - mains243.xml
    private ArrayList<Movie> parsedMovies;
    private Movie tempMovie;
    private String tempDirector;

    // For parsing - actors63.xml
    // ...


    // For parsing - casts124.xml
    // ...


    // The main function of the parser
    public static void main(String[] args) {

        MainParser parser = new MainParser();

        parser.parseDocument(Constants.movieFileName);

        parser.parseDocument(Constants.castFileName);

        parser.parseDocument(Constants.actorFileName);

        parser.generateReport();
    }

    public MainParser() {

        this.tempVal = "";
        this.currentDocument = "";
        this.tempDirector = "";

        this.parsedMovies = new ArrayList<>();
    }

    private void parseDocument(String fileName) {

        this.currentDocument = fileName;

        // get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        factory.setValidating(true);

        try {
            // get a new instance of parser
            SAXParser sp = spf.newSAXParser();
            XMLReader xmlReader = sp.getXMLReader();
            xmlReader.setContentHandler(this);
            xmlReader.setErrorHandler(new ErrorHandler(System.err));

            InputSource source = new InputSource(currentDocument);
            source.setEncoding("ISO-8859-1");

            xmlReader.parse(source);

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

        } else if (currentDocument.equals(Constants.actorFileName)) {

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
                tempMovie.setId("AddedMovie-" + tempVal);

            } else if (qName.equalsIgnoreCase("t")) {
                tempMovie.setTitle(tempVal);

            } else if (qName.equalsIgnoreCase("year")) {

                try {
                    tempMovie.setYear(Integer.parseInt(tempVal));

                } catch (NumberFormatException e) {
                    tempMovie.setYear(null); // Inconsistency!!

                }

            } else if (qName.equalsIgnoreCase("cat")) {

                String[] result = tempVal.trim().toLowerCase().split(" ");

                for (String r : result) {
                    if (Constants.genreMapping.containsKey(r)) {

                        String mappedGenre = Constants.genreMapping.get(r);

                        tempMovie.addGenre(mappedGenre);
                    } else {
                        // Process Genre Not In Mapping
                        // System.out.println("Genre Not In Mapping: " + r);
                    }
                }

            } else if (qName.equalsIgnoreCase("film")) {

                // Finished an entire film

                // But set price, director first
                // Then validate the movie

                tempMovie.setPrice(generatePrice());

                tempMovie.setDirector(tempDirector);

                if (tempMovie.validate()) {
                    parsedMovies.add(tempMovie);

                } else {
                    // Process Movie Inconsistencies
                    /*
                    System.out.println("Movie Parsing Failed Because Of Missing Required Field(s):");
                    System.out.println(tempMovie.GetDetails());
                    */
                }

            }

        } else if (currentDocument.equals(Constants.castFileName)) {



        } else if (currentDocument.equals(Constants.actorFileName)) {



        }
    }

    public void generateReport() {

        // More report is needed

        System.out.println("Parsed Movies Count: " + parsedMovies.size());
    }

    private Double generatePrice() {
        // Range: 5 - 20
        return Math.round((5 + random.nextDouble() * (20 - 5)) * 100) / 100.0;
    }

}

private static class ErrorHandler implements ErrorHandler {
    private PrintStream out;

    MyErrorHandler(PrintStream out) {
        this.out = out;
    }

    private String getParseExceptionInfo(SAXParseException spe) {
        String systemId = spe.getSystemId();

        if (systemId == null) {
            systemId = "null";
        }

        String info = "URI=" + systemId + " Line="
                + spe.getLineNumber() + ": " + spe.getMessage();

        return info;
    }

    public void warning(SAXParseException spe) throws SAXException {
        out.println("Warning: " + getParseExceptionInfo(spe));
    }

    public void error(SAXParseException spe) throws SAXException {
        String message = "Error: " + getParseExceptionInfo(spe);
        throw new SAXException(message);
    }

    public void fatalError(SAXParseException spe) throws SAXException {
        String message = "Fatal Error: " + getParseExceptionInfo(spe);
        throw new SAXException(message);
    }
}
