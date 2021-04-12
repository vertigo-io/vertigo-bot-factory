# conventions
k = key 			ex {{u/name}}
kt = keyTemplate  	ex {{u/name}} or {{u/name/{{idx}}}}
kp = keyPattern
BTN = BehaviorTreeNode  //returns succeeded or failed or running
BTC = BehaviorTreeCondition //returns succeeded or failed

# say
say msg 

examples : 
- say "hello"
- say "hello {{u/name}}"

# ask
- ask kt question
the kt contains the result as a string param

- ask:integer kt question

examples : 
- ask {{u/name}} "what's your name ?"
- ask {{u/name/age}} "How old are you ?"

#[Conditions] on Integers

xx kt value
with xx in  
- largerThan
- largerThanOrEqual
- greaterThan
- greaterThanOrEqual
- equals 
- notEquals

example 
- greaterThan {{u/age}} 75

# Basic operations on Integers

- decr kt
- incr kt
- add kt value   //value as int or kt
- mult kt value     //value as int or kt

# [Conditions] on Strings

xx kt value
with xx in  
- equals
- notEquals
- equalsIgnoreCase
- startsWith
- endsWith
- contains

# Basic operations on Strings

append kt something

# Vars
- copy kt1 kt2
- isFilled kt
- isEmpty kt
- delete kp 
- set kt value or something  // Integer or String

examples : 
- isFilled {{u/name}}

# Status
succeed [returns a condition]
fail [returns a condition]
???? running 

# Composites 

```
begin sequence 
	BTN+
end sequence
	
begin selector 
	BTN+
end selector

begin try tries
	BTN+
end try

begin loop  loops
	BTN+
end loop

begin shuffle
	BTN+
end shuffle
```

example :
context : {{color}} contains "A" "B" or "C"

## example with selector 

``` ex selector
begin selector 
	begin sequence 
		equals {{color}} "A"
		say "the color is blue"
	end sequence
	begin sequence
		equals {{color}} "B"
		say "the color is white"
	end sequence
	begin sequence
		equals {{color}} "C"
		say "the color is red"
	end sequence
	say "I don't know the chosen color ;-("
end selector
```
## example with shuffle

``` ex shuffle
begin shuffle
	say "Hi"
	say "Hello"
	say  Hey!"
end shuffle
```
```
begin switch {{color}}
	case "A"
		say ...	
		say ..
	case "B"
		say...	
	case "C"
		say...	
	say...	
	say...	
	say...	
end switch
```
```choose
begin choose:button {{color}} "what is your ....color ?" 
	button "A" "bleu" 
	button "B" "blanc"
	button "C" "rouge"
end choose
```

???? How load a list of buttons (countries for example) 

# comments
``` comments 
-- this is a comment 
-- this is another line of comment
```

begin sequence -- a comment after a command
 
???? # while / until
``` while ???
begin while [ {{u/age}}>=75 or {{u/illness}}==1 ]
	((BTC and BTC) or BTC)  
	BTN+
end while 
```	

# trees
How to declare the main trees and these used inside ?

```
declare tree "administrator"
	say "hello, you're an admin"
end tree
```

```
declare tree "main"
	begin switch {{role}}
		case "C"
			tree "contributor" {{/contributor}}
		case "A"
			tree "administrator" {{/administrator}}
		case "U"
			tree "user" {{/user}} 	
	end switch 
end tree
```

namespaces
/
/administrator
/contributor 
/user


