(ns dcript.view.ciphertext
  (:require [om.core :as om]
            [om.dom :as dom]

            [cljs.core.async :as a]))

(defn ciphertext-view [{:keys [ciphertext] :as app-state} owner]
  (let [chan (-> owner om/get-shared :chan)
        change (fn [e]
                 (let [val (.. e -target -value)]
                   (a/put! chan [:update-ciphertext val])))]
    (reify
      om/IRender
      (render [_]
              (dom/textarea #js {:onChange change
                                 :value ciphertext})))))
