package ssafy.batt.api.service.transfer.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ssafy.batt.api.service.transfer.info.TransferParticipantInfo;
import ssafy.batt.domain.booking.Booking;
import ssafy.batt.domain.transfer.Status;
import ssafy.batt.domain.transfer.Transfer;

@Getter
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class TransferDetailResponse {

  private Long transferId;

  private Integer currentHighestBid;

  private Integer price;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate performanceDate;

  @JsonFormat(pattern = "HH:mm")
  private LocalTime performanceTime;

  private List<String> seatNumber;

  private Instant transferEndDateTime;

  private Status status;

  private Long highestBidderId;

  private Long transferSellerId;

  private TransferDetailResponse(Transfer transfer, Booking booking) {
    this.transferId = transfer.getId();
    this.currentHighestBid = transfer.getCurrentHighestBid();
    this.transferSellerId = transfer.getSeller().getId();
    this.price = booking.getTotalAmount();
    this.performanceDate = booking.getPerformanceSchedule().getPerformanceDate();
    this.performanceTime = booking.getPerformanceSchedule().getPerformanceTime();
    this.seatNumber = booking.getBookingSeats()
        .stream()
        .map(bookingSeat -> bookingSeat.getSeat().generateFormattingSeatNumber())
        .toList();
    this.transferEndDateTime = transfer.getTransferEndDateTime();
    this.status = transfer.getStatus();
  }

  private TransferDetailResponse(Transfer transfer, Booking booking, TransferParticipantInfo participantInfo) {
    this(transfer, booking);
    this.highestBidderId = participantInfo.highestBidderId();
    this.transferSellerId = participantInfo.transferSellerId();
  }

  public static TransferDetailResponse from(Transfer transfer, Booking booking) {
    return new TransferDetailResponse(transfer, booking);
  }

  public static TransferDetailResponse from(Transfer transfer, Booking booking, TransferParticipantInfo participantInfo) {
    return new TransferDetailResponse(transfer, booking, participantInfo);
  }
}