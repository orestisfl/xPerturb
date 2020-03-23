src = $(wildcard src/*.c)
obj = $(src:.c=.o)

src/wb_reference: $(obj)
	$(CC) $(CFLAGS) -o $@ $^
