#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>
#include <time.h>

#include "libxml/tree.h"
#include "libxml/parser.h"
#include "libxml/xpath.h"
#include "libxml/xpathInternals.h"

#define PONE 0
#define MONE 1
#define BOOL 2
#define BUF_SIZE 80

#if defined(LIBXML_XPATH_ENABLED) && defined(LIBXML_SAX1_ENABLED) && \
    defined(LIBXML_OUTPUT_ENABLED)

//Execution help
static void usage(const char *name);
//Finds all perturbation points
static xmlXPathObjectPtr explore(xmlDocPtr doc, int pMode, int suppressOutput);
//Inserts perturbation in selected node
static void perturb(xmlNodeSetPtr nodes, int nodeToPerturb, int pMode, int iterationToPerturb, int maxIter);
//Lists all points found in explore
static void listPPoints(xmlNodeSetPtr nodes, FILE* output, xmlXPathContextPtr xpathCtx, xmlDocPtr doc);
//Creates a perturbation node needed for some cases
static xmlNodePtr createPerturbNode(xmlChar* var, int pMode, int iterationToPerturb, int maxIter);
//Same as above, but other cases
static void onePNode(xmlNodePtr cur, int pMode);



int main(int argc, char **argv)
{
  xmlDocPtr doc;
  //Looping variable
  int i;
  int pMode;
  //Standard values for perturbation
  int nodeToPerturb = 2;
  int iterationToPerturb = 0;
  //How much output
  int suppressOutput = 0;
  //If overriding stdout
  int outFile = 0;
  //Specific for perturbations within for.loops, states what the break point for the loop is
  int maxIter = 100;
  //Auxiliary variable for string manipulation
  char* p = malloc(sizeof(char)*100);
  FILE* out = malloc(sizeof(char)*200);

  if (argc < 3)
  {
     fprintf(stderr, "Error: wrong number of arguments.\n");
     usage(argv[0]);
     return(-1);
  }

  assert(argv[1]);
  assert(argv[2]);

  //Determining what perturbation mode is to be used.
  if (strcmp(argv[2], "MONE") == 0) pMode = MONE;
  else if (strcmp(argv[2], "PONE") == 0) pMode = PONE;
  else
  {
    fprintf(stderr, "Error: Invalid perturbation mode. Valid modes: PONE or MONE \n");
    return -1;
  }

  //Other execution options such as suppress, output and what point to perturb
  for (i = 1; i < argc; i++)
  {
    if (strcmp(argv[i], "-s") == 0) suppressOutput = 1;
  }

  if (argc > 3)
  {
    assert(argv[3]);
    if(strcmp(argv[3], "-o") == 0)
    {
      assert(argv[4]);
      out = fopen(argv[4] , "w");
      outFile = 1;
    }
    if (argc > 5)
    {
        if(strcmp(argv[5], "-i") == 0)
        {
            assert(argv[6]);
            nodeToPerturb = (int) strtol(argv[6], &p, 10);
        }
    }
  }
  //

  //Initiating libxml and our documents
  xmlInitParser();
  LIBXML_TEST_VERSION
  doc = xmlParseFile(argv[1]);
  if (doc == NULL)
  {
     fprintf(stderr, "Error: unable to parse file \"%s\"\n", argv[1]);
     usage(argv[0]);
  }

  //Gets a list of all perturbation nodes, if suppress = 0 also prints them
  xmlXPathObjectPtr xpathObj = explore(doc, pMode, suppressOutput);
  if (xpathObj->nodesetval == NULL)
  {
    usage(argv[0]);
    return -1;
  }

  //Limits the node we want to the nr of nodes
  nodeToPerturb = (nodeToPerturb >= xpathObj->nodesetval->nodeNr) ? xpathObj->nodesetval->nodeNr-1 : nodeToPerturb;
  if(!suppressOutput) printf("Perturbing node #%d.\n", nodeToPerturb);

  //Injects the perturbation itself
  perturb(xpathObj->nodesetval, nodeToPerturb, pMode, iterationToPerturb, maxIter);

  //How output of the perturbed code works
  if (outFile == 1)
  {
    xmlDocDump(out, doc);
    fclose(out);
  }
  else{
    xmlDocDump(stdout, doc);
  }

  xmlMemUsed();
  xmlXPathFreeObject(xpathObj);
  xmlFreeDoc(doc);
  xmlCleanupParser();
  xmlMemoryDump();

  return 0;
}

//Standard usage output
static void usage(const char *name)
{
    assert(name);
    fprintf(stderr, "\nUsage: %s <xml-file> <perturbation-mode>\n", name);
    fprintf(stderr, "Perturbation modes: PONE, MONE\n");
    fprintf(stderr, "Extra options:\n");
    fprintf(stderr, "<-o> (str) <output-file>\n");
    fprintf(stderr, "<-i> (int) <node-to-perturb>\n");
    fprintf(stderr, "Example : %s example.xml PONE -o example2.xml -i 2\n", name);
}



