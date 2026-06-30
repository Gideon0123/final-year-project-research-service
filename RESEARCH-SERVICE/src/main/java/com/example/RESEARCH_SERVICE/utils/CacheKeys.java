package com.example.RESEARCH_SERVICE.utils;

public final class CacheKeys {

    private CacheKeys() {}

    public static final String CATEGORY_ID = "'category:' + #id";

    public static final String CATEGORY_ALL = "'all'";

    public static final String CATEGORY_SEARCH =
            "'search:' + #keyword"
                    + " + ':page=' + #pageable.pageNumber"
                    + " + ':size=' + #pageable.pageSize"
                    + " + ':sort=' + #pageable.sort.toString()";

    public static final String PAPER_ID =
            "#paperId";

    public static final String ALL_PAPERS =
            "#page + ':' + #size + ':' + #sortBy + ':' + #sortDirection";

    public static final String MY_PAPERS =
            "#root.target.currentUserService.currentUser.id" +
                    " + ':' + #page + ':' + #size + ':' + #sortBy +" +
                    " ':' + #sortDirection";

    public static final String PAPER_SEARCH =
            "#request.toCacheKey() + ':' + #pageable.pageNumber + ':' + #pageable.pageSize";

}