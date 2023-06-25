package net.starly.gacha.gacha;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.starly.gacha.GachaMain;
import net.starly.gacha.addon.AddonManager;
import net.starly.gacha.preset.PresetExecutor;
import net.starly.gacha.util.EncodeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
public class Gacha {

    private String name;
    private PresetExecutor presetExecutor;
    private List<GachaItem> itemList;

    public HashMap<String, Object> serialize() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("preset", AddonManager.getInstance().getPreset(presetExecutor));

        List<String> items = new ArrayList<>();
        itemList.forEach(gachaItem -> items.add(EncodeUtil.encode(gachaItem)));

        result.put("itemList", items);

        return result;
    }

    public static Gacha deserialize(Map<String, Object> map) {
        try {
            String name = (String) map.get("name");
            PresetExecutor executor = AddonManager.getInstance().getExecutor((String) map.get("preset"));
            if (executor == null) {
                GachaMain.getInstance().getLogger().severe("[ ST-Gacha ] " + name + "의 프리셋이 적용되지 않았습니다.");
                return null;
            }

            List<GachaItem> itemList = new ArrayList<>();
            if (map.containsKey("itemList")) {
                //itemList.add(EncodeUtil.decode(value, GachaItem.class)
                ((List<String>) map.get("itemList")).forEach(value -> itemList.add(EncodeUtil.decode(value, GachaItem.class)));
            }

            return new Gacha(name, executor, itemList);
        } catch (Error | Exception e) {
            e.printStackTrace();
            GachaMain.getInstance().getLogger().severe("가챠 설정 로드 중 문제가 발생했습니다, 해당 가챠 로드를 스킵합니다.");
            return null;
        }
    }
}