//Creates a list of all perturbable points in the code
static xmlXPathObjectPtr explore(xmlDocPtr doc, int pMode, int suppressOutput)
{

      xmlXPathContextPtr xpathCtx;
      //Manipulation variables as well as an auxiliary one
      xmlXPathObjectPtr xpathObj, xpathObj2;
      //XPath expressions
      xmlChar* xpathExpr  = malloc(4000+1);
      xmlChar* newPath  = malloc(4000+1);
      //Manipulation string for Path
      char auxString[4000];
      //Variable name
      xmlChar* c  = malloc(100+1);
      //List of nodes
      xmlNodeSetPtr nodes;
      //Naviagtion variable, current
      xmlNodePtr cur;
      //Loop Variable
      int i;

      // Create xpath evaluation context
      xpathCtx = xmlXPathNewContext(doc);
      if(xpathCtx == NULL)
      {
          fprintf(stderr,"Error: unable to create new XPath context\n");
          xmlFreeDoc(doc);
          return NULL;
      }

      //Defines the namespaces used by srcml
      xmlXPathRegisterNs(xpathCtx, (const xmlChar*) "src", (const xmlChar*) "http://www.srcML.org/srcML/src");
      xmlXPathRegisterNs(xpathCtx, (const xmlChar*) "cpp", (const xmlChar*) "http://www.srcML.org/srcML/cpp");

      //First xpath expression which looks for the names of all variables in the code.
      //It takes variable names, which are not arrays. The or part tells it to look for other variables defined on the same line
      xpathExpr = (xmlChar*) "//src:decl[src:type/src:name = 'int'][not(src:index)] | //src:decl[src:type/src:name = 'int']/following-sibling::src:decl";

      //Finds the nodes with the expression above
      xpathObj = xmlXPathEvalExpression(xpathExpr, xpathCtx);
      if(xpathObj == NULL)
      {
          fprintf(stderr,"Error: unable to evaluate xpath variable expression \"%s\"\n", xpathExpr);
          xmlXPathFreeContext(xpathCtx);
          xmlFreeDoc(doc);
          return NULL;
      }
      if (xpathObj->nodesetval == NULL)
      {
        printf("No declared variables.");
        return NULL;
      }

      nodes = xpathObj->nodesetval;
      xpathExpr = (xmlChar*) "";

      //Builds the xpath expression for the points themselves
      for(i = 0; i < nodes->nodeNr; i++)
      {
        //First gets the name of each variable from the list above
        cur = nodes ->nodeTab[i];
        cur = xmlFirstElementChild(nodes->nodeTab[i]);
        while(xmlStrcmp(cur->name, (const xmlChar*)"name"))
        {
          cur = cur->next;
        }
        c = xmlNodeGetContent(cur->xmlChildrenNode);
        //Checks if it's an inserted system variable
        if(xmlStrstr(c, (const xmlChar*) "cPerturbVar") == NULL)
        {
          //If not, appends neccessary xpath
          //Xpath checks for name, not coupled to a ++ or -- operator and not an array
          //(array part is not actually neccessary due to previous check)
          if(i==0)
          {
            strcpy(auxString, "//src:expr[src:name = '");
            strcat(auxString , (char*) c);
            strcat(auxString, "'][not(src:operator = '++')][not(src:operator = '--')][not(src:name/src:index)]");
          }
          else
          {
            strcat(auxString, " | ");
            strcat(auxString, "//src:expr[src:name = '");
            strcat(auxString, (char*) c);
            strcat(auxString, "'][not(src:operator = '++')][not(src:operator = '--')][not(src:name/src:index)]");
          }
        }
      }

      // Evaluate xpath expression
      newPath = (xmlChar*) auxString;
      xpathObj2 = xmlXPathEvalExpression(newPath, xpathCtx);
      if(xpathObj2 == NULL)
      {
        fprintf(stderr,"Error: unable to evaluate xpath expression \"%s\"\n", newPath);
        xmlXPathFreeContext(xpathCtx);
        xmlFreeDoc(doc);
        return NULL;
      }

      if(suppressOutput == 0)
      {
        listPPoints(xpathObj2->nodesetval, stdout, xpathCtx, doc);
      }
      // Cleanup of XPath data
      xmlXPathFreeContext(xpathCtx);

      return xpathObj2;
}



