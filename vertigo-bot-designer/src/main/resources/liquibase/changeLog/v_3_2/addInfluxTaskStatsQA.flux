import "date"
import "array"

#TASK_DEF#

timeForFirstExec = date.sub(d: 1h, from: now())
taskName = "aggregStatsQA"
lastSuccessMeasurementName = "chatbot_task_lastSuccess"

ti = #FROM#
|> range(start: 0, stop: 1)
|> filter(fn: (r) => r._measurement == lastSuccessMeasurementName and r._field == taskName)
|> findRecord(fn: (key) => true, idx: 0)
lastExec = if not exists ti._value then timeForFirstExec else time(v: ti._value)

// Aggregate question/answer clicks by hour
data = #FROM#
|> range(start: date.truncate(t: date.sub(d: 1m, from: lastExec), unit: 1h))
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

// Persist lastSuccess
array.from(rows: [{_time: time(v: 0), _measurement: lastSuccessMeasurementName, _field: taskName, _value: now()}])
#TO#
