# The following change is required for RDK 2019q2:
BBCLASSEXTEND = "native nativesdk"
RDEPENDS_${PN}_class-native = " "
PACKAGES_class-nativesdk = "${PN} ${PN}-dbg ${PN}-dev ${PN}-doc ${PN}-staticdev ${PN}-switch"
RDEPENDS_${PN}_class-nativesdk = " "
RDEPENDS_${PN}-switch_class-nativesdk = " "
