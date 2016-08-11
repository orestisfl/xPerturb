[![Travis Build Status](https://api.travis-ci.org/Spirals-Team/jPerturb.svg?branch=master)](https://travis-ci.org/Spirals-Team/jPerturb)

# jPerturb : a state perturbation tool for Java.

## Download & Install

To retrieve the project:
```
git clone https://github.com/Spirals-Team/jPerturb
```

To install and run test:
```
mvn test
```

## Usage

usage:
```
java -jar target/jPerturb-0.0.1-SNAPSHOT-jar-with-dependencies.jar (-type <types>) (-r) -i <i> (-o <o>) (-x)
```
options:

* -type <types>: every primitive type, separated by ":" (all primitive type will be processed by default)
    * special token for \<type\>: IntNum it will process all integer expression (from byte to long, with java.util.BigInteger)
* -r: will rename classes by adding "Instr" as suffix
* -i \<i\>: path to classes to be perturbed
* -o \<o\>: path to output (default is the same as \<i\>)
* -x: no classpath mode of spoon

Example:

Process and inject perturbation to the resources classes used for test.

```
java -jar target/jPerturb-0.0.1-SNAPSHOT-jar-with-dependencies.jar -type IntNum:boolean -i src/test/resources/ -o target/trash/
```

Process and inject perturbation to the resources classes used for test with rename.

```
java -jar target/jPerturb-0.0.1-SNAPSHOT-jar-with-dependencies.jar -r -type IntNum:boolean -i src/test/resources/ -o target/trash/
```

## Experiments

You can find code of our experiments at [jPerturb-experiments](http://github.com/Spirals-Team/jPerturb-experiments.git).