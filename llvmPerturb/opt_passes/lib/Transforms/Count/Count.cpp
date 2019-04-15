#define DEBUG_TYPE "operationMap"
#include "llvm/Pass.h"
#include "llvm/IR/Function.h"
#include "llvm/Support/raw_ostream.h"
#include <map>
using namespace llvm;
namespace {
  struct CountOperations : public FunctionPass {

    std::map<std::string, int> operationMap;
    static char ID;
    CountOperations() : FunctionPass(ID) {}

    virtual bool runOnFunction(Function &F) {
      errs() << "Function " << F.getName() << '\n';
       // For each Basic Block
      for (Function::iterator bb = F.begin(), e = F.end(); bb != e; ++bb) {

        //For each operation inside a basic block
        for (BasicBlock::iterator i = bb->begin(), e = bb->end(); i != e; ++i) {
          if(operationMap.find(i->getOpcodeName()) == operationMap.end()) {
            operationMap[i->getOpcodeName()] = 1;
          } else {
            operationMap[i->getOpcodeName()] += 1;
          }
        }
      }

      // Print the Map
      std::map <std::string, int>::iterator i = operationMap.begin();
      std::map <std::string, int>::iterator e = operationMap.end();
      while (i != e) {
        errs() << i->first << ": " << i->second << "\n";
        i++;
      }
      errs() << "\n";
      operationMap.clear();
      return false;
    }
  };
}
char CountOperations::ID = 0;
static RegisterPass<CountOperations> X("Count", "Counts opcodes per functions");
