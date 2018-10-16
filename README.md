# Joana and Frameworks (JoFrames)

## About

Java Object-Sensitive ANAlysis (JOANA, http://joana.ipd.kit.edu) is an information flow control system for Java Bytecode, based on the T. J. Watson Libraries for Analysis (WALA, http://wala.sourceforge.net/wiki/index.php/Main_Page). JoFrames generates an artificial main method for framework-based applications to make them analyzable with JOANA as it requires a single entry method for an application and framework-based applications consist usually of more than one entry point. JoFrames includes an API as well as a command-line and graphical user interface.

## How to build

Curently, only Linux is supported.

The build process is automated through build scripts located in the scripts directory. "build-maven.sh" builds the project without running tests, but with generating the documentation that can be found afterwards in the directory "workspace/joframes-parent/target/site". "build-maven-with-tests.sh" and "build-maven-with-tests-benchmark.sh" build the project and run the tests once which does the first script or ten times for benchmarking. It is recommended to not run all tests unless it is necessary because they take up several hours to complete. All other scripts are help scripts and used throughout the first mentioned ones.

JoFrames depends on WALA and JOANA and on some other projects for the tests. Therefore, all of them, especially WALA and JOANA, have to be build once before JoFrames can be used. Executing a build script enables the possibility to import all JoFrames modules directly in Eclipse as maven projects and to use them. Additionaly, the modules com.ibm.wala.core, com.ibm.wala.shrike, com.ibm.wala.util, joana.api, joana.api.annotations.passon, joana.contrib.lib, joana.ifc.sdg.core, joana.ifc.sdg.graph, joana.ifc.sdg.irlsod, joana.ifc.sdg.mhpoptimization, joana.ifc.sdg.util, joana.ui.annotations, joana.ui.ifc.wala.console, joana.util, joana.wala.core, joana.wala.flowless, joana.wala.summary and joana.wala.util can be imported as simple Eclipse projects.

## Tests

All test cases for JoFrames were run at least ten times. The detailed results can be found under "docs/test-results.ods".

## License

The source code is released under the MIT-License.

Copyright (C) 2018, Martin Armbruster

