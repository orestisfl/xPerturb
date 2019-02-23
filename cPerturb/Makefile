CC = gcc
CFLAGS = -Wall
TARGET = cPerturb
TESTS = cPerturb bsearch knapsack quicksort permutations mergesort
INCLUDES = -I/usr/include/libxml2
LFLAGS = -L/usr/lib
LIBS = -lxml2

perturb: $(TARGET).c
	$(CC) $(CFLAGS) $(INCLUDES) -o $(TARGET) $(TARGET).c $(LFLAGS) $(LIBS)

all: $(TARGET).c bsearch.c knapsack.c quicksort.c permutations.c mergesort.c
	$(CC) $(CFLAGS) $(INCLUDES) -o $(TARGET) $(TARGET).c $(LFLAGS) $(LIBS)

	$(CC) $(CFLAGS) -o bsearch bsearch.c
	$(CC) $(CFLAGS) -o knapsack knapsack.c
	$(CC) $(CFLAGS) -o quicksort quicksort.c
	$(CC) $(CFLAGS) -o permutations permutations.c
	$(CC) $(CFLAGS) -o mergesort mergesort.c

depend: $(TARGET).c
	makedepend $(INCLUDES) $^
