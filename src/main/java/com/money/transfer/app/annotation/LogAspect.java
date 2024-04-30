package com.money.transfer.app.annotation;

import com.money.transfer.app.dto.TransferRequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class LogAspect {

    @Pointcut("(@within(LogRequestCourse) || @annotation(LogRequestCourse)) && args(requestBody)")
    public void logRequest(TransferRequestBody requestBody) {}

    @Before(value = "logRequest(requestBody)", argNames = "requestBody")
    public void logBeforeMethod(TransferRequestBody requestBody) {
        log.info("Initiating transfer from account {} to account {} for amount {} {}",
                requestBody.getSourceAccountId(), requestBody.getTargetAccountId(),
                requestBody.getAmount(), requestBody.getCurrency());
    }

    @AfterReturning(pointcut = "logRequest(requestBody)", argNames = "requestBody")
    public void logAfterMethodReturning(TransferRequestBody requestBody) {
        log.info("Transfer of {}{} from account {} to account {} completed successfully.",
                requestBody.getAmount(), requestBody.getCurrency(),
                requestBody.getSourceAccountId(), requestBody.getTargetAccountId());
    }

    @AfterThrowing(pointcut = "logRequest(requestBody)", throwing = "ex", argNames = "requestBody,ex")
    public void logAfterThrowingException(TransferRequestBody requestBody, Throwable ex) {
        log.error("{} during transfer from account {} to account {}",
                ex.getClass().getSimpleName(), requestBody.getSourceAccountId(), requestBody.getTargetAccountId());
        log.error(ex.getMessage());
    }
}
