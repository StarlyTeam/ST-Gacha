package net.starly.gacha.gacha;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.starly.gacha.preset.PresetExecutor;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class Gacha {
    private String name;
    private PresetExecutor presetExecutor;
    private List<GachaItem> itemList;
}
