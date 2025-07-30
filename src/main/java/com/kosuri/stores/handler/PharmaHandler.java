package com.kosuri.stores.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.kosuri.stores.dao.PharmacistEntity;
import com.kosuri.stores.dao.PharmacistRepository;
import com.kosuri.stores.model.request.PharmasistRequest;
import com.kosuri.stores.model.response.SearchPhramacistResponse;
import com.kosuri.stores.model.search.PharmasistSearchResult;
import com.kosuri.stores.s3.config.AmazonS3Service;

@Service
public class PharmaHandler {

	@Autowired
	private PharmacistRepository pharmacistRepository;

	@Autowired
	private RepositoryHandler repositoryHandler;

	@Autowired
	private AmazonS3Service amazonService;

	private static String folderName = "pharmacist/";

	public boolean addPharmacist(PharmasistRequest request) throws Exception {
		if (!repositoryHandler.validatePharmacist(request)) {
			return false;
		}

		PharmacistEntity entity = getEntityFromPharmaRequest(request, false);
		boolean isPharmacistAdded;
		try {
			isPharmacistAdded = repositoryHandler.addPharmacist(entity);
		} catch (DataIntegrityViolationException e) {
			throw new Exception(e.getCause().getCause().getMessage());
		}
		return isPharmacistAdded;
	}

	public boolean updatePharmacist(PharmasistRequest request) throws Exception {
		Optional<PharmacistEntity> pharmacistEntityOptional = pharmacistRepository
				.findByPharmacistEmailAddress(request.getPharmaUserEmail());
		if (pharmacistEntityOptional.isPresent()) {
			PharmacistEntity entity = updateEntityFromPharmaRequest(request, pharmacistEntityOptional.get());
			repositoryHandler.updatePharmacist(entity);
		}
		return true;
	}

	public Map<String, Object> searchPharmacistById(String pharmacistId) throws Exception {

		PharmacistEntity pharmacistEntityList = pharmacistRepository.findById(pharmacistId).orElse(null);
		return Map.of("pharmasistSearchResults", pharmacistEntityList);
	}

	public Map<String, Object> searchPharmacist(String mobileNumber, String emailAddress, String availableLocation)
			throws Exception {
		List<PharmacistEntity> pharmacistEntityList = repositoryHandler.findPharmacist(mobileNumber, emailAddress,
				availableLocation);
		SearchPhramacistResponse searchResponse = setSearchResponse(pharmacistEntityList);
		return Map.of("data", searchResponse);
	}

	private SearchPhramacistResponse setSearchResponse(List<PharmacistEntity> pharmacistEntityList) {
		SearchPhramacistResponse searchResponse = new SearchPhramacistResponse();

		List<PharmasistSearchResult> pharmacistList = new ArrayList<>();
		PharmasistSearchResult pharmacistSearchResult = new PharmasistSearchResult();
		for (PharmacistEntity pharmacistEntity : pharmacistEntityList) {
			pharmacistSearchResult.setName(pharmacistEntity.getPharmacistName());
			pharmacistSearchResult.setPharmaUserEmail(pharmacistEntity.getPharmacistEmailAddress());
			pharmacistSearchResult.setPharmaUserContact(pharmacistEntity.getPharmacistContact());
			pharmacistSearchResult.setExperience(pharmacistEntity.getPharmacistExperience());
			pharmacistSearchResult.setAvailableLocation(pharmacistEntity.getPharmacistAvailableLocation());
			pharmacistSearchResult.setEducation(pharmacistEntity.getPharmacistEducation());
			pharmacistSearchResult.setPciCertified(pharmacistEntity.getPharmacistPciCertified());
			pharmacistSearchResult.setPciExpiryDate(pharmacistEntity.getPharmacistPciExpiryDate());
		}
		pharmacistList.add(pharmacistSearchResult);
		searchResponse.setPharmasistSearchResults(pharmacistList);
		return searchResponse;
	}

