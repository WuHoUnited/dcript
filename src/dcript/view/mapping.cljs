(ns dcript.view.mapping
  (:require [om.core :as om]
            [om.dom :as dom]

            [dcript.core :as dcript]
            [clojure.string :as string]))

(defn convert-key-code [code default]
  ;; This doesn't work in firefox because it uses charCode instead of keyCode
  (cond
   (dcript/letter? (.fromCharCode js/String code)) (string/lower-case (.fromCharCode js/String code))
   :else default))

(defn handle-keypress [e update-structure cipher plain]
  (let [val (convert-key-code (.-charCode e) plain)]
    (om/update! update-structure
                [cipher]
                val)))

(defn handle-keydown [e update-structure cipher]
  (when (#{8 46} (.-keyCode e))
    (om/update! update-structure
                [cipher]
                nil)))

(defn single-mapping-view [{:keys [plain cipher update-structure] :as mapping} owner]
  (reify om/IRender
    (render [_]
            (dom/li #js {}
                    (dom/div #js {}
                             cipher)
                    (dom/input #js {:maxLength 1
                                    :className "mapping-input"
                                    :value plain
                                    :onKeyPress #(handle-keypress % update-structure cipher plain)
                                    :onKeyDown #(handle-keydown % update-structure cipher)})))))

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
