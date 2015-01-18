(ns dcript.main
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]

            [clojure.string :as string]

            [dcript.view.frequency :as freq]
            [dcript.view.mapping :as mapping]
            [dcript.view.standard-simple :as standard-simple]
            [dcript.view.standard :as standard]
            [dcript.core :as dcript]))

(enable-console-print!)

(def app-state (atom {:plaintext "Hello World"
                      :ciphertext "Xfmma Yasmq"
                      :guessed-mapping {\b \x}}))


@app-state

;; I would like to combine the methods that operate on the app-state together.
;; There is a lot of converting to lower-case and checking for nils and things
;; like that that is currently part of the views themselves.


(defn ciphertext-view [{:keys [ciphertext] :as app-state} owner]
  (let [change (fn [e]
                 (let [val (.. e -target -value)]
                   (om/update! app-state [:ciphertext] val)))]
    (reify
      om/IRender
      (render [_]
              (dom/textarea #js {:onChange change
                                 :value ciphertext})))))

(defn decode [ciphertext mapping default-character]
  (->> ciphertext
       (map string/lower-case)
       (map (fn [character]
              (if (dcript/letter? character)
                (let [char (get mapping character)]
                  (or char default-character))
                character)))
       (apply str)))

(defn plaintext-view [{:keys [ciphertext guessed-mapping]} owner]
  (om/component
   (dom/div #js {:className "plaintext"}
            (decode ciphertext guessed-mapping \-))))

(defn app-view [app-state owner]
  (om/component
   (dom/div #js {}
            (om/build standard-simple/standard-simple-view app-state)
            (om/build standard/standard-view app-state)
            (om/build ciphertext-view app-state)
            (om/build plaintext-view app-state)
            (om/build mapping/mapping-view app-state)
            (om/build freq/string-frequency-view app-state {:fn (fn [{:keys [ciphertext]}]
                                                                    {:string ciphertext})})
            (om/build freq/english-frequency-view nil))))

(defn ^:export main []
  (om/root
   app-view
   app-state
   {:target (. js/document (getElementById "app"))}))
