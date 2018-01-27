package com.clover.simplerssreader.saxparse;

import android.util.Log;

import com.clover.simplerssreader.model.RssFeed;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by aleksandrgranin on 26/01/2018.
 */

public class RssReader {
    public static void read(InputStream stream, RssFeed rssFeed) throws SAXException, IOException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            RssHandler handler = new RssHandler(rssFeed);
            InputSource input = new InputSource(stream);

            reader.setContentHandler(handler);
            reader.parse(input);

            handler.saveResult();
        } catch (ParserConfigurationException e) {
            throw new SAXException();
        } catch (Exception e){
            Log.e("RssReader", e.getMessage(), e);
        }
    }
}
