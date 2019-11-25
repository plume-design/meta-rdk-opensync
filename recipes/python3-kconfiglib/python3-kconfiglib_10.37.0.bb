DESCRIPTION = "Kconfiglib is a Kconfig implementation in Python"
LICENSE = "ISC"

LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=712177a72a3937909543eda3ad1bfb7c"

SRC_URI[md5sum] = "294b7c256da427dc116a5518b2ea1051"
SRC_URI[sha256sum] = "7207ca85be9fe622d26c97fb520066b022562940687bdfac375e20f26e17965a"

BBCLASSEXTEND = "native nativesdk"

inherit pypi setuptools3
