import "date"
//import "influxdata/influxdb/tasks"
import "array"
import "regexp"
import "strings"

#TASK_DEF#


timeForFirstExec = date.sub(d: 1h, from: now())

// tasks.lastSuccess does not work on InfluxDb 2.7.1, we do it manually by persisting a record on a dedicated measurement
// https://github.com/influxdata/flux/issues/3430
// lastExec = tasks.lastSuccess(orTime: timeForFirstExec)

taskName = "aggregStatsConv"
lastSuccessMeasurementName = "chatbot_task_lastSuccess"

ti = #FROM#
|> range(start: 0, stop: 1)
|> filter(fn: (r) => r._measurement == lastSuccessMeasurementName and r._field == taskName)
|> findRecord(fn: (key) => true, idx: 0)
lastExec = if not exists ti._value then timeForFirstExec else time(v: ti._value)

// Aggregate statistics for active sessions

sessions = #FROM#
|> range(start: date.sub(d: 1m, from: lastExec)) 
|> filter(fn: (r) => r._measurement == "conversation" and r._field == "name" and r.isUserMessage == "1")
|> group()
|> distinct(column: "sessionId")
|> findColumn(fn: (key) => true, column: "_value")
|> array.concat(v: ["notAnId"]) // add a fake id to filter when no active session found

regex = regexp.compile(v: strings.joinStr(arr: sessions, v: "|"))


conv = #FROM# 
|> range(start: 0)
//|> range(start: date.sub(d: 1d, from: lastExec)) // can be used in case of performance issues, we assume a conv can't be longer than this time (otherwise it WILL pollute statistics)
|> filter(fn: (r) => r._measurement == "conversation" and r._field == "name" and r.isUserMessage == "1")
|> filter(fn: (r) => r.sessionId =~ regex)
|> keep(columns: ["_time","_field","_value","botId","nodId","isUserMessage","sessionId","modelName"]) 
|> group(columns: ["sessionId"]) 


msgCount = conv 
|> count() 
|> set(key: "_field", value: "interactions") 
|> toString()
|> group(columns: ["_field"]) 

modelName = conv 
|> keep(columns: ["modelName", "sessionId"]) 
|> first(column: "modelName") 
|> rename(columns: {modelName: "_value"}) 
|> set(key: "_field", value: "modelName") 
|> group(columns: ["_field"]) 

sessionTime = conv
|> keep(columns: ["_time", "sessionId"]) 
|> rename(columns: {_time: "_value"}) 
|> min() 
|> set(key: "_field", value: "_time") 
|> group(columns: ["_field"]) 

botId = conv
|> keep(columns: ["sessionId", "botId"]) 
|> rename(columns: {botId: "_value"}) 
|> set(key: "_field", value: "botId") 
|> group(columns: ["_field"]) 

nodId = conv
|> keep(columns: ["sessionId", "nodId"]) 
|> rename(columns: {nodId: "_value"}) 
|> set(key: "_field", value: "nodId") 
|> group(columns: ["_field"]) 

rate = #FROM# 
|> range(start: 0)
//|> range(start: date.sub(d: 1d, from: lastExec)) // can be used in case of performance issues, we assume a conv can't be longer than this time (otherwise it WILL pollute statistics) 
|> filter(fn: (r) => r._measurement == "rating" and r._field=="rating")
|> filter(fn: (r) => r.sessionId =~ regex)
|> keep(columns: ["_field","_value","sessionId"]) 
|> toString()
|> group(columns: ["_field"]) 

ratingComment = #FROM# 
|> range(start: 0)
//|> range(start: date.sub(d: 1d, from: lastExec)) // can be used in case of performance issues, we assume a conv can't be longer than this time (otherwise it WILL pollute statistics)
|> filter(fn: (r) => r._measurement == "rating" and r._field=="name" and exists r.ratingComment)
|> filter(fn: (r) => r.sessionId =~ regex)
|> keep(columns: ["sessionId","ratingComment"]) 
|> set(key: "_field", value: "ratingComment")
|> rename(columns: {ratingComment: "_value"})
|> group(columns: ["_field"]) 


