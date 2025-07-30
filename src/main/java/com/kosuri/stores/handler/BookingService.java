package com.kosuri.stores.handler;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.kosuri.stores.dao.CustomerRegisterEntity;
import com.kosuri.stores.dao.CustomerRegisterRepository;
import com.kosuri.stores.dao.DcBookingDetails;
import com.kosuri.stores.dao.DcBookingDetailsRepository;
import com.kosuri.stores.dao.DcBookingHeader;
import com.kosuri.stores.dao.DcBookingHeaderRepository;
import com.kosuri.stores.dao.RequestMethod;
import com.kosuri.stores.dao.RequestMethodRepository;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreRepository;
import com.kosuri.stores.dao.TabStoreRepository;
import com.kosuri.stores.dao.TabStoreUserEntity;
import com.kosuri.stores.exception.ResourceNotFoundException;
import com.kosuri.stores.model.dto.AppintmentBookingRequest;
import com.kosuri.stores.model.dto.AppintmentBookingRequestdc;
import com.kosuri.stores.model.dto.AppintmentBookingResponse;
import com.kosuri.stores.model.dto.BookingRequest;
import com.kosuri.stores.model.dto.BookingResponse;
import com.kosuri.stores.model.dto.CancelLineStatusRequest;
import com.kosuri.stores.model.dto.CancelLineStatusResponse;
import com.kosuri.stores.model.dto.CustomerDetailsDto;
import com.kosuri.stores.model.dto.DCBookingRequestDto;
import com.kosuri.stores.model.dto.GetBookingResponse;
import com.kosuri.stores.model.dto.GetBookingResponseCustomerInfo;
import com.kosuri.stores.model.dto.PackageRequest;
import com.kosuri.stores.model.dto.PackageResponse;
import com.kosuri.stores.model.dto.ServiceRequest;
import com.kosuri.stores.model.dto.ServiceResponse;
import com.kosuri.stores.model.dto.StoreDetailsDto;
import com.kosuri.stores.model.enums.Status;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
@Service
public class BookingService {

	@Autowired
	private DcBookingDetailsRepository bookingDetailsRepository;

	@Autowired
	private DcBookingHeaderRepository bookingHeaderRepository;

	@Autowired
	private CustomerRegisterRepository customerRegisterRepository;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private TabStoreRepository tabStoreRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private RequestMethodRepository requestMethodRepository;

