package com.kosuri.stores.handler;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import com.kosuri.stores.dao.CustomerRegisterEntity;
import com.kosuri.stores.dao.CustomerRegisterRepository;
import com.kosuri.stores.dao.DCBookingRequestDetails;
import com.kosuri.stores.dao.DCBookingRequestDetailsHistory;
import com.kosuri.stores.dao.DCBookingRequestDetailsHistoryRepository;
import com.kosuri.stores.dao.DCBookingRequestDetailsRepository;
import com.kosuri.stores.dao.DCBookingRequestHeader;
import com.kosuri.stores.dao.DCBookingRequestHeaderHistory;
import com.kosuri.stores.dao.DCBookingRequestHeaderHistoryRepository;
import com.kosuri.stores.dao.DCBookingRequestHeaderRepository;
import com.kosuri.stores.dao.DcBookingDetails;
import com.kosuri.stores.dao.DcBookingHeader;
import com.kosuri.stores.dao.RequestMethod;
import com.kosuri.stores.dao.RequestMethodRepository;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreRepository;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.AppintmentBookingPCUpdateResponse;
import com.kosuri.stores.model.dto.AppintmentBookingRequest;
import com.kosuri.stores.model.dto.CancelLineStatusRequest;
import com.kosuri.stores.model.dto.CancelLineStatusResponse;
import com.kosuri.stores.model.dto.CustomerDetailsDto;
import com.kosuri.stores.model.dto.DCBookingGetDto;
import com.kosuri.stores.model.dto.DCBookingReponseDto;
import com.kosuri.stores.model.dto.DCBookingRequestDto;
import com.kosuri.stores.model.dto.DCBookingStoreGetDto;
import com.kosuri.stores.model.dto.DcBookingRequestServiceDto;
import com.kosuri.stores.model.dto.DcBookingRequestServiceStoreDto;
import com.kosuri.stores.model.dto.StoreDetailsDto;
import com.kosuri.stores.model.enums.Status;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;

@Service
public class DCBookingRequestService {

	@Autowired
	private DCBookingRequestHeaderRepository dcBookingRequestHeaderRepository;

	@Autowired
	private DCBookingRequestDetailsRepository dcBookingRequestDetailsRepository;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private DCBookingRequestHeaderHistoryRepository dcBookingRequestHeaderHistoryRepository;

	@Autowired
	private DCBookingRequestDetailsHistoryRepository dcBookingRequestDetailsHistoryRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private CustomerRegisterRepository customerRegisterRepository;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private EmailService emailService;

	@Autowired
	private TabStoreRepository tabStoreRepository;
	
	@Autowired
	private RequestMethodRepository requestMethodRepository;
	
	@Transactional
	public DCBookingReponseDto createBookingRequestDetails(DCBookingRequestDto bookingRequest) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<CustomerRegisterEntity> login = customerRegisterRepository.findByEmail(loggedInUserEmail);

		Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		if (login.isEmpty() && loginStore.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer/store users only.");
		}
//		double totalAmount = 0;
		int lineNumber = 1;
		String serviceRequestId = generateServiceRequestId();
		bookingRequest.setServiceRequestId(serviceRequestId);

		for (DcBookingRequestServiceDto service : bookingRequest.getSelectedServices()) {
//			double serviceTotal = calculateTotalAmount(service);
//			totalAmount += serviceTotal;

			String serviceRequestLineId = serviceRequestId + "_" + String.format("%04d", lineNumber++);

			DCBookingRequestDetails bookingDetails = new DCBookingRequestDetails();
			bookingDetails.setServiceRequestLineId(serviceRequestLineId);
			bookingDetails.setServiceRequestId(serviceRequestId);
			bookingDetails.setServiceId(service.getServiceId());
			bookingDetails.setAmount(service.getPrice());
//			bookingDetails.setDiscount(service.getDiscount());
			bookingDetails.setServiceName(service.getServiceName());
			service.setServiceRequestLineId(serviceRequestLineId);

			dcBookingRequestDetailsRepository.save(bookingDetails);
		}

