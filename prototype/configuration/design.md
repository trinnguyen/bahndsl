## Elements of BahnDSL

### Module
- Project name
- The outer-most element containing all elements including the configuration and interlocking table
- Properties
    + name: module name
    + boards: set of boards
    + segments: set of segments
    + signals: set of signals
    + points: set of points
    + blocks: set of blocks
    + routes: set of routes (optional)
    + aspects: set of pre-defined aspect for signal

### Board
- Represent a hardware board
- Properties
    + id
    + uniqueID
    + features: set of feature

### Segment
- Represent a track segment
- Up to two neighbors at 2 positions: start, end
- Neighbor can be: Block, Segment, Point
- Properties
    + id
    + address
    + boardId
    + startElemId: Id of block, segment or point 
    + endElemId: Id of block, segment or point

### Signal
- Represent a signal installed on the exit of a Block
- Always connected to a block
- Properties
    + id
    + number
    + boardId
    + aspects: set of aspects (yellow/green/red or green/red or on/off)
    + initial: initial aspect
    + connectedBlockId: Id of block

### Point
- Represent a point
- 2 pre-defined aspects: normal, reverse
- Up to 3 neighbors at positions: start, end, branch
- The normal aspect actives the connection from start to end
- The reverse aspect actives the connection from start to reverse
- No traveling ability from end to reverse or vice versa
- Properties
    + id
    + number
    + boardId
    + startElemId: Id of block, segment or point 
    + endElemId: Id of block, segment or point
    + sideElemId: Id of block, segment or point

### Block
- Contains 1 main segment and at least 1 buffer segment (up to 2)
- Contains 1 exit signal
- Up to 2 neighbors at positions: start, end
- Direction: bidirectional, clockwise, anti-clockwise
- Properties
    + id
    + mainSegmentId
    + bufferSegmentIds: set of buffer segment ids
    + existSignalId
    + direction
    + startElemId: Id of block, segment or point 
    + endElemId: Id of block, segment or point

### Route
- Properties (original)
    + id
    + sourceSignalId
    + destinationSignalId
    + direction
    + pathSegmentIds
    + pointIds
    + signalIds
    + conflictRouteIds
- Properties with Block instead of signal and segment
    + id
    + sourceBlockIds
    + destinationBlockId
    + direction
    + pathBlockIds
    + pointIds
    + blockIds
    + conflictRouteIds

### Aspect
- Define a common aspect for signal
- Properties
    + id
    + value