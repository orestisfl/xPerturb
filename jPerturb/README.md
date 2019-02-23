# jPerturb : a runtime perturbation analysis tool for Java.

See  [Correctness Attraction: A Study of Stability of Software Behavior Under Runtime Perturbation](https://hal.archives-ouvertes.fr/hal-01378523/file/correctness-attraction.pdf) (Benjamin Danglot, Philippe Preux, Benoit Baudry and Martin Monperrus), In Empirical Software Engineering, Springer Verlag, 2017.

```
@article{danglot2016correctness,
 title = {{Correctness Attraction: A Study of Stability of Software Behavior Under Runtime Perturbation}},
 author = {Danglot, Benjamin and Preux, Philippe and Baudry, Benoit and Monperrus, Martin},
 journal = {{Empirical Software Engineering}},
 publisher = {{Springer Verlag}},
 year = {2017},
 doi = {10.1007/s10664-017-9571-8},
}
```

## Download & Install

To retrieve the project:
```
git clone https://github.com/Spirals-Team/jPerturb
cd jPerturb
```

To run the tests
```
mvn test
```


To install in the local repository
```
mvn install
```

## Usage with command line

To process and inject perturbation points to the resources classes used for test.

```
java -jar target/jPerturb-0.0.1-SNAPSHOT-jar-with-dependencies.jar -type IntNum:boolean -i src/test/resources/ -o target/trash/
```

Process and inject perturbation to the resources classes used for test with rename.

```
java -jar target/jPerturb-0.0.1-SNAPSHOT-jar-with-dependencies.jar -r -type IntNum:boolean -i src/test/resources/ -o target/trash/
```

To perform a correctness attraction analysis with `IntegerExplorationPlusOne`:

```
mvn exec:java -Dexec.mainClass="experiment.Main2" -Dexec.args="-v -s quicksort.QuickSortManager -nb 10 -size 10 -exp call pone"
```

## Usage with API

To instrument a single class:

```java
Main main = new Main();
main.addInputResource("src/main/java/quicksort/QuickSort.java");
main.run();
```

## Experiments

You can find code of our experiments at [jPerturb-experiments](http://github.com/Spirals-Team/jPerturb-experiments.git).
