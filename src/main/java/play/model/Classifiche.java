package play.model;

import org.json.JSONObject;
import java.io.File;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Classifiche {
    public static class ClassificaItem {
        public final String username;
        public final int successCount;
        public final long totalTime;
        public ClassificaItem(String u, int s, long t) {
            username = u;
            successCount = s;
            totalTime = t;
        }
    }

    private static final JSONObject SAVES = loadSaves();

    private static JSONObject loadSaves() {
        try {
            File f = new File("saves.json");
            if (!f.exists()) return new JSONObject();
            byte[] bytes = Files.readAllBytes(f.toPath());
            String text = new String(bytes, StandardCharsets.UTF_8).trim();
            return text.isEmpty() ? new JSONObject() : new JSONObject(text);
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public static List<ClassificaItem> getSuccessRanking() {
        List<ClassificaItem> list = new ArrayList<>();
        for (String user : SAVES.keySet()) {
            JSONObject userObj = SAVES.getJSONObject(user);
            int cnt = 0;
            for (String ex : userObj.keySet()) {
                JSONObject levels = userObj.getJSONObject(ex);
                for (String lvl : levels.keySet()) {
                    if ("success".equals(levels.getJSONObject(lvl).optString("risultato")))
                        cnt++;
                }
            }
            list.add(new ClassificaItem(user, cnt, 0));
        }
        list.sort((a, b) -> Integer.compare(b.successCount, a.successCount));
        return list;
    }

    public static List<ClassificaItem> getTimeRanking() {
        List<ClassificaItem> list = new ArrayList<>();
        for (String user : SAVES.keySet()) {
            JSONObject userObj = SAVES.getJSONObject(user);
            long sum = 0;
            for (String ex : userObj.keySet()) {
                JSONObject levels = userObj.getJSONObject(ex);
                for (String lvl : levels.keySet()) {
                    sum += levels.getJSONObject(lvl).optLong("tempo", 0);
                }
            }
            list.add(new ClassificaItem(user, 0, sum));
        }
        list.sort((a, b) -> Long.compare(b.totalTime, a.totalTime));
        return list;
    }
}