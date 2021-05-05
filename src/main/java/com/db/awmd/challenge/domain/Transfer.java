package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class Transfer {

  @NotNull
  @NotEmpty
  private final String accountFromId;
  
  @NotNull
  @NotEmpty
  private final String accountToId;

  @NotNull
  @Min(value = 0, message = "The amount to transfer should always be a positive number.")
  private BigDecimal amount;

  @JsonCreator
  public Transfer(@JsonProperty("accountFromId") String accountFromId,
	@JsonProperty("accountToId") String accountToId,	  
	@JsonProperty("balance") BigDecimal amount) {
    this.accountFromId = accountFromId;
    this.accountToId = accountToId;
    this.amount = amount;
  }
}
