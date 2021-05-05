package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransfersService {
  private final String TRANSFER_FROM_MESSAGE = "Transfer sent to account %s with an amount of %s";
  private final String TRANSFER_TO_MESSAGE = "Transfer received from account %s with an amount of %s";
  
  
  @Getter
  private final AccountsRepository accountsRepository;
  
  private final NotificationService notificationService;

  @Autowired
  public TransfersService(AccountsRepository accountsRepository, NotificationService notificationService) {
    this.accountsRepository = accountsRepository;
    this.notificationService = notificationService;
  }
  
  public void transfer(Transfer transfer) {
      
	  validAmountValue(transfer.getAmount());
	  
	  validNegativeBalance(transfer);
	  
	  transferUpdateFromAccountAndSendNotification(transfer);
	  
	  transferUpdateToAccountAndSendNotification(transfer);
	  
  }
  
  private void validAmountValue(BigDecimal amount) {
	  if (amount.compareTo(BigDecimal.ZERO) == -1) {
	      throw new NegativeBalanceException(
	    	        "Negative balance in the request");
	  }
  }
  
  private void validNegativeBalance(Transfer transfer) {
	  Account accountFrom = accountsRepository.getAccount(transfer.getAccountFromId());
	  BigDecimal total = accountFrom.getBalance().subtract(transfer.getAmount());
	  if (total.compareTo(BigDecimal.ZERO) == -1) {
	      throw new NegativeBalanceException(
	    	        "Account id " + accountFrom.getAccountId() + " negative balance");
	  }
  }
  
  private void transferUpdateFromAccountAndSendNotification(Transfer transfer) {
	  Account account = accountsRepository.getAccount(transfer.getAccountFromId());
	  
	  BigDecimal total = account.getBalance().subtract(transfer.getAmount());
	  account.setBalance(total);
	  
	  String message = TRANSFER_FROM_MESSAGE.replaceFirst("%s", transfer.getAccountToId()).replace("%s", transfer.getAmount().toPlainString());
	  
	  sendNotification(account, message);
  }
  
  private void transferUpdateToAccountAndSendNotification(Transfer transfer) {
	  Account account = accountsRepository.getAccount(transfer.getAccountToId());

	  BigDecimal total = account.getBalance().add(transfer.getAmount());
	  account.setBalance(total);
	  
	  String message = TRANSFER_TO_MESSAGE.replaceFirst("%s", transfer.getAccountFromId()).replace("%s", transfer.getAmount().toPlainString());
	  
	  sendNotification(account, message);
  }
  
  
  private void sendNotification(Account account, String message) {
	  log.debug(message);
	  notificationService.notifyAboutTransfer(account, message);
  }
  
}
