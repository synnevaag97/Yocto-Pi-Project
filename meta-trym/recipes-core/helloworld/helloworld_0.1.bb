SUMMARY = "Hello world"
DESCRIPTION = "Adds the hello world script to the image."
RDEPENDS:${PN} = "bash"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://helloWorld.sh"

S = "${WORKDIR}"

do_install() {
install -d ${D}${bindir}
install -m 0755 helloWorld.sh ${D}${bindir}
}
