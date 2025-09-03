package co.com.crediya.usecase.listloanapplicationswithdetails;

import co.com.crediya.common.PageRequest;

public record ListLoanApplicationsWithDetailsUseCaseInput (
        String identityDocument,
        String statusCode,
        String loanTypeCode,
        Boolean autoApproval,
        PageRequest page
) { }