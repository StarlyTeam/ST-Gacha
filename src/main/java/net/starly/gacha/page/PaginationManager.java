package net.starly.gacha.page;

import lombok.Getter;
import net.starly.gacha.gacha.GachaItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PaginationManager {
    @Getter private final List<RewardPage> pages;
    @Getter
    private int currentPage;

    public PaginationManager(List<GachaItem> items) {
        this.pages = paginateItems(items);
        this.currentPage = 1;
    }

    public void nextPage() {
        if (currentPage < pages.size()) currentPage++;
    }

    public void prevPage() {
        if (currentPage > 1) currentPage--;
    }

    public RewardPage getCurrentPageData() { return pages.get(currentPage - 1); }

    public List<RewardPage> paginateItems(List<GachaItem> items) {
        List<RewardPage> pages = new ArrayList<>();

        if (items.isEmpty()) {
            pages.add(new RewardPage(1,Collections.emptyList()));
            return pages;
        }

        int itemCount = items.size();
        int pageCount = (int) Math.ceil((double) itemCount / 45);
        for (int i = 0; i < pageCount; i++) {
            int start = i * 45;
            int end = Math.min(start + 45, itemCount);
            List<GachaItem> pageItems = items.subList(start, end);
            pages.add(new RewardPage(i + 1, pageItems));
        }
        return pages;
    }
}
