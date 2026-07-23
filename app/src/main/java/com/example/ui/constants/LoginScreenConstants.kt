package com.example.ui.constants

/**
 * Constants for login screen configuration and strings.
 */
object LoginScreenConstants {
    // Connection types
    const val XTREAM_MODE = "XTREAM"
    const val M3U_MODE = "M3U"
    
    // Default values
    const val DEFAULT_CONNECTION_NAME = "Baddbeatz Premium Vault"
    const val EMPTY_STRING = ""
    
    // Virtual demo constants
    const val VIRTUAL_DEMO_URL = "http://virtueel-netwerk.local:8080"
    const val VIRTUAL_DEMO_USER = "VirtueleGebruiker"
    const val VIRTUAL_DEMO_PASSWORD = "DemoPassword"
    const val VIRTUAL_DEMO_LATENCY_MS = 800L
    const val DEMO_M3U_OUTPUT = "http://virtueel-netwerk.local:8080/get.php?output=ts"
    
    // Xtream presets
    val XTREAM_PRESETS = listOf(
        "http://line.tanvi.xyz",
        "http://line.tivi-ott.net",
        "Custom URL"
    )
    
    // Error messages
    const val ERROR_USERNAME_PASSWORD_REQUIRED = "Gebruikersnaam en wachtwoord mogen niet leeg zijn."
    const val ERROR_M3U_URL_REQUIRED = "M3U Playlist URL mag niet leeg zijn."
    const val ERROR_TEST_CREDENTIALS_REQUIRED = "Vul gebruikersnaam en wachtwoord in om de verbinding te testen."
    const val ERROR_TEST_M3U_URL_REQUIRED = "Voer een M3U Playlist URL in om de verbinding te testen."
    const val ERROR_CONNECTION_FAILED = "Verbindingstest mislukt, controleer uw inloggegevens."
    const val ERROR_M3U_VALIDATION_FAILED = "Valideer verbinding is mislukt. Controleer de URL."
    
    // Success messages
    const val SUCCESS_XTREAM_CONNECTED = "Succesvol verbonden met Xtream server"
    const val SUCCESS_M3U_VALIDATED = "M3U Playlist met succes gevalideerd en verbonden!"
    const val SUCCESS_VIRTUAL_DEMO = "Virtuele demo-parameters geladen!"
    
    // Loading messages
    const val MSG_CONNECTING = "Verbinding maken..."
    const val MSG_VALIDATE_CONNECT = "Valideer & Verbind"
    const val MSG_TESTING_CONNECTION = "Bezig met API-verbinding testen..."
    const val MSG_DEMO_LOADING = "Demo laden..."
    
    // Tab labels
    const val TAB_SECURITY = "Security"
    const val TAB_QR_SYNC = "Sync"
    const val ICON_SECURITY = "🔒 Security"
    const val ICON_QR_SYNC = "📱 QR Sync"
    
    // UI Labels
    const val LABEL_CONNECTION_METHOD = "Verbindingsmethode selecteren"
    const val LABEL_XTREAM_CODES = "Xtream Codes API"
    const val LABEL_M3U_PLAYLIST = "M3U Playlist URL"
    const val LABEL_CONNECTION_NAME = "Verbindingsnaam / Profiel"
    const val LABEL_RECOMMENDED_SERVERS = "Aanbevolen Servers (Xtream Codes compatible)"
    const val LABEL_SERVER_URL = "Server URL (inclusief poort)"
    const val LABEL_USERNAME = "Xtream Codes Gebruikersnaam"
    const val LABEL_PASSWORD = "Xtream Codes Wachtwoord"
    const val LABEL_M3U_URL = "M3U Playlist URL"
    const val LABEL_M3U_USERNAME = "M3U Gebruikersnaam (Optioneel)"
    const val LABEL_M3U_PASSWORD = "M3U Wachtwoord (Optioneel)"
    const val LABEL_SHOW_PASSWORD = "Toon "
    const val LABEL_HIDE_PASSWORD = "Verberg "
    
    // Placeholders
    const val PLACEHOLDER_SERVER_URL = "http://example.com:8080"
    const val PLACEHOLDER_M3U_URL = "https://yourprovider.com/get.php?auth=xyz"
    
    // SSL/HTTPS messages
    const val MSG_SSL_SECURE = "🔒 SSL-versleuteling actief: metadata-transmissie is beveiligd."
    const val MSG_HTTP_UNSECURE = "⚠️ Onveilig: Verkeer is onversleuteld over HTTP. Gebruik bij voorkeur HTTPS."
    const val MSG_DEMO_TIP = "💡 Geen M3U-afspeellijst? Start de Virtuele Demo om de app direct met voorbeeldkanalen te ervaren."
    
    // Button labels
    const val BTN_TEST_CONNECTION = "Verbinding Testen"
    const val BTN_VIRTUAL_DEMO = "Virtuele Demo"
    const val BTN_CANCEL = "Annuleren"
    
    // Screen titles
    const val TITLE_XTREAM_LOGIN = "Connect Xtream Codes"
    const val TITLE_M3U_LOGIN = "Connect M3U Playlist"
    
    // Screen descriptions
    const val DESC_XTREAM = "Injecteer uw legale private streaming metadata bibliotheek dynamisch."
    const val DESC_M3U = "Laad en valideer uw M3U-afspeellijst met beveiligde SSL/HTTPS verbindingstests."
    
    // Security tab content
    const val SECURITY_TITLE = "Baddbeatz Secure Sync"
    const val SECURITY_DESC = "Your digital environment handles metadata safely and acts purely as an advanced render pipeline for authorized stream sources."
    const val SECURITY_ENCRYPTION_TITLE = "🔒 Secured Metadata Transit"
    const val SECURITY_ENCRYPTION_DESC = "Connections are encrypted where supported. Playback logs are stored entirely in memory."
    const val SECURITY_COMPLIANT_TITLE = "🛡️ Compliant Platform"
    const val SECURITY_COMPLIANT_DESC = "This client conforms fully to Amazon Appstore and Google Play requirements for personal player applications."
    
    // Verification messages
    const val MSG_VERIFICATION_SUCCESSFUL = "SSL & API Verificatie Geslaagd!"
    const val MSG_VERIFICATION_FAILED = "Verificatie Mislukt"
    const val MSG_M3U_FORMAT_VALID = "M3U Formaat Gevalideerd ✓"
    const val MSG_M3U_FORMAT_UNKNOWN = "Onbekend formaat"
    const val MSG_VERIFICATION_SUGGESTION = "Suggestie: Start de Virtuele Demo of controleer de URL."
    
    // Connection status
    const val STATUS_CONNECTION = "Verbindingsstatus: HTTP"
    const val STATUS_LATENCY = "Server Latency (RTT):"
    const val STATUS_CONTENT_TYPE = "Inhoudstype:"
    const val STATUS_PLAYLIST_STRUCTURE = "Playlist structuur:"
    const val STATUS_ERROR_TYPE = "Fouttype:"
    
    // Icon descriptions
    const val DESC_SELECTED = "Geselecteerd"
    const val DESC_SSL_SECURE = "SSL Secure"
    const val DESC_HTTP_UNSECURE = "HTTP Unsecure"
    const val DESC_SUCCESS = "Success"
    const val DESC_ERROR = "Error"
    
    // Timing constants (milliseconds)
    const val DELAY_DEMO_VERIFICATION = 800L
}
