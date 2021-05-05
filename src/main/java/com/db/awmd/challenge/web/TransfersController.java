package com.db.awmd.challenge.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.service.TransfersService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/transfer")
@Slf4j
public class TransfersController {
	
	  private final TransfersService transfersService;
	  
	  @Autowired
	  public TransfersController(TransfersService transfersService) {
	    this.transfersService = transfersService;
	  }
	
	  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<Object> transfer(@RequestBody @Valid Transfer transfer) {
	    log.info("Creating account {}", transfer);

	    try {
	    	this.transfersService.transfer(transfer);
	    } catch (NegativeBalanceException nbe) {
	      return new ResponseEntity<>(nbe.getMessage(), HttpStatus.BAD_REQUEST);
	    }

	    return new ResponseEntity<>(HttpStatus.CREATED);
	  }

}
