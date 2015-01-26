(ns dcript.view.mapping
  (:require [om.core :as om]
            [om.dom :as dom]

            [dcript.core :as dcript]

            [clojure.string :as string]

            [cljs.core.async :as a]))

(defn handle-change [e chan cipher]
  (let [val (.. e -target -value)]
    (a/put! chan [:guess-letter cipher val])))

(defn single-mapping-view [{:keys [plain cipher update-structure] :as mapping} owner]
  (let [chan (-> owner om/get-shared :chan)]
    (reify om/IRender
      (render [_]
              (dom/li #js {}
                      (dom/div #js {}
                               cipher)
                      (dom/input #js {:maxLength 1
                                      :className "mapping-input"
                                      :value plain
                                      :onChange #(handle-change % chan cipher)}))))))

(defn mapping-view [{:keys [guessed-mapping]} owner]
  (reify om/IRender
    (render [_]
            (apply dom/ul #js {:className "cipher-mapping"}
                   (om/build-all single-mapping-view
                                 dcript/ENGLISH_LETTERS
                                 {:fn (fn [letter]
                                        {:cipher letter
                                         :plain (guessed-mapping letter)
                                         :update-structure guessed-mapping})})))))
