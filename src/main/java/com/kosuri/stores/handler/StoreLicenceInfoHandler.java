package com.kosuri.stores.handler;

import com.kosuri.stores.dao.*;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.model.enums.Status;
import com.kosuri.stores.model.request.StoreLicenceInfoRequest;
import com.kosuri.stores.model.response.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StoreLicenceInfoHandler {

    @Autowired
    private StoreLicenceInfoRepository storeLicenceInfoRepository;

    @Autowired
    private TabStoreRepository tabStoreRepository;

    @Autowired
    private StoreRepository storeRepository;
    public GenericResponse addStoreLicenceInfo(StoreLicenceInfoRequest request) throws Exception {

        if (!isStoreIdPresent(request.getStoreId())){
            throw new APIException("Store Is Not present in the System");
        }

        if (!isStoreActive(request.getStoreId())){
            throw new APIException("Store Is Not Active. Please Activate The Store");
        }

        GenericResponse response = new GenericResponse();

        StoreLicenceInfoEntity entity =  createStoreLicenceInfoEntityFromRequest(request);

        try{
            StoreLicenceInfoEntity resultEntity = storeLicenceInfoRepository.save(entity);
            response.setResponseMessage("Store Licence Information Added SuccessFully");
        }catch (DataIntegrityViolationException e) {
            throw new Exception(e.getCause().getCause().getMessage());
        }

        return response;
    }

    private boolean isStoreActive(String storeId) {
        Optional<StoreEntity> tabStoreOptional = storeRepository.findById(storeId);
        if (tabStoreOptional.isPresent()){
            StoreEntity tabStore = tabStoreOptional.get();
            String status = tabStore.getStatus();
            return status.equalsIgnoreCase(Status.ACTIVE.name());
        }
        return false;
    }

    private boolean isStoreIdPresent(String storeId) {
        Optional<StoreEntity> tabStore = storeRepository.findById(storeId);
        return tabStore.isPresent();
    }

    public StoreLicenceInfoEntity createStoreLicenceInfoEntityFromRequest(StoreLicenceInfoRequest request) {
        StoreLicenceInfoEntity entity = new StoreLicenceInfoEntity();

        entity.setStoreId(request.getStoreId());
        entity.setPharmacyLicense(request.getPharmacyLicense());
        entity.setGstLicense(request.getGstLicense());
        entity.setLicenceNumber(request.getLicenceNumber());
        entity.setGstNumber(request.getGstNumber());
        entity.setPharmacyLicenseExpiry(request.getPharmacyLicenseExpiry());
        entity.setUpdatedBy(request.getUpdatedBy());
        entity.setUpdatedDate(LocalDate.now().toString());
        entity.setLicenseRegisteredState(request.getLicenseRegisteredState());
        entity.setLicenseRegisteredDistrict(request.getLicenseRegisteredDistrict());
        entity.setLicenseRegisteredDivision(request.getLicenseRegisteredDivision());

        return entity;
    }

    public StoreLicenceInfoEntity getStoreLicenceInfo(String storeId) {

        return storeLicenceInfoRepository.findByStoreId(storeId);
    }

    public List<StoreLicenceInfoEntity> getAllStoreLicenceInfo() {
        return storeLicenceInfoRepository.findAll();
    }

    public boolean updateStoreLicenceInfo(StoreLicenceInfoRequest request) {

        Optional<StoreLicenceInfoEntity> entityOptional = storeLicenceInfoRepository.findById(request.getStoreId());
        if (entityOptional.isPresent()) {
            StoreLicenceInfoEntity storeLicenceInfo = entityOptional.get();
            storeLicenceInfo.setPharmacyLicense(request.getPharmacyLicense());
            storeLicenceInfo.setGstLicense(request.getGstLicense());
            storeLicenceInfo.setLicenceNumber(request.getLicenceNumber());
            storeLicenceInfo.setGstNumber(request.getGstNumber());
            storeLicenceInfo.setPharmacyLicenseExpiry(request.getPharmacyLicenseExpiry());
            storeLicenceInfo.setUpdatedBy(request.getUpdatedBy());
            storeLicenceInfo.setUpdatedDate(LocalDate.now().toString());
            storeLicenceInfo.setLicenseRegisteredState(request.getLicenseRegisteredState());
            storeLicenceInfo.setLicenseRegisteredDistrict(request.getLicenseRegisteredDistrict());
            storeLicenceInfo.setLicenseRegisteredDivision(request.getLicenseRegisteredDivision());
            storeLicenceInfoRepository.save(storeLicenceInfo);
            return true;
        } else {
            return false;
        }

    }
}
