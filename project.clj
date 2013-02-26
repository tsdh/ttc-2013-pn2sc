(defproject ttc-2013-pn2sc "0.1.0"
  :description "Solution to the TTC PetriNet2StateChart Case"
  :url "http://example.com/FIXME"
  :license {:name "GNU General Public License, Version 3"
            :url "http://www.gnu.org/licenses/gpl.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [funnyqt "0.4.22"]]
  :plugins [[lein-git-deps "0.0.1-SNAPSHOT" :exclusions [org.clojure/clojure]]]
  :git-dependencies [["git://github.com/tsdh/ttc-2013-pn2sc-validation.git"]]
  :source-paths ["src/" ".lein-git-deps/ttc-2013-pn2sc-validation/src/"]
  :jvm-opts ["-Xms500m" "-Xmx600M"])