//Prints the poiuts found in explore
void listPPoints(xmlNodeSetPtr nodes, FILE* output, xmlXPathContextPtr xpathCtx, xmlDocPtr doc)
{

    xmlNodePtr cur, aux;
    int size;
    int i;
    //Checks if the node is in a for-loop or condition
    int inFor, inForC;

    assert(output);
    size = (nodes) ? nodes->nodeNr : 0;

    fprintf(output, "\nResult (%d perturbation points):\n", size);

    for(i = 0; i < size; i++)
    {
       inFor = 0;
       inForC = 0;

	     assert(nodes->nodeTab[i]);

       //Edge print cases
	     if(nodes->nodeTab[i]->type == XML_NAMESPACE_DECL)
       {
	        xmlNsPtr ns;
	        ns = (xmlNsPtr)nodes->nodeTab[i];
	        cur = (xmlNodePtr)ns->next;

          if(cur->ns)
          {
	           fprintf(output, "= namespace \"%s\"=\"%s\" for node %s:%s\n",
		         ns->prefix, ns->href, cur->ns->href, cur->name);
	        }
          else
          {
	           fprintf(output, "= namespace \"%s\"=\"%s\" for node %s\n",
		         ns->prefix, ns->href, cur->name);
	        }

	     }
       else if(nodes->nodeTab[i]->type == XML_ELEMENT_NODE)
       {

          cur = nodes->nodeTab[i];
          cur = cur->children;
          aux = cur->parent;

          //Checks different conditions if in for, control or condition
          while(aux != NULL)
          {
             //If it finds function it has gone too far
             if((xmlStrcmp(aux->name, (const xmlChar*)"function"))==0) break;
             aux = aux->parent;

             if((xmlStrcmp(aux->name, (const xmlChar*)"control"))==0) inForC = 1;
             if((xmlStrcmp(aux->name, (const xmlChar*)"for"))==0){
                inFor = 1;
                break;
             }
          }

          //Prints different cases
          if(cur->ns)
          {
             if(inForC==1)
             {
               fprintf(output, "Node #%d - node in a for-loop control block on line %ld\n", i, xmlGetLineNo(cur)-1);
             }
             if(inFor==1 && inForC==0)
             {
                fprintf(output, "Node #%d - node in a for-loop condition block on line %ld\n", i, xmlGetLineNo(cur)-1);
             }
             if(inFor==0)
             {
                fprintf(output, "Node #%d - node on line %ld\n", i, xmlGetLineNo(cur)-1);
             }
	        }

          else
          {
    	       fprintf(output, "= element node \"%s\"\n",
		         cur->name);
	        }
	        } else
          {
	           cur = nodes->nodeTab[i];
	           fprintf(output, "= node \"%s\": type %d\n", cur->name, cur->type);
	        }
    }
    fprintf(output, "\n");
}



//Injects the perturbation itself
static void perturb(xmlNodeSetPtr nodes, int nodeToPerturb, int pMode, int iterationToPerturb, int maxIter)
{

    //Different scenario variables
    int inFor, inForC, inDecl, isIndex, inTernary, isFArg;
    //Name of the variable
    xmlChar* c = malloc(40+1);
    //Navigation variables
    xmlNodePtr cur, aux, decl;

    //Initialization
    //if in a for loop
    inFor = 0;
    //if in for control
    inForC = 0;
    //if in declaration of variable
    inDecl = 0;
    //if is index to an array
    isIndex = 0;
    //if in a ternary operator
    inTernary = 0;
    //if argument in a function call
    isFArg = 0;

    assert(nodes->nodeTab[nodeToPerturb]);
    cur = nodes->nodeTab[nodeToPerturb];
    aux = cur->parent;

    //Checks different scenarios
    while(aux != NULL)
    {
       if((xmlStrcmp(aux->name, (const xmlChar*)"function"))==0) break;
       if((xmlStrcmp(aux->name, (const xmlChar*)"call"))==0) isFArg = 1;
       if((xmlStrcmp(aux->name, (const xmlChar*)"control"))==0) inForC = 1;
       if((xmlStrcmp(aux->name, (const xmlChar*)"index"))==0) isIndex = 1;
       if((xmlStrcmp(aux->name, (const xmlChar*)"ternary"))==0) inTernary = 1;
       if((xmlStrcmp(aux->name, (const xmlChar*)"for"))==0) inFor = 1;
       //Decl needs an extra step for navigation purposes
       if((xmlStrcmp(aux->name, (const xmlChar*)"decl"))==0)
       {
         decl = aux;
         inDecl = 1;
       }
       aux = aux->parent;
    }

    //Slightly different procedures for different cases, mostly due to different position of pointsrs

    if (isFArg == 1)
    {
        //Navigate
        cur = xmlGetLastChild(cur);
        //Perturb
        onePNode(cur,pMode);
    }

    else if(iterationToPerturb == -1 || inForC == 1 || inFor == 0 || isIndex ==1 || inTernary == 1)
    {

        //Navigate to right place
        if (inDecl == 1)
        {
          cur = xmlGetLastChild(decl);
        }

        else
        {
          while(cur->next != NULL)
          {
            cur = cur->next;
          }
          cur = cur->prev;
        }
        //Insert perturbation
        onePNode(cur, pMode);
    }

    else if(inForC == 0 && inFor==1 && isIndex == 0)
    {
      //Navigate
      aux = cur->children;
      c = aux->children->content;
      cur = cur->parent;
      //Perturb
      xmlAddNextSibling(cur, createPerturbNode(c, pMode, iterationToPerturb, maxIter));
    }

    //Edge case
    if (nodes->nodeTab[nodeToPerturb]->type != XML_NAMESPACE_DECL) nodes->nodeTab[nodeToPerturb] = NULL;
}



