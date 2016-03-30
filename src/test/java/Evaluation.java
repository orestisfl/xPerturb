import org.apache.log4j.Level;
import org.junit.Test;
import spoon.Launcher;
import util.Util;

/**
 * Created by spirals on 03/03/16.
 */
public class Evaluation {

    @Test
    public void processSortLibrary() {
        Launcher l = Util.createSpoonWithPerturbationProcessors();

        l.addInputResource("../sort/src/");

        l.setSourceOutputDirectory("../Spooned/sort/src/");
        l.setBinaryOutputDirectory("../Spooned/sort/bin/");

        l.run();
    }

    @Test
    public void processBitcoinJ() {
        Launcher l = Util.createSpoonWithPerturbationProcessors();

        l.getEnvironment().setLevel(Level.ALL.toString());

        final String M2_REPO = "/home/spirals/.m2/repository/";

        l.addInputResource("../bitcoinj/core/src/main/java/");
        l.setSourceOutputDirectory("../Spooned/bitcoinj/core/src/main/java/");
        l.setBinaryOutputDirectory("../Spooned/bitcoinj/core/target/classes");

        l.getEnvironment().setCopyResources(false);

        l.getEnvironment().setSourceClasspath(new String[]{
                M2_REPO + "junit/junit/4.12/junit-4.12.jar",
                M2_REPO + "org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar",
                M2_REPO + "org/easymock/easymock/3.2/easymock-3.2.jar",
                M2_REPO + "cglib/cglib-nodep/2.2.2/cglib-nodep-2.2.2.jar",
                M2_REPO + "org/objenesis/objenesis/1.3/objenesis-1.3.jar",
                M2_REPO + "org/slf4j/slf4j-jdk14/1.7.7/slf4j-jdk14-1.7.7.jar",
                M2_REPO + "org/slf4j/slf4j-api/1.7.7/slf4j-api-1.7.7.jar",
                M2_REPO + "com/fasterxml/jackson/core/jackson-databind/2.5.2/jackson-databind-2.5.2.jar",
                M2_REPO + "com/fasterxml/jackson/core/jackson-annotations/2.5.0/jackson-annotations-2.5.0.jar",
                M2_REPO + "com/fasterxml/jackson/core/jackson-core/2.5.1/jackson-core-2.5.1.jar",
                M2_REPO + "com/h2database/h2/1.3.167/h2-1.3.167.jar",
                M2_REPO + "com/madgag/spongycastle/core/1.51.0.0/core-1.51.0.0.jar",
                M2_REPO + "com/google/protobuf/protobuf-java/2.6.1/protobuf-java-2.6.1.jar",
                M2_REPO + "com/google/guava/guava/18.0/guava-18.0.jar",
                M2_REPO + "com/google/code/findbugs/jsr305/2.0.1/jsr305-2.0.1.jar",
                M2_REPO + "net/jcip/jcip-annotations/1.0/jcip-annotations-1.0.jar",
                M2_REPO + "com/lambdaworks/scrypt/1.4.0/scrypt-1.4.0.jar",
                M2_REPO + "postgresql/postgresql/9.1-901.jdbc4/postgresql-9.1-901.jdbc4.jar",
                M2_REPO + "mysql/mysql-connector-java/5.1.33/mysql-connector-java-5.1.33.jar",
                M2_REPO + "org/fusesource/leveldbjni/leveldbjni-all/1.8/leveldbjni-all-1.8.jar",
                M2_REPO + "org/bitcoinj/orchid/1.2/orchid-1.2.jar",
                M2_REPO + "com/squareup/okhttp/okhttp/2.7.2/okhttp-2.7.2.jar",
                M2_REPO + "com/squareup/okio/okio/1.6.0/okio-1.6.0.jar"
        });

        l.run();
    }

    @Test
    public void processSample() {
        Launcher l = Util.createSpoonWithPerturbationProcessors();

        l.addInputResource("resources/Main.java");

        l.setSourceOutputDirectory("../Spooned/Test/src/");
        l.setBinaryOutputDirectory("../Spooned/Test/bin/");


        l.run();
    }

}
