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
#include "llvm/Transforms/Utils/BasicBlockUtils.h" /* ReplaceInstWithInst */
#include "llvm/Support/CommandLine.h"


#include "llvm/IR/IRBuilder.h"
#include "llvm/IR/LLVMContext.h"

#include <stdlib.h>     /* srand, rand */
#include <map>
#include <vector>
#include <time.h> /* time */
//  #include <string>

#include "Random.hpp"

using namespace llvm;

int first = 1;

PerturbationPoint::PerturbationPoint(llvm::Instruction* instruction, Point p)
                  : instruction(instruction), point(p){}
PerturbationPoint::PerturbationPoint(llvm::Instruction* instruction, Point p, std::string arc)
                  : instruction(instruction), point(p), arc(arc){}

std::vector<PerturbationPoint*> perturb_points;
int pp = 0;
char PerturbeOperation::ID = 0;
static RegisterPass
  <PerturbeOperation> X("Random", "Make random adjustments to the code");

cl::opt<int> PerturbationIndex("pp", cl::desc("Specify the perturbation point to be perturbed"), cl::value_desc("number"));


CallInst * callPoneFunction( Module &M, BinaryOperator *op){
  //errs()<<"Insert dice call\n";
  Constant *hookFunc = M.getOrInsertFunction("pone", IntegerType::get(M.getContext(), 32));
  Function *hook= cast<Function>(hookFunc);
  IRBuilder<> builder(op);
  return builder.CreateCall(hook, llvm::NoneType::None, "perturbation");
}

bool PerturbeOperation::runOnModule(Module &M){
  bool modifyed  = false;
  for(Module::iterator F = M.begin(), E = M.end(); F != E; ++F) {
    runOnFunction(*F, M);
  }

  // At this point we have analysed the whole code and populated the vector
  // int pp_rand = random() % perturb_points.size();
  // errs() << "Inserting perturbation at point nr: " << pp_rand+1 << "/" << perturb_points.size() << "\n";

  int pp_rand = PerturbationIndex;

  if (auto* op = dyn_cast<BinaryOperator>(perturb_points[pp_rand]->instruction)) {
    switch (perturb_points[pp_rand]->instruction->getOpcode()) {
      // Commutative operators
      case Instruction::Add:
      case Instruction::FAdd:{
        switch (perturb_points[pp_rand]->point) {
          // Add is comutative, all pps is treated as the same
          case PerturbationPoint::Point::LONLEY_OPERAND: // Never suposed to get here
          case PerturbationPoint::Point::RESULT:
          case PerturbationPoint::Point::OPERAND_0:
          case PerturbationPoint::Point::OPERAND_1:{
            IRBuilder<> builder(op);
            Value* lhs = op->getOperand(0);

            auto pert = callPoneFunction(M, op);
            Value* inc = builder.CreateBinOp(Instruction::Add, lhs, pert, "inc");
            perturb_points[pp_rand]->instruction->setOperand(0, inc);
              break;
          }
        }
        break;
      }
      case Instruction::Sub:
      case Instruction::FSub:
      case Instruction::Mul:
      case Instruction::FMul:
      case Instruction::UDiv:
      case Instruction::SDiv:
      case Instruction::FDiv:
      case Instruction::URem:
      case Instruction::SRem:
      case Instruction::FRem:
      case Instruction::Shl:
      case Instruction::LShr:
      case Instruction::AShr:
      case Instruction::And:
      case Instruction::Or:
      case Instruction::Xor:{
        switch (perturb_points[pp_rand]->point) {
          case PerturbationPoint::Point::LONLEY_OPERAND: // Never suposed to get here
          case PerturbationPoint::Point::RESULT:{
            IRBuilder<> builder(op);
            Value* lhs = op->getOperand(0);
            Value* rhs = op->getOperand(1);
            Value* tmp = builder.CreateBinOp((Instruction::BinaryOps)perturb_points[pp_rand]->instruction->getOpcode(), lhs, rhs, "tmp");
            auto pert = callPoneFunction(M, op);
            Instruction* a = BinaryOperator::CreateAdd(tmp, pert, "tmp2");
            ReplaceInstWithInst(op, a);
            break;
          }
          case PerturbationPoint::Point::OPERAND_0:{
            IRBuilder<> builder(op);
            Value* lhs = op->getOperand(0);

            auto pert = callPoneFunction(M, op);
            Value* inc = builder.CreateBinOp(Instruction::Add, lhs, pert, "inc");
            perturb_points[pp_rand]->instruction->setOperand(0, inc);
            break;
          }
          case PerturbationPoint::Point::OPERAND_1:{
            IRBuilder<> builder(op);
            Value* lhs = op->getOperand(1);

            auto pert = callPoneFunction(M, op);
            Value* inc = builder.CreateBinOp(Instruction::Add, lhs, pert, "inc");
            perturb_points[pp_rand]->instruction->setOperand(1, inc);
              break;
          }
        }
        break;
      }
    }

  } // else if (OTHER OPERATOR TYPES GOES HERE){} OSV.

  return modifyed;
}

Function * monitor;

bool PerturbeOperation::runOnFunction(Function &F, Module &M) {
  // errs() << "Function: " << F.getName() << '\n';
  // Do not perturbe our perturbation algorithm!!!
  // Keep adding our perturbation schemes to this here, in the future do something more nice looking!

  if (F.getName() == "init_pone") {
    monitor = cast<Function>(&F);
    return true;
  }
  if (F.getName() == "pone") {return true;}
  if (F.getName() == "rdtsc") {return true;}


  // LLVMContext& C = F.getContext();
  for (Function::iterator bb = F.begin(), e = F.end(); bb != e; ++bb) {
    // For each operation inside a basic block
    for (BasicBlock::iterator i = bb->begin(), e = bb->end(); i != e; ++i) {
      if ((F.getName() == "main") && first && !(isa<AllocaInst>(i))) {
        first = 0;
        Instruction *newInst = CallInst::Create(monitor, "");
        bb->getInstList().insert(i, newInst);
      }

      Instruction* ii = &*(i);
      // Find all perturbation points inside of binary operators
      if (isa<BinaryOperator>(i)) {
        // Mark the perturbationpoints type and location according to a pre defined scheme!
        // See Random.hpp
        perturb_points.push_back(
          new PerturbationPoint(ii, PerturbationPoint::Point::OPERAND_0)
        );
        perturb_points.push_back(
          new PerturbationPoint(ii, PerturbationPoint::Point::OPERAND_1)
        );
        perturb_points.push_back(
          new PerturbationPoint(ii, PerturbationPoint::Point::RESULT)
        );

        pp = pp+3;

        // Minor speed opt
        if ( pp > PerturbationIndex ) { return false; }
      }
    }
  }
  return true;
}
