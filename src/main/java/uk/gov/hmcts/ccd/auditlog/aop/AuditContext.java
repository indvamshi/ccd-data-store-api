package uk.gov.hmcts.ccd.auditlog.aop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import uk.gov.hmcts.ccd.auditlog.OperationType;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder(builderMethodName = "auditContextWith")
public class AuditContext {

    private String caseId;
    private String caseType;
    private String jurisdiction;
    private String eventName;
    private String targetIdamId;
    private List<String> targetCaseRoles;
    private OperationType operationType;
}