	public BookingResponse createBooking(BookingRequest bookingRequest) {

		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<CustomerRegisterEntity> login = customerRegisterRepository.findByEmail(loggedInUserEmail);
		Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		if (login.isEmpty() && loginStore.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer/store users only.");
		}
		String serviceRequestId = generateServiceRequestId();

		DcBookingHeader dcBookingHeader = new DcBookingHeader();
		dcBookingHeader.setServiceRequestId(serviceRequestId);
		dcBookingHeader.setCustomerId(bookingRequest.getCustomerId());
		dcBookingHeader.setUserIdStoreId(bookingRequest.getUserIdStoreId());
		dcBookingHeader.setHomeService(bookingRequest.getHomeService());
		dcBookingHeader.setWalkinService(bookingRequest.getWalkinService());
		dcBookingHeader.setAppointmentDate(bookingRequest.getAppointmentDate());
		dcBookingHeader.setAppointmentTime(bookingRequest.getAppointmentTime());
//        dcBookingHeader.setUpdatedBy(login.get().getCId());
		String updatedBy = null;
		String requestedMethod = null;
		if (login.isPresent()) {
//			dcBookingHeader.setUpdatedBy(login.get().getCId()); // Customer ID
			updatedBy = login.get().getCId(); // Customer ID
			requestedMethod = "online";
		} else if (loginStore.isPresent()) {
//			dcBookingHeader.setUpdatedBy(loginStore.get().getUserId()); // Store User ID
			updatedBy = loginStore.get().getUserId(); // Store User ID
			requestedMethod = "walkin";
		}
		dcBookingHeader.setUpdatedBy(updatedBy);
		dcBookingHeader.setUpdatedDate(LocalDate.now());
		dcBookingHeader.setBookingDate(LocalDate.now());
		dcBookingHeader.setStatus(Status.NEW);
		dcBookingHeader.setBookingTotal(bookingRequest.getBookingTotal());
		dcBookingHeader = bookingHeaderRepository.save(dcBookingHeader);

		// List to store package responses
		List<PackageResponse> packageResponses = new ArrayList<>();

		int serviceRequestLineNumber = 1; // Reset for each bookingRequest

		for (PackageRequest packageRequest : bookingRequest.getSelectedPackages()) {
			int packageLineNumber = 1; // Reset for each package

			List<ServiceResponse> serviceResponses = new ArrayList<>();
			double totalPackageAmount = 0;

			for (ServiceRequest serviceRequest : packageRequest.getSelectedServices()) {
				// Calculate service discount and final amount
				double discountAmount = serviceRequest.getAmount() * serviceRequest.getDiscount() / 100;
				double serviceAmount = serviceRequest.getAmount() - discountAmount;

				// Generate unique IDs
				String serviceRequestLineId = serviceRequestId + "_"
						+ String.format("%04d", serviceRequestLineNumber++);
				String packageIdLineId = packageRequest.getPackageId() + "_"
						+ String.format("%04d", packageLineNumber++);

				// Save booking details
				DcBookingDetails dcBookingDetails = new DcBookingDetails();
				dcBookingDetails.setServiceRequestLineId(serviceRequestLineId);
				dcBookingDetails.setServiceRequestId(serviceRequestId);
				dcBookingDetails.setPackageId(packageRequest.getPackageId());
				dcBookingDetails.setPackageName(packageRequest.getPackageName());
				dcBookingDetails.setServiceID(serviceRequest.getServiceID());
				dcBookingDetails.setAmount(serviceRequest.getAmount());
				dcBookingDetails.setDiscount(serviceRequest.getDiscount());
				dcBookingDetails.setServiceName(serviceRequest.getServiceName());
				dcBookingDetails.setTotalAmount(serviceAmount);
				dcBookingDetails.setPackageIdLineId(packageIdLineId);

				DcBookingDetails dcBookingDetailsres = bookingDetailsRepository.save(dcBookingDetails);

				// Add service response to list
				serviceResponses.add(new ServiceResponse(serviceRequest.getServiceID(), serviceRequest.getAmount(),
						serviceRequest.getDiscount(), serviceRequest.getServiceName(), serviceRequest.getStatus(),
						dcBookingDetailsres.getPackageIdLineId()));

				totalPackageAmount += serviceAmount;
			}

//            packageTotalAmount += totalPackageAmount;
			// Add package response to list
			packageResponses.add(new PackageResponse(packageRequest.getPackageId(), packageRequest.getPackageName(),
					totalPackageAmount, serviceRequestId,
					serviceRequestId + "_" + String.format("%04d", serviceRequestLineNumber - 1), // Ensure correct
																									// numbering
					serviceResponses));
		}

		// Create and return the booking response
		BookingResponse bookingResponse = new BookingResponse();
		bookingResponse.setServiceRequestId(serviceRequestId);
		bookingResponse.setCustomerId(bookingRequest.getCustomerId());
		bookingResponse.setUserIdStoreId(bookingRequest.getUserIdStoreId());
		bookingResponse.setHomeService(bookingRequest.getHomeService());
		bookingResponse.setWalkinService(bookingRequest.getWalkinService());
		bookingResponse.setAppointmentDate(bookingRequest.getAppointmentDate());
		bookingResponse.setAppointmentTime(bookingRequest.getAppointmentTime());
		bookingResponse.setUpdatedDate(dcBookingHeader.getUpdatedDate());
		bookingResponse.setUpdatedBy(dcBookingHeader.getUpdatedBy());
		bookingResponse.setBookingDate(dcBookingHeader.getBookingDate());
		bookingResponse.setSelectedPackages(packageResponses);
		bookingResponse.setBookingTotal(dcBookingHeader.getBookingTotal());
		bookingResponse.setStatus(dcBookingHeader.getStatus());
		
		// Save the request method in the RequestMethod entity
		RequestMethod requestMethodEntity = new RequestMethod();
		requestMethodEntity.setServiceRequestId(serviceRequestId);
		requestMethodEntity.setRequestedMethod(requestedMethod);
		RequestMethod saved = requestMethodRepository.save(requestMethodEntity);
		
		String htmlContent = generateBookingHtml(dcBookingHeader);

	    // Generate PDF
	    byte[] pdfAttachment = generateBookingDetailsPdf(packageResponses);

	    // Send email
	    String subject ="Booking Confirmation";
	    String recipientEmail = login.isPresent() ? login.get().getEmail() : loginStore.get().getStoreUserEmail();
		emailService.sendEmailMessagePdf(recipientEmail, subject, htmlContent, pdfAttachment);
//	    emailService.sendEmailMessagePdf(login.get().getEmail(), subject, htmlContent, pdfAttachment);
		return bookingResponse;
	}
	public static String generateBookingHtml(DcBookingHeader booking) {
		return "<html><body>" +
	            "<h2>Booking Confirmation</h2>" +
	            "<table border='1' cellspacing='0' cellpadding='5'>" +
	            "<tr>" +
	                "<th>Booking ID</th>" +
	                "<th>Customer ID</th>" +
	                "<th>Store/User ID</th>" +
	                "<th>Home Service</th>" +
	                "<th>Walk-in Service</th>" +
	                "<th>Appointment Date</th>" +
	                "<th>Total Amount</th>" +
	                "<th>Status</th>" +
	            "</tr>" +
	            "<tr>" +
	                "<td>" + booking.getServiceRequestId() + "</td>" +
	                "<td>" + booking.getCustomerId() + "</td>" +
	                "<td>" + booking.getUserIdStoreId() + "</td>" +
	                "<td>" + (booking.getHomeService() ? "Yes" : "No") + "</td>" +
	                "<td>" + (booking.getWalkinService() ? "Yes" : "No") + "</td>" +
	                "<td>" + booking.getAppointmentDate() + "</td>" +
	                "<td>" + booking.getBookingTotal() + "</td>" +
	                "<td>" + booking.getStatus() + "</td>" +
	            "</tr>" +
	            "</table>" +
	            "</body></html>";
    }
	
