package com.medminder.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateSchedulesRequest(
    @NotEmpty
    @Size(max = 4)
    List<@Valid CreateScheduleRequest> schedules
) {
}
