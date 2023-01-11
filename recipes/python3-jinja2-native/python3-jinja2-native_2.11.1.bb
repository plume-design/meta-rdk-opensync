inherit pypi setuptools3 native

DESCRIPTION = "Jinja2 is a modern and designer friendly templating language for Python, modelled after Django’s templates."
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=5dc88300786f1c214c1e9827a5229462"
PYPI_PACKAGE = "jinja2"
#Remove the SRC_URI set by pypi.bbclass
SRC_URI_remove="${PYPI_SRC_URI}"
SRC_URI="https://files.pythonhosted.org/packages/d8/03/e491f423379ea14bb3a02a5238507f7d446de639b623187bccc111fbecdf/Jinja2-2.11.1.tar.gz"
SRC_URI[md5sum] = "5d88c7e77aa63fc852a04f65dbfe5594"
SRC_URI[sha256sum] = "93187ffbc7808079673ef52771baa950426fd664d3aad1d0fa3e95644360e250"
S = "${WORKDIR}/Jinja2-${PV}"
CLEANBROKEN = "1"

DEPENDS := "python3-markupsafe-native"
