#ifndef COUNT_HEADER
#define COUNT_HEADER

namespace llvm {
  struct CountPP : public ModulePass {
    static char ID; // Pass identification
    CountPP() : ModulePass(ID) {}
    bool runOnModule(Module &);
    bool runOnFunction(Function &, Module &, int &);
  };
}

#endif
