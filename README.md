# Repository for Yocto project
Yocto project with several smaller task to learn various aspect on Yocto. With a final objective of creating a minimal image that boots in less then 2 seconds with bluetooth capabilities. 

# Raspberry Pi

These tasks are completed on a Raspberry Pi 3 Model B 64bit.  
This Pi has a Broadcom BCM 2837 SoC which is based on the ARM Cortex-A53 architecture i.e. a 64 bit ARMv8 architecture. 
It contains a quad-core ARM Cortex-A53 CPU running at 1.2GHz, along with GPU, RAM, USB controllers Ethernet controller etc. 

## Booting
The booting process of a raspberry pi is described here:
https://www.youtube.com/watch?v=ni72NvaX7kU


# Useful Commands
* grep
* rg
* find
* mount
* gparted
* lsmod : Listing modules.
* insmode : Inserts a kernel module into the running kernel.
* modprobe : Load/unload kernel modules into the running kernel. 
* dtc : Device tree compiler


# Repo
Poky and meta-layers that is not custom are added to the repo using submodules. 
https://git-scm.com/book/en/v2/Git-Tools-Submodules

# Tasks


| Task  | Description | Completed |
| ------------- | ------------- | ------------- |
| 1 | Boot up raspberry pi 3 64 bit with a "minimal" yocto image.  | ✅ |
| 2 | Update busybox from 1.35 to 1.36  | ✅ |
| 3   | Add Vim to the distribution  | ✅ |
| 4   | Change the keyboard to be Norwegian and not US |  |
| 5   | Create my own image which inherit/require from core-image-base, instead of changing build conf/layer.conf file.  | ✅ |
| 6 | Create a helloworld.sh bash script, add it to file system of image and run the file. | ✅ |  
| 7 | Add a TMP102 sensor and measure its values. Add  I2C related recipes, write a bash script to read out temperature data from the I2C temp sensor. | |
| 8 | Complete task 7 by overriding device tree, such that device is loaded when kernel is booted. | |
| 9 | Create and add out-of-tree Hello World kernel module | ✅ |
|    | Create an true minimal image which boots up in under 2 seconds. The 'minimal' images provided by poky takes up to 10 seconds to boot. The image shall have bluetooth capabilities.    | |



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

The Vim was not installed, so when we require from core-image-base.bb, we do not require from its .bbappend files. Good to know. 


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

The helloworld.sh file was loaded into the /usr/bin/ folder on the Pi and could run from there.


## Task 7 Add I2C Device for reading temperature measurments.
This task was solved by first connecting the TMP102 I2C sensor to the raspberry pi according the diagram provided at: https://pinout.xyz/pinout/i2c 

Then to include I2C tools for the image i added IMAGE_INSTALL+= "i2c-tools" in the trym-image.bbappend in the new recipe for the i2c temp sensor. 

When booting up the raspberry pi the i2c device was not accessible. This could be fixed multiple ways. Both these require setting configurations on the Pi after it is booted. 

#### Solution 1

One solution was to add 'dtparam=i2c_arm=on' to the /boot/config.txt file which is specific for the raspberry pi.
https://github.com/fivdi/i2c-bus/blob/master/doc/raspberry-pi-i2c.md#step-1---enable-i2c

Then reboot the system and load the driver. The drivers are located at /lib/modules/../kernel/drivers/. The i2c-dev driver can be loaded using modprobe. First to see which modules are inserted in the kernel we can run

    lsmod
    lsmod | grep i2c*

If i2c-dev is not listed here then we need to insert it such that the kernel have access to its functionality.

    insmod /path-to-driver/i2c-dev.ko.xz

Then the driver is inserted and we can load the module along with any dependencies for it to be operational 

    modprobe i2c-dev

But for this to work the config file must be set and the pi rebooted beforehand. 

To test the i2c temp sensor the script readTMP102.sh is loaded into the file system like in task 6 and run on the target. 

#### Solution 2
Another solution is to add these commands to the top of the bash script such that everything i automated when running the script. This is done in a second file readTMP102_v2.sh. However, we still need to set the variable in config.txt and reboot the system. 


## Task 8 Override Device Tree
Add i2c-dev into the dts and load that. 

Another solution is to set the driver in the device tree. This solution is more general and is yocto specific. The previous alternative is raspberry specific and must be done everytime after the pi is booted the first time. But we want the i2c driver to be initialised when we boot the iamge for the first time.

The meta-raspberrypi provide dtb files of their device tree. This is binary files of the dts files, and therefore not designed to be altered because they expect the user to change settings regarding devices on the raspberry pi terminal. 

After converting dtb to dts using the command formatted

    dtc -I dtb -O dts <filename>.dtb

it shows that the I2C is in fact status okay but still it dosen't initiate at startup. This could imply that the config.txt file overwrite these settings? 
Testing to include i2cdev which supposedly have some sort of i2c bus scanning utility lsi2c. 
Could not use i2cdev because it dosen't exist on the kirkstone version? 
I also don't think that this could be solved by adding recipes and features since it is Pi specific issue. 


Guide to enabling I2C with Yocto project using device tree: https://www.digikey.no/en/maker/projects/intro-to-embedded-linux-part-5-how-to-enable-i2c-in-the-yocto-project/6843bbf9a83c4c96888fccada1e7aedf


## Task 9 Out-of-Tree Kernel Module
This task was solved by following the guide at https://community.nxp.com/t5/i-MX-Processors-Knowledge-Base/Incorporating-Out-of-Tree-Modules-in-YOCTO/ta-p/1373825, and the guide https://tldp.org/LDP/lkmpg/2.6/html/lkmpg.html#AEN121. 

A layer for the kernel module was created and added using:

    bitbake-layers create-layer ../meta-<layer-name>
    bitbake-layers add-layer ../<layer-name>/

To get started the hello-mod in the meta-skeleton in the poky repository was copied over to our new layer. The recipe hello-mod was then added to the trym-image with 

    IMAGE_INSTALL += "hello-mod"

After building image, flashing it to the SD card and booting up the PI, we could load the module with 'modprobe hello' and remove it with 'rmmod hello', resulting in the terminal messages 'Hello World' and 'Goodbye Cruel World' respectively. 