		DCBookingRequestHeader bookingRequestHeader = new DCBookingRequestHeader();
		bookingRequestHeader.setServiceRequestId(serviceRequestId);
		String updatedBy;
		String requestedMethod;
		if (login.isPresent()) {
			updatedBy = login.get().getCId(); // Customer ID
			requestedMethod = "online";
		} else {
			updatedBy = loginStore.get().getUserId(); // Store User ID
			requestedMethod = "walkin";
		}
		bookingRequestHeader.setUpdatedBy(updatedBy);
//		bookingRequestHeader.setUpdatedBy(login.get().getCId());
		bookingRequestHeader.setCustomerId(bookingRequest.getCustomerId());
		bookingRequestHeader.setUserIdStoreId(bookingRequest.getUserIdStoreId());
//		bookingRequestHeader.setTotalAmount(totalAmount);
		bookingRequestHeader.setTotalAmount(bookingRequest.getTotalAmount());
		bookingRequestHeader.setHomeService(bookingRequest.getHomeService());
		bookingRequestHeader.setWalkinService(bookingRequest.getWalkinService());
		bookingRequestHeader.setAppointmentDate(bookingRequest.getAppointmentDate());
		bookingRequestHeader.setAppointmentTime(bookingRequest.getAppointmentTime());
		bookingRequestHeader.setUpdatedDate(LocalDate.now());
		bookingRequestHeader.setBookingDate(LocalDate.now());
//		bookingRequestHeader.setStatus("New");
		bookingRequestHeader.setStatus(Status.NEW);
		DCBookingRequestHeader savedBookingRequestHeader=dcBookingRequestHeaderRepository.save(bookingRequestHeader);

		// Save the request method in the RequestMethod entity
	    RequestMethod requestMethodEntity = new RequestMethod();
	    requestMethodEntity.setServiceRequestId(serviceRequestId);
	    requestMethodEntity.setRequestedMethod(requestedMethod);
	    RequestMethod saved =requestMethodRepository.save(requestMethodEntity);
	    
		generateBookingHtml(bookingRequest, serviceRequestId);
		generateBookingPdf(bookingRequest);
//		sendEmailWithPdfAndHtml(login.get().getEmail(), bookingRequest, serviceRequestId);
		String recipientEmail = login.isPresent() ? login.get().getEmail() : loginStore.get().getStoreUserEmail();
		sendEmailWithPdfAndHtml(recipientEmail, bookingRequest, serviceRequestId);
		
