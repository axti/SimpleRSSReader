package com.clover.simplerssreader;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by aleksandrgranin on 26/01/2018.
 */

public class RssHandler extends DefaultHandler {
    private RssFeed rssFeed;
    private RssFeedItem rssItem;
    private StringBuilder stringBuilder;
    String imageUrl;

    public RssHandler(RssFeed rssFeed) {
        super();
        this.rssFeed = rssFeed;
//        if (rssFeed.getFeedItems() != null && rssFeed.getFeedItems().size() > 0)
//            clearAllFeedItems();
    }

    private void clearAllFeedItems() {
        for (RssFeedItem rssFeedItem: rssFeed.getFeedItems()) {
            rssFeedItem.delete();
        }
        rssFeed.feedItems.clear();
        rssFeed.storeFeed();
    }

    @Override
    public void startDocument() {
        if (rssFeed.getFeedItems() != null && rssFeed.getFeedItems().size() > 0)
            clearAllFeedItems();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        stringBuilder = new StringBuilder();

        if(qName.equals("item") && rssFeed != null) {
            rssItem = new RssFeedItem();
//            rssItem.feedId = rssFeed.id;
            rssFeed.feedItems.add(rssItem);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        stringBuilder.append(ch, start, length);
    }

    public void saveResult() {
        Log.i("RssHandler", "saveResult " + rssFeed.toString());
        rssFeed.storeFeed();
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if(rssFeed != null && rssItem == null) {
            try {
                if (qName != null && qName.length() > 0) {
                    if (qName.toLowerCase().equals("url"))
                        imageUrl = stringBuilder.toString();
                    if (qName.toLowerCase().equals("image"))
                    {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(imageUrl);
                    }

                    setField(rssFeed, qName, stringBuilder.toString());
//                    String methodName = "set" + qName.substring(0, 1).toUpperCase() + qName.substring(1);
//                    rssFeed.getClass().getField(qName)
//                    Method method = rssFeed.getClass().getMethod(methodName, String.class);
//                    method.invoke(rssFeed, stringBuilder.toString());
                }
            } catch (SecurityException e) {
            } catch (NoSuchMethodException e) {
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            } catch (NoSuchFieldException e) {
            } catch (Exception e) {
            }
        } else if (rssItem != null) {
            // Parse item properties
            if (qName.toLowerCase().equals("description"))
            {
                //hack for some image uri
                String s = stringBuilder.toString();
                if (s.contains("img src=\"/")){
                    String baseUrl =rssFeed.feedUrl;
                    try {
                        URI mUrl = new URI(rssFeed.feedUrl);
                        baseUrl = mUrl.getScheme() + "://" + mUrl.getHost();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    s = s.replaceAll("img src=\"/","img src=\"" + baseUrl + "/");
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(s);
                }

            }
            try {
                setField(rssItem, qName, stringBuilder.toString());
            } catch (SecurityException e) {
            } catch (NoSuchMethodException e) {
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            } catch (NoSuchFieldException e) {
            } catch (Exception e) {
            }
        }
    }

    public static void setField(Object object, String fieldName, Object value) throws Exception {
        Field field = getField(object.getClass(), fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    public static Field getField(Class<?> clazz, String name) {
        Field field = null;
        while (clazz != null && field == null) {
            try {
                field = clazz.getDeclaredField(name);
            } catch (Exception e) {
            }
            clazz = clazz.getSuperclass();
        }
        return field;
    }
}
