package com.example.RESEARCH_SERVICE.utils;

public final class CacheKeys {

    private CacheKeys() {}

    public static final String CATEGORY_ID = "'category:' + #id";

    public static final String CATEGORY_ALL = "'all'";

    public static final String CATEGORY_SEARCH =
            "#request.toString() + ':' + #pageable.pageNumber + ':' + #pageable.pageSize";

    public static final String PAPER_ID = "'paper:' + #paperId";

    public static final String PAPER_SEARCH =
            "#request.toString() + ':' + #pageable.pageNumber + ':' + #pageable.pageSize";
}