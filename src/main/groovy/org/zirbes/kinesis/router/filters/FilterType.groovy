package org.zirbes.kinesis.router.filters

enum FilterType {

    AND,
    DATETIME,
    OPEN,
    OR,
    RATE

    DataFilter filter(String param) {
        switch (this) {
            case AND:
                return new AndFilterChain()
            case DATETIME:
                return new DateTimeFilter(param)
            case OPEN:
                return new OpenFilter()
            case OR:
                return new OrFilterChain()
            case RATE:
                return new RateFilter(param as Double)
            default:
                return new OpenFilter()
        }
    }

    static final List<FilterType> CHAINS = [ OR, AND ].asImmutable()

    static FilterType fromString(String value) {
        if (value == null) { return null }
        FilterType type = values().find { it.name().equalsIgnoreCase(value) }
        if (!type && (value != 'null')) {
            String errorMessage = "No matching '${FilterType}' found for '${value}' expected one of ${values()}"
            throw new IllegalArgumentException(errorMessage)
        }
        return type
    }
}
