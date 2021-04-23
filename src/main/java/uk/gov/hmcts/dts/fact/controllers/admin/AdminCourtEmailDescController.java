package uk.gov.hmcts.dts.fact.controllers.admin;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    path = "courts/{slug}/emailDescription",
    produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class AdminCourtEmailDescController {


}
