# Nand2Tetris — Jack Analyzer (Java)

Java implementation of the **Jack Analyzer** from the [Nand2Tetris](https://www.nand2tetris.org/) course.  
Parses `.jack` high-level source code and outputs a structured XML representation of its syntax tree.

## Features
- Parses complete Jack programs into XML according to the Nand2Tetris grammar
- Supports classes, variables, subroutines, statements, expressions, and terms
- Produces XML output compatible with Nand2Tetris syntax test tools
- Includes a `Makefile` and `JackAnalyzer` shell script for easy compilation and execution

## Build & Run

### Using Makefile & JackAnalyzer script
```bash
# Compile the project
make

# Run the analyzer on a single file
./JackAnalyzer path/to/YourFile.jack

# Or analyze an entire directory
./JackAnalyzer path/to/YourDirectory
```

## Notes
- Converts Jack source code into a well-formed XML syntax tree, without code generation.
- Passed all official Nand2Tetris Jack Analyzer tests.
- Works with any Java 8+ installation.
- No external dependencies required.

