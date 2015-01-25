(ns dcript.analysis.frequency
  (:require [dcript.core :as dcript]))

(defn safe-division [dividend divisor default]
  (if (zero? divisor)
    default
    (/ dividend divisor)))

(defn calculate-frequencies [text]
  (let [filtered-text (filter dcript/letter? text)
        blank-map (zipmap dcript/ENGLISH_LETTERS (repeat 0))
        counts (reduce
                (fn [map-count letter]
                  (update-in map-count letter inc))
                blank-map
                filtered-text)
        total (count filtered-text)]
    (reduce-kv (fn [m k v]
                 (assoc m k (safe-division v total 0)))
               blank-map
               counts)))
