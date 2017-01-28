package com.guidedmeditationtreks.binaural;

import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mrrussell on 2/15/16.
 */
public class Linkify {

    /**
     * @param textView
     *            textView who's text you want to change
     * @param linkThis
     *            a regex of what text to turn into a link
     * @param toThis
     *            the url you want to send them to
     */
    public static void addLinks(TextView textView, String linkThis, String toThis) {
        Pattern pattern = Pattern.compile(linkThis);
        String scheme = toThis;
        android.text.util.Linkify.addLinks(textView, pattern, scheme, new android.text.util.Linkify.MatchFilter() {
            @Override
            public boolean acceptMatch(CharSequence s, int start, int end) {
                return true;
            }
        }, new android.text.util.Linkify.TransformFilter() {

            @Override
            public String transformUrl(Matcher match, String url) {
                return "";
            }
        });
    }
}
