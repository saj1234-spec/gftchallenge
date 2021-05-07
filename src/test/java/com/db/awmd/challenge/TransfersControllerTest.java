package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.service.AccountsService;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransfersControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private AccountsService accountsService;
  
  @Autowired
  private AccountsRepository accountsRepository;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void prepareMockMvc() throws Exception {
    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

    // Reset the existing accounts before each test.
    accountsRepository.clearAccounts();
  }

  @Test
  public void createTransfer() throws Exception {
	  
    Account account = new Account("Id-123", new BigDecimal("1000"));
    this.accountsService.createAccount(account);
    
    account = new Account("Id-456", new BigDecimal("1000"));
    this.accountsService.createAccount(account);
    
    this.mockMvc.perform(post("/v1/transfers").contentType(MediaType.APPLICATION_JSON)
  	      .content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"Id-456\",\"amount\":500}")).andExpect(status().isCreated());
    

    Account accountFrom = accountsService.getAccount("Id-123");
    assertThat(accountFrom.getBalance()).isEqualByComparingTo("500");
    
    Account accountTo = accountsService.getAccount("Id-456");
    assertThat(accountTo.getBalance()).isEqualByComparingTo("1500");
  }

  @Test
  public void createTransferNoAccountFromId() throws Exception {
	    this.mockMvc.perform(post("/v1/transfers").contentType(MediaType.APPLICATION_JSON)
	    	      .content("{\"accountToId\":\"Id-456\",\"amount\":500}")).andExpect(status().isBadRequest());	  
  }

  @Test
  public void createTransferNoAccountToId() throws Exception {
	    this.mockMvc.perform(post("/v1/transfers").contentType(MediaType.APPLICATION_JSON)
	    	      .content("{\"accountFromId\":\"Id-123\",\"amount\":500}")).andExpect(status().isBadRequest());	  
  }

  @Test
  public void createTransferNoAmount() throws Exception {
	    this.mockMvc.perform(post("/v1/transfers").contentType(MediaType.APPLICATION_JSON)
	    	      .content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"Id-456\"}")).andExpect(status().isBadRequest());
  }
  
  @Test
  public void createTransferNegativeAmount() throws Exception {
	    this.mockMvc.perform(post("/v1/transfers").contentType(MediaType.APPLICATION_JSON)
	    	      .content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"Id-456\",\"amount\":-500}")).andExpect(status().isBadRequest());
  }
  
  @Test
  public void createTransferNegativeBalance() throws Exception {
	    Account account = new Account("Id-123", new BigDecimal("1000"));
	    this.accountsService.createAccount(account);
	    
	    account = new Account("Id-456", new BigDecimal("1000"));
	    this.accountsService.createAccount(account);
	    
	    this.mockMvc.perform(post("/v1/transfers").contentType(MediaType.APPLICATION_JSON)
	  	      .content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"Id-456\",\"amount\":1001}")).andExpect(status().isBadRequest());
  }
}
