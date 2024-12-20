# BahnDSL: A Domain-Specific Language for Configuring and Modelling Model Railways

[![CI/CD](https://github.com/trinnguyen/bahndsl/actions/workflows/gradle.yml/badge.svg?branch=master)](https://github.com/trinnguyen/bahndsl/actions)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/trinnguyen/bahndsl?style=social)](https://github.com/trinnguyen/bahndsl/releases)

![Screen Shot 2021-08-03 at 22 57 28](https://user-images.githubusercontent.com/7035180/128086359-643f8a4b-61f2-4f93-8780-ffdb69c96e40.png)


## Installation

### Requirements
- Java SE 17: https://adoptopenjdk.net
- C Compiler (clang or gcc) for compiling shared libraries
  + macOS: clang is provided by Xcode
  + Linux (Ubuntu): `apt install clang`
  + Windows:
    + clang (LLVM-[VERSION]-win64.exe): https://github.com/llvm/llvm-project/releases
    + gcc (via MSYS2): https://packages.msys2.org/package/mingw-w64-x86_64-gcc

### bahnc (Bahn Compiler CLI)
- [macOS/Linux/Windows](https://github.com/trinnguyen/bahndsl/releases/latest) `bahnc-<version>.zip`

### Bahn IDE
- macOS: [Intel](https://github.com/trinnguyen/bahndsl/releases/latest/download/BahnIDE-macosx.cocoa.x86_64.tar.gz), [Apple Silicon](https://github.com/trinnguyen/bahndsl/releases/latest/download/BahnIDE-macosx.cocoa.aarch64.tar.gz)
  + Remove the quarantine flag after downloading and unzipping: `xattr -c "Bahn IDE.app"`
- [Linux](https://github.com/trinnguyen/bahndsl/releases/latest/download/BahnIDE-linux.gtk.x86_64.tar.gz)
- [Windows](https://github.com/trinnguyen/bahndsl/releases/latest/download/BahnIDE-win32.win32.x86_64.zip)


## Usage

### bahnc (command-line compiler)

```
OVERVIEW: Bahn compiler 1.0.6

USAGE: bahnc [-o <path>][-r <route>] [-m <mode>] [-v] [-d] file
  -o <path>     output folder
  -r <route>    route generation mode (simple, extended)
  -m <mode>     code generation mode (default, c-code, library)
  -v            verbose output
  -d            debug output

EXAMPLE: 
  bahnc example.bahn
  bahnc -r simple -m library -v example.bahn
  bahnc -o output/src-gen example.bahn
```

- Options and usage:
  - Add `-o <path>` to change output folder. Default is `src-gen`
  - Add `-r <route>` to change the type of routes to generate. `simple` only generates routes between any two signals with no intermediate signals. `extended` generates routes between any two signals with zero or more intermediate signals
  - Add `-m <mode>` to specify the type of outputs to generate
  - Add `-v` to enable verbose output
  - Add `-d` for detailed log level (recommended for development only) 

- Examples:
  - Generate YAML files (with simple routes) and SCCharts models with verbose output
  ```
  bahnc -v example.bahn
  ```
  - Generate YAML files (with extended routes) and SCCharts models
  ```
  bahnc -r extended example.bahn
  ```
  - Generate YAML files (with simple routes) and C code using embedded SCCharts compiler
  ```
  bahnc -m c-code -v example.bahn
  ```
  - Compile YAML files (with simple routes) and a shared C library using C compiler (cc/clang/gcc)
  ```
  bahnc -m library -v example.bahn
  ```

### Bahn IDE (Eclipse-based)

- Create a new project
  - Click File -> New -> Bahn Project
  - Specify a project name
  - Click finish
  - A new project is created, containing a default BahnDSL model, called `untitled.bahn`
- Create new BahnDSL model
  - Click File -> New -> Bahn File
  - Specify a file name
  - A new BahnDSL model is created, containing an empty railway configuration model and interlocking functions

- Output
  - Generates YAML configuration files, SCCharts models, and a railway layout diagram in a `src-gen` folder
- Generate C code:
  - Right-click on a Bahn model in the Project Explorer, and select "Generate C Code"
- Compile into a shared C library:
  - Right-click on a Bahn model in the Project Explorer, and select "Compile to shared C library"


## Getting started

- A BahnDSL model contains two parts: railway resource configuration and interlocking functions. A minimal Bahn model with all the necessary sections is shown below:

```ruby
module standard
   boards
     master 0x00
   end

   segments master
   end

   signals master
   end

   points master
   end
   
   peripherals master
   end

   blocks
   end

   crossings
   end

   layout
   end

   trains
   end
end

def request_route(string src_signal_id, string dst_signal_id, string train_id): string
   return ""
end

def drive_route(string route_id, string train_id, string segment_ids[])
end
```

- The formal syntax of all elements is presented in the next section with examples

## Syntax

### Configuration

```ruby
module module-name
  boards 
    board
  end

  segments board-name 
    segment 
  end

  signals board-name 
    signal 
  end

  points board-name 
    point 
  end

  blocks 
    block 
  end

  crossings 
    crossing 
  end

  layout 
    connector 
  end

  trains 
    train 
  end
end
```

### Board
- Syntax
```
board-name hex-value 
  features 
    feature-hex-key : feature-hex-value 
  end
```

- Example
```ruby
master 0xDA000D680052EF
  features
  0x03:0x14
  0x6E:0x00
  end
```

### Segment
- Syntax
```
segment-name hex-number length number length-unit
```

- Example
```ruby
seg1 0x00 length 10cm
```

### Signal
- Syntax
```
signal-type-name signal-name hex-number
```

- Signal type names are defined in the [standard library](https://github.com/trinnguyen/bahndsl/blob/master/src/de.uniba.swt.dsl/resources/standardlib.bahn)

- Composite signal (compound signal)
```ruby
composite signal-name 
  signals
    reference-signal-name-1
    reference-signal-name-2
    ...
    reference-signal-name-n
  end
```

- Example
```ruby
entry signal1 0x00
distant signal2 0x01

composite signal3
  signals
    signal1
    signal2
  end
```

### Point
- Syntax
```
point-name hex-number segment segment-name 
  normal hex-number 
  reverse hex-number 
  initial initial-aspect
```

- Example
```ruby
point1 0x00 segment seg4 normal 0x01 reverse 0x00 initial normal
```

### Crossing
- Syntax
```
crossing-name segment segment-name
```

- Example
```ruby
crossing1 segment seg35
```

### Peripheral
- Syntax
```
peripheral-type-name peripheral-name hex-number port hex-number
```
- Peripheral type names are defined in the [standard library](https://github.com/trinnguyen/bahndsl/blob/master/src/de.uniba.swt.dsl/resources/standardlib.bahn)

- Example
```ruby
onebit lantern 0x0A port 0x0027
```

### Train
- Syntax
```
train-name hex-number steps number 
  calibration 
    [number] 
  end
  weight number weight-unit
  length number length-unit
  type train-type
  peripherals
    peripheral-name bit number initial number
  end
```

- Example
```ruby
cargo_green 0x0006 steps 126 
  calibration 
    5 15 30 45 60 75 90 105 120 
  end 
  weight 100g 
  length 13cm 
  type cargo
  peripherals
    head_light bit 4 initial 1
  end
```

### Block
- Syntax
```
block-name overlap segment-name main segment-name overlap segment-name 
  reversed limit number speed-unit
  trains 
    train-type
  end
```

- Example
```ruby
block1 overlap seg20 main seg19 overlap seg18
  trains
    cargo 
    passenger
  end
```

### Platform (reuse syntax from block)
```
platform-name overlap segment-name main segment-name overlap segment-name 
  limit number speed-unit
  trains 
    train-type
  end
```

- Example
```ruby
platform1 overlap seg36 main seg37 overlap seg38
```


### Layout Connector
- Syntax
```
element-name.endpoint connector element-name.endpoint
```

- Layout Connector
```
stem | straight | side | down | up | down1 | down2 | up1 | up2
```

- Example

```ruby
point6.stem -- block1.down
```

```ruby
point6.stem -- block1.down -- block1.up
```

```ruby
point6.stem -- block1.down -> block1.up
```

## Interlocking behaviours

### Entry functions
- `request_route` is invoked by the SWTbahn-cli for selecting and grating route to train
- `drive_route` is invoked by SWTbahn-cli for monitoring physical track elements on the railway model during train driving session
- `request_route` is required to build the interlocking shared library. `drive_route` is optional.
```ruby
def request_route(string src_signal_id, string dst_signal_id, string train_id): string
   return ""
end
def drive_route(string route_id, string train_id, string segment_ids[])
end
```


### Data Type: Scalar
- Four primary types:
```c
int
float
bool
string
```

- Object, struct or custom data type are not supported
- Array is supported

- Example
```c
int index = 0
float rate = 0.5
bool valid = true
string text = "hello world"
string ids[] = { "route1", "route2" }
```

### Variables declaration
- Syntax
```
data-type name[]
```

- Example
```c
int index
float rate
string ids[]
```

### Assignments
- Syntax
```
variable-name = expression
```

- Example
```c
int index = 0
float rate = 0.5
bool valid = true
string text = "hello world"
string ids[] = { "route1", "route2" }
```

### Assignments for array item
- Syntax
```
variable-name[index] = expression
```

- Example
```c
string ids[] = { "route1", "route2" }
ids[0] = "route3"
```

### Function
- Syntax
```
def function-name(parameter-declaration-list): return-type
  statement-list
end
```

- Example
```c
def eval_str(string id1, string id2, string id3): string
  return ""
end
 
def eval(string id1, string id2, string arr[])
end
```

### Function call
- Syntax
```
function-name(argument-list)
```

- Example
```c
string result = eval_str("a", "b", "c")
```

- Use `extern` to call externally defined C functions
```c
string result = extern printf("Hello\n")
```

### Assignment and primary expressions
- Example
```
int index = 2
float rate = 0.5
bool is_success = true
bool is_failed = false
string text = "hello world"
int val = (3 + 4) * index

string ids[] = {"route0", "route1"}
string id = ids[0]
int size = ids.len
```

### Arithmetic expressions
- Operators (absolute and relative)
```
*    /   %    +    -
*=   /=  %=   +=   -=
```

- Example
```c
int a = 3 + 4
float b = 3 + 4.5
int c = 4 % 5
float d = 65 / 100
int e = 3 + (4 * 5)
int e *= 3 + 4
```

### Relational and equality expressions
- Operators
  - Relational: `>  >=  <  <=`
  - Equality: `==  !=`

- Example:
```c
bool greater = 3 > 4
bool neq = "train1" != "train2"
```

### Logical expressions
- Operators
  - AND: `&&`
  - OR: `||`
  - NOT: `!`

- Example
```c
bool b1 = true && true
bool b2 = false || true
bool b3 = !b1
bool b4 = false || (b1 == b2)
```

### Relative bitwise assignments
- Relative operators
  - Bitwise AND: `&=`
  - Bitwise OR: `|=`
  - Bitwise XOR: `^=`

- Example
 ```c
 int b1 = 3 // b1 = 3 (0b0011)
 b1 &= 1    // b1 = 1 (0b0001)
 b1 |= 4    // b1 = 5 (0b0101)
 b1 ^= 1    // b1 = 4 (0b0100)
 ```

### Relative shift assignments
- Relative operators
  - Shift left: `<<=`
  - Shift right: `>>=`
  - Shift right unsigned: `>>>=`

- Example
 ```c
 int s1 = -3  // s1 = -3
 s1 <<= 1     // s1 = -6
 s1 >>= 1     // s1 = -3
 ```

### Relative max and min assignments
- Relative operators
  - Max: `max=`
  - Min: `min=`

- Example
 ```c
 int m1 = 5   // m1 = 5
 m1 max= 1    // m1 = 5
 m1 min= 10   // m1 = 5
 m1 max= 20   // m1 = 20
 m1 min= 0    // m1 = 0
 ```

### if-else statement
- Syntax
```
if condition-expression
  statement-list-then
else
  statement-list-else
end
```

- Example
```c
int a = 1
int b = 2

if a > b
  return a
else
  return b
end
```

### while statement
- Syntax
```
while condition-expression
  statement-list
end
```

- Example
```c
int i = 0
int s = 0

while i < 10
  s = s + i
  i = i + 1
end
```

### for-in statement
- Syntax
```
for var-decl in var-reference
  statement-list
end
```

- Example
```c
int nums[] = {1,1,2,3,5}
int sum = 0
for int n in nums
  sum = sum + n
end
```

### log statement
- Syntax
```
log primary-expression
```

- Example
```c
log "message"
log (0.5 + 0.1)
log (1 + 2)
log (1 == 1)
```

### Domain-specific expressions
- Check segment occupation (returns true or false)
```c
bool is_occupied = is "seg1" occupied
```

- Check route availability (returns true or false)
```c
bool is_available = is "route1" available
```

- Check whether an item is a segment (returns true or false)
```c
bool is_segment = is "seg1" a_segment
```

- Check whether an item is a signal (returns true or false)
```c
bool is_signal = is "seg1" a_signal
```

- Get signal state (returns "stop", "go", "caution", or "shunt")
```c
string res = get state "signal1"
```

- Get point state (returns "normal" or "reverse")
```c
string res = get state "point1"
```

- Set signal state (stop, go, caution, or shunt)
```c
bool success = set state "signal1" to go
```

- Set point state (normal or reverse)
```c
bool success = set state "point1" to normal
```

- Get train speed
```c
int train_speed = get speed "cargo_db"
```

- Get config from YAML file (dot notation of the `schema` in the [standard library](https://github.com/trinnguyen/bahndsl/blob/master/src/de.uniba.swt.dsl/resources/standardlib.bahn))
```c
string src = get config route.source "route1"
string segment_ids[] = get config route.path "route1"
```

- Get routes from interlocking table
```c
string route_ids[] = get routes from src_signal_id to dst_signal_id
```

- Get expected point position in a route (returns "normal" or "reverse")
```c
string pos = get position "point1" in "route1"
```

- Grant route
```c
grant "route1" to "cargo_green"
```

### Built-in functions in the [standard library](https://github.com/trinnguyen/bahndsl/blob/master/src/de.uniba.swt.dsl/resources/standardlib.bahn)
- For a given array of routes, return the ID of the shortest route
```python
def get_shortest_route(string route_ids[]): string
```

- For a given route, return the block that precedes `block_id`
```python
def get_previous_block(string route_id, string block_id): string
```

- For a given route, return the first block in the route with main segment equal to `segment_ids[0]`
```python
def get_block(string route_id, string segment_ids[]) : string
```

- For a given block, return whether it is occupied
```python
def is_block_occupied(string block_id): bool
```

- For a given signal, return whether it is a composition of other signals
```python
def is_composition_signal(string id): bool
```
