package com.blackducksoftware.integration.hub.bdio.simple.model;

public enum Forge {
    maven(":"),
    pypi("/"),
    nuget("/"),
    npm("@"),
    rubygems("="),
    cocoapods(":");

    public final String separator;

    private Forge(final String separator) {
        this.separator = separator;
    }

}
