SUMMARY = "OpenSync"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=df3f42ef5870da613e959ac4ecaa1cb8"

PR = "r0"

inherit python3native

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

DEPENDS = "libev libgpg-error wireless-tools openssl jansson libtool mosquitto openvswitch protobuf-c dbus libpcap openvswitch-native hal-wifi halinterface mesh-agent python3-kconfiglib-native coreutils-native python3-jinja2-native python3-markupsafe-native libmxml"

# Please specify wifi HAL library here
DEPENDS += "hal-wifi-cfg80211"

RDEPENDS_${PN} += "openvswitch"

inherit python3native

SRCREV_core ?= "${AUTOREV}"
SRCREV_platform ?= "${AUTOREV}"
SRCREV_vendor ?= "${AUTOREV}"

OPENSYNC_CORE_REPO_PATH ?= "git://git@github.com/plume-design/opensync.git"
OPENSYNC_CORE_REPO_PROTOCOL ?= "https"
OPENSYNC_CORE_BRANCH ?= "osync_3.2.7"

OPENSYNC_CORE_URI ?= "${OPENSYNC_CORE_REPO_PATH};protocol=${OPENSYNC_CORE_REPO_PROTOCOL};branch=${OPENSYNC_CORE_BRANCH};name=core;destsuffix=git/core"
OPENSYNC_CORE_URI += "file://0002-Fix-missing-function-dhcp_option_name.patch"
OPENSYNC_CORE_URI += "file://0004-Initialize-DHCP-client-null-impl-fields.patch"
OPENSYNC_CORE_URI += "file://0005-Remove-target_bsal_client_measure-from-core.patch"
OPENSYNC_CORE_URI += "file://0006-Fix-conflict-with-yocto-kernel-tools-kconfiglib.patch"
OPENSYNC_CORE_URI += "file://0001-Create-CONFIG-option-for-private-key.patch"
OPENSYNC_CORE_URI += "file://0008-Add-default-route_sub.sh.patch"
OPENSYNC_CORE_URI += "file://0009-add-journal-logger.patch"

OPENSYNC_PLATFORM_REPO_PATH ?= "git://git@github.com/plume-design/opensync-platform-rdk.git"
OPENSYNC_PLATFORM_REPO_PROTOCOL ?= "https"
OPENSYNC_PLATFORM_BRANCH ?= "osync_3.2.7"

OPENSYNC_PLATFORM_URI ?= "${OPENSYNC_PLATFORM_REPO_PATH};protocol=${OPENSYNC_PLATFORM_REPO_PROTOCOL};branch=${OPENSYNC_PLATFORM_BRANCH};name=platform;destsuffix=git/platform/rdk"

OPENSYNC_VENDOR_REPO_PATH ?= "git://git@github.com/plume-design/opensync-vendor-rdk-template.git"
OPENSYNC_VENDOR_REPO_PROTOCOL ?= "https"
OPENSYNC_VENDOR_BRANCH ?= "osync_3.2.7"
OPENSYNC_VENDOR_SUFFIX ?= "turris"

OPENSYNC_VENDOR_URI ?="${OPENSYNC_VENDOR_REPO_PATH};protocol=${OPENSYNC_VENDOR_REPO_PROTOCOL};branch=${OPENSYNC_VENDOR_BRANCH};name=vendor;destsuffix=git/vendor/${OPENSYNC_VENDOR_SUFFIX}"

# The OpenSync Service Provider repo has the deployment-specific wifi credentials (for extender) and certificates.
OPENSYNC_SERVICE_PROVIDER_URI ?= "file://service.patch;patchdir=${WORKDIR}/git/"

SRC_URI = "${OPENSYNC_CORE_URI} ${OPENSYNC_PLATFORM_URI} ${OPENSYNC_VENDOR_URI} ${OPENSYNC_SERVICE_PROVIDER_URI}"
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
    ${prefix}/${PN}/* \
    ${prefix}/${PN}/.* \
"

FILES_${PN}-extras = " \
    ${prefix}/${PN}/scripts/start.d/* \
    ${prefix}/${PN}/scripts/stop.d/* \
    ${prefix}/${PN}/scripts/opensync_functions.sh \
    ${prefix}/${PN}/scripts/proc_mem.sh \
    ${prefix}/${PN}/scripts/vlan.funcs.sh \
    ${prefix}/${PN}/scripts/functions.d/10_kconfig.sh \
    ${prefix}/${PN}/bin/start.sh \
    ${prefix}/${PN}/bin/stop.sh \
"

FILES_${PN}-dbg = " \
    ${prefix}/src/debug \
    ${prefix}/${PN}/**/.debug \
    ${prefix}/opensync/**/**/.debug \
"

FILES_${PN}-dev = " \
    ${prefix}/${PN}/tools/wifi_hal_test \
    ${prefix}/${PN}/tools/band_steering_test \
    ${prefix}/${PN}/tools/wifi_hal_tool \
"

PACKAGES = "${PN}-dbg ${PN}-dev ${PN}-extras ${PN}"