	public static byte[] generateBookingDetailsPdf(List<PackageResponse> packages) {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    Document document = new Document(PageSize.A4, 15, 15, 15, 15); // A4 size with margins
	    try {
	        PdfWriter.getInstance(document, out);
	        document.open();
	        
	        // Set Font
	        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
	        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 7);

	        // Title
	        Paragraph title = new Paragraph("Booking Details\n\n", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD));
	        title.setAlignment(Element.ALIGN_CENTER);
	        document.add(title);

	        // Create a table with 10 columns
	        PdfPTable table = new PdfPTable(10);
	        table.setWidthPercentage(100); // Full width
	        table.setSpacingBefore(10f);
	        table.setSpacingAfter(10f);
	        table.setWidths(new float[]{1.5f, 1.5f, 1.5f, 2.5f, 2.5f, 1.5f, 1.5f, 1.5f, 1.5f, 2.5f});

	        // Add headers with styling
	        String[] headers = {"Package Id", "Package Name", "Total Amount", "ServiceRequestId", 
	                            "ServiceRequestIdLineId", "ServiceID", "Amount", "Discount", 
	                            "Service Name", "PackageIdLineId"};
	        
	        for (String header : headers) {
	            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
	            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
	            cell.setPadding(3);
	            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	            table.addCell(cell);
	        }

	        // Add package details as rows
	        for (PackageResponse pkg : packages) {
	            table.addCell(new PdfPCell(new Phrase(pkg.getPackageId(), cellFont)));
	            table.addCell(new PdfPCell(new Phrase(pkg.getPackageName(), cellFont)));
	            table.addCell(new PdfPCell(new Phrase(String.valueOf(pkg.getTotalAmount()), cellFont)));
	            table.addCell(new PdfPCell(new Phrase(pkg.getServiceRequestId(), cellFont)));
	            table.addCell(new PdfPCell(new Phrase(pkg.getServiceRequestIdLineId(), cellFont)));

	            // Handle multiple services properly
	            for (ServiceResponse service : pkg.getSelectedServices()) {
	                table.addCell(new PdfPCell(new Phrase(service.getServiceID(), cellFont)));
	                table.addCell(new PdfPCell(new Phrase(String.valueOf(service.getAmount()), cellFont)));
	                table.addCell(new PdfPCell(new Phrase(String.valueOf(service.getDiscount()), cellFont)));
	                table.addCell(new PdfPCell(new Phrase(service.getServiceName(), cellFont)));
	                table.addCell(new PdfPCell(new Phrase(service.getPackageIdLineId(), cellFont)));
	            }
	        }

