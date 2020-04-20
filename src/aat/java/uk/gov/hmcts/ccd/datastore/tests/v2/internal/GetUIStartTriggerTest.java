package uk.gov.hmcts.ccd.datastore.tests.v2.internal;

import static java.lang.Boolean.FALSE;
import static org.hamcrest.Matchers.*;
import static uk.gov.hmcts.ccd.datastore.tests.fixture.AATCaseType.CASE_TYPE;
import static uk.gov.hmcts.ccd.datastore.tests.fixture.AATCaseType.Event.CREATE;
import static uk.gov.hmcts.ccd.datastore.tests.fixture.AATCaseType.Event.UPDATE;
import static uk.gov.hmcts.ccd.datastore.tests.fixture.AATCaseType.Event.create;

import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ccd.datastore.tests.AATHelper;
import uk.gov.hmcts.ccd.datastore.tests.BaseTest;
import uk.gov.hmcts.ccd.datastore.tests.fixture.AATCaseBuilder.FullCase;
import uk.gov.hmcts.ccd.v2.V2;

@DisplayName("Get UI start trigger by case type and event ids")
class GetUIStartTriggerTest extends BaseTest {
    private static final String INVALID_CASE_TYPE_ID = "invalidCaseType";
    private static final String INVALID_EVENT_TRIGGER_ID = "invalidEvent";
    private static final String CREATE_NAME = "Create a new case";
    private static final String UPDATE_NAME = "Update";

    protected GetUIStartTriggerTest(AATHelper aat) {
        super(aat);
    }

    @Nested
    @DisplayName("Start case trigger")
    class StartCaseTrigger {

        @Test
        @DisplayName("should retrieve trigger when the case type and event exists")
        void shouldRetrieveWhenExists() {
            callGetStartTrigger(CASE_TYPE, CREATE)
                .when()
                .get("/internal/case-types/{caseTypeId}/event-triggers/{triggerId}")

                .then()
                .log().ifError()
                .statusCode(200)
                .assertThat()

                // Metadata
                .body("id", equalTo(CREATE))
                .body("event_token", is(not(isEmptyString())))
                .body("name", is(CREATE_NAME))
                .body("description", is(nullValue()))
                .body("case_id", is(nullValue()))
                .body("show_summary", is(true))
                .body("show_event_notes", is(nullValue()))
                .body("end_button_label", is(nullValue()))
                .body("can_save_draft", is(nullValue()))

                // Flexible data
                .body("case_fields", hasSize(16))
                .body("wizard_pages", hasSize(3))

                .rootPath("_links")
                .body("self.href", equalTo(String.format("%s/internal/case-types/%s/event-triggers/%s{?ignore-warning}", aat.getTestUrl(), CASE_TYPE, CREATE)));
        }

        @Test
        @DisplayName("should get 404 when case type does not exist")
        void should404WhenCaseTypeDoesNotExist() {
            callGetStartTrigger(INVALID_CASE_TYPE_ID, CREATE)
                .when()
                .get("/internal/case-types/{caseTypeId}/event-triggers/{triggerId}")

                .then()
                .statusCode(404);
        }

        @Test
        @DisplayName("should get 404 when event trigger does not exist")
        void should404WhenEventTriggerDoesNotExist() {
            callGetStartTrigger(CASE_TYPE, INVALID_EVENT_TRIGGER_ID)
                .when()
                .get("/internal/case-types/{caseTypeId}/event-triggers/{triggerId}")

                .then()
                .statusCode(404);
        }

        private RequestSpecification callGetStartTrigger(String caseTypeId, String eventId) {
            return asAutoTestCaseworker(FALSE)
                .get()
                .given()
                .pathParam("caseTypeId", caseTypeId)
                .pathParam("triggerId", eventId)
                .accept(V2.MediaType.UI_START_CASE_TRIGGER)
                .header("experimental", "true");
        }
    }

    @Nested
    @DisplayName("Start event trigger")
    class StartEventResult {
        private static final String INVALID_CASE_REFERENCE = "1234123412341234";
        private static final String NOT_FOUND_CASE_REFERENCE = "1234123412341238";

        @Test
        @DisplayName("should retrieve trigger when the case and event exists")
        void shouldRetrieveWhenExists() {
            // Prepare new case in known state
            final Long caseReference = create()
                .as(asAutoTestCaseworker())
                .withData(FullCase.build())
                .submitAndGetReference();

            callGetStartEventTrigger(String.valueOf(caseReference), UPDATE)
                .when()
                .get("/internal/cases/{caseId}/event-triggers/{triggerId}")

                .then()
                .log().ifError()
                .statusCode(200)
                .assertThat()

                // Metadata
                .body("id", equalTo(UPDATE))
                .body("event_token", is(not(isEmptyString())))
                .body("name", is(UPDATE_NAME))
                .body("description", is(nullValue()))
                .body("case_id", equalTo(caseReference.toString()))
                .body("show_summary", is(true))
                .body("show_event_notes", is(nullValue()))
                .body("end_button_label", is(nullValue()))
                .body("can_save_draft", is(nullValue()))

                // Flexible data
                .body("case_fields", hasSize(15))
                .body("wizard_pages", hasSize(3))

                .rootPath("_links")
                .body("self.href", equalTo(String.format("%s/internal/cases/%s/event-triggers/%s{?ignore-warning}", aat.getTestUrl(), caseReference, UPDATE)));
        }

        @Test
        @DisplayName("should get 400 when case reference invalid")
        void should400WhenCaseReferenceInvalid() {
            callGetStartEventTrigger(INVALID_CASE_REFERENCE, UPDATE)
                .when()
                .get("/internal/cases/{caseId}/event-triggers/{triggerId}")

                .then()
                .statusCode(400);
        }

        @Test
        @DisplayName("should get 404 when case does not exist")
        void should404WhenCaseDoesNotExist() {
            callGetStartEventTrigger(NOT_FOUND_CASE_REFERENCE, UPDATE)
                .when()
                .get("/internal/cases/{caseId}/event-triggers/{triggerId}")

                .then()
                .statusCode(404);
        }

        @Test
        @DisplayName("should get 404 when event trigger does not exist")
        void should404WhenEventTriggerDoesNotExist() {
            // Prepare new case in known state
            final Long caseReference = create()
                .as(asAutoTestCaseworker())
                .withData(FullCase.build())
                .submitAndGetReference();

            callGetStartEventTrigger(String.valueOf(caseReference), INVALID_EVENT_TRIGGER_ID)
                .when()
                .get("/internal/cases/{caseId}/event-triggers/{triggerId}")

                .then()
                .statusCode(404);
        }

        private RequestSpecification callGetStartEventTrigger(String caseId, String eventId) {
            return asAutoTestCaseworker(FALSE)
                .get()
                .given()
                .pathParam("caseId", caseId)
                .pathParam("triggerId", eventId)
                .accept(V2.MediaType.UI_START_EVENT_TRIGGER)
                .header("experimental", "true");
        }
    }
}
