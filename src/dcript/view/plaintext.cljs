(ns dcript.view.plaintext
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom]

            [dcript.core :as dcript]))

(defn plaintext-view [{:keys [ciphertext guessed-mapping]} owner]
  (om/component
   (dom/div #js {:className "plaintext"}
            (dcript/decode ciphertext guessed-mapping \-))))
