module SWTbahnLite
    aspects
        yellow 0x02
        green 0x01
        red 0x00
        on 0x00
        off 0x01 
        normal 0x01
        reverse 0x00
    end
    boards
        master 0xDA000D680052EF features 0x03:0x14, 0x6E:0x00
        lightcontrol 0x05000D6B0083EC
        onecontrol 0x05000D7500DBED
    end
    segments master
        seg1 0x00
        seg2 0x01
        seg3 0x02
        seg4 0x03
        seg5 0x04
        seg6 0x05
        seg7 0x06
        seg8 0x07
        seg9 0x08
        seg10 0x09
        seg11 0x0A
        seg12 0x0B
        seg13 0x0C
        seg14 0x0D
        seg15 0x0E
        seg16 0x0F
        seg17 0x10
        seg18 0x11
        seg19 0x12
        seg20 0x13
        seg21 0x14
        seg22 0x15
        seg23 0x16
        seg24 0x17
        seg25 0x18
        seg26 0x19
        seg27 0x1A
        seg28 0x1B
        seg29 0x1C
    end
    signals lightcontrol
        signal1 0x00 yellow, green, red initial red
        signal2 0x01 yellow, green, red initial red
        signal3 0x02 yellow, green, red initial red
        signal4 0x03 yellow, green, red initial red
        signal5 0x04 yellow, green, red initial red
        signal6 0x05 yellow, green, red initial red
        signal7 0x06 green, red initial red
        signal8 0x07 green, red initial red
        signal9 0x08 green, red initial red
        signal10 0x09 green, red initial red
        signal11 0x0A green, red initial red
        signal14 0x0B green, red initial red
        signalends 0x12 on, off initial on
        lanterns 0x13 
            aspects 
                on 0x00
                off 0x01
            initial on
    end
    points onecontrol
        point1 0x00 
            aspects 
                normal 0x01:seg1->seg2
                reverse 0x00:seg3->seg2
            initial normal
        point2 0x01 
            aspects 
                normal 0x01:seg1->seg2
                reverse 0x00:seg3->seg2
            initial normal
        point3 0x02 
            aspects 
                normal 0x01:seg1->seg2
                reverse 0x00:seg3->seg2
            initial normal
        point4 0x03 normal:seg1 -> seg2 reverse:seg3 -> seg4 initial normal
        point5 0x04 normal:seg1 -> seg2 reverse:seg3 -> seg4 initial normal
        point6 0x05 normal:seg1 -> seg2 reverse:seg3 -> seg4 initial normal
        point7 0x06 normal:seg1 -> seg2 reverse:seg3 -> seg4 initial normal
    end
    blocks
        block1 buffer:seg1, signal1 <-> main:seg2 <-> buffer:seg3, signal3
        block2 buffer:seg1, signal1 -> main:seg2
        block3 main:seg2 <- buffer: seg3, signal3
    end
    trains
        cargo_db 0x0001 steps 126 calibration 5, 15, 30, 45, 60, 75, 90, 105, 120 weight 100
            peripherals
                head_light bit 4 initial 1
                cabin_light bit 0 initial 1
        regional_odeg 0x0002 steps 126 calibration 5, 15, 30, 45, 60, 75, 90, 105, 120 weight 100
            peripherals
                head_light bit 4 initial 1
                cabin_light bit 0 initial 1
        regional_brengdirect 0x0004 steps 126 calibration 5, 15, 30, 45, 60, 75, 90, 105, 120 weight 100
            peripherals
                head_light bit 4 initial 1
                cabin_light bit 0 initial 1
        cargo_bayern 0x0005 steps 126 calibration 5, 15, 30, 45, 60, 75, 90, 105, 120 weight 100
            peripherals
                head_light bit 4 initial 1
                cabin_light bit 0 initial 1
        cargo_green 0x0006 steps 126 calibration 5, 15, 30, 45, 60, 75, 90, 105, 120 weight 100
            peripherals
                head_light bit 4 initial 1
    end
    layout
        block1 -- block2 -- point1.instart
        point1.inend -- block3a -- point2.instart
        point1.outend -- block3b -- point2.outstart
        point2.inend -- block5b -- point4.outstart
        point2.outend -- block5a -- point4.instart
        point4.inend -- block6 -- block1
    end
    platform
        length_unit cm
        weight_unit gr
        direction clockwise
        train_types cargo passenger
        length 300
    end
end