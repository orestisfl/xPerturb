package experiment;

import perturbation.location.PerturbationLocation;

import java.util.List;

/**
 * Provides an abstraction to be able to run correctness attraction analysis on any algorithm 
 */
public interface Manager<T, P> {

    /** Called by the framework to tell the implementation about the number of tasks, and task size.
    * A task is an input to the program under study (eg an array for QuickSort)
    * Implementing classes can then allocate some resources based on this information
    */
    void initialize(int numberOfTask, int sizeOfTask);

    /** Returns the "class under perturbation" (CUP), eg "QuickSort". It can be obtained calling `Class.forName("QuickSort")` */
    Class<?> getCUP();

    /**
     * @return a Java  callable for executing the program under perturbation on the given input
     */
    CallableImpl<T, P> getCallable(T input);

    /**
     * @return the oracle class for the program under perturbation. The oracle is the thing that enables on to say that an output is correct or not for a given input.
     */
    Oracle<T, P> getOracle();

    /**
     * Returns the list of perturbation points obtained with automated instrumentation.
     * Provided for free if you extended ManagerImpl
     */
    List<PerturbationLocation> getLocations();

    /**
     * Used this getter with filter on type of perturbation points.
     * Provided for free if you extended ManagerImpl
     */
    List<PerturbationLocation> getLocations(String filter);
    
    /** Provided for free if you extended ManagerImpl */
    List<Integer> getIndexTask();

    void setIndexTask(List<Integer> tasks);

    /**
     * @return the name of the subject
     */
    String getName();

    /**
     * @return a brief description of the subject
     */
    String getHeader();

    /**
     * @return the size of task
     */
    int getSizeOfTask();

    /**
     * This method should a return a clone of the task, in case of side effect
     * @param indexOfTask
     * @return
     */
    T getTask(int indexOfTask);

    /**
     * method used to recover a good state in case of errors/failure for stateful subject
     */
    void recover();

    /**
     * Method used to stop the subject properly
     */
    void stop();

}
