package com.spayker.consumer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 *  Account model entity that contains major information about consumer. Used almost everywhere that relates to Consumer:
 *  - repo layer
 *  - service layer
 *  - controller layer
 *  - dto forming
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "consumer")
public class Consumer {

	@Id
	@GeneratedValue( strategy = GenerationType.AUTO, generator = "native" )
	@GenericGenerator( name = "native", strategy = "native" )
	private long id;

	private String name;

	private String email;

	private Date createdDate;

	private Date modifiedDate;

}
