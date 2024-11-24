package org.basdat.basdatchemicals;

import java.util.List;

public record PagedListResult<T>(int currentPage, int maxPage, List<T> content) {}
