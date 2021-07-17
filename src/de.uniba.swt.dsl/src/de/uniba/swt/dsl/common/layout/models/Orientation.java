package de.uniba.swt.dsl.common.layout.models;

/* Orientation of a block, assuming the following up/down assignment:
 * ---------------| |-----------------------| |-----------------
 *  Block X    up | | down    Block Y    up | | down    Block Z
 * ---------------| |-----------------------| |-----------------
 */
public enum Orientation {
    AntiClockwise,  // right (blockY.down -> blockY.up)
    Clockwise       // left (blockY.up -> blockY.down)
}
