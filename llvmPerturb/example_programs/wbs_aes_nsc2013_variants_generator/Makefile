all: src/nosuchcon_2013_whitebox_noenc_generator src/wb_reference
src/nosuchcon_2013_whitebox_noenc_generator: src/nosuchcon_2013_whitebox_noenc_generator.c
	$(CC) $(CFLAGS) -o $@ $^
src/wb_reference: src/nosuchcon_2013_whitebox_noenc.c
	$(CC) $(CFLAGS) -o $@ $^
