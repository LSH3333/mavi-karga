package com.lsh.mavikarga.enums;

import java.util.Locale;

public enum Language {
    BASIC("KOR", Locale.KOREA), ENGLISH("ENG", Locale.ENGLISH);;

    private String lang;
    private Locale locale;

    Language(String lang, Locale locale) {
        this.lang = lang;
        this.locale = locale;
    }
}
