package co.com.crediya.usecase.listloanapplicationswithdetails;

import co.com.crediya.common.PageResponse;
import co.com.crediya.contract.ReactiveUseCase;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ListLoanApplicationsWithDetailsUseCase
        implements ReactiveUseCase<
            ListLoanApplicationsWithDetailsUseCaseInput,
            Mono<PageResponse<LoanApplication>>>
{

    private final LoanApplicationRepository loanApplicationRepository;
    private final UserRepository userRepository;

    @Override
    public Mono<PageResponse<LoanApplication>> execute(ListLoanApplicationsWithDetailsUseCaseInput input) {
        var content = loanApplicationRepository.findAllWithDetails(
                input.identityDocument(),
                input.statusCode(),
                input.loanTypeCode(),
                input.autoApproval(),
                input.page().pageNumber(),
                input.page().pageSize()
        );

        var usersMap = content
                .map(LoanApplication::getIdentityDocument)
                .distinct()
                .collectList()
                .flatMapMany(userRepository::findAllByIdentityDocuments)
                .collectMap(User::getIdentityDocument, u -> u);

        var total = loanApplicationRepository.countByFilters(
                input.identityDocument(),
                input.statusCode(),
                input.loanTypeCode(),
                input.autoApproval()
        );

        return usersMap.flatMap(users -> {
            var loanApplicationsWithUsers = content.map(loanApplication -> {
                var user = users.get(loanApplication.getIdentityDocument());
                loanApplication.setUser(user);

                return loanApplication;
            });

            return Mono.just(PageResponse.of(loanApplicationsWithUsers, total, input.page()));
        });
    }
}
