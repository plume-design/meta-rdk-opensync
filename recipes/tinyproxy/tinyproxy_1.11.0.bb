SUMMARY = "Lightweight http(s) proxy daemon"
HOMEPAGE = "https://tinyproxy.github.io/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/${PV}/${BP}.tar.gz \
           file://130-support-TPROXY-socket.patch \
           file://140-support-xtinyproxy-mac-header.patch \
           file://150-add-disable-http-errors-conf.patch \
           file://160-enable-ssl-proxy.patch"

SRC_URI[sha256sum] = "20f74769e40144e4d251d2977cc4c40d2d428a2bec8c1b8709cd07315454baef"

DEPENDS += "libmnl openssl"
RDEPENDS_${PN} += "libmnl openssl"

inherit autotools

EXTRA_OECONF += " \
    --enable-filter \
    --enable-transparent \
    --disable-regexcheck \
    --enable-reverse \
    --enable-upstream \
    --enable-xtinyproxy \
    --disable-manpage-support \
    --enable-xtinyproxymac \
    --enable-disablehttperrors \
    "
