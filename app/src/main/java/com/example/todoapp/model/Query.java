package com.example.todoapp.model;

import java.util.List;

public class Query {

    private String search;
    private List<String> searchAttribute;
    private Sort sort;
    private Filter filterObj;
    private int skip;
    private int limit = 20;

    public Query() {
        this.sort = new Sort();
        this.filterObj = new Filter();
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(final String search) {
        this.search = search;
    }

    public List<String> getSearchAttribute() {
        return searchAttribute;
    }

    public void setSearchAttribute(final List<String> searchAttribute) {
        this.searchAttribute = searchAttribute;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(final Sort sort) {
        this.sort = sort;
    }

    public Filter getFilterObj() {
        return filterObj;
    }

    public void setFilterObj(final Filter filterObj) {
        this.filterObj = filterObj;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(final int limit) {
        this.limit = limit;
    }

    public int getSkip() {
        return skip;
    }
}
