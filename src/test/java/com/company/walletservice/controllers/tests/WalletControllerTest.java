package com.company.walletservice.controllers.tests;

import com.company.walletservice.controllers.rest.WalletRestController;
import com.company.walletservice.models.requests.AddMoneyRequest;
import com.company.walletservice.models.responses.BalanceResponse;
import com.company.walletservice.models.responses.ReferenceResponse;
import com.company.walletservice.services.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletRestController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetBalance_ShouldReturnBalance() throws Exception {
        when(walletService.getBalance(1L)).thenReturn(new BalanceResponse(BigDecimal.valueOf(4000)));

        mockMvc.perform(get("/wallet/1/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(4000));
    }

    @Test
    void testAddMoney_ShouldReturnReferenceId() throws Exception {
        AddMoneyRequest request = new AddMoneyRequest(BigDecimal.valueOf(1000));

        when(walletService.addMoney(eq(1L), any())).thenReturn(new ReferenceResponse("ref-abc-123"));

        mockMvc.perform(put("/wallet/1/add-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceId").value("ref-abc-123"));
    }

    @Test
    void testAddMoney_WithNegativeAmount_ShouldStillReturnReference() throws Exception {
        AddMoneyRequest request = new AddMoneyRequest(BigDecimal.valueOf(-500));

        when(walletService.addMoney(eq(2L), any())).thenReturn(new ReferenceResponse("ref-negative"));

        mockMvc.perform(put("/wallet/2/add-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceId").value("ref-negative"));
    }

    @Test
    void testGetBalance_WithMissingUserId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/wallet/abc/balance")).andExpect(status().isBadRequest());
    }
}
