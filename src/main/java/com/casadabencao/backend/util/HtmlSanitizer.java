package com.casadabencao.backend.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class HtmlSanitizer {
    public static String sanitize(String inputHtml) {
        return Jsoup.clean(inputHtml, Safelist.basic()
                .addTags("p", "a", "strong", "em", "u", "ul", "ol", "li", "br")
                .addAttributes("a", "href", "target", "rel")
        );
    }
}
