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
#include <stdlib.h>     /* srand, rand */
#include <map>
#include <vector>

struct PerturbationPoint {
  llvm::Instruction* instruction;
  enum Point { OPERAND_0, OPERAND_1, RESULT, LONLEY_OPERAND };
  Point point;
  bool has_arc = false;
  std::string arc = "";

  PerturbationPoint(llvm::Instruction* instruction, Point p)
                    : instruction(instruction), point(p){}
  PerturbationPoint(llvm::Instruction* instruction, Point p, std::string arc)
                    : instruction(instruction), point(p), arc(arc){}
};

std::map<std::string, int> operationMap;
std::vector<PerturbationPoint*> perturb_points;
int pp = 1;

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

  // Here we have analysed the whole code and populated the vector
  errs() << perturb_points.size() << "\n";
  int pp_rand = rand() % perturb_points.size();
  // TODO This does not seem to be random... Investigate!!!
  errs() << "Choose nr: " << pp_rand << "\n";

  if (perturb_points[pp_rand]->instruction->getOpcode() == Instruction::Add) {

    switch (perturb_points[pp_rand]->point) {
      case PerturbationPoint::Point::OPERAND_0:
        break;
      case PerturbationPoint::Point::OPERAND_1:{
        errs() << "Entered OPERAND_1"<<"\n";
        if (auto* op = dyn_cast<BinaryOperator>(perturb_points[pp_rand]->instruction)) {
          IRBuilder<> builder(op);
          Value* lhs = op->getOperand(0);
          Value* inc = builder.CreateBinOp(
            Instruction::Add,
            lhs,
            builder.getInt32(1),
            "inc"
          );
          perturb_points[pp_rand]->instruction->setOperand(1, inc);
        }
          break;
      }
      case PerturbationPoint::Point::RESULT:
        break;
      case PerturbationPoint::Point::LONLEY_OPERAND:
        break;
    }
  }
  return modifyed;
}

bool CountOperations::runOnFunction(Function &F, Module &M) {
  errs() << "Function: " << F.getName() << '\n';
  LLVMContext& C = F.getContext();

  for (Function::iterator bb = F.begin(), e = F.end(); bb != e; ++bb) {
    // For each operation inside a basic block
    for (BasicBlock::iterator i = bb->begin(), e = bb->end(); i != e; ++i) {
      Instruction* ii = &*(i);
      if (i->getOpcode() == Instruction::Add) {
        perturb_points.push_back(
          new PerturbationPoint(ii, PerturbationPoint::Point::OPERAND_0)
        );
        perturb_points.push_back(
          new PerturbationPoint(ii, PerturbationPoint::Point::OPERAND_1)
        );
        perturb_points.push_back(
          new PerturbationPoint(ii, PerturbationPoint::Point::RESULT)
        );
        // Mark the perturbationpoints found on the instruciton!
        Metadata * Ops[4];
        Ops[0] = MDString::get(C, std::to_string(pp));
        Ops[1] = MDString::get(C, std::to_string(pp+1));
        Ops[2] = MDString::get(C, std::to_string(pp+2));
        MDNode * N = MDTuple::get(C, Ops);
        i->setMetadata("perturbation-point", N);
        pp = pp+3;
      }
      // Counting number and types of opcodes
      if(operationMap.find(i->getOpcodeName()) == operationMap.end()) {
        operationMap[i->getOpcodeName()] = 1;
      } else {
        operationMap[i->getOpcodeName()] += 1;
      }
    }
  }
  return false;
}
