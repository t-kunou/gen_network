(ns gen-network.core)

(import '(java.io BufferedReader FileReader))

(require '[clojure.string :as str])
(require '[loom.graph :as lg])
(require '[loom.io :as lio])

(def data-text "data.txt")

(def data-list
  (->> (FileReader. data-text)
    (BufferedReader.)
    (line-seq)
    (map #(str/split % #", "))))

(def keywords ["node-names"])

(defn word-relation [key list]
  (->> list
    (filter (fn [words] (some #(= % key) words)))
    (map (fn [words] (remove #(= key %) words)))
    (flatten)
    (group-by #(identity %))
    ((fn [group] (map #(vector key (first %) (count (second %))) group)))))
  
(defn main []
  (->> keywords 
    (reduce (fn [accum keyword] (concat accum (word-relation keyword data-list))) [])
    (remove (fn [x] (<= (nth x 2) 4)))
    (apply lg/weighted-graph)
    ((fn [graph] (lio/view graph :alg :fdp)))))

(main)
