(ns free-your-data.data)

(def talks
  (ref
   {"11" {:title "Coffee Break", :time "Monday 16:15-16:30"},
    "22" {:title "Lunch", :time "Tuesday 13:15-14:45"},
    "12"
    {:speaker "James Reeves",
     :title "Functional 3D Game Design",
     :time "Monday 16:30-17:15"},
    "23"
    {:speaker "Kai Wu - Daniel Janus - Ryan Greenhall",
     :title
     "Lig\nhtning talks:\n \"1) In search of workflow nirvana: Clojure, Emacs, Org, and literate programming\"\n \"2) Lithium: a small Clojure-inspired Lisp on the bare metal\"\n \"3) Templating In Clojure\"",
     :time "Tuesday 14:45-15:30"},
    "13"
    {:speaker "Jen Smith",
     :title "Common Clojure Smells",
     :time "Monday 17:15-18:00"},
    "24"
    {:speaker "Sam Aaron",
     :title "Meta-eX: How to live code your own band",
     :time "Tuesday 15:30-16:15"},
    "14" "Tuesday 15th",
    "25" {:title "Coffee Break", :time "Tuesday 16:15-16:30"},
    "15" {:title "Coffee", :time "Tuesday 8:30-9:00"},
    "26"
    {:speaker "Frazer Irving",
     :title "Enterprise integration with Clojure",
     :time "Tuesday 16:30-17:15"},
    "16"
    {:speaker "Stuart Halloway",
     :title
     "Keynote: Narcissistic Design",
     :time "Tuesday 9:00-10:00"},
    "27"
    {:speaker "Ryan Lemmer",
     :title "A Perfect Storm for Legacy Migration",
     :time "Tuesday 17:15-18:00"},
    "17"
    {:speaker "Paul Bellamy & Martin Trojer",
     :title "Using Clojure to Serve The Internet of Things",
     :time "Tuesday 10:00-10:45"},
    "18"
    {:speaker "Clifton Cunningham & Jon Pither",
     :title "Newspaper Massacre",
     :time "Tuesday 10:45-11:30"},
    "19" {:title "Coffee break", :time "Tuesday 11:30-11:45"},
    "0" {:title "Registrations + Coffee", :time "Monday 8:30-8:50"},
    "1" {:title "Opening", :time "Monday 8:50-9:00"},
    "2"
    {:speaker "Zach Tellman",
     :title "Keynote: States and Nomads: Handling Software Complexity",
     :time "Monday 9:00-10:00"},
    "3"
    {:speaker "Tom Hall",
     :title "Evolving Life In The Browser",
     :time "Monday 10:00-10:45"},
    "4"
    {:speaker "Bodil Stokke",
     :title "Build Your Own Lisp for Great Justice",
     :time "Monday 10:45-11:30"},
    "5" {:title "Coffee break", :time "Monday 11:30-11:45"},
    "6"
    {:speaker
     "Philipp Meier",
     :title "Liberator – free your data with RFC 2616",
     :time "Monday 11:45-12:30"},
    "7"
    {:speaker "Chris Ford",
     :title "Functional composition",
     :time "Monday 12:30-13:15"},
    "8" {:title "Lunch", :time "Monday 13:15-14:45"},
    "9"
    {:speaker "Jarppe Lansio",
     :title
     "(assert (= (+ quadrocopter motion-detector core.async) :awesomeness))",
     :time "Monday 14:45-15:30"},
    "20"
    {:speaker "Michał Marczyk",
     :title "(into reduce transient)",
     :time "Tuesday 11:45-12:30"},
    "10"
    {:speaker "Joseph Wilk",
     :title "Creative Machines",
     :time "Monday 15:30-16:15"},
    "21"
    {:speaker "Christophe Grand",
     :title "Megarefs",
     :time "Tuesday 12:30-13:15"}}))

(defn find-talk [id]
  (get @talks id))

(defn update-talk [id talk]
  (dosync
   (alter talks assoc id talk)))
