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

(def GETTYSBURG-ADDRESS (dcript/convert-string "Yhnk lvhkx tgw lxoxg rxtkl tzh hnk ytmaxkl ukhnzam yhkma hg mabl vhgmbgxgm, t gxp gtmbhg, vhgvxboxw bg Ebuxkmr, tgw wxwbvtmxw mh max ikhihlbmbhg matm tee fxg tkx vkxtmxw xjnte. Ghp px tkx xgztzxw bg t zkxtm vbobe ptk, mxlmbgz paxmaxk matm gtmbhg, hk tgr gtmbhg lh vhgvxboxw tgw lh wxwbvtmxw, vtg ehgz xgwnkx. Px tkx fxm hg t zkxtm utmmex-ybxew hy matm ptk. Px atox vhfx mh wxwbvtmx t ihkmbhg hy matm ybxew, tl t ybgte kxlmbgz ietvx yhk mahlx pah axkx ztox maxbk eboxl matm matm gtmbhg fbzam ebox. Bm bl temhzxmaxk ybmmbgz tgw ikhixk matm px lahnew wh mabl. Unm, bg t etkzxk lxglx, px vtg ghm wxwbvtmx -- px vtg ghm vhglxvktmx -- px vtg ghm ateehp -- mabl zkhngw. Max uktox fxg, ebobgz tgw wxtw, pah lmknzzexw axkx, atox vhglxvktmxw bm, ytk tuhox hnk ihhk ihpxk mh tww hk wxmktvm. Max phkew pbee ebmmex ghmx, ghk ehgz kxfxfuxk patm px ltr axkx, unm bm vtg gxoxk yhkzxm patm maxr wbw axkx. Bm bl yhk nl max ebobgz, ktmaxk, mh ux wxwbvtmxw axkx mh max ngybgblaxw phkd pabva maxr pah yhnzam axkx atox manl ytk lh ghuer twotgvxw. Bm bl ktmaxk yhk nl mh ux axkx wxwbvtmxw mh max zkxtm mtld kxftbgbgz uxyhkx nl -- matm ykhf maxlx ahghkxw wxtw px mtdx bgvkxtlxw wxohmbhg mh matm vtnlx yhk pabva maxr ztox max etlm ynee fxtlnkx hy wxohmbhg -- matm px axkx abzaer kxlheox matm maxlx wxtw latee ghm atox wbxw bg otbg -- matm mabl gtmbhg, ngwxk Zhw, latee atox t gxp ubkma hy ykxxwhf -- tgw matm zhoxkgfxgm hy max ixhiex, ur max ixhiex, yhk max ixhiex, latee ghm ixkbla ykhf max xtkma."))

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
            (om/build freq/string-frequency-view app-state {:fn (fn [{:keys [ciphertext active-cipher-letter]}]
                                                                  {:string ciphertext
                                                                   :active-letter active-cipher-letter})})
            (om/build freq/english-frequency-view nil))))

(defn ^:export main []
  (let [app-state (atom {:ciphertext #_GETTYSBURG-ADDRESS "abccd edfcg"
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
