data = #FROM#
#RANGE#
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
