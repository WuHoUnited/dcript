(ns dcript.view.standard
  (:require [om.core :as om]
            [om.dom :as dom]

            [dcript.core :as dcript]

            [cljs.core.async :as a]))

(defn handle-keypress [e chan cipher]
  (let [val (.fromCharCode js/String (.-charCode e))]
    (a/put! chan [:guess-letter cipher val])))

(defn handle-keydown [e chan cipher]
  (when (#{8 46} (.-keyCode e))
    (a/put! chan [:guess-letter cipher nil])))

(defn handle-focus [chan cipher]
  (a/put! chan [:activate-cipher-letter cipher]))

(defn handle-blur [chan]
  (a/put! chan [:activate-cipher-letter nil]))

(defn letter-view [{:keys [cipher guessed-mapping active]} owner]
  (let [chan (-> owner om/get-shared :chan)]
    (reify
      om/IRender
      (render [_]
              (let [plain (guessed-mapping cipher)
                    className (->> (concat ["standard-letter"]
                                           (if active ["active"]))
                                   (interpose \space)
                                   (apply str))]
                (dom/div #js {:className className}
                         (if (dcript/letter? cipher)
                           (dom/input #js {:className "standard-plaintext"
                                           :type "text"
                                           :size 1
                                           :value (guessed-mapping cipher)
                                           :onKeyPress #(handle-keypress % chan cipher)
                                           :onKeyDown #(handle-keydown % chan cipher)
                                           :onFocus #(handle-focus chan cipher)
                                           :onBlur #(handle-blur chan)})
                           (dom/span #js {:className "standard-plaintext"}
                                     cipher))
                         (dom/div #js {:className "standard-ciphertext"}
                                  cipher)))))))

(defn word-view [{:keys [word guessed-mapping active-cipher-letter] :as arg} owner]
  (reify
    om/IRender
    (render [_]
            (apply dom/div #js {:className "standard-word"}
                   (om/build-all letter-view (seq word) {:fn (fn [cipher]
                                                               {:cipher cipher
                                                                :guessed-mapping guessed-mapping
                                                                :active (= active-cipher-letter cipher)})})))))

(defn standard-view [{:keys [ciphertext guessed-mapping active-cipher-letter]} owner]
  (reify
    om/IRender
    (render [_]
            (apply dom/div #js {}
                   (om/build-all word-view
                                 (re-seq #"\S+" ciphertext)
                                 {:fn (fn [word]
                                        {:word word
                                         :guessed-mapping guessed-mapping
                                         :active-cipher-letter active-cipher-letter})})))))
