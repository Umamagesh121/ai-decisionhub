package com.aidecisionhub.backend.controller;

import com.aidecisionhub.backend.dto.RequestTraceResponse;
import com.aidecisionhub.backend.dto.SubmitRequestRequest;
import com.aidecisionhub.backend.service.RequestOrchestratorService;
import com.aidecisionhub.backend.service.TraceQueryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/requests")
public class RequestController {

    private final RequestOrchestratorService requestOrchestratorService;
    private final TraceQueryService traceQueryService;

    public RequestController(RequestOrchestratorService requestOrchestratorService, TraceQueryService traceQueryService) {
        this.requestOrchestratorService = requestOrchestratorService;
        this.traceQueryService = traceQueryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestTraceResponse submitRequest(@Valid @RequestBody SubmitRequestRequest request) {
        return requestOrchestratorService.submitAndExecute(request);
    }

    @GetMapping("/{id}")
    public RequestTraceResponse getRequestTrace(@PathVariable UUID id) {
        return traceQueryService.getTrace(id);
    }

    @GetMapping("/{id}/tasks")
    public Object getRequestTasks(@PathVariable UUID id) {
        return traceQueryService.getTrace(id).tasks();
    }
}
