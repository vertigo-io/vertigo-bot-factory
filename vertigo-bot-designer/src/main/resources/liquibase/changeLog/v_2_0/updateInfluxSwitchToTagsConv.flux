pivot = #FROM#
#RANGE# 
|> filter(fn: (r) => r._measurement == "conversation" and r._field !="name")
|> toString()
|> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value")
|> set(key: "_field", value:"name" )
|> duplicate(column: "name", as: "_value")
|> drop(columns: ["_start", "_stop"])
|> group()

pivot
|> drop(columns: ["duration", "inner_duration", "subprocesses"])
#TO#

pivot
|> set(key: "_field", value:"duration" )
|> drop(columns: ["_value", "confidence", "inner_duration", "subprocesses"])
|> rename(columns: {duration: "_value"})
|> toInt()
#TO#

pivot
|> set(key: "_field", value:"inner_duration" )
|> drop(columns: ["_value", "confidence", "duration", "subprocesses"])
|> rename(columns: {inner_duration: "_value"})
|> toInt()
#TO#

pivot
|> set(key: "_field", value:"subprocesses" )
|> drop(columns: ["_value", "confidence", "duration", "inner_duration"])
|> rename(columns: {subprocesses: "_value"})
|> toInt()
#TO#

