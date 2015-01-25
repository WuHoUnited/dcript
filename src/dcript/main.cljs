(ns dcript.main
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]

            [clojure.string :as string]

            [cljs.core.async :as a]

            [dcript.view.frequency :as freq]
            [dcript.view.mapping :as mapping]
            [dcript.view.standard-simple :as standard-simple]
            [dcript.view.standard :as standard]
            [dcript.view.ciphertext :as ciphertext-view]
            [dcript.view.plaintext :as plaintext-view]

            [dcript.core :as dcript])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(enable-console-print!)

(defmulti handle-message (fn [state [msg & args]] msg))

(defmethod handle-message :guess-letter [state [_ cipher plain]]
  (update-in state
             [:guessed-mapping]
             dcript/associate-letter
             cipher
             plain))

(defmethod handle-message :update-ciphertext [state [_ ciphertext]]
  (assoc-in state [:ciphertext] (dcript/convert-string ciphertext)))

(defmethod handle-message :activate-cipher-letter [state [_ letter]]
  (assoc-in state [:active-cipher-letter] letter))

(defn app-view [app-state owner]
  (om/component
   (dom/div #js {}
            (om/build standard-simple/standard-simple-view app-state)
            (om/build standard/standard-view app-state)
            (om/build ciphertext-view/ciphertext-view app-state)
            (om/build plaintext-view/plaintext-view app-state)
            (om/build mapping/mapping-view app-state)
            ;; string-frequency-view comes up with the wrong answer because of case sensitivity
            (om/build freq/string-frequency-view app-state {:fn (fn [{:keys [ciphertext active-cipher-letter]}]
                                                                  {:string ciphertext
                                                                   :active-letter active-cipher-letter})})
            (om/build freq/english-frequency-view nil))))

(defn ^:export main []
  (let [app-state (atom {:ciphertext "abccd edfcg"
                         :guessed-mapping {}
                         :active-cipher-letter nil})
        chan (a/chan)
        ref-cursor (om/ref-cursor (om/root-cursor app-state))]

    (go-loop []
             (let [v (a/<! chan)]
               (om/transact! ref-cursor #(handle-message % v))
               (recur)))

    (om/root
     app-view
     app-state
     {:shared {:chan chan}
      :target (. js/document (getElementById "app"))})))
