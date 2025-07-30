package com.kosuri.stores.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtItemListDto {

    private String itemCategory;

    private String itemSubcategory;

    private String brand;

    private String itemCode;

    private String manufacturer ;

    private Integer gst;

    private String updatedBy;

    private LocalDateTime updatedDate;
  
    private String userIdItemCode;
    
    private String itemname;

}
