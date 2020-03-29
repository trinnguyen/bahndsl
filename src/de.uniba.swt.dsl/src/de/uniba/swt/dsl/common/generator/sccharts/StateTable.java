package de.uniba.swt.dsl.common.generator.sccharts;

import de.uniba.swt.dsl.common.generator.sccharts.models.State;

import java.util.TreeMap;

public class StateTable {

    private TreeMap<String, Integer> mapId = new TreeMap<>();

    private String idPrefix;
    private int incre = 0;

    public StateTable(String superId) {
        this.idPrefix = superId + "_";
    }

    public StateTable() {
        this.idPrefix = "S";
    }

    public String nextStateId() {
        return idPrefix + (incre++);
    }

    public String finalStateId() {
        return idPrefix + "FINAL";
    }

    private String nextId(String id) {
        int num = 0;
        if (id != null) {
            if (mapId.containsKey(id)) {
                num = mapId.get(id) + 1;
            }
        }

        String resultId = idPrefix + num;
        mapId.put(resultId, num);
        return resultId;
    }
}
