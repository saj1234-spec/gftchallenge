package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.TransfersService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransfersServiceTest {

  @Autowired
  private AccountsService accountsService;
  
  @Autowired
  private TransfersService transfersService;
  
  @Test
  public void createTransfer() throws Exception {
    Account accountFrom = new Account("Id-123");
    accountFrom.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(accountFrom);

    Account accountTo = new Account("Id-456");
    accountTo.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(accountTo);
    
    Transfer transfer = new Transfer("Id-123", "Id-456", new BigDecimal(500));
    transfersService.transfer(transfer);
    
    accountFrom.setBalance(accountFrom.getBalance().subtract(new BigDecimal(500)));
    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(accountFrom);
    
    accountTo.setBalance(accountTo.getBalance().add(new BigDecimal(500)));
    assertThat(this.accountsService.getAccount("Id-456")).isEqualTo(accountTo);
  }

  
  @Test
  public void createTransferFailOnNegativeAmount() throws Exception {
    Account accountFrom = new Account("Id-123");
    accountFrom.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(accountFrom);

    Account accountTo = new Account("Id-456");
    accountTo.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(accountTo);

    try {
        Transfer transfer = new Transfer("Id-123", "Id-456", new BigDecimal(-500));
        transfersService.transfer(transfer);
      fail("Should have failed when adding negative amount");
    } catch (NegativeBalanceException ex) {
      assertThat(ex.getMessage()).isEqualTo("Negative balance in the request");
    }

  }

  @Test
  public void createTransferFailOnNegativeBalance() throws Exception {
    Account accountFrom = new Account("Id-123");
    accountFrom.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(accountFrom);

    Account accountTo = new Account("Id-456");
    accountTo.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(accountTo);

    try {
        Transfer transfer = new Transfer("Id-123", "Id-456", new BigDecimal(1001));
        transfersService.transfer(transfer);
      fail("Should have failed when balance is negative");
    } catch (NegativeBalanceException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + accountFrom.getAccountId() + " negative balance");
    }

  }
}
