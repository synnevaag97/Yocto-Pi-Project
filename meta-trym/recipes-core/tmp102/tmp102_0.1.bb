SUMMARY = "TMP102 measurment"
DESCRIPTION = "Adds a script for reading out temperature sensors"
RDEPENDS:${PN} = "bash"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://readTMP102.sh file://readTMP102_v2.sh "

S = "${WORKDIR}"

do_install() {
install -d ${D}${bindir}
install -m 0755 readTMP102.sh ${D}${bindir}
install -m 0755 readTMP102_v2.sh ${D}${bindir}
}