		return new DCBookingReponseDto(serviceRequestId, bookingRequest.getTotalAmount(),
//				totalAmount, 
//				login.get().getCId(),
				updatedBy,
				bookingRequest.getUserIdStoreId(), bookingRequest.getCustomerId(),
				bookingRequest.getHomeService(), bookingRequest.getWalkinService(), bookingRequest.getAppointmentDate(),
				bookingRequest.getAppointmentTime(),savedBookingRequestHeader.getUpdatedDate(),savedBookingRequestHeader.getBookingDate(),
				savedBookingRequestHeader.getStatus(), bookingRequest.getSelectedServices());
	}

	private String generateBookingHtml(DCBookingRequestDto bookingRequest, String serviceRequestId) {
		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("<html><body>");
		htmlBuilder.append("<h2>Booking Confirmation</h2>");

		// Start Table with Column Headers
		htmlBuilder.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
		htmlBuilder.append("<tr>"); // Column Headers
		htmlBuilder.append("<th style='text-align: left; padding: 8px;'>Service Request ID</th>");
		htmlBuilder.append("<th style='text-align: left; padding: 8px;'>Total Amount</th>");
		htmlBuilder.append("<th style='text-align: left; padding: 8px;'>Customer ID</th>");
		htmlBuilder.append("<th style='text-align: left; padding: 8px;'>Appointment Date</th>");
		htmlBuilder.append("<th style='text-align: left; padding: 8px;'>Appointment Time</th>");
		htmlBuilder.append("</tr>");

		// Row Values
		htmlBuilder.append("<tr>");
		htmlBuilder.append("<td style='padding: 8px;'>").append(serviceRequestId).append("</td>");
		htmlBuilder.append("<td style='padding: 8px;'>").append(bookingRequest.getTotalAmount()).append("</td>");
		htmlBuilder.append("<td style='padding: 8px;'>").append(bookingRequest.getCustomerId()).append("</td>");
		htmlBuilder.append("<td style='padding: 8px;'>").append(bookingRequest.getAppointmentDate()).append("</td>");
		htmlBuilder.append("<td style='padding: 8px;'>").append(bookingRequest.getAppointmentTime()).append("</td>");
		htmlBuilder.append("</tr>");

		htmlBuilder.append("</table>"); // End Table
		htmlBuilder.append("</body></html>");

		return htmlBuilder.toString();
	}

	private byte[] generateBookingPdf(DCBookingRequestDto bookingRequest) {
		StringBuilder pdfHtml = new StringBuilder();
		pdfHtml.append("<html><body>");
		pdfHtml.append("<h3>Selected Services:</h3>");

		// Start Table
		pdfHtml.append("<table border='1' style='border-collapse: collapse; width: 100%; text-align: left;'>");
		pdfHtml.append("<tr>").append("<th style='padding: 8px; border: 1px solid black;'>ServiceRequestLineId</th>")
				.append("<th style='padding: 8px; border: 1px solid black;'>Service Id</th>")
				.append("<th style='padding: 8px; border: 1px solid black;'>Service Name</th>")
				.append("<th style='padding: 8px; border: 1px solid black;'>Price</th>")

				.append("</tr>");

		for (DcBookingRequestServiceDto service : bookingRequest.getSelectedServices()) {
			pdfHtml.append("<tr>").append("<td style='padding: 8px; border: 1px solid black;'>")
					.append(service.getServiceRequestLineId()).append("</td>")
					.append("<td style='padding: 8px; border: 1px solid black;'>").append(service.getServiceId())
					.append("</td>").append("<td style='padding: 8px; border: 1px solid black;'>")
					.append(service.getServiceName()).append("</td>")
					.append("<td style='padding: 8px; border: 1px solid black;'>").append(service.getPrice())
					.append("</td>")

					.append("</tr>");
		}

		pdfHtml.append("</table>"); // End Table
		pdfHtml.append("</body></html>");

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document, outputStream);
			document.open();
			XMLWorkerHelper.getInstance().parseXHtml(writer, document, new StringReader(pdfHtml.toString()));
			document.close();
			return outputStream.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException("Error generating PDF", e);
		}
	}

	private void sendEmailWithPdfAndHtml(String emailId, DCBookingRequestDto bookingRequest, String serviceRequestId) {
		Optional<CustomerRegisterEntity> login = customerRegisterRepository.findByEmail(emailId);

		Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(emailId);
		
		if (login.isEmpty() && loginStore.isEmpty()) {
			throw new ResourceNotFoundException("Customer/store not found with ID: " + emailId);
		}

//		String recipientEmail = customer.get().getEmail();
		String recipientEmail = login.isPresent() ? login.get().getEmail() : loginStore.get().getStoreUserEmail();
		String emailHtml = generateBookingHtml(bookingRequest, serviceRequestId);
		byte[] pdfBytes = generateBookingPdf(bookingRequest);

		MimeMessage message = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(recipientEmail);
			helper.setSubject("Booking Confirmation - " + serviceRequestId);
			helper.setText(emailHtml, true);
			helper.setFrom("no_reply@kosuriers.com");

			// Attach PDF with services only
			helper.addAttachment("Booking_Confirmation.pdf", new ByteArrayResource(pdfBytes));

			javaMailSender.send(message);
			System.out.println("Email sent successfully to: " + recipientEmail);
		} catch (MessagingException e) {
			throw new RuntimeException("Failed to send email", e);
		}
	}
	private String generateServiceRequestId() {
		String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		int randomNum = new Random().nextInt(9000) + 1000; // Generates a 4-digit random number (1000 - 9999)
		return "PCSR" + timestamp + randomNum;
	}

