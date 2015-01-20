(ns dcript.view.frequency
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]

            [dcript.core :as dcript]))

(def *english-frequencies* {\a 0.08167
                            \b 0.01492
                            \c 0.02782
                            \d 0.04253
                            \e .12702
                            \f 0.02228
                            \g 0.02015
                            \h 0.06094
                            \i 0.06966
                            \j 0.00153
                            \k 0.00772
                            \l 0.04025
                            \m 0.02406
                            \n 0.06749
                            \o 0.07507
                            \p 0.01929
                            \q 0.00095
                            \r 0.05987
                            \s 0.06327
                            \t 0.09056
                            \u 0.02758
                            \v 0.00978
                            \w 0.02360
                            \x 0.00150
                            \y 0.01974
                            \z 0.00074})

(defn table-view [{:keys [caption columns data]} owner]
  (reify om/IRender
    (render [_]
            (dom/table #js {:className "frequencies"}
                       (dom/caption #js {}
                                    caption)
                       (dom/thead #js {}
                                  (apply dom/tr #js {}
                                         (om/build-all (fn [column]
                                                         (om/component (dom/td #js {}
                                                                               column)))
                                                       columns)))
                       (apply dom/tbody #js {}
                              (om/build-all (fn [datum]
                                              (om/component (apply dom/tr #js {}
                                                                   (om/build-all (fn [cell]
                                                                                   (om/component (dom/td #js {}
                                                                                                         cell)))
                                                                                 datum))))
                                            data))))))

(defn frequency-view [{:keys [frequencies caption]} owner]
  (let [freqs (->> frequencies
                   (map (fn [[letter number]]
                          [letter (.toFixed (* 100 number) 3)]))
                   (sort-by (fn [[letter number]]
                              [(- number) letter])))]
    (table-view {:caption caption
                 :columns ["Letter" "Frequency"]
                 :data freqs}
                owner)))

(defn string-frequency-view [{:keys [string]} owner]
  (frequency-view {:caption "Cipher Frequencies"
                   :frequencies (dcript/calculate-frequencies string)}
                  owner))

(defn english-frequency-view [_ owner]
  (frequency-view {:caption "English Frequencies"
                   :frequencies *english-frequencies*} owner))
