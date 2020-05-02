package de.uniba.swt.dsl.ui.wizard;

public class BahnUiSnippet {
    private BahnUiSnippet() {}

    public static String EmptyBahnSource = "module untitled\n" +
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
            "end\n" +
            "\n" +
            "def request_route(string src_signal_id, string dst_signal_id, string train_id): string\n" +
            "   return \"\"\n" +
            "end\n" +
            "\n" +
            "def drive_route(string route_id, string train_id, string segment_ids[])\n" +
            "end";
}
