package com.chris.gestionpersonal.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppConstants {
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Pagination{
        public static final String PAGE_NUMBER = "0";
        public static final String PAGE_SIZE = "20";
        public static final String SORT_BY = "date";
        public static final String SORT_DIRECTION = "desc";
    }
}
