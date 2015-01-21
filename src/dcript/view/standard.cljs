(ns dcript.view.standard
  (:require [om.core :as om]
            [om.dom :as dom]

            [clojure.string :as string]

            [dcript.core :as dcript]

            [cljs.core.async :as a]))

(defn handle-keypress [e chan cipher]
  (let [val (.fromCharCode js/String (.-charCode e))]
    (a/put! chan [:guess-letter cipher val])))

(defn handle-keydown [e chan cipher]
  (when (#{8 46} (.-keyCode e))
    (a/put! chan [:guess-letter cipher nil])))

(defn letter-view [{:keys [cipher guessed-mapping]} owner]
  (let [chan (-> owner om/get-shared :chan)]
    (reify
      om/IRender
      (render [_]
              (let [plain (guessed-mapping cipher)]
                (dom/div #js {:className "standard-letter"}
                         (dom/input #js {:className "standard-plaintext"
                                         :type "text"
                                         :size 1
                                         :value (guessed-mapping cipher)
                                         :onKeyPress #(handle-keypress % chan cipher)
                                         :onKeyDown #(handle-keydown % chan cipher)})
                         (dom/div #js {:className "standard-ciphertext"}
                                  cipher)))))))

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
