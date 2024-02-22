SUMMARY = "Simple image for learning how to create a custom image that \ 
inherit from core-image-base and adds functionality i want."
require recipes-core/images/core-image-base.bb
LICENSE = "MIT"
#IMAGE_INSTALL = "packagegroup-core-boot"
IMAGE_INSTALL += "helloworld"
IMAGE_INSTALL += "tmp102"
IMAGE_INSTALL += "hello-mod"
