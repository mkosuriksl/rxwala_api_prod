package com.kosuri.stores.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosuri.stores.dao.ItemCodeMaster;
import com.kosuri.stores.dao.ItemCodeMasterRepository;
import com.kosuri.stores.dao.ItemDiscountCurrent;
import com.kosuri.stores.dao.ItemDiscountCurrentRepository;
import com.kosuri.stores.dao.ItemOfferEntity;
import com.kosuri.stores.dao.ItemOfferRepository;
import com.kosuri.stores.dao.StockEntity;
import com.kosuri.stores.dao.StockRepository;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreRepository;
import com.kosuri.stores.model.dto.StockDistributor;
import com.kosuri.stores.model.dto.StockDistributorResult;
import com.kosuri.stores.model.dto.StoreDistributor;
import com.kosuri.stores.model.search.SearchResult;
import com.kosuri.stores.model.search.StockAndDiscountEnResult;
import com.kosuri.stores.model.search.StockAndDiscountResult;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class SearchHandler {

    @Autowired
    StockRepository stockRepository;
    
    @Autowired
    StoreRepository storeRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private ItemOfferRepository itemOfferRepository;
	
	@Autowired
	private ItemCodeMasterRepository itemCodeMasterRepository;

    public List<SearchResult> search(String medicine, String location, String category) {

        List<SearchResult> searchResultList = new ArrayList<>();

        Optional<List<StoreEntity>> storeList = Optional.ofNullable(storeRepository.findByLocationContaining(location));
        if (storeList.isPresent() && category.equalsIgnoreCase("medicine")) {
            for (StoreEntity storeEntity : storeList.get()) {
                if (storeEntity.getId().contains("DUMMY")) {
                    continue;
                }
                List<StockEntity> availableStockEntity = stockRepository.findFirstByItemNameContainingAndStoreIdAndBalQuantityGreaterThan(medicine,
                        storeEntity.getId(), 0D);

                for (StockEntity stockEntity : availableStockEntity) {
                    SearchResult searchResult = new SearchResult();
                    searchResult.setMedicineName(stockEntity.getItemName());
                    searchResult.setMrp(stockEntity.getMrpPack());
                    searchResult.setShopLocation(storeEntity.getLocation());
                    searchResult.setShopName(storeEntity.getName());
                    searchResult.setBatchNo(stockEntity.getBatch());
                    searchResult.setExpiryDate(stockEntity.getExpiryDate());

                    searchResultList.add(searchResult);
                }
            }
        }

        return searchResultList;
    }
    
    public List<StockDistributorResult> getMedicineDetails(String medicineName, String location, String mfName) {
        List<StockEntity> stockList = stockRepository.findByItemName(medicineName);

        // Map to hold unique stores with their list of stock entries
        Map<String, List<StockEntity>> storeStockMap = new LinkedHashMap<>();

        for (StockEntity stock : stockList) {
            StoreEntity store = storeRepository.findById(stock.getStoreId()).orElse(null);
            if (store == null) continue;

            // Apply all filters
            if (!store.getLocation().equalsIgnoreCase(location)) continue;
            if (!"PH".equalsIgnoreCase(store.getType())) continue;//alllows pharmacy
            if ("DT".equalsIgnoreCase(store.getStoreBusinessType())) continue;//it allows rt,wh
            if (mfName != null && !mfName.trim().isEmpty() && !mfName.equalsIgnoreCase(stock.getMfName())) continue;

            // Group by storeId
            storeStockMap.computeIfAbsent(store.getId(), k -> new ArrayList<>()).add(stock);
        }

        // Prepare final result
        List<StockDistributorResult> result = new ArrayList<>();

        for (Map.Entry<String, List<StockEntity>> entry : storeStockMap.entrySet()) {
            StoreEntity store = storeRepository.findById(entry.getKey()).orElse(null);
            if (store == null) continue;

            // Store DTO
            StoreDistributor storeDto = new StoreDistributor();
            storeDto.setStoreId(store.getId());
            storeDto.setStoreName(store.getName());
            storeDto.setLocation(store.getLocation());
            storeDto.setType(store.getType());
            storeDto.setStoreBusinessType(store.getStoreBusinessType());
            storeDto.setOwnerContact(store.getOwnerContact());
            storeDto.setOwnerEmail(store.getOwnerEmail());
            storeDto.setUserId(store.getUserId());
            storeDto.setUserIdStoreId(store.getUserIdStoreId());

            // Create a list of StockDistributor objects for the current store
            List<StockDistributor> stockListForStore = new ArrayList<>();
            for (StockEntity stock : entry.getValue()) {
                // Stock DTO
                StockDistributor stockDto = new StockDistributor();
                stockDto.setMedicineName(stock.getItemName());
                stockDto.setMfName(stock.getMfName());
                stockDto.setMrp(stock.getMrpPack());
                stockDto.setBatch(stock.getBatch());
                stockDto.setExpiryDate(stock.getExpiryDate());
                stockDto.setUserIdStoreIdItemCode(stock.getUserIdStoreIdItemCode());

                Optional<ItemCodeMaster> itemCodeOpt = itemCodeMasterRepository.findById(stock.getUserIdStoreIdItemCode());
                itemCodeOpt.ifPresent(item ->{ stockDto.setGst(item.getGst());
                stockDto.setItemCategory(item.getItemCategory());
                stockDto.setItemSubCategory(item.getItemSubCategory());
                });
                
                
                Optional<ItemOfferEntity> offerOpt = itemOfferRepository.findById(stock.getUserIdStoreIdItemCode());
                if (offerOpt.isPresent()) {
                    ItemOfferEntity offer = offerOpt.get();
                    stockDto.setDiscount(offer.getDiscount());
                    stockDto.setOfferQty(offer.getOfferQty());
                    stockDto.setBatchNumber(offer.getBatchNumber());
                    stockDto.setMinOrderQty(offer.getMinOrderQty());
                }

                stockListForStore.add(stockDto);
            }

            // Add the StockDistributorResult with store and its stock list
            result.add(new StockDistributorResult(storeDto, stockListForStore));
        }

        return result;
    }
//    public List<StockAndDiscountResult> getMedicineDetails(String medicineName, String location,String manufacturer) {
//        // Fetch stock details by medicineName
//        List<StockEntity> stockList = stockRepository.findByItemName(medicineName);
//        
//        if (stockList.isEmpty()) {
//            throw new RuntimeException("Medicine not found");
//        }
//
//        return stockList.stream()
//            .filter(stock -> {
//                StoreEntity store = storeRepository.findById(stock.getStoreId()).orElse(null);
//                
//                boolean locationMatch = store != null && store.getLocation().equalsIgnoreCase(location);
//                boolean businessTypeMatch = store != null && "RT".equals(store.getStoreBusinessType());
//
//                // ✅ Filter by manufacturer only if provided
//                boolean manufacturerMatch = true;
//                if (manufacturer != null && !manufacturer.trim().isEmpty()) {
//                    manufacturerMatch = stock.getManufacturer() != null &&
//                            stock.getManufacturer().equalsIgnoreCase(manufacturer);
//                }
//
//                return locationMatch && businessTypeMatch && manufacturerMatch;
////                return store != null && store.getLocation().equalsIgnoreCase(location);
//            })
//            .map(stock -> {
//                // Fetch store details
//                StoreEntity store = storeRepository.findById(stock.getStoreId())
//                        .orElseThrow(() -> new RuntimeException("Store not found"));
//                
//                if (!"RT".equals(store.getStoreBusinessType())) {
//                    return null; // Exclude if not "RT"
//                }
//
//
//                // Fetch discount (handling null itemCode safely)
//                Optional<ItemDiscountCurrent> discountEntityOptional = Optional.empty();
//                if (stock.getItemCode() != null && !stock.getItemCode().isEmpty()) {
//                    discountEntityOptional = itemDiscountCurrentRepository.findByItemCode(stock.getItemCode());
//                }
//
//                int discount = discountEntityOptional.map(ItemDiscountCurrent::getDiscount).orElse(0);
//
//                // Prepare response object
//                return new StockAndDiscountResult(
//                        store.getName(),
//                        store.getLocation(),
//                        stock.getItemName(),
//                        stock.getMrpPack(),
//                        stock.getBatch(),
//                        stock.getExpiryDate(),
//                        store.getOwnerContact(),
//                        store.getOwnerEmail(),
//                        discount,
//                        stock.getManufacturer()
//                );
//            }).collect(Collectors.toList());
//    }

//    public List<StockAndDiscountEnResult> getMedicineDetailsEn(String medicineName, String location,String mfName,String userId) {
//        // Fetch stock details by medicineName
//    	List<StockEntity> stockList = stockRepository.findByItemName(medicineName);
//
//    	if (medicineName != null && !medicineName.trim().isEmpty()) {
//    	    stockList = stockRepository.findByItemName(medicineName);
//    	} else {
//    	    stockList = stockRepository.findAll(); // or some other fallback logic
//    	}
//
//    	if (stockList.isEmpty()) {
//    	    throw new RuntimeException("Medicine name not found");
//    	}
//
//    	if (mfName != null && !mfName.trim().isEmpty()) {
//    	    stockList = stockList.stream()
//    	        .filter(stock -> stock.getMfName() != null &&
//    	                         stock.getMfName().equalsIgnoreCase(mfName))
//    	        .collect(Collectors.toList());
//    	}
//
//    	if (stockList.isEmpty()) {
//    	    throw new RuntimeException("No stock found for provided mfName");
//    	}
//
//
//        return stockList.stream()
//            .map(stock -> {
//                // Fetch store details
//                StoreEntity store = storeRepository.findById(stock.getStoreId()).orElse(null);
//
//                // Validate store existence and location
//                if (store == null || !store.getLocation().equalsIgnoreCase(location)) {
//                    return null; // Exclude if location does not match
//                }
//
//                // Hardcoded storeCategory as "Primary Care"
//                if (!"Pharmacy".equalsIgnoreCase(store.getType())) {
//                    return null; // Exclude if storeCategory is not "Primary Care"
//                }
//
//                // Ensure storeBusinessType is NOT "RT"
//                if ("RT".equalsIgnoreCase(store.getStoreBusinessType())) {
//                    return null; // Exclude if storeBusinessType is "RT"
//                }
//
//                if (userId != null && !userId.trim().isEmpty() &&
//                        !userId.equalsIgnoreCase(store.getUserId())) {
//                        return null;
//                    }
//
//                // Fetch discount (handling null itemCode safely)
//                Optional<ItemDiscountCurrent> discountEntityOptional = Optional.empty();
//                if (stock.getItemCode() != null && !stock.getItemCode().isEmpty()) {
//                    discountEntityOptional = itemDiscountCurrentRepository.findByItemCode(stock.getItemCode());
//                }
//
//                int discount = discountEntityOptional.map(ItemDiscountCurrent::getDiscount).orElse(0);
//
//                // Prepare response object
//                return new StockAndDiscountEnResult(
//                        store.getName(),
//                        store.getLocation(),
//                        stock.getItemName(),
//                        stock.getMfName(),
//                        stock.getMrpPack(),
//                        stock.getBatch(),
//                        stock.getExpiryDate(),
//                        store.getOwnerContact(),
//                        store.getOwnerEmail(),
//                        store.getType(),
//                        store.getStoreBusinessType(),
//                        store.getUserId(),
//                        store.getId(),
//                        store.getUserIdStoreId(),
//                        discount
//                );
//            })
//            .filter(Objects::nonNull) // Remove null values (filtered out)
//            .collect(Collectors.toList());
//    }  
    
//    public List<StockDistributorResult> getMedicineDetailsEn(String medicineName, String location, String mfName, String userId) {
////        List<StockEntity> stockList = stockRepository.findByItemName(medicineName);
//
//        List<StockEntity> stockList;
//        if(medicineName!=null && ! medicineName.trim().isEmpty()) {
//        	stockList = stockRepository.findByItemName(medicineName);
//        } else {
//        	stockList = stockRepository.findAll();
//        }
//        // Map to hold unique stores with their list of stock entries
//        Map<String, List<StockEntity>> storeStockMap = new LinkedHashMap<>();
//
//        for (StockEntity stock : stockList) {
//            StoreEntity store = storeRepository.findById(stock.getStoreId()).orElse(null);
//            if (store == null) continue;
//
//            // Apply all filters
//            if (!store.getLocation().equalsIgnoreCase(location)) continue;
//            if (!"Pharmacy".equalsIgnoreCase(store.getType())) continue;
//            if ("RT".equalsIgnoreCase(store.getStoreBusinessType())) continue;
//            if (userId != null && !userId.trim().isEmpty() && !userId.equalsIgnoreCase(store.getUserId())) continue;
//            if (mfName != null && !mfName.trim().isEmpty() && !mfName.equalsIgnoreCase(stock.getMfName())) continue;
//
//            // Group by storeId
//            storeStockMap.computeIfAbsent(store.getId(), k -> new ArrayList<>()).add(stock);
//        }
//
//        // Prepare final result
//        List<StockDistributorResult> result = new ArrayList<>();
//
//        for (Map.Entry<String, List<StockEntity>> entry : storeStockMap.entrySet()) {
//            StoreEntity store = storeRepository.findById(entry.getKey()).orElse(null);
//            if (store == null) continue;
//
//            // Store DTO
//            StoreDistributor storeDto = new StoreDistributor();
//            storeDto.setStoreId(store.getId());
//            storeDto.setStoreName(store.getName());
//            storeDto.setLocation(store.getLocation());
//            storeDto.setType(store.getType());
//            storeDto.setStoreBusinessType(store.getStoreBusinessType());
//            storeDto.setOwnerContact(store.getOwnerContact());
//            storeDto.setOwnerEmail(store.getOwnerEmail());
//            storeDto.setUserId(store.getUserId());
//            storeDto.setUserIdStoreId(store.getUserIdStoreId());
//
//            // Create a list of StockDistributor objects for the current store
//            List<StockDistributor> stockListForStore = new ArrayList<>();
//            for (StockEntity stock : entry.getValue()) {
//                // Stock DTO
//                StockDistributor stockDto = new StockDistributor();
//                stockDto.setMedicineName(stock.getItemName());
//                stockDto.setMfName(stock.getMfName());
//                stockDto.setMrp(stock.getMrpPack());
//                stockDto.setBatch(stock.getBatch());
//                stockDto.setExpiryDate(stock.getExpiryDate());
//                stockDto.setUserIdStoreIdItemCode(stock.getUserIdStoreIdItemCode());
//                
//                Optional<ItemOfferEntity> offerOpt = itemOfferRepository.findByUserIdStoreIdItemCode(stock.getUserIdStoreIdItemCode());
//                if (offerOpt.isPresent()) {
//                    ItemOfferEntity offer = offerOpt.get();
//                    stockDto.setDiscount(offer.getDiscount());
//                    stockDto.setOfferQty(offer.getOfferQty());
//                    stockDto.setBatchNumber(offer.getBatchNumber());
//                    stockDto.setMinOrderQty(offer.getMinOrderQty());
//                }
//                
//                stockListForStore.add(stockDto);
//            }
//
//            // Add the StockDistributorResult with store and its stock list
//            result.add(new StockDistributorResult(storeDto, stockListForStore));
//        }
//
//        return result;
//    }
    
    public Page<StockDistributorResult> getMedicineDetailsEn(
            String medicineName,
            String location,
            String mfName,
            String userId,
            String storeBusinessType,
            int page,
            int size) {

        List<StockEntity> stockList = (medicineName != null && !medicineName.trim().isEmpty())
                ? stockRepository.findByItemName(medicineName)
                : stockRepository.findAll();

        Map<String, List<StockEntity>> storeStockMap = new LinkedHashMap<>();

        for (StockEntity stock : stockList) {
            StoreEntity store = storeRepository.findById(stock.getStoreId()).orElse(null);
            if (store == null) continue;

            if (!store.getLocation().equalsIgnoreCase(location)) continue;
            if (!"PH".equalsIgnoreCase(store.getType())) continue;

            if (storeBusinessType != null && !storeBusinessType.trim().isEmpty()
                    && !storeBusinessType.equalsIgnoreCase(store.getStoreBusinessType())) {
                continue;
            }

            if (userId != null && !userId.trim().isEmpty() && !userId.equalsIgnoreCase(store.getUserId())) continue;
            if (mfName != null && !mfName.trim().isEmpty() && !mfName.equalsIgnoreCase(stock.getMfName())) continue;

            storeStockMap.computeIfAbsent(store.getId(), k -> new ArrayList<>()).add(stock);
        }

        // Convert to result list
        List<StockDistributorResult> fullResult = new ArrayList<>();

        for (Map.Entry<String, List<StockEntity>> entry : storeStockMap.entrySet()) {
            StoreEntity store = storeRepository.findById(entry.getKey()).orElse(null);
            if (store == null) continue;

            StoreDistributor storeDto = new StoreDistributor();
            storeDto.setStoreId(store.getId());
            storeDto.setStoreName(store.getName());
            storeDto.setLocation(store.getLocation());
            storeDto.setType(store.getType());
            storeDto.setStoreBusinessType(store.getStoreBusinessType());
            storeDto.setOwnerContact(store.getOwnerContact());
            storeDto.setOwnerEmail(store.getOwnerEmail());
            storeDto.setUserId(store.getUserId());
            storeDto.setUserIdStoreId(store.getUserIdStoreId());

            List<StockDistributor> stockListForStore = new ArrayList<>();
            for (StockEntity stock : entry.getValue()) {
                StockDistributor stockDto = new StockDistributor();
                stockDto.setMedicineName(stock.getItemName());
                stockDto.setMfName(stock.getMfName());
                stockDto.setMrp(stock.getMrpPack());
                stockDto.setBatch(stock.getBatch());
                stockDto.setExpiryDate(stock.getExpiryDate());
                stockDto.setUserIdStoreIdItemCode(stock.getUserIdStoreIdItemCode());

                itemOfferRepository.findByUserIdStoreIdItemCode(stock.getUserIdStoreIdItemCode())
                        .ifPresent(offer -> {
                            stockDto.setDiscount(offer.getDiscount());
                            stockDto.setOfferQty(offer.getOfferQty());
                            stockDto.setBatchNumber(offer.getBatchNumber());
                            stockDto.setMinOrderQty(offer.getMinOrderQty());
                        });

                stockListForStore.add(stockDto);
            }

            fullResult.add(new StockDistributorResult(storeDto, stockListForStore));
        }

        // Paginate manually
        int start = page * size;
        int end = Math.min(start + size, fullResult.size());
        List<StockDistributorResult> paginatedList = (start > fullResult.size()) ? Collections.emptyList() : fullResult.subList(start, end);

        return new PageImpl<>(paginatedList, PageRequest.of(page, size), fullResult.size());
    }

    
    public List<Map<String, String>> getUserIdAndStoreIdByName(String name) {
        // Query all stores matching the name
        List<StoreEntity> stores = storeRepository.findAllByName(name);

        // If no stores are found, throw an exception or handle as needed
        if (stores.isEmpty()) {
            throw new RuntimeException("No stores found with name: " + name);
        }

        // Map to hold the results
        List<Map<String, String>> results = new ArrayList<>();

        // Iterate through the stores and add userId and storeId
        for (StoreEntity store : stores) {
            Map<String, String> map = new HashMap<>();
            map.put("storeName", store.getName());
            map.put("userId", store.getUserId());
            map.put("storeId", store.getId());
            map.put("userIdStoreId", store.getUserIdStoreId());
            results.add(map);
        }

        return results;
    }
}

