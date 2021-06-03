SUMMARY = "OpenSync"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=df3f42ef5870da613e959ac4ecaa1cb8"

PR = "r0"

inherit python3native

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

DEPENDS = "libev libgpg-error wireless-tools openssl jansson libtool mosquitto openvswitch protobuf-c dbus libpcap openvswitch-native hal-wifi halinterface mesh-agent python3-kconfiglib-native coreutils-native python3-jinja2-native python3-markupsafe-native libmxml"
RDEPENDS_${PN} += "openvswitch"

inherit python3native

SRCREV_core ?= "${AUTOREV}"
SRCREV_platform ?= "${AUTOREV}"
SRCREV_vendor ?= "${AUTOREV}"

CORE_URI ?= "git://git@github.com/plume-design/opensync.git;protocol=ssh;branch=osync_2.4.1;name=core;destsuffix=git/core"
CORE_URI += "file://0001-Fix-compilation-flags.patch"
CORE_URI += "file://0002-Fix-missing-function-dhcp_option_name.patch"
CORE_URI += "file://0003-Fix-compilation-OSN-route-config.patch"
CORE_URI += "file://0004-Initialize-DHCP-client-null-impl-fields.patch"
CORE_URI += "file://0005-Remove-target_bsal_client_measure-from-core.patch"
CORE_URI += "file://0006-Fix-conflict-with-yocto-kernel-tools-kconfiglib.patch"

PLATFORM_URI ?= "git://git@github.com/plume-design/opensync-platform-rdk.git;protocol=ssh;branch=osync_2.4.1;name=platform;destsuffix=git/platform/rdk"
SERVICE_PROVIDER_URI ?= ""
VENDOR_URI ?= ""

SRC_URI = "${CORE_URI} ${PLATFORM_URI} ${VENDOR_URI} ${SERVICE_PROVIDER_URI}"
SRCREV_FORMAT ?= "core_platform_vendor"

S = "${WORKDIR}/git/core"

PREMIRRORS = ""
MIRRORS = ""
PARALLEL_MAKE = ""

EXTRA_OEMAKE = "MAKEFLAGS="
EXTRA_OEMAKE += "RDK_TARGET_ARCH=${TARGET_ARCH}"
EXTRA_OEMAKE += "RDK_MACHINE=${MACHINE}"
EXTRA_OEMAKE += "RDK_DISTRO=${DISTRO}"
EXTRA_OEMAKE += "PLATFORM_SDK=RDK"
EXTRA_OEMAKE += "TARGET=RDKB"
EXTRA_OEMAKE += "${PLUME_MAKE_ARGS}"

do_compile_prepend() {
    echo === pwd ===
    pwd
    echo === bb var ===
    echo SRCPV=${SRCPV}
    echo PV=${PV}
    echo S=${S}
    echo === env ===
    env
    echo === make ===
}

do_install_append() {
    bbnote make ${EXTRA_OEMAKE} INSTALL_DIR="${D}" install
    make ${EXTRA_OEMAKE} INSTALL_DIR=${D} install || bbfatal "make install failed"
}

FILES_${PN} = " \
    ${prefix}/${PN}/* \
    ${prefix}/${PN}/.* \
"

FILES_${PN}-dbg = " \
    ${prefix}/src/debug \
    ${prefix}/${PN}/**/.debug \
    ${prefix}/opensync/**/**/.debug \
"

FILES_${PN}-dev = " \
    ${prefix}/${PN}/tools/wifi_hal_test \
    ${prefix}/${PN}/tools/band_steering_test \
"

PACKAGES = "${PN}-dbg ${PN}-dev ${PN}"
