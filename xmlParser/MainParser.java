import java.sql.*;
import java.util.*;

import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import helpers.Constants;

public class MainParser extends DefaultHandler {

    public static void main(String[] args) {
        MainParser parser = new MainParser();

        parser.parseDocument(Constants.movieFileName);

        parser.parseDocument(Constants.castFileName);

        parser.parseDocument(Constants.actorFileName);
    }

    private void parseDocument(String document) {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            InputSource source = new InputSource(document);

            source.setEncoding("ISO-8859-1");

            //parse the file and also register this class for call backs
            sp.parse(source, this);

        } catch (SAXException | ParserConfigurationException | IOException error) {
            error.printStackTrace();
        }
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

    }

}