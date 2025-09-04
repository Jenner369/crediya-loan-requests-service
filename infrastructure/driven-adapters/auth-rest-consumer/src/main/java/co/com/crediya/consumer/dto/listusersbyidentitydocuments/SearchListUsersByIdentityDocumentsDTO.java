package co.com.crediya.consumer.dto.listusersbyidentitydocuments;

import java.util.List;

public record SearchListUsersByIdentityDocumentsDTO(
        List<String> identityDocuments
) { }
