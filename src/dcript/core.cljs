(ns dcript.core
  (:require [clojure.string :as string]))

(def ENGLISH_LETTERS
  (apply sorted-set "abcdefghijklmnopqrstuvwxyz"))

(defn letter? [character]
  (and character
       (ENGLISH_LETTERS (string/lower-case character))))

(defn convert-letter [letter]
  (if (letter? letter)
    (string/lower-case letter)
    letter))

(defn convert-string [string]
  (->> string
       (map convert-letter)
       (apply str)))

(defn associate-letter [mapping cipher plain]
  (let [converted-cipher (convert-letter cipher)
        converted-plain (convert-letter plain)
        cipher-is-letter (letter? converted-cipher)
        plain-is-letter (letter? converted-plain)]
    (cond (and cipher-is-letter plain-is-letter) (assoc mapping converted-cipher converted-plain)
          cipher-is-letter (dissoc mapping converted-cipher))))

(defn decode-letter [mapping character default]
  (if (letter? character)
    (get mapping character default)
    character))

(defn decode [ciphertext mapping default]
  (->> ciphertext
       (map #(decode-letter mapping % default))
       (apply str)))
