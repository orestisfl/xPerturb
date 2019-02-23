# cPerturb


cPerturb is a software that takes a piece of code in C, identifies points in the code that are suitable for perturbation and injects perturbations. This can then be tested on a large scale to identify the behaviour of different algorithms under perturbation and hopefully find so-called anti-fragile points.

## Prerequisites

To compile cPerturb, you need the library libxml2 and all the sublibraries it requires. To perform any tests you also need some kind of software that transforms code into XML and the other way around.
In this project we use srcML (https://www.srcml.org). You might need to add srcml to your PATH to be able to run it from terminal. Alternatively, you could add srcML as a library to GCC enabling you to use the function srcml() withing cPerturb and skipping a few command line steps.

On **Windows** there are a few alternatives. The easiest way is to install CygWin with the following packages:

- libxml2-dev
- libxslt1-dev
- libarchive-dev
- antlr
- libantlr-dev
- libcurl4-openssl-dev
- libssl-dev
- g++
- gcc

Using CygWin <https://www.cygwin.com/> you should not need to install libxml2 manually, but in case you need this is how:

Download libxml2 <http://www.zlatkovic.com/libxml.en.html>

Put libxml2 in the bin map of gcc

`…/gcc/bin libxml2`

On **OsX** install it using homebrew

`Brew install libxml2`

You will also need some program to convert source code to xml and the other way around. For this project we have used srcml.
In a later release the xml conversion will be a part of cPerturb using the scrml library. See <https://www.srcml.org>

##  Compilation

Compile cperturb using the makefile available. Command: make perturb (make all also works but compiles a few other files as well).


## Running the tests

To run cPerturb alone,

`./cperturb <xml file> <perturbation mode>`

There are three extra options available

`<-o>       <output-file>`

`<-i> (int) <node-to-perturb>`

and

`<-s>`
which suppresses output during execution.

Example:

`./cPerturb Examples/demo.xml PONE -o example.xml -i 2 -s`

To check the result of the perturbation:

`diff Examples/demo.xml example.xml`

This is automated within the script files. The script that performs the whole test end-to-end is called cPerturbTest.sh. For a small scale test you can use perturb.sh, which allows you to choose any file. cPerturbTest is streamlined to work specifically for the 5 algorithms we study in detail in this project.

## Experiments

### Programs

All programs are at the root of this repository, eg `quicksort.c`. All programs contain a main function that generates inputs based on a unique seed passed as parameter. For Knapsack, the content of the sack is predefined, and the main argument is the size of the input.

### Execution

If you open the shell-script file there are a few variables separated which are the ones you should modify for different tests.
Basically you have the following:

- ALGO
Here you choose a value between 1 and 5 which defines what algorithm the script will be testing against. This also sets the end point for each algorithm as default. The 5 algorithms can be seen in the script but are as following:
  1- Permutations
  2- Binary Search
  3- Knapsack
  4- Quicksort
  5- Merge sort

- PMODE
This selects what perturbation mode will be used. Accepted are PONE and MONE.

- MIN_LOOP and MAX_LOOP
These are the boundaries for the looping of inputs for each perturbed point. They can be tweaked however you want to just look at a few input values, but in general it only changes the amount of inputs that are tested.

- PPOINT
This sets the perturbation point at which the script starts testing.

- FORCE_MAX
When selecting an algorithm in ALGO, it sets a value for the last point by default. If this variable is set to 0 it will follow the default, otherwise it uses the value here. This is useful if you don't want to investigate the whole perturbation spece.

- SUPPRESS
This controls how much text is output in the execution of the script. It can be 0, 1 or 2. Where 2 has the least amount of text and is recommended.

**Warnings** Many of the files are written on a Windows-environment, because of this most, if not all, files use the CRLF-convention instead of the Unix-standard LF. If any programs (especially the .sh files) don't work, this is a good first place to look at.

The programs being tested have to output their result as stdout. The script will then compare everything the program outputs with and without perturbations. This does not include return values as these are considered error codes. Because of this the program has to be written in a way that supports this.

The algorithms tested have been slightly modified so they take in a seed as input data and then internally generate all the input they need. Because the seeded RNG yields the same results this allows us to compare with the oracle. This does mean that you need to slightly adapt any new programs you might want to test against.

## Authors

- Alexander Hesseborn
- Mårten Vuorinen

## Acknowledgments

<https://github.com/Spirals-Team/jPerturb>
<https://github.com/Spirals-Team/correctness-attraction-experiments>
