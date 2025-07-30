package com.kosuri.stores.handler;

import com.kosuri.stores.dao.RetailerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RetailerHandler {

    @Autowired
    private RepositoryHandler repositoryHandler;

    public List<RetailerEntity> getAllRetailers() {
        return repositoryHandler.getAllRetailers();
    }
}
