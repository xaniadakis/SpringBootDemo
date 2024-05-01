package com.money.transfer.app.annotation;

import com.money.transfer.app.dto.TransferRequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * Implements centralized logging for transactional operations using AOP.
 *
 * Utilizes Spring AOP to intercept transaction methods
 * before,
 * after normally returning and
 * after throwing exception scenarios
 * to log useful information about the course of the current transaction request.
 */
@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class LogAspect {

    /**
     * Defines a pointcut that targets
     * methods annotated with {@link LogRequestCourse}
     * and methods within classes annotated with {@link LogRequestCourse},
     * which also accept a {@link TransferRequestBody}.
     *
     * @param requestBody The details of the transaction request
     */
    @Pointcut("(@within(LogRequestCourse) || @annotation(LogRequestCourse)) && args(requestBody)")
    public void logRequest(TransferRequestBody requestBody) {}

    /**
     * Executes before the target method runs.
     * Logs an informational message indicating the initiation of a transfer.
     *
     * @param requestBody The details of the transaction request
     */
    @Before(value = "logRequest(requestBody)", argNames = "requestBody")
    public void logBeforeMethod(TransferRequestBody requestBody) {
        log.info("Initiating transfer from account {} to account {} for amount {} {}",
                requestBody.getSourceAccountId(), requestBody.getTargetAccountId(),
                requestBody.getAmount(), requestBody.getCurrency());
    }

    /**
     * Executes only if the target method completes successfully.
     * Logs a success message once the transaction is completed.
     *
     * @param requestBody The details of the transaction request
     */
    @AfterReturning(pointcut = "logRequest(requestBody)", argNames = "requestBody")
    public void logAfterMethodReturning(TransferRequestBody requestBody) {
        log.info("Transfer of {}{} from account {} to account {} completed successfully.",
                requestBody.getAmount(), requestBody.getCurrency(),
                requestBody.getSourceAccountId(), requestBody.getTargetAccountId());
    }

    /**
     *  Captures any exceptions thrown by the target method.
     *  Logs an error message containing the exception type and message.
     *
     * @param requestBody The details of the transaction request
     */
    @AfterThrowing(pointcut = "logRequest(requestBody)", throwing = "ex", argNames = "requestBody,ex")
    public void logAfterThrowingException(TransferRequestBody requestBody, Throwable ex) {
        log.error("{} during transfer from account {} to account {}",
                ex.getClass().getSimpleName(), requestBody.getSourceAccountId(), requestBody.getTargetAccountId());
        log.error(ex.getMessage());
    }
}
