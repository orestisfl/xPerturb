#include "llvm/Pass.h"
#include "llvm/IR/Type.h"
#include "llvm/IR/Module.h"
#include "llvm/IR/Function.h"
#include "llvm/IR/BasicBlock.h"
#include "llvm/IR/Instruction.h"
#include "llvm/IR/Instructions.h"
#include "llvm/IR/GlobalVariable.h"
#include "llvm/Support/raw_ostream.h"
#include "llvm/IR/SymbolTableListTraits.h"

#include "llvm/IR/IRBuilder.h"
#include "llvm/IR/LLVMContext.h"

#include "llvm/Support/raw_ostream.h"
#include <stdlib.h>     /* srand, rand */
#include <map>
#include <vector>
#include <time.h> /* time */

#include "Random.hpp"

using namespace llvm;

PerturbationPoint::PerturbationPoint(llvm::Instruction* instruction, Point p)
                  : instruction(instruction), point(p){}
PerturbationPoint::PerturbationPoint(llvm::Instruction* instruction, Point p, std::string arc)
                  : instruction(instruction), point(p), arc(arc){}

std::vector<PerturbationPoint*> perturb_points;
int pp = 1;
char PerturbeOperation::ID = 0;
static RegisterPass
  <PerturbeOperation> X("Random", "Make random adjustments to the code");

CallInst * callLinkedFunction( Module &M, BinaryOperator *op){
  Constant *hookFunc = M.getOrInsertFunction("pone", IntegerType::get(M.getContext(), 32));
  Function *hook= cast<Function>(hookFunc);
  IRBuilder<> builder(op);
  return builder.CreateCall(hook, llvm::NoneType::None, "perturbation");
}

bool PerturbeOperation::runOnModule(Module &M){
  bool modifyed  = false;
  for(Module::iterator F = M.begin(), E = M.end(); F != E; ++F) {
    modifyed = runOnFunction(*F, M);
  }
  srandom(time(0));

  // At this point we have analysed the whole code and populated the vector
  int pp_rand = random() % perturb_points.size();
  errs() << "Inserting perturbation at point nr: " << pp_rand << "/" << perturb_points.size() << "\n";
  errs() << "Instruction to perturbe: \"" << perturb_points[pp_rand]->instruction->getOpcodeName() << "\"\n";

  if (auto* op = dyn_cast<BinaryOperator>(perturb_points[pp_rand]->instruction)) {
    if (perturb_points[pp_rand]->instruction->getOpcode() == Instruction::Add) {
      switch (perturb_points[pp_rand]->point) {
        // Add is comutative, all pps is treated as the same
        case PerturbationPoint::Point::LONLEY_OPERAND: // Never suposed to get here
        case PerturbationPoint::Point::RESULT:
        case PerturbationPoint::Point::OPERAND_0:
        case PerturbationPoint::Point::OPERAND_1:{
          IRBuilder<> builder(op);
          Value* lhs = op->getOperand(0);

          auto pert = callLinkedFunction(M, op);
          Value* inc = builder.CreateBinOp(Instruction::Add, lhs, pert, "inc");
          perturb_points[pp_rand]->instruction->setOperand(0, inc);
            break;
        }
      }
    }// else if (OTHER BINARY OPERATOR GOES HERE){} OSV.
  } // else if (OTHER OPERATOR GOES HERE){} OSV.

  return modifyed;
}

bool PerturbeOperation::runOnFunction(Function &F, Module &M) {
  // errs() << "Function: " << F.getName() << '\n';
  // Do not perturbe our perturbation algorithm!!!
  // Keep adding our perturbation schemes to this here, in the future do something more nice looking!
  if (F.getName() == "pone") {return false;}

  LLVMContext& C = F.getContext();
  for (Function::iterator bb = F.begin(), e = F.end(); bb != e; ++bb) {
    // For each operation inside a basic block
    for (BasicBlock::iterator i = bb->begin(), e = bb->end(); i != e; ++i) {
      Instruction* ii = &*(i);
      // Find all perturbation points inside of binary operators
      if (isa<BinaryOperator>(i)) {
        // Mark the perturbationpoints type and location according to a pre defined scheme!
        // See Random.hpp
        // errs() << i->getOpcodeName() << "\n";
        perturb_points.push_back(
          new PerturbationPoint(ii, PerturbationPoint::Point::OPERAND_0)
        );
        perturb_points.push_back(
          new PerturbationPoint(ii, PerturbationPoint::Point::OPERAND_1)
        );
        perturb_points.push_back(
          new PerturbationPoint(ii, PerturbationPoint::Point::RESULT)
        );

        Metadata * Ops[3];
        Ops[0] = MDString::get(C, std::to_string(pp));
        Ops[1] = MDString::get(C, std::to_string(pp+1));
        Ops[2] = MDString::get(C, std::to_string(pp+2));
        MDNode * N = MDTuple::get(C, Ops);

        i->setMetadata("perturbation-point", N);
        pp = pp+3;
      }
    }
  }
  return false;
}
