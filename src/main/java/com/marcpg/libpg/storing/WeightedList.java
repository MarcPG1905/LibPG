package com.marcpg.libpg.storing;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A list that can be used to retrieve random items based on weights. Items with a higher weight
 * will be more likely to be chosen. <br>
 * If you don't care about the weight of a list and just need randomization, you can take a look
 * at {@link com.marcpg.libpg.util.Randomizer#fromCollection(Collection)} this}.
 * @param <T> The type of this weighted list's items.
 */
public class WeightedList<T> implements Collection<T> {
    private static class WeightedItem<T> extends Pair<T, Double> {
        public WeightedItem(T item, Double cumulativeWeight) {
            super(item, cumulativeWeight);
        }
    }

    private final List<WeightedItem<T>> items = new ArrayList<>();
    private double totalWeight = 0.0;
    private final Random random = new Random();

    /**
     * Adds a new item with the defined weight to the list.
     * @param item The item to add to the list.
     * @param weight The item's weight.
     */
    public void add(T item, double weight) {
        if (weight <= 0)
            throw new IllegalArgumentException("Weight cannot be equal or less than zero");

        totalWeight += weight;
        items.add(new WeightedItem<>(item, weight));
    }

    /**
     * Retrieves a random item from this weighted list by using binary search.
     * This will take O(log n) times and therefor is logarithmic.
     * @return A random item from this weighted list, where items with
     *         a higher weight are more likely to be chosen.
     */
    public T random() {
        if (items.isEmpty())
            throw new IllegalStateException("There are no elements to pick from");

        double randomWeight = random.nextDouble() * totalWeight;
        int low = 0;
        int high = items.size() - 1;
        while (low < high) {
            int mid = (low + high) / 2;
            if (randomWeight < items.get(mid).right()) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }
        return items.get(low).left();
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return items.stream().anyMatch(i -> i.left().equals(o));
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return items.stream().map(Pair::left).iterator();
    }

    @Override
    public Object @NotNull [] toArray() {
        return items.stream().map(Pair::left).toArray();
    }

    @Override
    public <T1> T1 @NotNull [] toArray(T1 @NotNull [] a) {
        return items.stream().map(Pair::left).toList().toArray(a);
    }

    /**
     * Adds the specified item to this weighted list with the default weight of 1.0.
     * If you want control over the weight, see {@link #add(Object, double)  this}!
     * @param item The item to add to this list.
     * @return Always true!
     */
    @Override
    public boolean add(T item) {
        add(item, 1.0);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).left().equals(o)) {
                double weight = i == 0 ? items.get(0).right() : items.get(i).right() - items.get(i - 1).right();
                totalWeight -= weight;
                items.remove(i);

                for (int j = i; j < items.size(); j++)
                    items.get(j).right -= weight;

                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return c.stream().allMatch(this::contains);
    }

    /**
     * Adds all items from the collection to this weighted list, with all having the default weight of 1.0.
     * @param c Collection containing elements to be added to this weighted list.
     * @return If this weighted list has been modified or not. Always true.
     */
    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        for (T item : c) add(item);
        return true;
    }

    /**
     * Removes all items from the collection from this weighted list.
     * @param c Collection containing elements to be removed from this weighted list.
     * @return If this weighted list has been modified or not.
     */
    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean modified = false;
        for (Object item : c) {
            remove(item);
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        Set<?> set = new HashSet<>(c);
        boolean modified = false;
        for (Iterator<WeightedItem<T>> iterator = items.iterator(); iterator.hasNext();) {
            WeightedItem<T> item = iterator.next();
            if (!set.contains(item.right())) {
                double weight = item.right() - (iterator.hasNext() ? items.get(items.indexOf(item) + 1).right() : totalWeight);
                totalWeight -= weight;
                iterator.remove();
                modified = true;
            }
        }
        if (modified) {
            double cumulativeWeight = 0;
            for (WeightedItem<T> item : items) {
                cumulativeWeight += item.right();
                item.right = cumulativeWeight;
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        items.clear();
        totalWeight = 0.0;
    }
}
