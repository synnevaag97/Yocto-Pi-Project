# Repository for Yocto project
Minor project on completed specific tasks with the Yocto Project. 


## Tasks


| Task  | Description | Completed |
| ------------- | ------------- | ------------- |
| 1 | Boot up raspberry pi 3 64 bit with a "minimal" yocto image.  | ✅ |
| 2 | Update busybox from 1.35 to 1.36  | ✅ |
| 3   | Add Vim to the distribution  | ✅ |
| 4   | Change the keyboard to be Norwegian and not US |  |
| 5   | Create my own image which inherit/require from core-image-base, instead of changing build conf/layer.conf file.  | ✅ |
| 6 | Create a helloworld.sh bash script, add it to file system of image and run the file. | ✅ |  
|    | Create an true minimal image which boots up in under 2 seconds. The 'minimal' images provided by poky takes up to 10 seconds to boot.   | |



## Task 1
* Clone poky repo
* Clone meta-raspberrypi and include into the build.
* Make sure both repos are on same branch or yocto version.
* Change MACHINE variable to the raspberry pi in use. 
* Build a minimal image with bitbake 'image_name'
* Partition an SD card and copy over the build.
* Boot up the raspberry pi with SD card

This is the simplest hello world build of an minimal image. Here conf and bblayers are modified in the build/conf folder. 


## Task 2
This was already more complicated. My solution was to copy over the recipe from poky which defines the busybox into my own layer, and then set i to the newer version. 
The new recipe is found in 'meta-trym/recipes-core/busybox'.


* Copy the busybox recipe
* Change version name
* Add correct SHA256sum in the busybox_1.36.bb which is for the new version of busybox. 
* Fix, update or removed patches from the previous version. 

The two last bullet points can be done with the help from 
[busybox index layer infortmation page.](https://layers.openembedded.org/layerindex/recipe/5249/)


## Task 3
Adding Vim to the distribution is easy when you know what to do. The simplest way of doing this is similar to what i did in task 1. We can add a line of code in the build/conf/layer.conf file:

    CORE_IMAGE_EXTRA_INSTALL += "vim"

This line will include the recipe or package vim into the build. When you boot up you will see that vi is replaced with vim. 

Alternatively, you can add an .bbappend file in you meta layer. In my meta-trym layer i have added a folder images and then a file core-image-base.bbappend. This file will append wathever is inside the file onto the original image core-image-base. This way we do not have to change the build folder files and instead simply adding a line in the .bbappend file. 


## Task 5
Was solved by creating a trym-image.bb file which require from core-image-base.bb. It was not possible to use inherit for this problem, because inherit is only used for .bbclass files. For .bb files we need to use require which has different syntax to it. 
Also a package was added in the image for testing. 

The Vim was still installed, so when we require from core-image-base.bb, we also require from its .bbappend files. Good to know. 


## Task 6
The helloWorld.sh script was added by creating the recipe helloworld in the meta-trym layer. The recipe was the added to the image with: IMAGE_INSTALL += "helloworld". 

The recipe is built up of these elements: 
* SUMMARY : Provides a breif description of the package i.e. recipe. 
* DESCRIPTION : Gives a more detailed description of the package. 
* RDEPENDS : Specifies runtime dependencies. ${PN} is a variable refering to the package name. In this case the recipe depends on bash. 
* LICENCE : Specifies the lincence under which the pacakge is distributed. In this case MIT. 
* LIC_FILES_CHKSUM : Checks the checksum of the licence file to ensure its integrity. 
* SRC_URI : Specifies the location of the source files. It fetches the bash script from the local filesystem i.e. the folder files. 
* S : Specifies the location of the source files within the Yocto build environment. ${WORKDIR} is the working directory. 
* do_install() : Is a bitbake task that defines what action should be taken to install the package onto the target filesystem. The first line create the target directory, and the second line installs the bash script into the direcotry with permission 0755 which is (read, wirte, execute owner, read and execute for group and others). 

Variables:
* ${D} : Is the root directory in target.
* ${binddir} : Is the folder where binary files (executables) are installed. 