//One of the perturbation methods
static void onePNode(xmlNodePtr cur, int pMode)
{

    //Creates the perturbation
    xmlNodePtr new;
    new = xmlNewNode(NULL, (const xmlChar*) "operator");

    //Sign
    if(pMode == PONE) xmlNodeAddContent(new, (const xmlChar*) "+");
    if(pMode == MONE) xmlNodeAddContent(new, (const xmlChar*) "-");

    xmlAddNextSibling(cur, new);

    cur = new;
    new = xmlNewNode(NULL, (const xmlChar*) "literal");
    xmlNewProp(new, (const xmlChar*)"type", (const xmlChar*)"number");
    xmlNodeAddContent(new, (const xmlChar*) "1");

    //Adds perturbation
    xmlAddNextSibling(cur, new);
}




//Perturbation method for for-loops
static xmlNodePtr createPerturbNode(xmlChar* var, int pMode, int iterationToPerturb, int maxIter)
{

    //Navigation variables
    xmlNodePtr node, cur, par, aux;

    char* value = malloc(40+1);;
    //Auxiliary variables
    xmlChar* var2 = malloc(40+1);;
    var2 = var;

    //Starts creating the node
    node = xmlNewNode(NULL, (const xmlChar*) "if");
    xmlNodeSetContent(node, (const xmlChar*) "\n\t\t\tif");

    cur = node;

    //Starts creating the condition
    cur = xmlNewTextChild(cur, NULL, (const xmlChar*) "condition", (const xmlChar*) "(");
    par = cur;
    cur = xmlNewTextChild(cur, NULL, (const xmlChar*) "expr", NULL);
    xmlNewTextChild(cur, NULL, (const xmlChar*) "name", var);
    xmlNewTextChild(cur, NULL, (const xmlChar*) "operator", (const xmlChar*) "==");

    //If iteration to perturb is defined it uses it, otherwise gets a random iteration
    if (iterationToPerturb == 0)
    {
      srand(time(NULL));
      snprintf(value, 100000, "%d", rand()%maxIter);
    }
    else snprintf(value, 100000, "%d", iterationToPerturb);

    //Finalizes condition
    aux = xmlNewTextChild(cur, NULL, (const xmlChar*) "literal", (xmlChar*) value);
    xmlNewProp(aux, (const xmlChar*) "type", (const xmlChar*) "number");
    cur = par;
    xmlNodeAddContent(cur, (const xmlChar*) ") ");

    //Starts building then-block
    cur = xmlNewTextChild(node, NULL,(const xmlChar*) "then", NULL);
    cur = xmlNewTextChild(cur, NULL, (const xmlChar*) "block", NULL);
    xmlNewProp(cur, (const xmlChar*) "type", (const xmlChar*) "pseudo");
    cur = xmlNewTextChild(cur, NULL, (const xmlChar*) "expr_stmt", NULL);
    par = cur;
    cur = xmlNewTextChild(cur, NULL, (const xmlChar*) "expr", NULL);
    xmlNewTextChild(cur, NULL, (const xmlChar*) "name", var2);

    //Chooses operator depending on mode
    switch (pMode)
    {
      case PONE:
        xmlNewTextChild(cur, NULL, (const xmlChar*) "operator", (const xmlChar*) "++");
        break;
      case MONE:
        xmlNewTextChild(cur, NULL, (const xmlChar*) "operator", (const xmlChar*) "--");
        break;
      default:
        fprintf(stderr, "Error: Invalid perturbation mode.");
        return NULL;
    }


    cur = par;
    xmlNodeAddContent(cur, (const xmlChar*) ";");
    //Perturbation done

    return node;
}


//Libxml checks
#else
int main(void)
{
    fprintf(stderr, "XPath support not compiled in\n");
    exit(1);
}
#endif
