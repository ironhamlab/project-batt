package com.a506.batticket.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter

/**
 * QR 코드 생성 유틸리티 클래스
 */
object QRCodeGenerator {
    
    /**
     * 티켓 정보를 기반으로 QR 코드를 생성합니다
     * 
     * @param ticketData 티켓 데이터 (예: 예약번호, 티켓ID 등)
     * @param width QR 코드 너비 (픽셀)
     * @param height QR 코드 높이 (픽셀)
     * @return QR 코드 비트맵 또는 null (생성 실패 시)
     */
    fun generateQRCode(ticketData: String, width: Int = 300, height: Int = 300): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(ticketData, BarcodeFormat.QR_CODE, width, height)
            matrixToBitmap(bitMatrix)
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 티켓 예약번호로 QR 코드 생성 (기본 크기 200x200)
     */
    fun generateTicketQRCode(bookingNumber: String): Bitmap? {
        // 실제 서비스에서는 더 복잡한 데이터 구조를 사용할 수 있습니다
        // 예: JSON 형태 {"ticketId": "T123", "booking": "B456", "seat": "A1", "date": "2024-01-01"}
        val ticketInfo = createTicketQRData(bookingNumber)
        return generateQRCode(ticketInfo, 200, 200)
    }
    
    /**
     * 티켓 정보를 QR 코드 데이터로 변환
     * 실제 서비스에서는 암호화나 서명을 추가할 수 있습니다
     */
    private fun createTicketQRData(bookingNumber: String): String {
        // 간단한 형태 (실제로는 JSON이나 더 구조화된 데이터 사용)
        return "BATT-TICKET:$bookingNumber:${System.currentTimeMillis()}"
    }
    
    /**
     * BitMatrix를 Bitmap으로 변환
     */
    private fun matrixToBitmap(matrix: BitMatrix): Bitmap {
        val width = matrix.width
        val height = matrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        
        return bitmap
    }
    
    /**
     * 더 복잡한 티켓 정보로 QR 코드 생성 (확장 가능)
     */
    fun generateAdvancedTicketQRCode(
        bookingNumber: String,
        performanceTitle: String,
        seatInfo: String,
        date: String
    ): Bitmap? {
        // JSON 형태로 더 많은 정보를 포함
        val ticketData = """
            {
                "app": "BATT",
                "booking": "$bookingNumber",
                "performance": "$performanceTitle",
                "seat": "$seatInfo",
                "date": "$date",
                "generated": ${System.currentTimeMillis()}
            }
        """.trimIndent()
        
        return generateQRCode(ticketData, 250, 250)
    }
}