//	private double calculateTotalAmount(DcBookingRequestServiceDto service) {
//		return service.getAmount() - (service.getAmount() * service.getDiscount() / 100);
//	}

	@Transactional
	public DCBookingRequestDto updateBookingRequestDetails(DCBookingRequestDto dcBookingRequestDto) {
		
		 String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		    Optional<CustomerRegisterEntity> login = customerRegisterRepository.findByEmail(loggedInUserEmail);

		    if (login.isEmpty()) {
		        throw new ResourceNotFoundException("Access denied. This API is restricted to customer users only.");
		    }
		    String serviceRequestId = dcBookingRequestDto.getServiceRequestId();
		    Optional<DCBookingRequestHeader> existingHeaderOpt  = dcBookingRequestHeaderRepository.findById(serviceRequestId);

		    if (existingHeaderOpt .isEmpty()) {
		        throw new ResourceNotFoundException("Appointment not found with serviceRequestId: " + serviceRequestId);
		    }

		    DCBookingRequestHeader existingHeader  = existingHeaderOpt.get();
		    
		    if (existingHeader .getStatus() == Status.COMPLETED) {
		        throw new IllegalStateException("Cannot update an appointment that is already COMPLETED.");
		    }

		    // Allow only NEW and PENDING status updates
		    if (dcBookingRequestDto.getStatus() == Status.NEW || dcBookingRequestDto.getStatus() == Status.PENDING ||dcBookingRequestDto.getStatus() == Status.CANCELED ||  
		    		dcBookingRequestDto.getStatus() == Status.COMPLETED) {
		    	
		    	existingHeader.setStatus(dcBookingRequestDto.getStatus());
		    } else {
		        throw new IllegalArgumentException("Invalid status update. Only NEW,IN_PROGRESS and PENDING statuses are allowed.");
		    }
//		DCBookingRequestHeader existingHeader = dcBookingRequestHeaderRepository.findById(serviceRequestId)
//				.orElseThrow(() -> new ResourceNotFoundException("ServiceRequest ID not found: " + serviceRequestId));
		DCBookingRequestHeaderHistory headerHistory = new DCBookingRequestHeaderHistory();
		BeanUtils.copyProperties(existingHeader, headerHistory);
		dcBookingRequestHeaderHistoryRepository.save(headerHistory);
		List<DCBookingRequestDetails> existingDetails = dcBookingRequestDetailsRepository
				.findByServiceRequestId(serviceRequestId);
		for (DCBookingRequestDetails existingDetail : existingDetails) {
			DCBookingRequestDetailsHistory detailsHistory = new DCBookingRequestDetailsHistory();
			BeanUtils.copyProperties(existingDetail, detailsHistory);
			dcBookingRequestDetailsHistoryRepository.save(detailsHistory);
		}
		
		StringBuilder messageBuilder = new StringBuilder();
	    messageBuilder.append("Service request updates: ");

//		double totalAmount = 0.0;
		for (DcBookingRequestServiceDto service : dcBookingRequestDto.getSelectedServices()) {
			String serviceRequestLineId = service.getServiceRequestLineId(); // Get packageIdLineId from DTO

			String extractedServiceRequestId = serviceRequestLineId.split("_")[0];
			if (!extractedServiceRequestId.equals(serviceRequestId)) {
				throw new IllegalArgumentException("Mismatch: serviceRequestId " + serviceRequestId
						+ " does not match extracted " + extractedServiceRequestId);
			}

			Optional<DCBookingRequestDetails> optionalDetail = dcBookingRequestDetailsRepository
					.findByServiceRequestIdAndServiceRequestLineId(serviceRequestId, serviceRequestLineId);

			if (optionalDetail.isEmpty()) {
				throw new IllegalArgumentException(
						"ServiceRequestLineId " + serviceRequestLineId + " not found in details.");
			}
			DCBookingRequestDetails packageDetail = optionalDetail.get();
//			double discountedAmount = service.getAmount() - (service.getAmount() * service.getDiscount() / 100);
			packageDetail.setAmount(service.getPrice());
//			packageDetail.setDiscount(service.getDiscount());
			packageDetail.setServiceName(service.getServiceName());
			dcBookingRequestDetailsRepository.save(packageDetail);
//			totalAmount += discountedAmount;
			
			 messageBuilder.append("ServiceRequestLineId Number ").append(serviceRequestLineId)
             .append(" is updated to ").append(dcBookingRequestDto.getStatus()).append(", ");
		}
		existingHeader.setTotalAmount(dcBookingRequestDto.getTotalAmount());
//		existingHeader.setTotalAmount(totalAmount);
		existingHeader.setUpdatedDate(LocalDate.now());
		existingHeader.setStatus(dcBookingRequestDto.getStatus());
		dcBookingRequestHeaderRepository.save(existingHeader);
//		dcBookingRequestDto.setTotalAmount(totalAmount);
//		dcBookingRequestDto.setStatus(saved.getStatus());
		
		String emailMessage = messageBuilder.toString();
	    emailService.sendEmailMessage(loggedInUserEmail,emailMessage, "Booking Request Update");
		return dcBookingRequestDto;
	}

	public List<DCBookingGetDto> getCustomerBookingsWithCustomerDetails(String userIdStoreId, LocalDate bookingDateFrom,
			LocalDate bookingDateTo, LocalDate appointmentDateFrom, LocalDate appointmentDateTo,
			String customerUserContact) {

		Specification<DCBookingRequestHeader> specification = (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (userIdStoreId != null) {
//				predicates.add(criteriaBuilder.equal(root.get("userIdStoreId"), userIdStoreId));
				predicates.add(criteriaBuilder.like(root.get("userIdStoreId"), userIdStoreId + "%"));
			}
			if (bookingDateFrom != null && bookingDateTo != null) {
				predicates.add(criteriaBuilder.between(root.get("bookingDate"), bookingDateFrom, bookingDateTo));
			}
			if (appointmentDateFrom != null && appointmentDateTo != null) {
				predicates.add(
						criteriaBuilder.between(root.get("appointmentDate"), appointmentDateFrom, appointmentDateTo));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

		List<DCBookingRequestHeader> headers = dcBookingRequestHeaderRepository.findAll(specification);
		List<DCBookingGetDto> responseList = new ArrayList<>();

		for (DCBookingRequestHeader header : headers) {
			List<DCBookingRequestDetails> detailsList = dcBookingRequestDetailsRepository
					.findByServiceRequestId(header.getServiceRequestId());

			List<DcBookingRequestServiceStoreDto> selectedServices = detailsList.stream()
					.map(details -> new DcBookingRequestServiceStoreDto(details.getServiceId(), details.getAmount(),
//							details.getDiscount(),
							details.getStatus(),
							details.getServiceName(),details.getServiceRequestLineId()))
					.collect(Collectors.toList());
			Optional<CustomerRegisterEntity> customerUser = customerRegisterRepository
					.findByCid(header.getCustomerId());
			CustomerDetailsDto customerDetailsDto = null;
			if (customerUser.isPresent() && customerUser.get().getCId().equals(header.getCustomerId())) {
				if (customerUserContact != null && !customerUserContact.isEmpty()) {
					if (customerUser.get().getPhoneNumber().equals(customerUserContact)) {
						customerDetailsDto = new CustomerDetailsDto(customerUser.get().getId(),
								customerUser.get().getCId(), customerUser.get().getName(),
								customerUser.get().getEmail(), customerUser.get().getPhoneNumber(),
								customerUser.get().getLocation(), customerUser.get().getRegisteredDate(),
								customerUser.get().getUpdatedDate());
					}
				} else {
					customerDetailsDto = new CustomerDetailsDto(customerUser.get().getId(), customerUser.get().getCId(),
							customerUser.get().getName(), customerUser.get().getEmail(),
							customerUser.get().getPhoneNumber(), customerUser.get().getLocation(),
							customerUser.get().getRegisteredDate(), customerUser.get().getUpdatedDate());
				}
			}
			DCBookingGetDto response = new DCBookingGetDto(header.getServiceRequestId(), header.getTotalAmount(),
					header.getUpdatedBy(), header.getCustomerId(), header.getUserIdStoreId(), header.getHomeService(),
					header.getWalkinService(), header.getAppointmentDate(), header.getAppointmentTime(),header.getBookingDate(),
					header.getStatus(),
					selectedServices, customerDetailsDto // Include store details...
			);
			if (customerDetailsDto != null) {
				responseList.add(response);
			}
		}

		return responseList;
	}

	public List<DCBookingStoreGetDto> getCustomerBookingsWithStore(String customerId, LocalDate bookingDateFrom,
			LocalDate bookingDateTo, LocalDate appointmentDateFrom, LocalDate appointmentDateTo) {

		Specification<DCBookingRequestHeader> specification = (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (customerId != null) {
				predicates.add(criteriaBuilder.equal(root.get("customerId"), customerId));
			}
			if (bookingDateFrom != null && bookingDateTo != null) {
				predicates.add(criteriaBuilder.between(root.get("bookingDate"), bookingDateFrom, bookingDateTo));
			}
			if (appointmentDateFrom != null && appointmentDateTo != null) {
				predicates.add(
						criteriaBuilder.between(root.get("appointmentDate"), appointmentDateFrom, appointmentDateTo));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

		List<DCBookingRequestHeader> headers = dcBookingRequestHeaderRepository.findAll(specification);
		List<DCBookingStoreGetDto> responseList = new ArrayList<>();

		for (DCBookingRequestHeader header : headers) {
			List<DCBookingRequestDetails> detailsList = dcBookingRequestDetailsRepository
					.findByServiceRequestId(header.getServiceRequestId());

			List<DcBookingRequestServiceStoreDto> selectedServices = detailsList.stream()
					.map(details -> new DcBookingRequestServiceStoreDto(details.getServiceId(), details.getAmount(),
//							details.getDiscount(),
							
							details.getStatus(),details.getServiceName(), details.getServiceRequestLineId()))
					.collect(Collectors.toList());
			Optional<StoreEntity> customerUser = storeRepository.findByUserIdStoreId(header.getUserIdStoreId());
			StoreDetailsDto customerDetailsDto = null;
			if (customerUser.isPresent() && customerUser.get().getUserIdStoreId().equals(header.getUserIdStoreId())) {
				if (header.getUserIdStoreId() != null && !header.getUserIdStoreId().isEmpty()) {
					if (customerUser.get().getUserIdStoreId().equals(header.getUserIdStoreId())) {
						customerDetailsDto = new StoreDetailsDto(customerUser.get().getType(),
								customerUser.get().getUserIdStoreId(), customerUser.get().getId(),
								customerUser.get().getName(), customerUser.get().getPincode(),
								customerUser.get().getDistrict(), customerUser.get().getState(),
								customerUser.get().getLocation(), customerUser.get().getOwner(),
								customerUser.get().getOwnerContact(), customerUser.get().getOwnerEmail());
					}
				} else {
					customerDetailsDto = new StoreDetailsDto(customerUser.get().getType(),
							customerUser.get().getUserIdStoreId(), customerUser.get().getId(),
							customerUser.get().getName(), customerUser.get().getPincode(),
							customerUser.get().getDistrict(), customerUser.get().getState(),
							customerUser.get().getLocation(), customerUser.get().getOwner(),
							customerUser.get().getOwnerContact(), customerUser.get().getOwnerEmail());
				}
			}
			DCBookingStoreGetDto response = new DCBookingStoreGetDto(header.getServiceRequestId(),
					header.getTotalAmount(), header.getUpdatedBy(), header.getCustomerId(), header.getUserIdStoreId(),
					header.getHomeService(), header.getWalkinService(), header.getAppointmentDate(),
					header.getAppointmentTime(),header.getBookingDate(),header.getStatus(), selectedServices, customerDetailsDto // Include store details...
			);

			if (customerDetailsDto != null) {
				responseList.add(response);
			}
		}

		return responseList;
	}

//	 @Transactional
//	    public String cancelLineItemStatus(String serviceRequestId, String serviceRequestIdLineId) {
//	        // Fetch the booking header first
//	        Optional<DCBookingRequestHeader> bookingHeaderOpt = dcBookingRequestHeaderRepository.findByServiceRequestId(serviceRequestId);
//
//	        if (bookingHeaderOpt.isPresent()) {
//	        	DCBookingRequestHeader bookingHeader = bookingHeaderOpt.get();
//	            if (bookingHeader.getStatus() == Status.COMPLETED) {
//	                return "Cancellation not allowed. The service request " + serviceRequestId + " is already completed.";
//	            }
//	            Optional<DCBookingRequestDetails> bookingDetailsOpt =
//	            		dcBookingRequestDetailsRepository.findByServiceRequestIdAndServiceRequestLineId(serviceRequestId, serviceRequestIdLineId);
//
//	            if (bookingDetailsOpt.isPresent()) {
//	            	DCBookingRequestDetails bookingDetails = bookingDetailsOpt.get();
//	                if (bookingDetails.getStatus() == Status.CANCELED) {
//	                    return "Service request line " + serviceRequestIdLineId + " is already cancelled.";
//	                }
//	                bookingDetails.setStatus(Status.CANCELED);
//	                dcBookingRequestDetailsRepository.save(bookingDetails);
////	                double newTotal = bookingHeader.getBookingTotal() - bookingDetails.getTotalAmount();
////	                bookingHeader.setBookingTotal(Math.max(newTotal, 0));
//
//	                List<DCBookingRequestDetails> remainingActiveItems = dcBookingRequestDetailsRepository
//	                        .findByServiceRequestIdAndStatusNot(serviceRequestId, Status.CANCELED);
//
//	                if (remainingActiveItems.isEmpty()) {
//	                    bookingHeader.setStatus(Status.CANCELED);
//	                }
//	                dcBookingRequestHeaderRepository.save(bookingHeader);
//
//	                return "Service request line " + serviceRequestIdLineId + " has been successfully marked as CANCELLED.";
//	            } else {
//	                return "Service request line not found for cancellation.";
//	            }
//	        } else {
//	            return "Booking header not found for service request: " + serviceRequestId;
//	        }
//	    }
	
//	 @Transactional
//	    public void cancelLineItem(String serviceRequestId, String serviceRequestLineId) {
//	        // Find the line item
//	        Optional<DCBookingRequestDetails> optionalDetail = dcBookingRequestDetailsRepository.findById(serviceRequestLineId);
//
//	        if (optionalDetail.isPresent()) {
//	        	DCBookingRequestDetails detail = optionalDetail.get();
//	            detail.setStatus(Status.CANCELED);
//	            dcBookingRequestDetailsRepository.save(detail); // Update the details table
//
//	            // Check if all details are canceled
//	            List<DCBookingRequestDetails> detailsList = dcBookingRequestDetailsRepository.findByServiceRequestId(serviceRequestId);
//	            boolean allCancelled = detailsList.stream().allMatch(d -> d.getStatus() == Status.CANCELED);
//
//	            // If all line items are canceled, update header status
//	            if (allCancelled) {
//	                Optional<DCBookingRequestHeader> optionalHeader = dcBookingRequestHeaderRepository.findById(serviceRequestId);
//	                if (optionalHeader.isPresent()) {
//	                	DCBookingRequestHeader header = optionalHeader.get();
//	                    header.setStatus(Status.CANCELED);
//	                    dcBookingRequestHeaderRepository.save(header);
//	                }
//	            }
//	        } else {
//	            throw new EntityNotFoundException("Service Request Line ID not found: " + serviceRequestLineId);
//	        }
//	    }


	@Transactional
	public CancelLineStatusResponse cancelLineItems(CancelLineStatusRequest request) {
	    String serviceRequestId = request.getServiceRequestId();
	    List<String> serviceRequestLineIds = request.getServiceRequestLineId();
	    List<String> canceledLineIds = new ArrayList<>();
	    List<String> alreadyCanceledIds = new ArrayList<>();

	    // Fetch all details for the given serviceRequestId
	    List<DCBookingRequestDetails> allDetails = dcBookingRequestDetailsRepository.findByServiceRequestId(serviceRequestId);

	    if (allDetails.isEmpty()) {
	        return new CancelLineStatusResponse(serviceRequestId, canceledLineIds, false, 0.0, 
	                "No details found for the given service request ID.");
	    }

	    double totalDeductedAmount = 0.0;

	    // Process cancellation of specified line items
	    for (String lineId : serviceRequestLineIds) {
	        Optional<DCBookingRequestDetails> optionalDetail = dcBookingRequestDetailsRepository.findById(lineId);

	        if (optionalDetail.isPresent()) {
	            DCBookingRequestDetails detail = optionalDetail.get();

	            // Check if the item is already canceled
	            if (detail.getStatus() == Status.CANCELED) {
	                alreadyCanceledIds.add(lineId);
	            } else {
	                detail.setStatus(Status.CANCELED);
	                dcBookingRequestDetailsRepository.save(detail);
	                canceledLineIds.add(lineId);

	                // Deduct price from total
	                totalDeductedAmount += detail.getAmount();
	            }
	        }
	    }

	    double updatedTotalAmount = 0.0;

	    // Check if all details for this service request ID are now canceled
	    boolean allCanceled = allDetails.stream().allMatch(detail -> detail.getStatus() == Status.CANCELED);

	    // Update header information
	    Optional<DCBookingRequestHeader> optionalHeader = dcBookingRequestHeaderRepository.findById(serviceRequestId);
	    if (optionalHeader.isPresent()) {
	        DCBookingRequestHeader header = optionalHeader.get();

	        // Reduce total amount in the header
	        updatedTotalAmount = Math.max(header.getTotalAmount() - totalDeductedAmount, 0); // Ensure it doesn't go negative
	        header.setTotalAmount(updatedTotalAmount);

	        if (allCanceled) {
	            header.setStatus(Status.CANCELED);
	        }

	        dcBookingRequestHeaderRepository.save(header);
	    }

	    // Construct response message
	    String message = !alreadyCanceledIds.isEmpty() && canceledLineIds.isEmpty()
	            ? "Selected services were already canceled. No new changes made."
	            : alreadyCanceledIds.isEmpty()
	            ? (allCanceled ? "All items are canceled. Header status updated." 
	                           : "Some items canceled. Booking total updated.")
	            : "Some items were already canceled. Remaining items have been canceled. Booking total updated.";

	    return new CancelLineStatusResponse(serviceRequestId, canceledLineIds, allCanceled, updatedTotalAmount, message);
	}



	 @Transactional
		public AppintmentBookingPCUpdateResponse updateAppointment(AppintmentBookingRequest request) {
		    String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		    Optional<CustomerRegisterEntity> login = customerRegisterRepository.findByEmail(loggedInUserEmail);

		    Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		    
		    if (login.isEmpty() && loginStore.isEmpty()) {
		        throw new ResourceNotFoundException("Access denied. This API is restricted to customer/store users only.");
		    }

		    // Fetch appointment by serviceRequestId
		    Optional<DCBookingRequestHeader> appointmentOptional = dcBookingRequestHeaderRepository.findByServiceRequestId(request.getServiceRequestId());

		    if (appointmentOptional.isEmpty()) {
		        throw new ResourceNotFoundException("Appointment not found with serviceRequestId: " + request.getServiceRequestId());
		    }

		    // Update appointment details
		    DCBookingRequestHeader appointment = appointmentOptional.get();
		    
		    if (appointment.getStatus() == Status.COMPLETED) {
		        throw new IllegalStateException("Cannot update an appointment that is already COMPLETED.");
		    }

		    // Allow only NEW and PENDING status updates
		    if (request.getStatus() == Status.NEW || request.getStatus() == Status.PENDING ||request.getStatus() == Status.CANCELED ||  
		    		request.getStatus() == Status.COMPLETED) {
		    	
		        appointment.setStatus(request.getStatus());
		    } else {
		        throw new IllegalArgumentException("Invalid status update. Only NEW,IN_PROGRESS,COMPLETED and PENDING statuses are allowed.");
		    }
		    appointment.setAppointmentDate(request.getAppointmentDate());
		    appointment.setAppointmentTime(request.getAppointmentTime());
		    appointment.setHomeService(request.getHomeService());
		    appointment.setWalkinService(request.getWalkinService());
		    
		    String updatedBy;
			String requestedMethod;
			if (login.isPresent()) {
				updatedBy = login.get().getCId(); // Customer ID
				requestedMethod = "online";
			} else {
				updatedBy = loginStore.get().getUserId(); // Store User ID
				requestedMethod = "walkin";
			}
			appointment.setUpdatedBy(updatedBy);
//		    appointment.setUpdatedBy(login.get().getCId());
		    appointment.setUpdatedDate(LocalDate.now());

		    // Save updated appointment
		    DCBookingRequestHeader updatedAppointment = dcBookingRequestHeaderRepository.save(appointment);
		    
		    String emailMessage = "Service request updates: ServiceRequestId " + updatedAppointment.getServiceRequestId() +
                    " is updated to " + updatedAppointment.getStatus().name();
		    
		 // Save the request method in the RequestMethod entity
		    RequestMethod requestMethodEntity = new RequestMethod();
		    requestMethodEntity.setServiceRequestId(updatedAppointment.getServiceRequestId());
		    requestMethodEntity.setRequestedMethod(requestedMethod);
		    RequestMethod saved =requestMethodRepository.save(requestMethodEntity);
		    
//		    emailService.sendEmailMessage(login.get().getEmail(), emailMessage, );
		    
		    String recipientEmail = login.isPresent() ? login.get().getEmail() : loginStore.get().getStoreUserEmail();
		    emailService.sendEmailMessage(recipientEmail, emailMessage, "Appointment Update Notification");

		    return new AppintmentBookingPCUpdateResponse(
		            updatedAppointment.getServiceRequestId(),
		            updatedAppointment.getCustomerId(),
		            updatedAppointment.getUserIdStoreId(),
		            updatedAppointment.getHomeService(),
		            updatedAppointment.getWalkinService(),
		            updatedAppointment.getAppointmentDate(),
		            updatedAppointment.getAppointmentTime(),
//		            updatedAppointment.getUpdatedBy(),
		            updatedBy,
		            updatedAppointment.getAppointmentDate(),
		            updatedAppointment.getBookingDate(),
		            updatedAppointment.getTotalAmount(),
		            updatedAppointment.getStatus()     
		    );
		}
}
