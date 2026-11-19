package com.bank.balance.api;

import com.bank.balance.service.BalanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/balances")
public class BalanceController {
    private final BalanceService service;

    public BalanceController(BalanceService service) {
        this.service = service;
    }

    @PostMapping("/enquiry")
    public ResponseEntity<BalanceResponse> enquire(
            @RequestHeader("X-FeatureName") String featureName,
            @RequestHeader("X-ServiceCode") String serviceCode,
            @RequestHeader("X-ServiceName") String serviceName,
            @RequestHeader("X-ChannelCategory") String channelCategory,
            @RequestHeader("X-ChannelCode") String channelCode,
            @RequestHeader("X-ChannelName") String channelName,
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody BalanceRequest request) {
        return ResponseEntity.ok(service.enquire(request));
    }
}
