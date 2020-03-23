src = $(wildcard src/*.c)
obj = $(src:.c=.o)

src/DemoKey_table_encrypt: $(obj)
	$(CC) $(CFLAGS) -o $@ $^
