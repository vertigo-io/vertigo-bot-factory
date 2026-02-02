data = #FROM#
#RANGE#
|> filter(fn: (r) => r._measurement == "questionanswer" and r._field == "name")
|> group(columns: ["botId", "nodId"])
|> window(every: 1h, createEmpty: false)

// Total clicks count
aggDataClicks = data
|> count()
|> set(key: "_field", value: "clicks:count")
|> rename(columns: {_start: "_time"})
|> drop(columns: ["_stop"])
|> set(key: "_measurement", value: "questionanswer_stat")
#TO#

// Clicks per Q&A (qaId + question + catLabel)
aggDataPerQA = data
|> keep(columns: ["_time", "_field", "_value", "qaId", "question", "catLabel", "botId", "nodId", "_start", "_stop"])
|> group(columns: ["botId", "nodId", "qaId", "question", "catLabel", "_start", "_stop"])
|> count()
|> set(key: "_field", value: "qaId:count")
|> rename(columns: {_start: "_time"})
|> drop(columns: ["_stop"])
|> set(key: "_measurement", value: "questionanswer_stat")
#TO#
