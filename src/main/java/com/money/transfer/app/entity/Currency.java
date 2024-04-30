package com.money.transfer.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a currency entity within the financial application.
 * This entity is mapped stores the code, name, and associated
 * country of each currency.
 *
 * This information is used to validate user requests and provide
 * users with acceptable currency options.
 */
@Getter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "currency", schema = "public")
public class Currency {

    @Id
    private String code;

    private String name;

    private String country;

}
