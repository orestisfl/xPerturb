#define DEBUG_TYPE "operationMap"
#include "llvm/Pass.h"
#include "llvm/IR/Module.h"
#include "llvm/IR/Function.h"
#include "llvm/IR/BasicBlock.h"
#include "llvm/IR/Instructions.h"
#include "llvm/IR/GlobalVariable.h"
#include "llvm/Support/raw_ostream.h"

#include "llvm/IR/IRBuilder.h"
#include "llvm/IR/LLVMContext.h"


#include "llvm/Support/raw_ostream.h"
#include <map>
#include <vector>

std::map<std::string, int> operationMap;
std::vector<llvm::Instruction*> perturb_points;

void printMap(std::map<std::string, int> m){
  std::map <std::string, int>::iterator i = m.begin();
  std::map <std::string, int>::iterator e = m.end();
  while (i != e) {
    llvm::errs() << i->first << ": " << i->second << "\n";
    i++;
  }
  llvm::errs() << "\n";
}

using namespace llvm;
namespace {

  struct CountOperations : public ModulePass {
    static char ID; // Pass identification
    CountOperations() : ModulePass(ID) {}
    bool runOnModule(Module &M);
    bool runOnFunction(Function &F, Module &M);

  }; // Struct CountOperations - End
} // Namespace - End

char CountOperations::ID = 0;

static RegisterPass
  <CountOperations> X("Count", "Counts opcodes per functions");

bool CountOperations::runOnModule(Module &M){
  bool modifyed  = false;
  for(Module::iterator F = M.begin(), E = M.end(); F != E; ++F) {
        modifyed = runOnFunction(*F, M);
  }
  printMap(operationMap);

  return modifyed;
}

bool CountOperations::runOnFunction(Function &F, Module &M) {
  errs() << "Function: " << F.getName() << '\n';
   // For each basic block
  for (Function::iterator bb = F.begin(), e = F.end(); bb != e; ++bb) {
    // For each operation inside a basic block
    for (BasicBlock::iterator i = bb->begin(), e = bb->end(); i != e; ++i) {
      Instruction* ii = &*(i);
      // Increment lhs in an add statement by adding and modifying the IR
      if (auto* op = dyn_cast<BinaryOperator>(i)) {
        IRBuilder<> builder(op);
        Value* lhs = op->getOperand(0);
        Value* inc = builder.CreateBinOp(Instruction::Add, lhs, builder.getInt32(1), "inc");
        i->setOperand(0, inc);
      }
      // Counting number and types of opcodes
      if (i->getOpcode() == Instruction::Add) {
        perturb_points.push_back(ii);
      }
      if(operationMap.find(i->getOpcodeName()) == operationMap.end()) {
        operationMap[i->getOpcodeName()] = 1;
      } else {
        operationMap[i->getOpcodeName()] += 1;
      }
    }
  }
  return false;
}
