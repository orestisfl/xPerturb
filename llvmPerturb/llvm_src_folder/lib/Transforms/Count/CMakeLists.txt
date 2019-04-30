add_llvm_library( LLVMPerturbCount MODULE
  Count.cpp

  PLUGIN_TOOL
  opt
  )
