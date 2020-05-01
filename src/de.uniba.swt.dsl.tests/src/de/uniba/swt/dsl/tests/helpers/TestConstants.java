package de.uniba.swt.dsl.tests.helpers;

public class TestConstants {
    public final static String SampleEmptyConfig = "module config_empty\n" +
            "   boards\n" +
            "       master 0x00\n" +
            "   end\n" +
            "\n" +
            "   segments master\n" +
            "   end\n" +
            "\n" +
            "   signals master\n" +
            "   end\n" +
            "\n" +
            "   points master\n" +
            "   end\n" +
            "\n" +
            "   blocks\n" +
            "   end\n" +
            "\n" +
            "   crossings\n" +
            "   end\n" +
            "\n" +
            "   layout\n" +
            "   end\n" +
            "\n" +
            "   trains\n" +
            "   end\n" +
            "\n" +
            "end";

    public final static String SampleStandardConfig = "module standard " +
            "boards " +
            "   onecontrol1 0x05000D7500E8ED " +
            "   onecontrol2 0x05000D7500DAED " +
            "   lightcontrol1 0x05000D6B0072EC " +
            "   lightcontrol2 0x05000D6B0067EC " +
            "   master 0xDA000D680001EE features 0x03:0x14 end " +
            "end " +
            "segments master seg2 0x00 length 11cm end " +
            "signals lightcontrol1 entry sign1 0x00 end " +
            "signals lightcontrol2 end " +
            "points onecontrol1 point1 0x00 segment seg2 normal 0x01 reverse 0x00 initial normal end " +
            "points onecontrol2 end " +
            "peripherals onecontrol1 entry sign2 0x02 end " +
            "peripherals onecontrol2 end " +
            "crossings end " +
            "blocks end " +
            "platforms end " +
            "layout end " +
            "trains end " +
            "end";

    public final static String SampleLiteConfig = "module standard " +
            "boards master 0xDA000D680052EF\n" +
            "      features\n" +
            "        0x03:0x14\n" +
            "        0x6E:0x00\n" +
            "      end\n" +
            "    lightcontrol 0x05000D6B0083EC\n" +
            "    onecontrol 0x05000D7500DBED\n" +
            "end " +
            "segments master end " +
            "signals lightcontrol end " +
            "points onecontrol end " +
            "blocks end " +
            "platforms end " +
            "layout end " +
            "trains end " +
            "end";

    public final static String SampleConfigSignals = "module test " +
            "boards master 0x00 end " +
            "signals master" +
            "   entry signal1 0x03 " +
            "   distant signal2 0x04 " +
            "   composite signal100 signals signal1 signal2 end " +
            "end " +
            "end";

    public final static String SampleLayoutConfig = "module layout1 " +
            "boards " +
            "   master 0x00 " +
            "   lightc 0x01 " +
            "   conc 0x02 " +
            "end " +
            "segments master " +
            "   seg1 0x01 length 11cm " +
            "   seg2 0x02 length 11cm " +
            "   seg3 0x03 length 11cm " +
            "   seg4 0x04 length 11cm " +
            "end " +
            "signals lightc " +
            "   entry sig5 0x05 " +
            "   entry sig6 0x06 " +
            "   entry sig7 0x07 " +
            "end " +
            "points conc " +
            "   point1 0x01 segment seg1 normal 0x01 reverse 0x00 initial normal " +
            "end " +
            "blocks " +
            "   block2 main seg2 " +
            "   block3 main seg3 " +
            "   block4 main seg4 " +
            "end " +
            "layout " +
            "   block2.up -- point1.side " +
            "   block3.up -- point1.straight " +
            "   block4.down -- point1.stem " +
            "   block2.up -- sig5 " +
            "   block3.up -- sig6 " +
            "   block4.up -- sig7 " +
            "end " +
            "end";

    public final static String SampleTrainConfig1 = "module test " +
            "trains " +
            "   cargo_db 0x0001 " +
            "       steps 126 " +
            "       calibration 5 15 30 45 60 75 90 105 120 end " +
            "       weight 100g " +
            "       length 7cm " +
            "       type cargo " +
            "       peripherals " +
            "           head_light bit 4 initial 1 " +
            "           cabin_light bit 0 initial 1 " +
            "       end " +
            "end " +
            "end";

    public final static String SampleTrainConfig2 = "module test " +
            "trains " +
            "   cargo_db 0x0001 " +
            "       steps 126 " +
            "       calibration 5 15 30 45 60 75 90 105 120 end " +
            "       peripherals " +
            "           head_light bit 4 initial 1 " +
            "           cabin_light bit 0 initial 1 " +
            "       end " +
            "       weight 100g " +
            "       length 7cm " +
            "       type cargo " +
            "end " +
            "end";

    public final static String SampleIfElse = "def test_expr(int a, int b): int if a > b return a else return b end end";

    public final static String SampleWhile = "def test_expr(): int int s = 0 while s < 10 s = s + 1 end return s end";

    public final static String SampleOperators = "def test_expr() int a = 3 + 4 bool b = true && !(a == 5) float c = 4 / 5 end";

    public final static String SampleRequestRouteEmpty = "def request_route(string src_signal_id, string dst_signal_id, string train_id): string " +
            "return \"\" end";

    public final static String SampleDriveRouteEmpty = "def drive_route(string route_id, string train_id, string segment_ids[]) end";

    public final static String SampleInterlockingEmpty = SampleRequestRouteEmpty + "\n" + SampleDriveRouteEmpty;

    public final static String SampleRequestRouteForeach = "def request_route(string src_signal_id, string dst_signal_id, string train_id): string " +
            "string ids[] = get routes from src_signal_id to dst_signal_id " +
            "return get_shortest_route(ids) " +
            "end";

    public final static String SampleRequestRoute = "def request_route(string src_signal_id, string dst_signal_id, string train_id): string " +
            "string ids[] = get routes from src_signal_id to dst_signal_id " +
            "return test() " +
            "end " +
            "def test() : string return \"\" end";

    public final static String SampleDriveRoute = "def drive_route(string route_id, string train_id, string segment_ids[]) " +
            "string block_id = get_block(route_id, segment_ids) " +
            "string pre_block = get_previous_block(route_id, block_id) " +
            "string signal_ids[] = get config block.block_signals pre_block " +
            "for string signal_id in signal_ids " +
            "   set state signal_id to stop " +
            "end " +
            "end";
}
