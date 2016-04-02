package processor;

import perturbation.location.PerturbationLocationImpl;
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
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.ModifierKind;
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

    public static List<String> perturbableTypes = new ArrayList<>();

    public static final String PACKAGE_NAME_PERTURBATION = "perturbation";

    public static final String QUALIFIED_NAME_PERTURBATOR = PACKAGE_NAME_PERTURBATION+".PerturbationEngine";

    public static final String QUALIFIED_NAME_LOCATION = PACKAGE_NAME_PERTURBATION+".location.PerturbationLocation";

    public static final String QUALIFIED_NAME_LOCATION_IMPL = PACKAGE_NAME_PERTURBATION+".location.PerturbationLocationImpl";

    private static final String INIT_METHOD_NAME = "initPerturbationLocation";

    static {
        perturbableTypes.add("char");

        perturbableTypes.add("byte");
        perturbableTypes.add("short");
        perturbableTypes.add("int");
        perturbableTypes.add("Integer");
        perturbableTypes.add("long");

        perturbableTypes.add("boolean");

        perturbableTypes.add("float");
        perturbableTypes.add("double");
    }

    private UtilPerturbation() {
        perturbator = null;
    }

    private static CtClass perturbator = null;

    private static Map<CtClass, List<CtMethod>> methodsByClass = new HashMap<>();

    private static Map<CtClass, List<CtField>>  listOfFieldByClass = new HashMap<>();

    private static Map<CtClass, CtAnonymousExecutable>  staticBlockByClass = new HashMap<>();

    private static int currentLocation = 0;

    public static boolean checkIsNotInPerturbatorPackage(CtElement candidate) {
        CtPackage parent =  candidate.getParent(CtPackage.class);
        return parent.getQualifiedName().startsWith(PACKAGE_NAME_PERTURBATION);
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

        currentLocation++;

        return factory.Code().createInvocation(typeAccess, execRef, args);

    }

    private static CtFieldRead addFieldLocationToClass(Factory factory, CtExpression argument, String typeOfPerturbation) {

        CtClass clazz = argument.getParent(new TypeFilter<CtClass>(CtClass.class) {
            @Override
            public boolean matches(CtClass element) {
                return element.getParent(CtClass.class) != null ? element.getModifiers().contains(ModifierKind.STATIC) : super.matches(element);
            }
        });


        if (!listOfFieldByClass.containsKey(clazz)) {
            listOfFieldByClass.put(clazz, new ArrayList<>());
            methodsByClass.put(clazz, new ArrayList<>());
        }

        String fieldName = "__L" + currentLocation;

        CtField fieldLocation = factory.Core().createField();
        fieldLocation.setSimpleName(fieldName);
        fieldLocation.setType(factory.Type().createReference(PerturbationLocationImpl.class));
        fieldLocation.addModifier(ModifierKind.PUBLIC);
        fieldLocation.addModifier(ModifierKind.STATIC);
        fieldLocation.setParent(clazz);

        listOfFieldByClass.get(clazz).add(fieldLocation);

        String position = argument.getPosition().getCompilationUnit().getFile().getName() + ":" + argument.getPosition().getLine();
        CtConstructorCall constructorCall = factory.Code().createConstructorCall(factory.Type().get(QUALIFIED_NAME_LOCATION_IMPL).getReference(),
                factory.Code().createLiteral(position), factory.Code().createLiteral(currentLocation), factory.Code().createLiteral(typeOfPerturbation));

        CtFieldWrite writeField = factory.Core().createFieldWrite();
        writeField.setVariable(fieldLocation.getReference());

        CtAssignment assignmentField = factory.Core().createAssignment();
        assignmentField.setAssigned(writeField);
        assignmentField.setAssignment(constructorCall);

        //Methods
        if (!methodsByClass.get(clazz).isEmpty()) {
            if (methodsByClass.get(clazz).get(methodsByClass.get(clazz).size()-1).getBody().getStatements().size() < 1000) {
                methodsByClass.get(clazz).get(methodsByClass.get(clazz).size()-1).getBody().insertEnd(assignmentField);
            } else
                addInitMethodTo(clazz, factory, assignmentField);
        } else
            addInitMethodTo(clazz, factory, assignmentField);

        //Build the read of the field for the parameter of the perturbation function
        CtFieldRead readFieldLocation = factory.Core().createFieldRead();
        readFieldLocation.setVariable(fieldLocation.getReference());

        return readFieldLocation;
    }

    private static void addInitMethodTo(CtClass clazz, Factory factory, CtStatement firstStatement) {

        CtTypeReference typeReference = clazz.getReference();

        CtMethod initMethod = factory.Core().createMethod();
        initMethod.setSimpleName(INIT_METHOD_NAME+(methodsByClass.get(clazz).size()));

        Set<ModifierKind> modifierKinds = new HashSet<ModifierKind>();
        modifierKinds.add(ModifierKind.PRIVATE);
        modifierKinds.add(ModifierKind.STATIC);
        initMethod.setModifiers(modifierKinds);
        initMethod.setType(factory.Type().VOID_PRIMITIVE);
        initMethod.setBody(factory.Code().createCtBlock(firstStatement));

        methodsByClass.get(clazz).add(initMethod);

        CtTypeAccess typeAccess = factory.Core().createTypeAccess();
        typeAccess.setType(typeReference);
        typeAccess.setAccessedType(typeReference);

        CtExecutableReference execRef = factory.Core().createExecutableReference();
        execRef.setDeclaringType(clazz.getReference());
        execRef.setSimpleName(INIT_METHOD_NAME+(methodsByClass.get(clazz).size()-1));
        execRef.setStatic(true);

        if (staticBlockByClass.containsKey(clazz)) {
            staticBlockByClass.get(clazz).getBody().insertEnd(factory.Code().createInvocation(typeAccess, execRef));
        } else {
            CtBlock initBlock = factory.Code().createCtBlock(factory.Code().createInvocation(typeAccess, execRef));
            CtAnonymousExecutable staticBlock = factory.Core().createAnonymousExecutable();
            staticBlock.addModifier(ModifierKind.STATIC);
            staticBlock.setBody(initBlock);
            staticBlockByClass.put(clazz, staticBlock);
        }

    }

    public static void addAllFieldsAndMethods(Factory factory) {
        CtClass perturbator = (CtClass) factory.Class().get(QUALIFIED_NAME_PERTURBATOR);

        CtField nbPerturbation = factory.Core().createField();
        nbPerturbation.setSimpleName("nbPerturbation");
        nbPerturbation.setAssignment(factory.Code().createLiteral(currentLocation));
        nbPerturbation.setType(factory.Type().createReference(int.class));
        nbPerturbation.addModifier(ModifierKind.PUBLIC);
        nbPerturbation.addModifier(ModifierKind.STATIC);
        nbPerturbation.addModifier(ModifierKind.FINAL);
        nbPerturbation.setParent(perturbator);

        perturbator.addField(nbPerturbation);

        //maps have the sames key
        for(CtClass clazz : listOfFieldByClass.keySet()) {
            //Add all fields at top of the current class
            for (CtField field : listOfFieldByClass.get(clazz)) {
                clazz.addFieldAtTop(field);
            }

            //Add Methods
            List<CtMethod> methods = methodsByClass.get(clazz);
            for (CtMethod method : methods) {
                clazz.addMethod(method);
            }
            //Add static block init
            List<CtAnonymousExecutable> anonymousExecutables = new ArrayList<CtAnonymousExecutable>();
            List<CtAnonymousExecutable> existingExecutables = clazz.getAnonymousExecutables();
            for (CtAnonymousExecutable existing : existingExecutables) {
                anonymousExecutables.add(existing);
            }

            //Put the static block in first statement
            SourcePosition position = factory.Core().createSourcePosition(clazz.getPosition().getCompilationUnit(),-1,-1,1,new int[0]);

            staticBlockByClass.get(clazz).setPosition(position);

            anonymousExecutables.add(staticBlockByClass.get(clazz));
            clazz.setAnonymousExecutables(anonymousExecutables);
        }
    }

}
