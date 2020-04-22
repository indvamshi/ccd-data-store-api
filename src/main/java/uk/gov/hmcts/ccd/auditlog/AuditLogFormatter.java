package uk.gov.hmcts.ccd.auditlog;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class AuditLogFormatter {

    private static final String TAG = "CLA-CCD";
    private static final String COMMA = ",";
    private static final String COLON = ":";

    public String format(AuditEntry entry) {
        return new StringBuilder(TAG)
            .append(" ")
            .append(entry.getDateTime())
            .append(" operationType:" + entry.getOperationType())
            .append(getPair("caseId", entry.getCaseId()))
            .append(getPair("idamId", entry.getIdamId()))
            .append(getPair("invokingService", entry.getInvokingService()))
            .append(getPair("endpointCalled", entry.getHttpMethod() + " " + entry.getPath()))
            .append(getPair("operationOutcome", String.valueOf(entry.getHttpStatus())))
            .append(getPair("caseType", entry.getCaseType()))
            .append(getPair("jurisdiction", entry.getJurisdiction()))
            .append(getPair("eventSelected", entry.getEventSelected()))
            .append(getPair("idamIdOfTarget", entry.getTargetIdamId()))
            .append(getPair("targetCaseRoles", commaSeparatedList(entry.getTargetCaseRoles())))
            .append(getPair("X-Request-ID", entry.getRequestId()))
            .toString();
    }

    private String getPair(String label, String value) {
        return isNotBlank(value) ? COMMA + label + COLON + value : "";
    }

    private String commaSeparatedList(List<String> list) {
        return list.stream().map(String::toString).collect(Collectors.joining(COMMA));
    }

}