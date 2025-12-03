package com.a506.batticket.presentation.ticket

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.a506.batticket.databinding.ItemTicketQrBinding
import com.a506.batticket.domain.model.TicketModel
import com.a506.batticket.util.QRCodeGenerator

class TicketPagerAdapter(private val tickets: List<TicketModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private fun generateQRCode(data: String, imageView: ImageView) {
        try {
            // 실제 QR 코드 생성
            val qrBitmap = QRCodeGenerator.generateTicketQRCode(data)
            
            if (qrBitmap != null) {
                // QR 코드 이미지 설정
                imageView.setImageBitmap(qrBitmap)
                imageView.setBackgroundColor(Color.WHITE) // QR 코드 배경
                imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            } else {
                // QR 코드 생성 실패 시 대체 UI
                setFallbackQRDisplay(imageView, data)
            }
        } catch (e: Exception) {
            // 예외 발생 시 대체 UI
            setFallbackQRDisplay(imageView, data)
        }
    }
    
    /**
     * QR 코드 생성 실패 시 대체 UI 표시
     */
    private fun setFallbackQRDisplay(imageView: ImageView, data: String) {
        // QR 코드 생성에 실패한 경우 회색 배경에 텍스트 표시
        imageView.setBackgroundColor(Color.LTGRAY)
        imageView.setImageDrawable(null)
        imageView.scaleType = ImageView.ScaleType.CENTER
        // 실제로는 여기서 TextView를 overlay하거나 다른 방식으로 예약번호 표시
    }
    inner class TicketViewHolder(val binding: ItemTicketQrBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ticket: TicketModel) {

            binding.reservationNumberText.text = ticket.bookingNumber
            binding.seatText.text = ticket.seat
            binding.priceText.text = ticket.price
            // 티켓 예약번호로 QR 코드 생성 및 표시
            generateQRCode(ticket.bookingNumber, binding.qrCodeImageView)
        }
//        val qrCodeImageView: ImageView = itemView.findViewById(R.id.qrCodeImageView)
//        val reservationNumberText: TextView = itemView.findViewById(R.id.reservationNumberText)
//        val seatText: TextView = itemView.findViewById(R.id.seatText)
//        val priceText: TextView = itemView.findViewById(R.id.priceText)
//        val statusText: TextView = itemView.findViewById(R.id.statusText)
    }

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ticket_qr, parent, false)
//        return TicketViewHolder(view)
//    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
    TicketViewHolder(ItemTicketQrBinding.inflate(
        LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TicketViewHolder).bind(tickets[position])
    }

    override fun getItemCount(): Int = tickets.size

}