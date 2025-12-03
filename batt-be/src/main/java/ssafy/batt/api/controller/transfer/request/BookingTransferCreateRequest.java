package ssafy.batt.api.controller.transfer.request;

import java.time.Instant;

public record BookingTransferCreateRequest(
    Instant transferEndDateTime
) {

}