	private PharmacistEntity updateEntityFromPharmaRequest(PharmasistRequest request,
			PharmacistEntity pharmacistEntity) {

		pharmacistEntity.setPharmacistName(request.getName());
		pharmacistEntity.setPharmacistContact(request.getPharmaUserContact());
		//pharmacistEntity.setPharmacistEmailAddress(request.getPharmaUserEmail());
		if (request.getPersonalPhoto() != null) {
			String existingUrl = pharmacistEntity.getPersonalPhoto();
			String imageName = existingUrl.substring(existingUrl.lastIndexOf("/") + 1);
			amazonService.deleteImages(folderName, imageName);
			String url = amazonService.uploadFile(folderName, request.getPersonalPhoto());
			pharmacistEntity.setPersonalPhoto(url);
		}

		if (request.getExperienceDoc() != null) {
			String existingUrl = pharmacistEntity.getExperienceDoc();
			String imageName = existingUrl.substring(existingUrl.lastIndexOf("/") + 1);
			amazonService.deleteImages(folderName, imageName);
			String url = amazonService.uploadFile(folderName, request.getExperienceDoc());
			pharmacistEntity.setExperienceDoc(url);
		}

		if (request.getPciCertifiedDoc() != null) {
			String existingUrl = pharmacistEntity.getPciDoc();
			String imageName = existingUrl.substring(existingUrl.lastIndexOf("/") + 1);
			amazonService.deleteImages(folderName, imageName);
			String url = amazonService.uploadFile(folderName, request.getPciCertifiedDoc());
			pharmacistEntity.setPciDoc(url);
		}

		if (request.getPharmacyDoc() != null) {
			String existingUrl = pharmacistEntity.getPharmacyDoc();
			String imageName = existingUrl.substring(existingUrl.lastIndexOf("/") + 1);
			amazonService.deleteImages(folderName, imageName);
			String url = amazonService.uploadFile(folderName, request.getPharmacyDoc());
			pharmacistEntity.setPharmacyDoc(url);
		}
		pharmacistEntity.setPharmacistEducation(request.getEducation());
		pharmacistEntity.setPharmacistPciExpiryDate(request.getPciExpiryDate());
		pharmacistEntity.setPharmacistAvailableLocation(request.getAvailableLocation());

		return pharmacistEntity;
	}

	private PharmacistEntity getEntityFromPharmaRequest(PharmasistRequest request, boolean isUpdate) {
		PharmacistEntity pharmacistEntity = new PharmacistEntity();

		if (!isUpdate) {
			pharmacistEntity.setPharmacistId(request.getName().replace(" ", "") + "_" + OtpHandler.generateOTP(false));

		}
		pharmacistEntity.setPharmacistName(request.getName());
		pharmacistEntity.setPharmacistContact(request.getPharmaUserContact());
		pharmacistEntity.setPharmacistEmailAddress(request.getPharmaUserEmail());
		if (request.getPersonalPhoto() != null) {
			String url = amazonService.uploadFile(folderName, request.getPersonalPhoto());
			pharmacistEntity.setPersonalPhoto(url);
		}

		if (request.getExperienceDoc() != null) {
			String url = amazonService.uploadFile(folderName, request.getExperienceDoc());
			pharmacistEntity.setExperienceDoc(url);
		}

		if (request.getPciCertifiedDoc() != null) {
			String url = amazonService.uploadFile(folderName, request.getPciCertifiedDoc());
			pharmacistEntity.setPciDoc(url);
		}

		if (request.getPharmacyDoc() != null) {
			String url = amazonService.uploadFile(folderName, request.getPharmacyDoc());
			pharmacistEntity.setPharmacyDoc(url);
		}
		pharmacistEntity.setPharmacistEducation(request.getEducation());
		pharmacistEntity.setPharmacistPciExpiryDate(request.getPciExpiryDate());
		pharmacistEntity.setPharmacistAvailableLocation(request.getAvailableLocation());

		return pharmacistEntity;
	}
}
