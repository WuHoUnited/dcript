(ns dcript.core
  (:require [clojure.string :as string]))

(def ENGLISH_LETTERS
  (apply sorted-set "abcdefghijklmnopqrstuvwxyz"))

(defn letter? [char]
  (ENGLISH_LETTERS (string/lower-case char)))

(defn filter-letters [text valid-letters]
  (filter valid-letters text))

(defn safe-division [dividend divisor default]
  (if (zero? divisor)
    default
    (/ dividend divisor)))

(defn calculate-frequencies [text]
  (let [filtered-text (filter-letters text ENGLISH_LETTERS)
        blank-map (zipmap ENGLISH_LETTERS (repeat 0))
        counts (reduce
                (fn [map-count letter]
                  (update-in map-count (string/lower-case letter) inc))
                blank-map
                filtered-text)
        total (count filtered-text)]
    (reduce-kv (fn [m k v]
                 (assoc m k (safe-division v total 0)))
               blank-map
               counts)))
