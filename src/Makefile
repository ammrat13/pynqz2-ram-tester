TARGET   := pynqz2-ram-tester
LDSCRIPT := ldscript
OFILES   := src/startup.o src/main.o
BIF      := bif

CC := clang --target=arm-none-eabi
BOOTGEN := bootgen

ASFLAGS := -march=armv7a -mcpu=cortex-a9 -v
CFLAGS  := \
	-march=armv7a -mcpu=cortex-a9 \
	-Os -v \
	-Wall -Wno-main-return-type -Werror \
	-Iinclude/
LDFLAGS := -v -nostdlib
BOOTGENFLAGS := -arch zynq

.PHONY: all
all: BOOT.BIN

.PHONY: clean
clean:
	rm -fv BOOT.BIN $(TARGET).elf $(OFILES)

BOOT.BIN: $(BIF) $(TARGET).elf
	$(BOOTGEN) $(BOOTGENFLAGS) -o $@ -image $(BIF)

$(TARGET).elf: $(LDSCRIPT) $(OFILES)
	$(CC) $(LDFLAGS) -o $@ -T$(LDSCRIPT) $(OFILES)

%.o: %.s
	$(CC) $(ASFLAGS) -c -o $@ $^

%.o: %.c
	$(CC) $(CFLAGS) -c -o $@ $^
