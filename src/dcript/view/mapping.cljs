(ns dcript.view.mapping
  (:require [om.core :as om]
            [om.dom :as dom]

            [dcript.core :as dcript]

            [clojure.string :as string]

            [cljs.core.async :as a]))

(defn handle-keypress [e chan cipher]
  (let [val (.fromCharCode js/String (.-charCode e))]
    (a/put! chan [:guess-letter cipher val])))

(defn handle-keydown [e chan cipher]
  (when (#{8 46} (.-keyCode e))
    (a/put! chan [:guess-letter cipher nil])))

(defn single-mapping-view [{:keys [plain cipher update-structure chan] :as mapping} owner]
  (reify om/IRender
    (render [_]
            (dom/li #js {}
                    (dom/div #js {}
                             cipher)
                    (dom/input #js {:maxLength 1
                                    :className "mapping-input"
                                    :value plain
                                    :onKeyPress #(handle-keypress % chan cipher)
                                    :onKeyDown #(handle-keydown % chan cipher)})))))

(defn mapping-view [{:keys [guessed-mapping chan]} owner]
  (reify om/IRender
    (render [_]
            (apply dom/ul #js {:className "cipher-mapping"}
                   (om/build-all single-mapping-view
                                 dcript/ENGLISH_LETTERS
                                 {:fn (fn [letter]
                                        {:cipher letter
                                         :plain (guessed-mapping letter)
                                         :update-structure guessed-mapping
                                         :chan chan})})))))
