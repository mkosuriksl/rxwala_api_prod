package com.kosuri.stores.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_payment_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPaymentDetail {

	@Id
	private String id; // "pay_Pk3L0LLrRxmF7x"

	private String entity; // "payment"

	private Integer amount; // 100000

	private String currency; // "INR"

	private String status; // "captured"
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "membership_request_id")
	private MembershipRequest membershipRequest;
	
	private String razorPaySignature;

	@Column(name = "order_id")
	private String order_id; // "order_Pk3GOzE3jkixok"

	@Column(name = "invoice_id")
	private String invoice_id; // null

	private Boolean international; // false

	private String method; // "netbanking"

	@Column(name = "amount_refunded")
	private Long amount_refunded; // 0

	@Column(name = "refund_status")
	private String refund_status; // null

	private Boolean captured; // true

	private String description; // "Test Transaction"

	@Column(name = "card_id")
	private String card_id; // null

	private String bank; // "BARB_R"

	private String wallet; // null

	private String vpa; // null

	private String email; // "gaurav.kumar@example.com"

	private String contact; // "+919000090000"

//	@ElementCollection
//	@CollectionTable(name = "payment_notes", joinColumns = @JoinColumn(name = "id"))
//	@JsonProperty("notes")
//	private Map<String, String> notes; 

	private Integer fee; // 2360

	private Long tax; // 360

	@Column(name = "error_code")
	private String error_code; // null

	@Column(name = "error_description")
	private String error_description; // null

	@Column(name = "error_source")
	private String error_source; // null

	@Column(name = "error_step")
	private String error_step; // null

	@Column(name = "error_reason")
	private String error_reason; // null

	@Column(name = "created_at")
//	@JsonProperty("created_at")
	private Long created_at;

}

