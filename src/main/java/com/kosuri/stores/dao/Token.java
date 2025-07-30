package com.kosuri.stores.dao;

import lombok.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "token")
public class Token {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long tokenId;

	public @Column(name = "token", length = 1024) String token;

	public @Column(name = "tokenType") String tokenType = TokenType.BEARER.name();

	public @Column(name = "revoked") boolean revoked;

	public @Column(name = "expired") boolean expired;

	public @Column(name = "user_id") String userId;

}
