package com.kosuri.stores.model.response;

import java.util.List;

import com.kosuri.stores.model.search.PharmasistSearchResult;
import com.kosuri.stores.model.search.SearchResult;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchPhramacistResponse {

    private List<SearchResult> searchResultList;

    private List<PharmasistSearchResult> pharmasistSearchResults;

}

