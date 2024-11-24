package org.basdat.basdatchemicals;

import java.util.List;

public record ListResult<T>(int currentPage, int maxPage, List<T> content) {}
