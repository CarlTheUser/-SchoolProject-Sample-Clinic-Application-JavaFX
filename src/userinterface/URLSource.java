package userinterface;

import java.net.URL;

public final class URLSource {

//    private static final URLSource instance = new URLSource();
    public static URL getURL(String url){
        return URLSource.class.getResource(url);
    }

}
