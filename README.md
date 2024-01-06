# [LibPG](https://marcpg.com/libpg) by [MarcPG](https://marcpg.com/)

LibPG is a utility library made by MarcPG for his own projects and some others.  
LibPG adds many features that are missing from default Java or are just made easier in this library, such as time counting, etc.

---

**Here are all the features:**

## Color

### McFormat

McFormat provides coloring and general formatting for Minecraft plugins, mods, etc. that don't have a color library like the Adventure-Component-API built in.  
You can use various color and even make your own ones (custom ones might have limited compatibility). There are also features like bold or italic text.  
After colored text, you should normally use McFormat.RESET. You can also transform the McFormat to Ansi by using .ansi(), but the [Ansi](#ansi) class is generally more recommended for doing that.

### Ansi

Ansi provides coloring and other formatting for shells that support Ansi, which are almost all shells.  
You can also use it for things like background colors, custom colors, etc.

## Data

Data is used to store common data values/counts, such as time, bits, bytes, etc.

### Storage

BitData and ByteData can be used to store an amount of data in either bits (bits, megabits, gigabits, etc.) or in bytes (bytes, megabytes, gigabytes, etc.).  
It is generally more common in normal usage cases to display data in bytes, not bits, as it's the standard and easier to convert.

### Time

#### Time

Time can be used to store, simplify, count, get and format time.  
It's object based and also used in the [timer](#timer) for counting using #increment() and #decrement().  
You can also format time into strings using the #getOneUnitFormatted() and #getPreciselyFormatted() methods, which return it with proper units like s, min, h, etc.

#### Timer

You can use the Timer interface to make your own timers. That's pretty much all there is to it.

## Storing

### Pair

Pairs can be used to store two objects in one, like a Map, but with only one pair.  
The two values are called left and right and can be changed whenever you want, as they are not final.

## Text

### Completer

The completer can be used to complete input based on the current input and the suggested possibilities.  
There are three different completion types:
- Start Complete: Completes the text based on every possibility that can be there after the string that was already typed.
- Contain Complete: Completes the text based on every possibility that contains the string that was already typed.
- Semi-Smart Complete: Completes the text in a somewhat smart way by using start completion when the input is three characters or less and contain completion when there's more than three characters typed.

### Formatter

The formatter helps when handling text.  
There is currently only #toPascalCase, which is used to convert names of static variables or enum values into good looking text.

## Util

### Randomizer

Provides functions for randomizing things like characters, strings, booleans with chances, etc. without the need for additional code.  
You can use the boolean randomizers to generate a boolean with a specified chance, like 70% for example.
