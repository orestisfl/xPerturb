#define DEBUG_TYPE "operationMap"
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

char PerturbeOperation::ID = 0;

static RegisterPass
  <PerturbeOperation> X("Random", "Make random adjustments to the code");

void createFunctionFromScratch(Module &M){
  Constant* c = M.getOrInsertFunction("mul_add",
  /*ret type*/                           IntegerType::get(M.getContext(), 32),
  /*args*/                               IntegerType::get(M.getContext(), 32),
                                         IntegerType::get(M.getContext(), 32),
                                         IntegerType::get(M.getContext(), 32)
  /*varargs terminated with null*/       );

  Function* mul_add = cast<Function>(c);
  mul_add->setCallingConv(CallingConv::C);
  Function::arg_iterator args = mul_add->arg_begin();
  Value* x = args++;
  x->setName("x");
  Value* y = args++;
  y->setName("y");
  Value* z = args++;
  z->setName("z");

  BasicBlock* block = BasicBlock::Create(M.getContext(), "entry", mul_add);
  IRBuilder<> _builder(block);

  Value* tmp = _builder.CreateBinOp(Instruction::Mul,
                                    x, y, "tmp");
  Value* tmp2 = _builder.CreateBinOp(Instruction::Add,
                                    tmp, z, "tmp2");

  _builder.CreateRet(tmp2);

}

void callLinkedFunction( Module &M, BinaryOperator *op){
  Constant *hookFunc = M.getOrInsertFunction("pone", Type::getVoidTy(M.getContext()));
  Function *hook= cast<Function>(hookFunc);


  // Instruction *newInst = CallInst::Create(hook, "");
  // perturb_points[pp_rand]->instruction->getParent()->getInstList().insert((Instruction*)op, newInst);
  IRBuilder<> builder(op);
  builder.CreateCall(hook);
}

bool PerturbeOperation::runOnModule(Module &M){
  bool modifyed  = false;

  // createFunctionFromScratch(M);

  for(Module::iterator F = M.begin(), E = M.end(); F != E; ++F) {
        modifyed = runOnFunction(*F, M);
  }
  printMap(operationMap);
  srandom(time(0));

  // At this point we have analysed the whole code and populated the vector

  int pp_rand = random() % perturb_points.size();
  errs() << "Choose nr: " << pp_rand << "/" << perturb_points.size() << "\n";

  callLinkedFunction(M, dyn_cast<BinaryOperator>(perturb_points[pp_rand]->instruction));

  if (perturb_points[pp_rand]->instruction->getOpcode() == Instruction::Add) {
    switch (perturb_points[pp_rand]->point) {
      case PerturbationPoint::Point::OPERAND_0:
        break;
      case PerturbationPoint::Point::OPERAND_1:{
        errs() << "Entered OPERAND_1"<<"\n";
        if (auto* op = dyn_cast<BinaryOperator>(perturb_points[pp_rand]->instruction)) {

          // Constant *hookFunc = M.getOrInsertFunction("pone", Type::getVoidTy(M.getContext()));
          // Function *hook= cast<Function>(hookFunc);
          //
          //
          // // Instruction *newInst = CallInst::Create(hook, "");
          // // perturb_points[pp_rand]->instruction->getParent()->getInstList().insert((Instruction*)op, newInst);
          //
          //
          IRBuilder<> builder(op);
          // builder.CreateCall(hook);
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

bool PerturbeOperation::runOnFunction(Function &F, Module &M) {
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
