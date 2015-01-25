(ns dcript.view.standard-simple
  (:require [om.core :as om]
            [om.dom :as dom]

            [dcript.core :as dcript]))

(defn letter-view [{:keys [cipher guessed-mapping]} owner]
  (reify
    om/IRender
    (render [_]
            (dom/div #js {:className "standard-letter"}
                     (dom/div #js {:className "standard-plaintext"}
                              (if (dcript/letter? cipher)
                                (or (guessed-mapping cipher) "-")
                                cipher))
                     (dom/div #js {:className "standard-ciphertext"}
                              cipher)))))

(defn word-view [{:keys [word guessed-mapping] :as arg} owner]
  (reify
    om/IRender
    (render [_]
            (apply dom/div #js {:className "standard-word"}
                   (om/build-all letter-view (seq word) {:fn (fn [cipher]
                                                               {:cipher cipher
                                                                :guessed-mapping guessed-mapping})})))))

(defn standard-simple-view [{:keys [ciphertext guessed-mapping]} owner]
  (reify
    om/IRender
    (render [_]
            (apply dom/div #js {}
                   (om/build-all word-view
                                 (re-seq #"\S+" ciphertext)
                                 {:fn (fn [word]
                                        {:word word
                                         :guessed-mapping guessed-mapping})})))))
