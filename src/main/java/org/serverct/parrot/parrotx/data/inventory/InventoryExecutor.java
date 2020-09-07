package org.serverct.parrot.parrotx.data.inventory;

import lombok.NonNull;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ListIterator;
import java.util.Objects;

public interface InventoryExecutor extends InventoryHolder {
    Inventory construct();

    void execute(InventoryClickEvent event);

    default void open(InventoryOpenEvent event) {
    }

    default void close(InventoryCloseEvent event) {
    }

    default void event(InventoryEvent event) {
    }

    default void refresh(@NonNull Inventory inventory) {
        Inventory inv = construct();
        ListIterator<ItemStack> iterator = inv.iterator();
        while (iterator.hasNext()) inventory.setItem(iterator.nextIndex(), iterator.next());
    }

    default boolean check(final InventoryClickEvent event) {
        final Inventory clickedInv = event.getClickedInventory();
        return !Objects.isNull(clickedInv) && !Objects.isNull(clickedInv.getHolder()) && clickedInv.getHolder().equals(this);
    }
}
