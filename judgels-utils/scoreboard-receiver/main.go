package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"os"
)

var receiverSecret string

type Data struct {
	ReceiverSecret string                 `json:"receiverSecret"`
	ContestJID     string                 `json:"contestJid"`
	ContestStyle   string                 `json:"contestStyle"`
	Scoreboards    map[string]interface{} `json:"scoreboards"`
	UpdatedTime    int                    `json:"updatedTime"`
}

type Entry struct {
	Type         string      `json:"type"`
	ContestStyle string      `json:"contestStyle"`
	UpdatedTime  int         `json:"updatedTime"`
	Scoreboard   interface{} `json:"scoreboard"`
}

func main() {
	receiverSecret = os.Getenv("RECEIVER_SECRET")
	if receiverSecret == "" {
		panic("RECEIVER_SECRET is not set")
	}

	http.HandleFunc("/receive", receive)
	http.HandleFunc("/serve", serve)
	http.ListenAndServe(":9144", nil)
}

func receive(w http.ResponseWriter, r *http.Request) {
	var data Data
	err := json.NewDecoder(r.Body).Decode(&data)
	if err != nil {
		log.Println("Received bad scoreboard update")
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	if data.ReceiverSecret != receiverSecret {
		log.Println("Received scoreboard update with wrong secret")
		w.WriteHeader(http.StatusForbidden)
		return
	}

	log.Printf("Received scoreboard update for contest %s", data.ContestJID)

	for typ, scoreboard := range data.Scoreboards {
		entry := Entry{
			Type:         typ,
			ContestStyle: data.ContestStyle,
			UpdatedTime:  data.UpdatedTime,
			Scoreboard:   scoreboard,
		}
		filename := getFilename(data.ContestJID, typ)
		entryBytes, _ := json.MarshalIndent(entry, "", "  ")

		err := ioutil.WriteFile(filename, entryBytes, 0644)
		if err != nil {
			log.Println(err.Error())
			w.WriteHeader(http.StatusInternalServerError)
			return
		}
	}
}

func serve(w http.ResponseWriter, r *http.Request) {
	contestJID := r.URL.Query().Get("contestJid")
	if contestJID == "" {
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	typ := r.URL.Query().Get("type")
	if typ != "OFFICIAL" && typ != "FROZEN" {
		typ = "OFFICIAL"
	}

	filename := getFilename(contestJID, "OFFICIAL")
	if typ == "FROZEN" {
		frozenFilename := getFilename(contestJID, "FROZEN")
		_, err := os.Stat(frozenFilename)
		if err == nil {
			filename = frozenFilename
		}
	}

	w.Header().Set("Content-Type", "application/json")
	http.ServeFile(w, r, filename)
}

func getFilename(contestJID string, typ string) string {
	return fmt.Sprintf("var/data/%s-%s.json", contestJID, typ)
}
