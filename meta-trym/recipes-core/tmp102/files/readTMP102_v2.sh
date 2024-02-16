#!/bin/bash
echo "Hello World"

# Insert the i2c-dev module into the kernel.
insmod /lib/modules/5.15.92-v8/kernel/drivers/i2c/i2c-dev.ko.xz

# Load the i2c-dev module
modprobe i2c-dev

# Raspberry Pi TMP102 I2C temperature sample code.

# By default the address of TMP102 is set to 0x48
address=0x48

# Check if another address has been specified
if [ ! -z "$1" ]; then
	address=$1
fi

# Read from I2C and print temperature
i2cget -y 1 $address 0x00 w |
awk '{printf("%.2f\n", (a=( \
(("0x"substr($1,5,2)substr($1,3,1))*0.0625)+0.1) \
)>128?a-256:a)}'
