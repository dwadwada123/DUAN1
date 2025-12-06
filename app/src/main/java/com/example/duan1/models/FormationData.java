package com.example.duan1.models;

import java.util.HashMap;
import java.util.Map;

public class FormationData {

    private static final Map<String, Position[]> formations = new HashMap<>();

    static {
        // SƠ ĐỒ 4-3-3
        formations.put("4-3-3", new Position[]{
                new Position(0.5f, 0.92f), // 0: GK
                new Position(0.15f, 0.75f), // 1: LB
                new Position(0.38f, 0.75f), // 2: CB
                new Position(0.62f, 0.75f), // 3: CB
                new Position(0.85f, 0.75f), // 4: RB
                new Position(0.2f, 0.5f),   // 5: CM
                new Position(0.5f, 0.5f),   // 6: CM
                new Position(0.8f, 0.5f),   // 7: CM
                new Position(0.15f, 0.25f), // 8: LW
                new Position(0.5f, 0.2f),   // 9: ST
                new Position(0.85f, 0.25f)  // 10: RW
        });

        // SƠ ĐỒ 4-4-2
        formations.put("4-4-2", new Position[]{
                new Position(0.5f, 0.92f), // 0: GK
                new Position(0.15f, 0.75f), // 1: LB
                new Position(0.38f, 0.75f), // 2: CB
                new Position(0.62f, 0.75f), // 3: CB
                new Position(0.85f, 0.75f), // 4: RB
                new Position(0.15f, 0.5f),  // 5: LM
                new Position(0.38f, 0.5f),  // 6: CM
                new Position(0.62f, 0.5f),  // 7: CM
                new Position(0.85f, 0.5f),  // 8: RM
                new Position(0.35f, 0.25f), // 9: ST
                new Position(0.65f, 0.25f)  // 10: ST
        });

        // SƠ ĐỒ 3-5-2
        formations.put("3-5-2", new Position[]{
                new Position(0.5f, 0.92f), // 0: GK
                new Position(0.2f, 0.75f),  // 1: CB
                new Position(0.5f, 0.75f),  // 2: CB
                new Position(0.8f, 0.75f),  // 3: CB
                new Position(0.1f, 0.5f),   // 4: LWB
                new Position(0.3f, 0.5f),   // 5: CM
                new Position(0.5f, 0.55f),  // 6: CDM
                new Position(0.7f, 0.5f),   // 7: CM
                new Position(0.9f, 0.5f),   // 8: RWB
                new Position(0.35f, 0.25f), // 9: ST
                new Position(0.65f, 0.25f)  // 10: ST
        });

        // SƠ ĐỒ 5-3-2
        formations.put("5-3-2", new Position[]{
                new Position(0.5f, 0.92f), // 0: GK
                new Position(0.1f, 0.7f),   // 1: LWB
                new Position(0.3f, 0.75f),  // 2: CB
                new Position(0.5f, 0.75f),  // 3: CB
                new Position(0.7f, 0.75f),  // 4: CB
                new Position(0.9f, 0.7f),   // 5: RWB
                new Position(0.3f, 0.5f),   // 6: CM
                new Position(0.5f, 0.5f),   // 7: CM
                new Position(0.7f, 0.5f),   // 8: CM
                new Position(0.35f, 0.25f), // 9: ST
                new Position(0.65f, 0.25f)  // 10: ST
        });
    }

    public static Position[] getCoordinates(String formationName) {
        if (formations.containsKey(formationName)) {
            return formations.get(formationName);
        }
        return formations.get("4-3-3");
    }

    public static class Position {
        public float hBias;
        public float vBias;

        public Position(float hBias, float vBias) {
            this.hBias = hBias;
            this.vBias = vBias;
        }
    }
}