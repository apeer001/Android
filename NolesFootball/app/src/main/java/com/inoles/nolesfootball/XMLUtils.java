package com.inoles.nolesfootball;

import android.support.annotation.NonNull;

public class XMLUtils {
    private static final String[][] BASIC_ARRAY = {
            {"quot" , "34"}, // " - double-quote
            {"amp"  , "38"}, // & - ampersand
            {"lt"   , "60"}, // < - less-than
            {"gt"   , "62"}, // > - greater-than
            {"apos" , "39"}, // XML apostrophe
    };

    private XMLUtils() {}

    @NonNull
    public static String unescape(String str) {
        int entityValue;

        StringBuilder buf = new StringBuilder(str.length());
        for (int i = 0, l = str.length(); i < l; ++i) {
            char ch = str.charAt(i);
            if (ch == '&') {
                int semi = str.indexOf(';', i + 1);
                if (semi == -1) {
                    buf.append(ch);
                    continue;
                }

                String entityName = str.substring(i + 1, semi);
                if (entityName.charAt(0) == '#') {
                    char charAt1 = entityName.charAt(1);
                    if (charAt1 == 'x' || charAt1 == 'X') {
                        entityValue = Integer.valueOf(entityName.substring(2), 16);
                    } else {
                        entityValue = Integer.parseInt(entityName.substring(1));
                    }
                } else {
                    entityValue = getEntities(entityName);
                }
                if (entityValue == -1) {
                    buf.append('&');
                    buf.append(entityName);
                    buf.append(';');
                } else {
                    buf.append((char) entityValue);
                }
                i = semi;
            } else {
                buf.append(ch);
            }
        }

        return buf.toString();
    }

    private static int getEntities(String text) {
        String[][] localArray = BASIC_ARRAY;
        int len = localArray.length;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < len; ++i) {
            if (localArray[i][0].equals(text)) {
                return Integer.parseInt(localArray[i][1]);
            }
        }
        return -1;
    }
}
