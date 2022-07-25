TARGET   := pynqz2-ram-tester
LDSCRIPT := ldscript
BIF      := bif
OFILES := \
	src/startup.o \
	src/main.o src/uart.o src/rand.o

CC := clang --target=arm-none-eabi
BOOTGEN := bootgen

ASFLAGS := -march=armv7a -mcpu=cortex-a9 -v
CFLAGS  := \
	-march=armv7a -mcpu=cortex-a9 \
	-Os -v \
	-Wall -Wno-main-return-type -Werror \
	-ffunction-sections -fdata-sections \
	-Iinclude/
LDFLAGS := -v -nostdlib
BOOTGENFLAGS := -arch zynq -w on

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
