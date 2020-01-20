package de.uniba.swt.dsl.common.generator.sccharts;

import de.uniba.swt.dsl.common.generator.sccharts.models.State;

import java.util.TreeMap;

public class StateTable {

    private final static String ID_PREFIX = "S";
    private TreeMap<String, Integer> mapId = new TreeMap<>();

    int incre = 0;
    public String nextStateId() {
        return ID_PREFIX + (incre++);
    }

    public State nextState(State state) {
        String curId = state != null ? state.getId() : null;

        var result = new State();
        result.setId(nextId(curId));
        return result;
    }

    private String nextId(String id) {
        int num = 0;
        if (id != null) {
            if (mapId.containsKey(id)) {
                num = mapId.get(id) + 1;
            }
        }

        String resultId = ID_PREFIX + num;
        mapId.put(resultId, num);
        return resultId;
    }
}