hasEnded = #FROM# 
|> range(start: 0)
//|> range(start: date.sub(d: 1d, from: lastExec)) // can be used in case of performance issues, we assume a conv can't be longer than this time (otherwise it WILL pollute statistics)
|> filter(fn: (r) => r._measurement == "chatbotmessages" and r._field=="name")
|> filter(fn: (r) => r.sessionId =~ regex)
|> filter(fn: (r) => exists r.name and r.name == "!END")
|> filter(fn: (r) => exists r.sessionId)
|> keep(columns: ["sessionId"]) 
|> set(key: "_field", value: "isEnded")
|> set(key: "_value", value: "1")
|> group(columns: ["_field"]) 
|> toString()

lastTopic = #FROM# 
|> range(start: 0)
//|> range(start: date.sub(d: 1d, from: lastExec)) // can be used in case of performance issues, we assume a conv can't be longer than this time (otherwise it WILL pollute statistics) 
|> filter(fn: (r) => r._measurement == "chatbotmessages" and r._field == "name" and r.isTechnical == "0")
|> filter(fn: (r) => r.sessionId =~ regex)
|> keep(columns: ["_time","name","sessionId"]) 
|> rename(columns: {name: "_value"})
|> set(key: "_field", value: "lastTopic")
|> group(columns: ["sessionId"]) 
|> sort(columns: ["_time"], desc:false)
|> last()
|> group(columns: ["_field"]) 



union(tables: [msgCount, sessionTime, modelName, botId, nodId]) 
|> pivot(rowKey:["sessionId"], columnKey: ["_field"], valueColumn: "_value") 
|> filter(fn: (r) => exists r._time and exists r.interactions)
|> set(key: "_field", value: "interactions")
|> rename(columns: {interactions: "_value"})
|> set(key: "_measurement", value: "conversation_stat")
#TO#

union(tables: [rate, sessionTime, modelName, botId, nodId]) 
|> pivot(rowKey:["sessionId"], columnKey: ["_field"], valueColumn: "_value") 
|> filter(fn: (r) => exists r._time and exists r.rating)
|> set(key: "_field", value: "rating")
|> rename(columns: {rating: "_value"})
|> set(key: "_measurement", value: "conversation_stat")
#TO#

union(tables: [ratingComment, sessionTime, modelName, botId, nodId]) 
|> pivot(rowKey:["sessionId"], columnKey: ["_field"], valueColumn: "_value") 
|> filter(fn: (r) => exists r._time and exists r.ratingComment)
|> set(key: "_field", value: "ratingComment")
|> rename(columns: {ratingComment: "_value"})
|> set(key: "_measurement", value: "conversation_stat")
#TO#

union(tables: [hasEnded, sessionTime, modelName, botId, nodId]) 
|> pivot(rowKey:["sessionId"], columnKey: ["_field"], valueColumn: "_value") 
|> filter(fn: (r) => exists r._time and exists r.isEnded)
|> set(key: "_field", value: "isEnded")
|> rename(columns: {isEnded: "_value"})
|> set(key: "_measurement", value: "conversation_stat")
#TO#

union(tables: [lastTopic, sessionTime, modelName, botId, nodId]) 
|> pivot(rowKey:["sessionId"], columnKey: ["_field"], valueColumn: "_value") 
|> filter(fn: (r) => exists r._time and exists r.lastTopic)
|> set(key: "_field", value: "lastTopic")
|> rename(columns: {lastTopic: "_value"})
|> set(key: "_measurement", value: "conversation_stat")
#TO#


// persist new lastSuccess (cf comment on top)
array.from(rows: [{_time: time(v: 0), _measurement: lastSuccessMeasurementName, _field: taskName, _value: now()}])
#TO#