	        // Add the table to the document
	        document.add(table);

	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        document.close(); // Manually close the document
	    }
	    return out.toByteArray();
	}
	
	public void sendEmailMessagePdf(String emailId,String subject, DcBookingHeader htmlContent, List<PackageResponse> pdfAttachment) throws MessagingException {
		Optional<CustomerRegisterEntity> login = customerRegisterRepository.findByEmail(emailId);

		Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(emailId);
		
		if (login.isEmpty() && loginStore.isEmpty()) {
			throw new ResourceNotFoundException("Customer not found with ID: " + emailId);
		}
		String recipientEmail = login.isPresent() ? login.get().getEmail() : loginStore.get().getStoreUserEmail();
//		String recipientEmail = customer.get().getEmail();
		String emailHtml = generateBookingHtml(htmlContent);
		byte[] pdfBytes = generateBookingDetailsPdf(pdfAttachment);
		MimeMessage message = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom("no_reply@kosuriers.com");
			helper.setTo(recipientEmail);
			helper.setSubject("Booking Confirmation - ");
			helper.setText(emailHtml, true);
			

			// Attach PDF with services only
			helper.addAttachment("Booking_Confirmation.pdf", new ByteArrayResource(pdfBytes));

			javaMailSender.send(message);
			System.out.println("Email sent successfully to: " + recipientEmail);
		} catch (MessagingException e) {
			throw new RuntimeException("Failed to send email", e);
		}

        javaMailSender.send(message);
    }
	// Helper method to generate serviceRequestId
	private String generateServiceRequestId() {
		String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		int randomNum = new Random().nextInt(9000) + 1000;
		return "DCSR" + timestamp + randomNum;
	}

	@Transactional
	public AppintmentBookingResponse updateAppointment(AppintmentBookingRequestdc request) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<CustomerRegisterEntity> login = customerRegisterRepository.findByEmail(loggedInUserEmail);

		Optional<TabStoreUserEntity> loginStore = tabStoreRepository.findByStoreUserEmail(loggedInUserEmail);
		
		if (login.isEmpty() && loginStore.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer/store users only.");
		}

		// Fetch appointment by serviceRequestId
		Optional<DcBookingHeader> appointmentOptional = bookingHeaderRepository
				.findByServiceRequestId(request.getServiceRequestId());

		if (appointmentOptional.isEmpty()) {
			throw new ResourceNotFoundException(
					"Appointment not found with serviceRequestId: " + request.getServiceRequestId());
		}

		// Update appointment details
		DcBookingHeader appointment = appointmentOptional.get();

		if (appointment.getStatus() == Status.COMPLETED) {
			throw new IllegalStateException("Cannot update an appointment that is already COMPLETED.");
		}

		// Allow only NEW and PENDING status updates
		if (request.getStatus() == Status.NEW || request.getStatus() == Status.PENDING
				|| request.getStatus() == Status.CANCELED || request.getStatus() == Status.COMPLETED) {

			appointment.setStatus(request.getStatus());
		} else {
			throw new IllegalArgumentException(
					"Invalid status update. Only NEW,IN_PROGRESS,COMPLETED and PENDING statuses are allowed.");
		}
		appointment.setAppointmentDate(request.getAppointmentDate());
		appointment.setAppointmentTime(request.getAppointmentTime());
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
//		appointment.setUpdatedBy(login.get().getCId());
		appointment.setUpdatedDate(LocalDate.now());
		appointment.setHomeService(request.getHomeService());
		appointment.setWalkinService(request.getWalkinService());
		// Save updated appointment
		DcBookingHeader updatedAppointment = bookingHeaderRepository.save(appointment);
		RequestMethod requestMethodEntity = new RequestMethod();
		requestMethodEntity.setServiceRequestId(updatedAppointment.getServiceRequestId());
		requestMethodEntity.setRequestedMethod(requestedMethod);
		RequestMethod saved =requestMethodRepository.save(requestMethodEntity);
		
		return new AppintmentBookingResponse(updatedAppointment.getServiceRequestId(),
				updatedAppointment.getCustomerId(), updatedAppointment.getUserIdStoreId(),
				updatedAppointment.getHomeService(), updatedAppointment.getWalkinService(),
				updatedAppointment.getAppointmentDate(), updatedAppointment.getAppointmentTime(),
				updatedAppointment.getUpdatedBy(), updatedAppointment.getAppointmentDate(),
				updatedAppointment.getBookingDate(), updatedAppointment.getBookingTotal(),
				updatedAppointment.getStatus());
	}

	public List<GetBookingResponse> getCustomerBookingsWithStore(String customerId, LocalDate bookingDateFrom,
			LocalDate bookingDateTo, LocalDate appointmentDateFrom, LocalDate appointmentDateTo) {

		Specification<DcBookingHeader> specification = (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (customerId != null) {
				predicates.add(criteriaBuilder.equal(root.get("customerId"), customerId));
			}
			if (bookingDateFrom != null && bookingDateTo != null) {
				predicates.add(criteriaBuilder.between(root.get("bookingDate"), bookingDateFrom, bookingDateTo));
			} else if (bookingDateFrom != null) {
				predicates.add(criteriaBuilder.equal(root.get("bookingDate"), bookingDateFrom));
			}
			if (appointmentDateFrom != null && appointmentDateTo != null) {
				predicates.add(
						criteriaBuilder.between(root.get("appointmentDate"), appointmentDateFrom, appointmentDateTo));
			} else if (appointmentDateFrom != null) {
				predicates.add(criteriaBuilder.equal(root.get("appointmentDate"), appointmentDateFrom));
			}
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

		List<DcBookingHeader> headers = bookingHeaderRepository.findAll(specification);
		List<GetBookingResponse> responseList = new ArrayList<>();

		for (DcBookingHeader header : headers) {
			List<DcBookingDetails> detailsList = bookingDetailsRepository
					.findByServiceRequestId(header.getServiceRequestId());

			Map<String, PackageResponse> packageMap = new HashMap<>();

//            for (DcBookingDetails details : detailsList) {
//                ServiceResponse serviceResponse = new ServiceResponse(
//                        details.getServiceID(), details.getAmount(), details.getDiscount(),
//                        details.getServiceName(),details.getStatus(), details.getPackageIdLineId());
//
//                packageMap.computeIfAbsent(details.getPackageId(), k -> new PackageResponse(
//                        details.getPackageId(), details.getPackageName(), details.getTotalAmount(),
//                        details.getServiceRequestId(), details.getServiceRequestLineId(), new ArrayList<>()))
//                        .getSelectedServices().add(serviceResponse);
//            }

			for (DcBookingDetails details : detailsList) {

				ServiceResponse serviceResponse = new ServiceResponse(details.getServiceID(), details.getAmount(),
						details.getDiscount(), details.getServiceName(), details.getStatus(),
						details.getPackageIdLineId());

				Double amount = serviceResponse.getDiscount() > 0
						? serviceResponse.getAmount()
								- (serviceResponse.getAmount() * serviceResponse.getDiscount()) / 100
						: serviceResponse.getAmount();

				// Initialize packageResponse if it doesn't exist
				packageMap.computeIfAbsent(details.getPackageId(),
						k -> new PackageResponse(details.getPackageId(), details.getPackageName(), 0.0,
								details.getServiceRequestId(), details.getServiceRequestLineId(), new ArrayList<>()));

				// Retrieve the existing PackageResponse
				PackageResponse packageResponse = packageMap.get(details.getPackageId());

				// Add the service to the package
				packageResponse.getSelectedServices().add(serviceResponse);

				// Accumulate totalAmount for only this package
				packageResponse.setTotalAmount(packageResponse.getTotalAmount() + amount);
			}

			Optional<StoreEntity> storeOptional = storeRepository.findByUserIdStoreId(header.getUserIdStoreId());
			StoreDetailsDto storeDetailsDto = storeOptional
					.map(store -> new StoreDetailsDto(store.getType(), store.getUserIdStoreId(), store.getId(),
							store.getName(), store.getPincode(), store.getDistrict(), store.getState(),
							store.getLocation(), store.getOwner(), store.getOwnerContact(), store.getOwnerEmail()))
					.orElse(null);

			GetBookingResponse response = new GetBookingResponse();
			response.setServiceRequestId(header.getServiceRequestId());
			response.setCustomerId(header.getCustomerId());
			response.setUserIdStoreId(header.getUserIdStoreId());
			response.setHomeService(header.getHomeService());
			response.setWalkinService(header.getWalkinService());
			response.setAppointmentDate(header.getAppointmentDate());
			response.setAppointmentTime(header.getAppointmentTime());
			response.setUpdatedDate(header.getUpdatedDate());
			response.setUpdatedBy(header.getUpdatedBy());
			response.setBookingDate(header.getBookingDate());
			response.setBookingTotal(header.getBookingTotal());
			response.setStatus(header.getStatus());
			response.setSelectedPackages(new ArrayList<>(packageMap.values()));
			response.setStoreDetails(storeDetailsDto); // <-- Add store details to response

			responseList.add(response);
		}
		return responseList;
	}

	public List<GetBookingResponseCustomerInfo> getCustomerBookingsWithCustomer(String userIdStoreId,
			LocalDate bookingDateFrom, LocalDate bookingDateTo, LocalDate appointmentDateFrom,
			LocalDate appointmentDateTo) {

		Specification<DcBookingHeader> specification = (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (userIdStoreId != null) {
			    predicates.add(criteriaBuilder.like(root.get("userIdStoreId"), userIdStoreId + "%"));
			}
			if (bookingDateFrom != null && bookingDateTo != null) {
				predicates.add(criteriaBuilder.between(root.get("bookingDate"), bookingDateFrom, bookingDateTo));
			} else if (bookingDateFrom != null) {
				predicates.add(criteriaBuilder.equal(root.get("bookingDate"), bookingDateFrom));
			}
			if (appointmentDateFrom != null && appointmentDateTo != null) {
				predicates.add(
						criteriaBuilder.between(root.get("appointmentDate"), appointmentDateFrom, appointmentDateTo));
			} else if (appointmentDateFrom != null) {
				predicates.add(criteriaBuilder.equal(root.get("appointmentDate"), appointmentDateFrom));
			}
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

		List<DcBookingHeader> headers = bookingHeaderRepository.findAll(specification);
		List<GetBookingResponseCustomerInfo> responseList = new ArrayList<>();

		for (DcBookingHeader header : headers) {
			List<DcBookingDetails> detailsList = bookingDetailsRepository
					.findByServiceRequestId(header.getServiceRequestId());

			Map<String, PackageResponse> packageMap = new HashMap<>();

//            for (DcBookingDetails details : detailsList) {
//                ServiceResponse serviceResponse = new ServiceResponse(
//                        details.getServiceID(), details.getAmount(), details.getDiscount(),
//                        details.getServiceName(),details.getStatus(), details.getPackageIdLineId());
//
//                packageMap.computeIfAbsent(details.getPackageId(), k -> new PackageResponse(
//                        details.getPackageId(), details.getPackageName(), details.getTotalAmount(),
//                        details.getServiceRequestId(), details.getServiceRequestLineId(), new ArrayList<>()))
//                        .getSelectedServices().add(serviceResponse);
//            }

			for (DcBookingDetails details : detailsList) {

				ServiceResponse serviceResponse = new ServiceResponse(details.getServiceID(), details.getAmount(),
						details.getDiscount(), details.getServiceName(), details.getStatus(),
						details.getPackageIdLineId());

				Double amount = serviceResponse.getDiscount() > 0
						? serviceResponse.getAmount()
								- (serviceResponse.getAmount() * serviceResponse.getDiscount()) / 100
						: serviceResponse.getAmount();

				// Initialize packageResponse if it doesn't exist
				packageMap.computeIfAbsent(details.getPackageId(),
						k -> new PackageResponse(details.getPackageId(), details.getPackageName(), 0.0,
								details.getServiceRequestId(), details.getServiceRequestLineId(), new ArrayList<>()));

				// Retrieve the existing PackageResponse
				PackageResponse packageResponse = packageMap.get(details.getPackageId());

				// Add the service to the package
				packageResponse.getSelectedServices().add(serviceResponse);

				// Accumulate totalAmount for only this package
				packageResponse.setTotalAmount(packageResponse.getTotalAmount() + amount);
			}

			Optional<CustomerRegisterEntity> customerUser = customerRegisterRepository.findByCid(header.getCustomerId());
			CustomerDetailsDto customerDetailsDto = customerUser
					.map(customer -> new CustomerDetailsDto(customerUser.get().getId(), customerUser.get().getCId(),
							customerUser.get().getName(), customerUser.get().getEmail(),
							customerUser.get().getPhoneNumber(), customerUser.get().getLocation(),
							customerUser.get().getRegisteredDate(), customerUser.get().getUpdatedDate()))
					.orElse(null);

			Optional<StoreEntity> storeDetailsEntity = storeRepository.findByUserIdStoreId(header.getUserIdStoreId());
			StoreDetailsDto storeDetails = storeDetailsEntity
					.map(store -> new StoreDetailsDto(store.getType(), store.getUserIdStoreId(), store.getId(),
							store.getName(), store.getPincode(), store.getDistrict(), store.getState(),
							store.getLocation(), store.getOwner(), store.getOwnerContact(), store.getOwnerEmail()))
					.orElse(null);

			GetBookingResponseCustomerInfo response = new GetBookingResponseCustomerInfo();
			response.setServiceRequestId(header.getServiceRequestId());
			response.setCustomerId(header.getCustomerId());
			response.setUserIdStoreId(header.getUserIdStoreId());
			response.setHomeService(header.getHomeService());
			response.setWalkinService(header.getWalkinService());
			response.setAppointmentDate(header.getAppointmentDate());
			response.setAppointmentTime(header.getAppointmentTime());
			response.setUpdatedDate(header.getUpdatedDate());
			response.setUpdatedBy(header.getUpdatedBy());
			response.setBookingDate(header.getBookingDate());
			response.setBookingTotal(header.getBookingTotal());
			response.setStatus(header.getStatus());
			response.setSelectedPackages(new ArrayList<>(packageMap.values()));
			response.setCustomerDetailsDto(customerDetailsDto); // <-- Add store details to response
			response.setStoreDetailsDto(storeDetails);
			responseList.add(response);
		}
		return responseList;
	}

//    @Transactional
//    public CancelLineStatusResponse cancelServiceRequestLines(String serviceRequestId, List<String> serviceRequestLineIds) {
//        List<String> canceledLineIds = new ArrayList<>();
//
//        // Fetch all details for the given serviceRequestId
//        List<DcBookingDetails> allDetails = bookingDetailsRepository.findByServiceRequestId(serviceRequestId);
//
//        if (allDetails.isEmpty()) {
//            return new CancelLineStatusResponse(serviceRequestId, canceledLineIds, false, "No details found for the given service request ID.");
//        }
//
//        AtomicReference<Double> refundAmount = new AtomicReference<>(0.0);
//
//        // Cancel the specified serviceRequestLineIds
//        for (String lineId : serviceRequestLineIds) {
//            allDetails.stream()
//                .filter(detail -> detail.getServiceRequestLineId().equals(lineId))
//                .findFirst()
//                .ifPresent(detail -> {
//                    detail.setStatus(Status.CANCELED);
//                    bookingDetailsRepository.save(detail);
//                    canceledLineIds.add(lineId);
//                    refundAmount.updateAndGet(v -> v + detail.getTotalAmount()); // Accumulate the refund amount
//                });
//        }
//
//        // Check if all details for this service request ID are now canceled
//        boolean allCanceled = allDetails.stream().allMatch(detail -> detail.getStatus() == Status.CANCELED);
//
//        // Update header status & recalculate booking total
//        bookingHeaderRepository.findById(serviceRequestId).ifPresent(header -> {
//            if (allCanceled) {
//                header.setStatus(Status.CANCELED);
//            }
//            header.setBookingTotal(header.getBookingTotal() - refundAmount.get()); // Reduce canceled amounts
//            bookingHeaderRepository.save(header);
//        });
//
//        // Construct response
//        String message = allCanceled ? "All items are canceled. Header status updated." 
//                                     : "Some items canceled. Booking total updated.";
//        return new CancelLineStatusResponse(serviceRequestId, canceledLineIds, allCanceled, message);
//    }

	@Transactional
	public CancelLineStatusResponse cancelServiceRequestLines(CancelLineStatusRequest request) {
	    String serviceRequestId = request.getServiceRequestId();
	    List<String> serviceRequestLineIds = request.getServiceRequestLineId();
	    List<String> canceledLineIds = new ArrayList<>();
	    List<String> alreadyCanceledIds = new ArrayList<>();

	    // Fetch all details for the given serviceRequestId
	    List<DcBookingDetails> allDetails = bookingDetailsRepository.findByServiceRequestId(serviceRequestId);

	    if (allDetails.isEmpty()) {
	        return new CancelLineStatusResponse(serviceRequestId, canceledLineIds, false, 0.0,
	                "No details found for the given service request ID.");
	    }

	    AtomicReference<Double> refundAmount = new AtomicReference<>(0.0);

	    // Cancel the specified serviceRequestLineIds
	    for (String lineId : serviceRequestLineIds) {
	        allDetails.stream()
	            .filter(detail -> detail.getServiceRequestLineId().equals(lineId))
	            .findFirst()
	            .ifPresent(detail -> {
	                if (detail.getStatus() == Status.CANCELED) {
	                    alreadyCanceledIds.add(lineId);  // Track already canceled IDs
	                } else {
	                    detail.setStatus(Status.CANCELED);
	                    bookingDetailsRepository.save(detail);
	                    canceledLineIds.add(lineId);
	                    refundAmount.updateAndGet(v -> v + detail.getTotalAmount()); // Accumulate refund
	                }
	            });
	    }

	    boolean allCanceled = allDetails.stream().allMatch(detail -> detail.getStatus() == Status.CANCELED);
	    double updatedTotalAmount = 0.0;

	    // Update header status & recalculate booking total
	    Optional<DcBookingHeader> optionalHeader = bookingHeaderRepository.findById(serviceRequestId);
	    if (optionalHeader.isPresent()) {
	        DcBookingHeader header = optionalHeader.get();

	        // Reduce the refund amount from total, ensuring it doesn't go negative
	        updatedTotalAmount = Math.max(header.getBookingTotal() - refundAmount.get(), 0.0);
	        header.setBookingTotal(updatedTotalAmount);

	        if (allCanceled) {
	            header.setStatus(Status.CANCELED);
	        }

	        bookingHeaderRepository.save(header);
	    }

	    // Construct response message using ternary operator
	    String message = !alreadyCanceledIds.isEmpty() && canceledLineIds.isEmpty()
	            ? "Selected services were already canceled. No new changes made."
	            : alreadyCanceledIds.isEmpty()
	            ? (allCanceled ? "All items are canceled. Header status updated." 
	                           : "Some items canceled. Booking total updated.")
	            : "Some items were already canceled. Remaining items have been canceled. Booking total updated.";

	    return new CancelLineStatusResponse(serviceRequestId, canceledLineIds, allCanceled, updatedTotalAmount, message);
	}


//    @Transactional
//    public void cancelLineItem(String serviceRequestId, String serviceRequestLineId) {
//        // Find the line item
//        Optional<DcBookingDetails> optionalDetail = bookingDetailsRepository.findById(serviceRequestLineId);
//
//        if (optionalDetail.isPresent()) {
//            DcBookingDetails detail = optionalDetail.get();
//            detail.setStatus(Status.CANCELED);
//            bookingDetailsRepository.save(detail); // Update the details table
//
//            // Check if all details are canceled
//            List<DcBookingDetails> detailsList = bookingDetailsRepository.findByServiceRequestId(serviceRequestId);
//            boolean allCancelled = detailsList.stream().allMatch(d -> d.getStatus() == Status.CANCELED);
//
//            // If all line items are canceled, update header status
//            if (allCancelled) {
//                Optional<DcBookingHeader> optionalHeader = bookingHeaderRepository.findById(serviceRequestId);
//                if (optionalHeader.isPresent()) {
//                    DcBookingHeader header = optionalHeader.get();
//                    header.setStatus(Status.CANCELED);
//                    bookingHeaderRepository.save(header);
//                }
//            }
//        } else {
//            throw new EntityNotFoundException("Service Request Line ID not found: " + serviceRequestLineId);
//        }
//    }

//    @Transactional
//    public String cancelLineItemStatus(String serviceRequestId, String serviceRequestIdLineId) {
//        // Fetch the booking header first
//        Optional<DcBookingHeader> bookingHeaderOpt = bookingHeaderRepository.findByServiceRequestId(serviceRequestId);
//
//        if (bookingHeaderOpt.isPresent()) {
//            DcBookingHeader bookingHeader = bookingHeaderOpt.get();
//            if (bookingHeader.getStatus() == Status.COMPLETED) {
//                return "Cancellation not allowed. The service request " + serviceRequestId + " is already completed.";
//            }
//            Optional<DcBookingDetails> bookingDetailsOpt =
//                bookingDetailsRepository.findByServiceRequestIdAndServiceRequestLineId(serviceRequestId, serviceRequestIdLineId);
//
//            if (bookingDetailsOpt.isPresent()) {
//                DcBookingDetails bookingDetails = bookingDetailsOpt.get();
//                if (bookingDetails.getStatus() == Status.CANCELED) {
//                    return "Service request line " + serviceRequestIdLineId + " is already cancelled.";
//                }
//                bookingDetails.setStatus(Status.CANCELED);
//                bookingDetailsRepository.save(bookingDetails);
//                double newTotal = bookingHeader.getBookingTotal() - bookingDetails.getTotalAmount();
//                bookingHeader.setBookingTotal(Math.max(newTotal, 0));
//
//                List<DcBookingDetails> remainingActiveItems = bookingDetailsRepository
//                        .findByServiceRequestIdAndStatusNot(serviceRequestId, Status.CANCELED);
//
//                if (remainingActiveItems.isEmpty()) {
//                    bookingHeader.setStatus(Status.CANCELED);
//                }
//                bookingHeaderRepository.save(bookingHeader);
//
//                return "Service request line " + serviceRequestIdLineId + " has been successfully marked as CANCELLED.";
//            } else {
//                return "Service request line not found for cancellation.";
//            }
//        } else {
//            return "Booking header not found for service request: " + serviceRequestId;
//        }
//    }

}
