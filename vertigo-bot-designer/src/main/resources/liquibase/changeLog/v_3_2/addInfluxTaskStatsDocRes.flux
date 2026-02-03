import "date"
import "array"

#TASK_DEF#

timeForFirstExec = date.sub(d: 1h, from: now())
taskName = "aggregStatsDocRes"
lastSuccessMeasurementName = "chatbot_task_lastSuccess"

ti = #FROM#
|> range(start: 0, stop: 1)
|> filter(fn: (r) => r._measurement == lastSuccessMeasurementName and r._field == taskName)
|> findRecord(fn: (key) => true, idx: 0)
lastExec = if not exists ti._value then timeForFirstExec else time(v: ti._value)

// Aggregate documentary resource clicks by hour
data = #FROM#
|> range(start: date.truncate(t: date.sub(d: 1m, from: lastExec), unit: 1h))
|> filter(fn: (r) => r._measurement == "documentaryresource" and r._field == "name")
|> group(columns: ["botId", "nodId"])
|> window(every: 1h, createEmpty: false)

// Total clicks count
aggDataClicks = data
|> count()
|> set(key: "_field", value: "clicks:count")
|> rename(columns: {_start: "_time"})
|> drop(columns: ["_stop"])
|> set(key: "_measurement", value: "documentaryresource_stat")
#TO#

// Clicks per resource (dreId + title + dreTypeCd)
aggDataPerResource = data
|> keep(columns: ["_time", "_field", "_value", "dreId", "title", "dreTypeCd", "botId", "nodId", "_start", "_stop"])
|> group(columns: ["botId", "nodId", "dreId", "title", "dreTypeCd", "_start", "_stop"])
|> count()
|> set(key: "_field", value: "dreId:count")
|> rename(columns: {_start: "_time"})
|> drop(columns: ["_stop"])
|> set(key: "_measurement", value: "documentaryresource_stat")
#TO#

// Persist lastSuccess
array.from(rows: [{_time: time(v: 0), _measurement: lastSuccessMeasurementName, _field: taskName, _value: now()}])
#TO#
