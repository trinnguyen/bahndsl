# BahnDSL Syntax

- A BahnDSL model contains two parts: railway model configuration and interlocking functions. Empty source code with all necessary is shown below:

```
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

## Configuration
### Module: 
```
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
```
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
```
seg1 0x00 length 10cm
```

### Signal
- Syntax
```
signal-type-name signal-name hex-number
```

- Composite signal (compound signal)
```
composite signal-name 
    signals
        reference-signal-name-1
        reference-signal-name-2
        ...
        reference-signal-name-n
    end
```

- Example
```
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
```
point1 0x00 segment seg4 normal 0x01 reverse 0x00 initial normal
```

### Peripheral (reuse signal syntax)
- Syntax
```
signal-type-name signal-name hex-number
```

- Example
```
platformlight platformlights 0x0A
```

### Crossing
- Syntax
```
crossing-name segment segment-name
```

- Example
```
crossing1 segment seg35
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
```
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
    limit number speed-unit
    trains 
        train-type
    end
```

- Example
```
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
```
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

```
point6.stem -- block1.down
```

```
point6.stem -- block1.down -- block1.up
```

```
point6.stem -- block1.down -> block1.up
```

## Interlocking behaviours

### Entry functions
- `request_route` is invoked by the SWTbahn-cli for selecting and grating route to train
- `drive_route` is invoked by SWTbahn-cli for monitoring physical track elements on the railway model during train driving session
- `request_route` is required to build the interlocking shared library. `drive_route` is optional.
```
def request_route(string src_signal_id, string dst_signal_id, string train_id): string
   return ""
end
def drive_route(string route_id, string train_id, string segment_ids[])
end
```


### Data Type: Scalar
- Four primary types:
```
int
float
bool
string
```

- Object, struct or custom data type are not supported
- Array is supported

- Example
```
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
```
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
```
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
```
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
```
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
```
string result = eval_str("a", "b", "c")
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
- Operators
```
*   /    %   +   -
```

- Example
```
int a = 3 + 4
float b = 3 + 4.5
int c = 4 % 5
float d = 65 / 100
int e = 3 + (4 * 5)
```

### Relational and equality expressions
- Operators
    - Relational operators: `>    >=    <    <=`
    - Equality operators: `==    !=`

- Example:
```
bool greater = 3 > 4
bool neq = "train1" != "train2"
```

### Logical expressions
- Operators
    - AND: `&&`
    - OR: `||`
    - NOT: `!`

- Example
```
bool b1 = true && true
bool b2 = false || true
bool b3 = !b1
bool b4 = false || (b1 == b2)
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
```
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
```
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
```
int nums[] = {1,1,2,3,5}
int sum = 0
for int n in nums
    sum = sum + n
end
```

### Domain-specific expressions
- Check segment occupation
```
bool is_occupied = is "seg1" occupied
```

- Get signal state
```
string res = get state "signal1"
```

- Get point state
```
string res = get state "point1"
```

- Set signal state
```
bool success = set state "signal1" to clear
```

- Set point state
```
bool success = set state "point1" to normal
```

- Get config from YAML file
```
string src = get config route.source "route1"
string[] segment_ids = get config route.path "route1"
```

- Get routes from interlocking table
```
string route_ids[] = get routes from src_signal_id to dst_signal_id
```

- Get expected point position in a route
```
string pos = get position "point1" in "route1"
```

- Grant route
```
grant "route1" to "cargo_green"
```

### Built-in functions in standard library
```
def get_shortest_route(string route_ids[]): string
def get_previous_block(string route_id, string block_id): string
def get_block(string route_id, string segment_ids[]) : string
def is_block_occupied(string block_id): bool
def is_composition_signal(string id): bool
```