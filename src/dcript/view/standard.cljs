(ns dcript.view.standard
  (:require [om.core :as om]
            [om.dom :as dom]

            [clojure.string :as string]

            [dcript.core :as dcript]))

(defn convert-key-code [code default]
  ;; This doesn't work in firefox because it uses charCode instead of keyCode
  (cond
   (dcript/letter? (.fromCharCode js/String code)) (string/lower-case (.fromCharCode js/String code))
   :else default))

(defn handle-keypress [e update-structure cipher plain]
  (let [val (convert-key-code (.-keyCode e) plain)]
    (om/update! update-structure
                [cipher]
                val)))

(defn handle-keydown [e update-structure cipher]
  (when (#{8 46} (.-keyCode e))
    (om/update! update-structure
                [cipher]
                nil)))

(defn letter-view [{:keys [cipher guessed-mapping]} owner]
  (reify
    om/IRender
    (render [_]
            (let [plain (guessed-mapping cipher)]
              (dom/div #js {:className "standard-letter"}
                       (dom/input #js {:className "standard-plaintext"
                                       :type "text"
                                       :size 1
                                       :value (guessed-mapping cipher)
                                       :onKeyPress #(handle-keypress % guessed-mapping cipher plain)
                                       :onKeyDown #(handle-keydown % guessed-mapping cipher)})
                       (dom/div #js {:className "standard-ciphertext"}
                                cipher))))))

(defn word-view [{:keys [word guessed-mapping] :as arg} owner]
  (reify
    om/IRender
    (render [_]
            (apply dom/div #js {:className "standard-word"}
                   (om/build-all letter-view (seq word) {:fn (fn [cipher]
                                                               {:cipher cipher
                                                                :guessed-mapping guessed-mapping})})))))

(defn standard-view [{:keys [ciphertext guessed-mapping]} owner]
  (reify
    om/IRender
    (render [_]
            (apply dom/div #js {}
                   (om/build-all word-view
                                 (re-seq #"\S+" (string/lower-case ciphertext))
                                 {:fn (fn [word]
                                        {:word word
                                         :guessed-mapping guessed-mapping})})))))
