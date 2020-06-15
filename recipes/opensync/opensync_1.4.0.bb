SUMMARY = "OpenSync"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=df3f42ef5870da613e959ac4ecaa1cb8"

PR = "r4"

inherit python3native

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

DEPENDS = "libev libgpg-error wireless-tools openssl jansson libtool mosquitto openvswitch protobuf-c dbus libpcap openvswitch-native hal-wifi halinterface mesh-agent python3-kconfiglib-native coreutils-native"
RDEPENDS_${PN} += "openvswitch"

inherit python3native

SRCREV_core ?= "${AUTOREV}"
SRCREV_platform ?= "${AUTOREV}"
SRCREV_vendor ?= "${AUTOREV}"

CORE_URI ?= "git://git@github.com/plume-design/opensync.git;protocol=ssh;branch=osync_1.4.0.1;name=core;destsuffix=git/core"
CORE_URI += "file://0001-inet-start-dhcps-always.patch"
CORE_URI += "file://0002-inet-inject-br-wan-and-br-lan-handling.patch"
CORE_URI += "file://0003-Use-osync_hal-in-inet_gretap.patch"
CORE_URI += "file://0004-Use-osync_hal-in-inet_vlan.patch"
CORE_URI += "file://0005-Add-vlan-support.patch"
CORE_URI += "file://0006-Fix-conflict-with-yocto-kernel-tools-kconfiglib.patch"
CORE_URI += "file://0007-bm-fix-HWM-configuration.patch"

PLATFORM_URI ?= "git://git@github.com/plume-design/opensync-platform-rdk.git;protocol=ssh;branch=osync_1.4.0.3;name=platform;destsuffix=git/platform/rdk"
VENDOR_URI ?= ""

SRC_URI = "${CORE_URI} ${PLATFORM_URI} ${VENDOR_URI}"
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
    ${prefix}/sbin/* \
    ${prefix}/plume/* \
    ${prefix}/plume/.* \
"

FILES_${PN}-dbg = " \
    ${prefix}/src/debug \
    ${prefix}/plume/**/.debug \
"

PACKAGES = "${PN}-dbg ${PN}"
