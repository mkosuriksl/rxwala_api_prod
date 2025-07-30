package com.kosuri.stores.controller;


import com.kosuri.stores.dao.RetailerEntity;
import com.kosuri.stores.handler.RetailerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/retailer")
public class RetailerController {

    @Autowired
    private RetailerHandler retailerHandler;


    @GetMapping("/getAllRetailers")
    private List<RetailerEntity> getAllRetailers(){
        return retailerHandler.getAllRetailers();
    }
}
