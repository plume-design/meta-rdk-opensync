FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://dont_install_kconfiglib.patch"

SRCREV = "7604d2d1a49d88e38d5b5854209dc1435b790893"

DEPENDS += "python3-kconfiglib-native"

LIC_FILES_CHKSUM = "file://git/Kconfiglib/LICENSE.txt;md5=448ee4da206e9be8f4a79c48e0741295"
