package net.starly.gacha.page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.starly.gacha.gacha.GachaItem;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RewardPage {
    private final int pageNum;
    private final List<GachaItem> items;
}
