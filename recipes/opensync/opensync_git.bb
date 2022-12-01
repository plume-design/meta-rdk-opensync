SUMMARY = "OpenSync"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=df3f42ef5870da613e959ac4ecaa1cb8"

PR = "r0"

inherit python3native
inherit systemd

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

DEPENDS = "libev libgpg-error wireless-tools openssl jansson libtool mosquitto openvswitch protobuf-c protobuf-c-native dbus libpcap openvswitch-native hal-wifi halinterface mesh-agent python3-kconfiglib-native coreutils-native python3-jinja2-native python3-markupsafe-native libmxml libnl curl libmnl python3-pydot-native rdk-logger"

DEPENDS_remove = "${@bb.utils.contains('DISTRO_FEATURES', 'extender', 'mesh-agent ', '', d)}"

RDEPENDS_${PN} += "openvswitch libnl"

inherit python3native

SRCREV_core ?= "${AUTOREV}"
SRCREV_platform ?= "${AUTOREV}"
SRCREV_vendor ?= "${AUTOREV}"
SRCREV_service-provider ?= "${AUTOREV}"

OPENSYNC_DEFAULT_PROTOCOL ?= "https"
OPENSYNC_DEFAULT_BRANCH ?= "osync_4.4.0"

OPENSYNC_CORE_REPO_PATH ?= "git://git@github.com/plume-design/opensync.git"
OPENSYNC_CORE_REPO_PROTOCOL ?= "${OPENSYNC_DEFAULT_PROTOCOL}"
OPENSYNC_CORE_BRANCH ?= "${OPENSYNC_DEFAULT_BRANCH}"

OPENSYNC_CORE_URI ?= "${OPENSYNC_CORE_REPO_PATH};protocol=${OPENSYNC_CORE_REPO_PROTOCOL};branch=${OPENSYNC_CORE_BRANCH};name=core;destsuffix=git/core"

OPENSYNC_PLATFORM_REPO_PATH ?= "git://git@github.com/plume-design/opensync-platform-rdk.git"
OPENSYNC_PLATFORM_REPO_PROTOCOL ?= "${OPENSYNC_DEFAULT_PROTOCOL}"
OPENSYNC_PLATFORM_BRANCH ?= "${OPENSYNC_DEFAULT_BRANCH}"

OPENSYNC_PLATFORM_URI ?= "${OPENSYNC_PLATFORM_REPO_PATH};protocol=${OPENSYNC_PLATFORM_REPO_PROTOCOL};branch=${OPENSYNC_PLATFORM_BRANCH};name=platform;destsuffix=git/platform/rdk"

OPENSYNC_VENDOR_REPO_PATH ?= "git://git@github.com/plume-design/opensync-vendor-rdk-template.git"
OPENSYNC_VENDOR_REPO_PROTOCOL ?= "${OPENSYNC_DEFAULT_PROTOCOL}"
OPENSYNC_VENDOR_BRANCH ?= "${OPENSYNC_DEFAULT_BRANCH}"
OPENSYNC_VENDOR_SUFFIX ?= "turris"

OPENSYNC_VENDOR_URI ?="${OPENSYNC_VENDOR_REPO_PATH};protocol=${OPENSYNC_VENDOR_REPO_PROTOCOL};branch=${OPENSYNC_VENDOR_BRANCH};name=vendor;destsuffix=git/vendor/${OPENSYNC_VENDOR_SUFFIX}"

# The OpenSync Service Provider repo has the deployment-specific wifi credentials (for extender) and certificates.
OPENSYNC_SERVICE_PROVIDER_REPO_PATH ?= "git://git@github.com/plume-design/opensync-service-provider-local.git"
OPENSYNC_SERVICE_PROVIDER_REPO_PROTOCOL ?= "${OPENSYNC_DEFAULT_PROTOCOL}"
OPENSYNC_SERVICE_PROVIDER_BRANCH ?= "${OPENSYNC_DEFAULT_BRANCH}"
OPENSYNC_SERVICE_PROVIDER_SUFFIX ?= "local"

OPENSYNC_SERVICE_PROVIDER_URI ?= "${OPENSYNC_SERVICE_PROVIDER_REPO_PATH};protocol=${OPENSYNC_SERVICE_PROVIDER_REPO_PROTOCOL};branch=${OPENSYNC_SERVICE_PROVIDER_BRANCH};name=service-provider;destsuffix=git/service-provider/${OPENSYNC_SERVICE_PROVIDER_SUFFIX}"

SRC_URI = "${OPENSYNC_CORE_URI} ${OPENSYNC_PLATFORM_URI} ${OPENSYNC_VENDOR_URI} ${OPENSYNC_SERVICE_PROVIDER_URI}"
SRCREV_FORMAT ?= "core_platform_vendor_service-provider"

S = "${WORKDIR}/git/core"

PREMIRRORS = ""
MIRRORS = ""
PARALLEL_MAKE = ""

EXTRA_OEMAKE = "MAKEFLAGS="
EXTRA_OEMAKE += "RDK_TARGET_ARCH=${TARGET_ARCH}"
EXTRA_OEMAKE += "RDK_DISTRO=${DISTRO}"
EXTRA_OEMAKE += "TARGET=${MACHINE}"
EXTRA_OEMAKE += "PLATFORM=rdk"
EXTRA_OEMAKE += "${PLUME_MAKE_ARGS}"
EXTRA_OEMAKE += "OPENSYNC_SERVICE_PROVIDER_SUFFIX=${OPENSYNC_SERVICE_PROVIDER_SUFFIX}"

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

SYSTEMD_SERVICE_${PN} = "opensync.service"
SYSTEMD_AUTO_ENABLE = "${@bb.utils.contains('DISTRO_FEATURES', 'extender', 'enable', 'disable', d)}"

inherit cml1
KCONFIG_CONFIG_COMMAND_append = "${EXTRA_OEMAKE} menuconfig"

# OpenSync doesn't support 'oldconfig' target, so override
# here the cml1's "do_configure" and don't call 'oldconfig'
cml1_do_configure_prepend() {
    set -e
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    return 0
}

do_diffconfig() {
    KCONFIG="$(make ${EXTRA_OEMAKE} kconfig/info |grep KCONFIG_WORK | awk '{print $3}')"
    CONFIG_PATH="${S}/$KCONFIG"
    FRAGMENT="fragment.cfg"
    # We know it will be non-zero and don't want to faile due to that hence always return true
    diff --unchanged-line-format= --old-line-format= --new-line-format="%L" "${CONFIG_PATH}" "${CONFIG_PATH}.old" > ${FRAGMENT} || true
    bbplain "Fragment file generated $(readlink -f fragment.cfg)"
}

FILES_${PN} = " \
    ${sysconfdir}/udhcpc.user \
    ${prefix}/sbin/* \
    ${prefix}/${PN}/* \
    ${prefix}/${PN}/.* \
    ${sysconfdir}/systemd/* \
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

FILES_${PN}-extra-tools = " \
    ${prefix}/${PN}/tools/wifi_hal_test \
    ${prefix}/${PN}/tools/band_steering_test \
    ${prefix}/${PN}/tools/wifi_hal_tool \
"

PACKAGES = "${PN}-dbg ${PN}-extra-tools ${PN}-extras ${PN}"

REQUIRED_DISTRO_FEATURES= "systemd"
