#ifndef PERTUREBE_HEADER
#define PERTUREBE_HEADER

namespace llvm {
  struct PerturbeOperation : public ModulePass {
    static char ID; // Pass identification
    PerturbeOperation() : ModulePass(ID) {}
    bool runOnModule(Module &M);
    bool runOnFunction(Function &F, Module &M);
  };
}
// 
// CallInst * callInitPoneFunction( Module &, Instruction *);
// CallInst * callPoneFunction( Module &, BinaryOperator *);


struct PerturbationPoint {
  llvm::Instruction* instruction;
  enum Point { OPERAND_0, OPERAND_1, RESULT, LONLEY_OPERAND };
  Point point;
  bool has_arc = false;
  std::string arc = "";

  PerturbationPoint(llvm::Instruction* instruction, Point p);
  PerturbationPoint(llvm::Instruction* instruction, Point p, std::string arc);
};

void printMap(std::map<std::string, int> m);

extern std::map<std::string, int> operationMap;
extern std::vector<PerturbationPoint*> perturb_points;



#endif
