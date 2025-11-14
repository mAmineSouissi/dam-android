package tn.esprit.dam_android.models.scan

data class ScanResponse(
    val scanId: String,
    val threatsFound: Int,
    val riskScore: Int
)