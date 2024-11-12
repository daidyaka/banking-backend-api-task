package com.trufanov.service;

import com.trufanov.dto.request.CreateAccountRequestDto;

public interface AccountService {

    Long createAccount(CreateAccountRequestDto request);

}
