data = #FROM#
#RANGE# 
|> filter(fn: (r) => r._measurement == "chatbotmessages" and r._field == "name")
|> group(columns: ["botId", "nodId"])
|> window(every: 1h, createEmpty:false )

aggDataSessionStart = data
|> filter(fn: (r) => r.isSessionStart == "1")
|> keep(columns: ["_time", "isSessionStart", "botId", "nodId", "_start", "_stop"])
|> rename(columns: {isSessionStart: "_value"})
|> count()
|> set(key: "_field", value:"isSessionStart:count" )
|> rename(columns: {_start: "_time"})
|> drop(columns: [ "_stop"]) 
|> set(key: "_measurement", value: "chatbotmessages_stat")
#TO#

aggDataFallback = data 
|> filter(fn: (r) => r.isFallback == "1" and r.isUserMessage == "1")
|> keep(columns: ["_time", "isFallback", "botId", "nodId", "_start", "_stop"])
|> rename(columns: {isFallback: "_value"})
|> count()
|> set(key: "_field", value:"isFallback:count" )
|> rename(columns: {_start: "_time"})
|> drop(columns: [ "_stop"]) 
|> set(key: "_measurement", value: "chatbotmessages_stat")
#TO#

aggDataNlu = data 
|> filter(fn: (r) => r.isNlu == "1" and r.isUserMessage == "1")
|> keep(columns: ["_time", "isNlu", "botId", "nodId", "_start", "_stop"])
|> rename(columns: {isNlu: "_value"})
|> count()
|> set(key: "_field", value:"isNlu:count" )
|> rename(columns: {_start: "_time"})
|> drop(columns: [ "_stop"]) 
|> set(key: "_measurement", value: "chatbotmessages_stat")
#TO#

aggDataUserAction = data 
|> keep(columns: ["_time", "_field", "_value", "sessionId", "botId", "nodId", "_start", "_stop"])
|> group(columns: ["botId", "nodId", "sessionId", "_start", "_stop"])
|> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value") 
|> keep(columns: ["_time","name", "botId", "nodId", "_start", "_stop"]) 
|> rename(columns: {name: "_value"}) 
|> count() 
|> set(key: "_field", value:"userAction:count" )
|> rename(columns: {_value: "_value"})
|> rename(columns: {_start: "_time"})
|> drop(columns: [ "_stop"])
|> set(key: "_measurement", value: "chatbotmessages_stat")
#TO#

aggDataIntents = data
|> filter(fn: (r) => r.isTechnical == "0")
|> keep(columns: ["_time", "_field", "_value", "name", "botId", "nodId", "_start", "_stop"])
|> group(columns: ["botId", "nodId", "name", "_start", "_stop"])
|> count()
|> set(key: "_field", value:"name:count" )
|> rename(columns: {_start: "_time"})
|> drop(columns: [ "_stop"]) 
|> set(key: "_measurement", value: "chatbotmessages_stat")
#TO#

