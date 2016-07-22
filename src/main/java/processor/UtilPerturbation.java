package processor;

import perturbation.location.PerturbationLocation;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UtilPerturbation {

    private static UtilPerturbation instance = null;

    public static final List<String> perturbableTypes = new ArrayList<>();

    public static final String PACKAGE_NAME_PERTURBATION = "perturbation";

    public static final String QUALIFIED_NAME_PERTURBATOR = PACKAGE_NAME_PERTURBATION+".PerturbationEngine";

    public static final String QUALIFIED_NAME_LOCATION = PACKAGE_NAME_PERTURBATION+".location.PerturbationLocation";

    public static final String QUALIFIED_NAME_LOCATION_IMPL = PACKAGE_NAME_PERTURBATION+".location.PerturbationLocationImpl";

    private static final String INIT_METHOD_NAME = "initPerturbationLocation";

    /**
     * Supported type by default
     */
    static {
        perturbableTypes.add("char");

        perturbableTypes.add("byte");
        perturbableTypes.add("short");
        perturbableTypes.add("int");
        perturbableTypes.add("Integer");
        perturbableTypes.add("long");
        perturbableTypes.add("BigInteger");

        perturbableTypes.add("boolean");

        perturbableTypes.add("float");
        perturbableTypes.add("double");
    }

    private UtilPerturbation() {
        perturbator = null;
        methodsByClass  = new HashMap<>();
        listOfFieldByClass = new HashMap<>();
        staticBlockByClass = new HashMap<>();
        currentLocation = 0;
    }

    private static CtClass perturbator = null;

    private Map<String, List<CtMethod>> methodsByClass;

    private Map<String, List<CtField>>  listOfFieldByClass;

    private Map<String, CtAnonymousExecutable>  staticBlockByClass;

    private int currentLocation;

    public static boolean checkIsNotInPerturbatorPackage(CtElement candidate) {
        CtPackage parent =  candidate.getParent(CtPackage.class);
        return parent.getQualifiedName().startsWith(PACKAGE_NAME_PERTURBATION);
    }

    private static UtilPerturbation getInstance() {
        if (instance == null)
            instance = new UtilPerturbation();
        return instance;
    }

    public static CtInvocation createStaticCallOfPerturbationFunction(Factory factory, String perturbedType, CtTypeReference originalType, CtExpression argument) {

        if (perturbator == null)
            perturbator = (CtClass) factory.Class().get(QUALIFIED_NAME_PERTURBATOR);

        CtTypeReference<?> classReference = factory.Type().createReference(perturbator);
        CtExecutableReference execRef = factory.Core().createExecutableReference();
        execRef.setDeclaringType(classReference);

        if (originalType.getSimpleName().equals("Integer"))
            execRef.setSimpleName("pint");
        else
            execRef.setSimpleName("p"+originalType.getSimpleName());

        execRef.setStatic(true);

        CtTypeAccess typeAccess = factory.Core().createTypeAccess();
        typeAccess.setType(classReference);
        typeAccess.setAccessedType(classReference);

        CtExpression[] args = new CtExpression[2];
        args[0] = addFieldLocationToClass(factory, argument, perturbedType);
        args[1] = argument;

        getInstance().currentLocation++;

        return factory.Code().createInvocation(typeAccess, execRef, args);

    }

    private static CtFieldRead addFieldLocationToClass(Factory factory, CtExpression argument, String typeOfPerturbation) {

        // find the top level class or the static class
        CtType clazz = argument.getParent(new TypeFilter<CtType>(CtType.class) {
            @Override
            public boolean matches(CtType element) {
                if (element.isTopLevel()) {
                    return true;
                }
                if (element.getModifiers().contains(ModifierKind.STATIC)) {
                    return true;
                }
                CtElement parent = element.getParent();
                if (parent instanceof CtInterface || parent instanceof CtAnnotationType) {
                    return true;
                }
                return false;
            }
        });

        String currentKey = clazz.getQualifiedName();

        if (!getInstance().listOfFieldByClass.containsKey(currentKey)) {
            getInstance().listOfFieldByClass.put(currentKey, new ArrayList<>());
            getInstance().methodsByClass.put(currentKey, new ArrayList<>());
        }

        String fieldName = "__L" + getInstance().currentLocation;

        CtField fieldLocation = factory.Core().createField();
        fieldLocation.setSimpleName(fieldName);
        fieldLocation.setType(factory.Type().createReference(PerturbationLocation.class));
        fieldLocation.addModifier(ModifierKind.PUBLIC);
        fieldLocation.addModifier(ModifierKind.STATIC);
        fieldLocation.setParent(clazz);

        getInstance().listOfFieldByClass.get(currentKey).add(fieldLocation);

        String position = argument.getPosition().getCompilationUnit().getFile().getName() + ":" + argument.getPosition().getLine();
        CtConstructorCall constructorCall = factory.Code().createConstructorCall(factory.Type().get(QUALIFIED_NAME_LOCATION_IMPL).getReference(),
                factory.Code().createLiteral(position), factory.Code().createLiteral(getInstance().currentLocation), factory.Code().createLiteral(typeOfPerturbation));

        CtFieldWrite writeField = factory.Core().createFieldWrite();
        writeField.setVariable(fieldLocation.getReference());

        CtAssignment assignmentField = factory.Core().createAssignment();
        assignmentField.setAssigned(writeField);
        assignmentField.setAssignment(constructorCall);

        //Methods
        if (!getInstance().methodsByClass.get(currentKey).isEmpty()) {
            if (getInstance().methodsByClass.get(currentKey).get(getInstance().methodsByClass.get(currentKey).size()-1).getBody().getStatements().size() < 1000) {
                getInstance().methodsByClass.get(currentKey).get(getInstance().methodsByClass.get(currentKey).size()-1).getBody().insertEnd(assignmentField);
            } else
                addInitMethodTo(clazz, factory, assignmentField);
        } else
            addInitMethodTo(clazz, factory, assignmentField);

        //Build the read of the field for the parameter of the perturbation function
        CtFieldRead readFieldLocation = factory.Core().createFieldRead();
        readFieldLocation.setVariable(fieldLocation.getReference());

        return readFieldLocation;
    }

    private static void addInitMethodTo(CtType clazz, Factory factory, CtStatement firstStatement) {

        CtTypeReference typeReference = clazz.getReference();

        String currentKey = clazz.getQualifiedName();

        CtMethod initMethod = factory.Core().createMethod();
        initMethod.setSimpleName(INIT_METHOD_NAME+(getInstance().methodsByClass.get(currentKey).size()));

        Set<ModifierKind> modifierKinds = new HashSet<ModifierKind>();
        modifierKinds.add(ModifierKind.PRIVATE);
        modifierKinds.add(ModifierKind.STATIC);
        initMethod.setModifiers(modifierKinds);
        initMethod.setType(factory.Type().VOID_PRIMITIVE);
        initMethod.setBody(factory.Code().createCtBlock(firstStatement));

        getInstance().methodsByClass.get(currentKey).add(initMethod);

        CtTypeAccess typeAccess = factory.Core().createTypeAccess();
        typeAccess.setType(typeReference);
        typeAccess.setAccessedType(typeReference);

        CtExecutableReference execRef = factory.Core().createExecutableReference();
        execRef.setDeclaringType(clazz.getReference());
        execRef.setSimpleName(INIT_METHOD_NAME+(getInstance().methodsByClass.get(currentKey).size()-1));
        execRef.setStatic(true);

        if (getInstance().staticBlockByClass.containsKey(currentKey)) {
            getInstance().staticBlockByClass.get(currentKey).getBody().insertEnd(factory.Code().createInvocation(typeAccess, execRef));
        } else {
            CtBlock initBlock = factory.Code().createCtBlock(factory.Code().createInvocation(typeAccess, execRef));
            CtAnonymousExecutable staticBlock = factory.Core().createAnonymousExecutable();
            staticBlock.addModifier(ModifierKind.STATIC);
            staticBlock.setBody(initBlock);
            getInstance().staticBlockByClass.put(currentKey, staticBlock);
        }

    }

    public static void addAllFieldsAndMethods(Factory factory) {
        //maps have the sames key
        for(String currentKey : getInstance().listOfFieldByClass.keySet()) {
            //Add all fields at top of the current class
            CtType clazz = factory.Type().get(currentKey);
            if (!(clazz instanceof CtClass)) {
                return;
            }
            if (clazz == null)
                System.out.println(clazz);
            for (CtField field : getInstance().listOfFieldByClass.get(currentKey)) {
                clazz.addFieldAtTop(field);
            }

            //Add Methods
            List<CtMethod> methods = getInstance().methodsByClass.get(currentKey);
            for (CtMethod method : methods) {
                clazz.addMethod(method);
            }
            //Add static block init
            List<CtAnonymousExecutable> anonymousExecutables = new ArrayList<CtAnonymousExecutable>();
            List<CtAnonymousExecutable> existingExecutables = ((CtClass) clazz).getAnonymousExecutables();
            for (CtAnonymousExecutable existing : existingExecutables) {
                anonymousExecutables.add(existing);
            }

            //Put the static block in first statement
            SourcePosition position = factory.Core().createSourcePosition(clazz.getPosition().getCompilationUnit(),-1,-1,1,new int[0]);

            getInstance().staticBlockByClass.get(currentKey).setPosition(position);

            anonymousExecutables.add(getInstance().staticBlockByClass.get(currentKey));
            ((CtClass) clazz).setAnonymousExecutables(anonymousExecutables);


        }
        instance = null;
    }

}
