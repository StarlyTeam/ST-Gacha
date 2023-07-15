package net.starly.gacha.addon.registry;

import net.starly.gacha.addon.AddonManager;
import net.starly.gacha.gacha.Gacha;
import net.starly.gacha.gacha.GachaItem;
import net.starly.gacha.manager.GachaManager;
import net.starly.gacha.preset.PresetExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AddonRegistry {

    private static final AddonManager manager = AddonManager.getInstance();

    /**
     * 프리셋을 플러그인에 등록합니다.
     * @param name 프리셋의 이름
     * @param executor 프리셋 클래스
     * @return 등록 성공 여부
     */
    public static boolean registerPreset(String name, PresetExecutor executor) {
        if (manager.hasPreset(name) && manager.hasPreset(executor)) return false;
        return manager.addPreset(name, executor);
    }

    /**
     * 프리셋을 플러그인에서 제거합니다.
     * @param name 제거 할 프리셋의 이름
     * @return 제거 성공 여부
     */
    public static boolean unregisterPreset(String name) {
        if (!manager.hasPreset(name)) return false;
        return manager.removePreset(name);
    }

    /**
     * 프리셋을 플러그인에서 제거합니다.
     * @param executor 제거 할 프리셋의 클래스
     * @return 제거 성공 여부
     */

    public static boolean unregisterPreset(PresetExecutor executor) {
        if (!manager.hasPreset(executor)) return false;
        return manager.removePreset(manager.getPreset(executor));
    }

    /**
     * 프리셋의 등록 여부를 체크합니다.
     * @param preset 프리셋의 이름
     * @return 프리셋의 등록 여부
     */

    public static boolean hasPreset(String preset) { return manager.hasPreset(preset); }

    /**
     * 프리셋의 등록 여부를 체크합니다.
     * @param executor 프리셋 클래스
     * @return 프리셋의 등록 여부
     */
    public static boolean hasPreset(PresetExecutor executor) { return manager.hasPreset(executor); }

    /**
     * 가챠를 이름으로 불러옵니다.
     * 가챠가 존재하지 않을 시 null 을 반환합니다.
     * @param name 불러올 가챠의 이름
     * @return 가챠
     */
    public static Gacha getGacha(String name) { return GachaManager.getInstance().getGacha(name); }

    public static boolean runGacha(Player player, String name) {
        Gacha gacha = GachaManager.getInstance().getGacha(name);
        if (gacha == null) { return false; }
        GachaManager.getInstance().roll(player, gacha);
        return true;
    }

    /**
     * 아이템 배열에서 지정된 확률에 따라 아이템을 선택합니다.
     * 확률은 0 초과 100 이하여야 합니다.
     * 정해진 아이템이 없을 시 null 을 반환합니다.
     * @param list 선택 할 아이템 목록
     * @return 선택 된 아이템
     */

    public static ItemStack chooseItem(List<GachaItem> list) {

        // 누적 확률을 계산하기 위한 리스트 생성
        List<Double> cumulativeProbabilities = new ArrayList<>();
        AtomicReference<Double> cumulativeProbability = new AtomicReference<>(0.0);

        List<GachaItem> items = new ArrayList<>();

        list.stream().filter(gachaItem -> gachaItem.getPercentage().doubleValue() > 0).forEach(items::add);

        items.stream().map(GachaItem::getPercentage).forEach(percentage -> {
            cumulativeProbability.updateAndGet(v -> v + percentage.doubleValue() / 100);
            cumulativeProbabilities.add(cumulativeProbability.get());
        });

        // 0부터 1 사이의 랜덤한 숫자 생성
        double randomValue = Math.random();

        // 누적 확률과 랜덤한 숫자를 비교하여 선택된 아이템을 반환
        for (int i = 0; i < cumulativeProbabilities.size(); i++) {
            if (randomValue < cumulativeProbabilities.get(i)) {
                return items.get(i);
            }
        }

        // 아이템이 선택되지 않은 경우 (예외적인 상황)
        return null;
    }

}
