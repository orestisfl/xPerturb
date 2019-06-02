#include "llvm/Pass.h"
#include "llvm/IR/Type.h"
#include "llvm/IR/Module.h"
#include "llvm/IR/Function.h"
#include "llvm/IR/BasicBlock.h"
#include "llvm/IR/Instruction.h"
#include "llvm/IR/Instructions.h"
#include "llvm/IR/GlobalVariable.h"
#include "llvm/Support/raw_ostream.h" /* outs, errs */
#include "llvm/IR/SymbolTableListTraits.h"
#include "llvm/Transforms/Utils/BasicBlockUtils.h" /* ReplaceInstWithInst */

#include "llvm/IR/IRBuilder.h"
#include "llvm/IR/LLVMContext.h"

#include "Count.hpp"

using namespace llvm;

char CountPP::ID = 0;

static RegisterPass<CountPP> X ("Count", "Count perturbation points");

bool CountPP::runOnModule(Module &M){
  int pp_counter = 0;
  bool modifyed = false;
  for(Module::iterator F = M.begin(), E = M.end(); F != E; ++F) {
        modifyed = runOnFunction(*F, M, pp_counter);
  }
  outs() << pp_counter << "\n";
  return modifyed;
}

bool CountPP::runOnFunction(Function &F, Module &M, int &pp_counter){
  for (Function::iterator bb = F.begin(), e = F.end(); bb != e; ++bb) {
    // For each operation inside a basic block
    for (BasicBlock::iterator i = bb->begin(), e = bb->end(); i != e; ++i) {
      // Find all perturbation points inside of binary operators
      if (isa<BinaryOperator>(i)) {
        pp_counter = pp_counter+3;
      }
    }
  }
  return false;
}
